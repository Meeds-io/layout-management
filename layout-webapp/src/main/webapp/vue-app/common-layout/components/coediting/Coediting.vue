<template>
  <div v-if="initialized">
    <slot v-if="display"></slot>
    <exo-confirm-dialog
      ref="confirmDialog"
      v-else-if="locked"
      :title="lockConfirmTitle"
      :message="lockMessage"
      :ok-label="lockConfirmOkLabel"
      persistent
      @ok="confirmLock" />
    <exo-confirm-dialog
      ref="confirmDialog"
      v-else-if="hasDraft"
      :title="draftConfirmTitle"
      :message="draftMessage"
      :ok-label="draftConfirmOkLabel"
      :cancel-label="draftConfirmCancelLabel"
      persistent
      @ok="confirmDraft"
      @dialog-closed="cancelDraft" />
    <div v-if="outdatedRevision" class="mask-color absolute-full-size z-index-two d-flex align-center justify-center">
      <v-icon size="24" color="white">fa-lock</v-icon>
      <v-card
        max-width="200px"
        class="transparent pa-5"
        flat>
        {{ $t('layout.pageBeingEditedByYouInOther') }}
      </v-card>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    value: { // revision
      type: String,
      default: null,
    },
    objectType: {
      type: String,
      default: null,
    },
    objectId: {
      type: String,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    messages: {
      type: Object,
      default: () => ({
        lockConfirmTitle: null,
        lockConfirmMessage: null,
        lockConfirmQuestion: null,
        lockConfirmOkLabel: null,
        draftConfirmTitle: null,
        draftConfirmMessage: null,
        draftConfirmQuestion: null,
        draftConfirmOkLabel: null,
        draftConfirmCancelLabel: null,
      }),
    },
  },
  data: () => ({
    lockingUsers: null,
    lockingUserIdentities: null,
    draft: null,
    initialized: false,
    outdatedRevision: false,
    revisionUpdatePeriod: 10000,
    revisionUpdateInterval: 0,
    draftConfirmed: false,
    dateFormat: {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    },
  }),
  computed: {
    revision() {
      return this.draft?.revision;
    },
    revisionTime() {
      return this.draft?.time;
    },
    lockHolders() {
      return this.lockingUsers?.filter?.(u => u !== eXo.env.portal.userName);
    },
    lockingUserNames() {
      return this.lockingUserIdentities?.map?.(identity => identity?.profile?.fullname) || [];
    },
    locked() {
      return !!this.lockHolders?.length;
    },
    hasDraft() {
      return this.revision && String(this.revision) !== String(this.value);
    },
    display() {
      return this.disabled || (this.initialized && !this.locked && !this.hasDraft);
    },
    lockConfirmTitle() {
      return this.$t(this.messages.lockConfirmTitle);
    },
    lockConfirmMessage() {
      return this.$t(this.messages.lockConfirmMessage, {
        0: `<strong>${this.lockingUserNames.join(', ')}</strong>`,
      });
    },
    lockConfirmQuestion() {
      return this.$t(this.messages.lockConfirmQuestion);
    },
    lockMessage() {
      return `${this.lockConfirmMessage}<br><br>${this.lockConfirmQuestion}`;
    },
    lockConfirmOkLabel() {
      return this.$t(this.messages.lockConfirmOkLabel);
    },
    draftConfirmTitle() {
      return this.$t(this.messages.draftConfirmTitle);
    },
    draftConfirmMessage() {
      return this.$t(this.messages.draftConfirmMessage, {
        0: `<strong>${this.revisionTime && this.$dateUtil.formatDateObjectToDisplay(new Date(this.revisionTime), this.dateFormat) || ''}</strong>`,
      });
    },
    draftConfirmQuestion() {
      return this.$t(this.messages.draftConfirmQuestion);
    },
    draftMessage() {
      return `${this.draftConfirmMessage}<br><br>${this.draftConfirmQuestion}`;
    },
    draftConfirmOkLabel() {
      return this.$t(this.messages.draftConfirmOkLabel);
    },
    draftConfirmCancelLabel() {
      return this.$t(this.messages.draftConfirmCancelLabel);
    },
  },
  watch: {
    initialized() {
      if (this.initialized) {
        this.$nextTick().then(() => {
          this.$refs?.confirmDialog?.open?.();
        });
      }
    },
    lockingUsers() {
      if (this.lockingUsers?.length) {
        Promise.all(this.lockingUsers.map(u => this.$identityService.getIdentityByProviderIdAndRemoteId('organization', u)))
          .then(identities => this.lockingUserIdentities = identities);
      }
    },
    locked() {
      if (this.locked) {
        this.$emit('locked');
      }
    },
    hasDraft() {
      if (!this.locked && this.hasDraft) {
        this.$emit('draft-detected');
      }
    },
    display() {
      if (this.display) {
        this.setPingRevisionInterval();
        this.$emit('initialized');
      }
    },
  },
  created() {
    if (!this.disabled) {
      this.init();
      this.$root.$on('coediting-set-lock', this.setRevision);
      this.$root.$on('coediting-remove-revision', this.removeLock);
    }
  },
  beforeDestroy() {
    this.$root.$off('coediting-set-lock', this.setRevision);
    this.$root.$off('coediting-remove-revision', this.removeLock);
    this.clearPingRevisionInterval();
  },
  methods: {
    init() {
      return this.getLockHolders()
        .then(() => this.getRevision())
        .finally(() => this.initialized = true);
    },
    getRevision() {
      return this.$coeditingService.getRevision(this.objectType, this.objectId)
        .then(data => this.draft = data);
    },
    setRevision() {
      this.setLock();
      this.setPingRevisionInterval();
    },
    setLock(revision) {
      if (revision) {
        this.$emit('input', revision);
      }
      revision = revision || this.value;
      if (revision) {
        return this.$coeditingService.getRevision(this.objectType, this.objectId)
          .then(data => {
            if (!data?.revision || String(revision) === String(data.revision)) {
              return this.$coeditingService.setLock(this.objectType, this.objectId, `${revision}`)
                .then(() => this.draft = {revision});
            } else {
              this.outdatedRevision = true;
            }
          });
      }
    },
    removeLock() {
      this.removeRevision();
      this.clearPingRevisionInterval();
    },
    removeRevision() {
      return this.$coeditingService.removeRevision(this.objectType, this.objectId)
        .then(() => this.draft = null);
    },
    getLockHolders() {
      return this.$coeditingService.getLockHolders(this.objectType, this.objectId)
        .then(data => this.lockingUsers = data);
    },
    confirmLock() {
      this.$emit('canceled');
    },
    confirmDraft() {
      this.draftConfirmed = true;
      return this.$coeditingService.setLock(this.objectType, this.objectId, this.revision)
        .then(() => {
          this.draft = {revision: this.revision};
          this.$emit('input', this.revision);
          this.setPingRevisionInterval();
        });
    },
    cancelDraft() {
      if (this.draftConfirmed) {
        return;
      }
      return this.removeRevision()
        .then(() => {
          this.draft = null;
          this.$emit('input', null);
          this.setPingRevisionInterval();
        });
    },
    clearPingRevisionInterval() {
      if (this.revisionUpdateInterval) {
        window.clearInterval(this.revisionUpdateInterval);
        this.revisionUpdateInterval = null;
      }
    },
    setPingRevisionInterval() {
      if (!this.revisionUpdateInterval) {
        this.revisionUpdateInterval = window.setInterval(() => this.pingRevision(), this.revisionUpdatePeriod);
      }
    },
    pingRevision() {
      if (this.initialized && this.value && this.display) {
        this.setLock();
      }
    },
  },
};
</script>