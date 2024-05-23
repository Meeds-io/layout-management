<template>
  <div>
    <v-data-table
      :headers="headers"
      :items="filteredPortlets"
      :loading="loading"
      :disable-sort="$root.isMobile"
      :hide-default-header="$root.isMobile"
      :custom-sort="applySortOnItems"
      must-sort
      disable-pagination
      hide-default-footer
      class="portletsTable px-5">
      <template slot="item" slot-scope="props">
        <portlets-item
          :key="props.item.id"
          :portlet="props.item" />
      </template>
    </v-data-table>
    <exo-confirm-dialog
      ref="deleteConfirmDialog"
      :title="$t('portlets.label.confirmDeleteTitle')"
      :message="$t('portlets.label.confirmDeleteMessage', {0: `<br><strong>${nameToDelete}</strong>`})"
      :ok-label="$t('portlets.label.confirm')"
      :cancel-label="$t('portlets.label.cancel')"
      @ok="deletePortlet(portletToDelete)"
      @closed="portletToDelete = null" />
  </div>
</template>
<script>
export default {
  props: {
    keyword: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    portlets: [],
    portletToDelete: null,
    loading: false,
    collator: new Intl.Collator(eXo.env.portal.language, {numeric: true, sensitivity: 'base'}),
  }),
  computed: {
    headers() {
      return this.$root.isMobile && [
        {
          text: this.$t('portlets.label.name'),
          value: 'name',
          align: 'left',
          sortable: true,
          class: 'portlet-name-header',
          width: '20%'
        },
        {
          text: this.$t('portlets.label.actions'),
          value: 'actions',
          align: 'center',
          sortable: false,
          class: 'portlet-actions-header',
          width: '50px'
        },
      ] || [
        {
          text: '',
          value: 'illustrationId',
          align: 'center',
          sortable: false,
          class: 'portlet-illustration-header',
          width: '60px'
        },
        {
          text: this.$t('portlets.label.name'),
          value: 'name',
          align: 'left',
          sortable: true,
          class: 'portlet-name-header',
          width: '20%'
        },
        {
          text: this.$t('portlets.label.description'),
          value: 'description',
          align: 'center',
          sortable: false,
          class: 'portlet-description-header',
          width: '70%'
        },
        {
          text: this.$t('portlets.label.instances'),
          value: 'instances',
          align: 'center',
          sortable: false,
          class: 'portlet-instances-header',
          width: '60px'
        },
        {
          text: this.$t('portlets.label.actions'),
          value: 'actions',
          align: 'center',
          sortable: false,
          class: 'portlet-actions-header',
          width: '50px'
        },
      ];
    },
    noEmptyPortlets() {
      const portlets = this.portlets?.filter?.(t => t.name) || [];
      portlets.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return portlets;
    },
    filteredPortlets() {
      return this.keyword?.length && this.noEmptyPortlets.filter(t => {
        const name = this.$te(t.name) ? this.$t(t.name) : t.name;
        const description = this.$te(t.description) ? this.$t(t.description) : t.description;
        return name?.toLowerCase?.()?.includes(this.keyword.toLowerCase())
          || this.$utils.htmlToText(description)?.toLowerCase?.()?.includes(this.keyword.toLowerCase());
      }) || this.noEmptyPortlets;
    },
    nameToDelete() {
      return this.portletToDelete && this.$te(this.portletToDelete?.name) ? this.$t(this.portletToDelete?.name) : this.portletToDelete?.name;
    },
  },
  created() {
    this.$root.$on('portlets-deleted', this.refreshPortlets);
    this.$root.$on('portlets-created', this.refreshPortlets);
    this.$root.$on('portlets-updated', this.refreshPortlets);
    this.$root.$on('portlets-enabled', this.refreshPortlets);
    this.$root.$on('portlets-disabled', this.refreshPortlets);
    this.$root.$on('portlets-saved', this.refreshPortlets);
    this.$root.$on('portlets-delete', this.deletePortletConfirm);
    this.refreshPortlets();
  },
  beforeDestroy() {
    this.$root.$off('portlets-deleted', this.refreshPortlets);
    this.$root.$off('portlets-created', this.refreshPortlets);
    this.$root.$off('portlets-updated', this.refreshPortlets);
    this.$root.$off('portlets-enabled', this.refreshPortlets);
    this.$root.$off('portlets-disabled', this.refreshPortlets);
    this.$root.$off('portlets-saved', this.refreshPortlets);
    this.$root.$off('portlets-delete', this.deletePortletConfirm);
  },
  methods: {
    applySortOnItems(portlets, sortFields, sortDescendings) {
      for (let i = 0; i < sortFields.length; i++) {
        portlets = this.applySortOnItemsUsingField(portlets, sortFields[i], sortDescendings[i]);
      }
      return portlets;
    },
    applySortOnItemsUsingField(portlets, field, desc) {
      if (field === 'name') {
        portlets.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      }
      if (desc) {
        portlets.reverse();
      }
      return portlets;
    },
    deletePortletConfirm(portlet) {
      this.portletToDelete = portlet;
      if (this.portletToDelete) {
        this.$refs.deleteConfirmDialog.open();
      }
    },
    refreshPortlets() {
      this.loading = true;
      return this.$portletService.getPortlets()
        .then(portlets => this.portlets = portlets || [])
        .finally(() => this.loading = false);
    },
    deletePortlet(portlet) {
      this.loading = true;
      this.$portletService.deletePortlet(portlet.id)
        .then(() => {
          this.$root.$emit('portlets-deleted', portlet);
          this.$root.$emit('alert-message', this.$t('portlets.delete.success'), 'success');
        })
        .catch(() => this.$root.$emit('alert-message', this.$t('portlets.delete.error'), 'error'))
        .finally(() => this.loading = false);
    },
  },
};
</script>
