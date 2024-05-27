<template>
  <tr>
    <!-- Illustration -->
    <td
      v-if="!$root.isMobile"
      align="center"
      width="70px">
      <layout-image-illustration
        :value="portletInstance"
        object-type="portletInstance"
        default-src="/layout/images/portlets/DefaultPortlet.png" />
    </td>
    <!-- name -->
    <td
      :width="$root.isMobile && '100%' || 'auto'"
      align="left">
      <v-card
        v-sanitized-html="name"
        class="text-truncate transparent"
        flat
        @click="$root.$emit('layout-illustration-preview', illustrationSrc)" />
    </td>
    <!-- description -->
    <td
      v-if="!$root.isMobile"
      align="left"
      class="text-truncate"
      v-sanitized-html="description"></td>
    <td
      v-if="!$root.isMobile"
      align="center"
      width="50px">
      <v-switch
        v-model="enabled"
        :loading="loading"
        :aria-label="enabled && $t('portlets.label.disableInstance') || $t('portlets.label.enableInstance')"
        class="mt-0 mx-auto"
        @click="changeStatus" />
    </td>
    <td
      align="center"
      width="50px">
      <portlets-instance-menu :portlet-instance="portletInstance" />
    </td>
  </tr>
</template>
<script>
export default {
  props: {
    portletInstance: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    hoverMenu: false,
  }),
  computed: {
    portletInstanceId() {
      return this.portletInstance?.id;
    },
    enabled() {
      return !this.portletInstance?.disabled;
    },
    name() {
      return this.$te(this.portletInstance?.name) ? this.$t(this.portletInstance?.name) : this.portletInstance?.name;
    },
    description() {
      return this.$te(this.portletInstance?.description) ? this.$t(this.portletInstance?.description) : this.portletInstance?.description;
    },
    illustrationId() {
      return this.portletInstance?.illustrationId;
    },
    illustrationSrc() {
      return this.illustrationId && `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/attachments/portletInstance/${this.portletInstanceId}/${this.illustrationId}` || '/layout/images/portlets/DefaultPortlet.png';
    },
  },
  watch: {
    hoverMenu() {
      if (!this.hoverMenu) {
        window.setTimeout(() => {
          if (!this.hoverMenu) {
            this.menu = false;
          }
        }, 200);
      }
    },
  },
  methods: {
    changeStatus() {
      this.$root.$emit('close-alert-message');
      this.loading = true;
      this.$portletInstanceService.getPortletInstance(this.portletInstance.id)
        .then(portletInstance => {
          portletInstance.disabled = this.enabled;
          return this.$portletInstanceService.updatePortletInstance(portletInstance)
            .then(() => {
              this.$root.$emit(`portlets-instance-${this.enabled && 'disabled' || 'enabled'}`, portletInstance);
            });
        })
        .then(() => {
          this.$root.$emit('alert-message', this.$t('portlets.status.update.success'), 'success');
        })
        .catch(() => this.$root.$emit('alert-message', this.$t('portlets.status.update.error'), 'error'))
        .finally(() => this.loading = false);
    },
  },
};
</script>