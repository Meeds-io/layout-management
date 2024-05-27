<template>
  <div>
    <v-data-table
      :headers="headers"
      :items="filteredPortletInstances"
      :loading="loading"
      :disable-sort="$root.isMobile"
      :hide-default-header="$root.isMobile"
      :custom-sort="applySortOnItems"
      must-sort
      disable-pagination
      hide-default-footer
      class="portletInstancesTable px-5">
      <template slot="item" slot-scope="props">
        <portlets-instance-item
          :key="props.item.id"
          :portlet-instance="props.item" />
      </template>
    </v-data-table>
    <exo-confirm-dialog
      ref="deleteConfirmDialog"
      :title="$t('portlets.label.confirmDeleteTitle')"
      :message="$t('portlets.label.confirmDeleteMessage', {0: `<br><strong>${nameToDelete}</strong>`})"
      :ok-label="$t('portlets.label.confirm')"
      :cancel-label="$t('portlets.label.cancel')"
      @ok="deletePortletInstance(portletInstanceToDelete)"
      @closed="portletInstanceToDelete = null" />
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
    portletInstances: [],
    portletInstanceToDelete: null,
    categoryId: 0,
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
          class: 'portlet-instance-name-header',
          width: '20%'
        },
        {
          text: this.$t('portlets.label.actions'),
          value: 'actions',
          align: 'center',
          sortable: false,
          class: 'portlet-instance-actions-header',
          width: '50px'
        },
      ] || [
        {
          text: '',
          value: 'illustrationId',
          align: 'center',
          sortable: false,
          class: 'portlet-instance-illustration-header',
          width: '60px'
        },
        {
          text: this.$t('portlets.label.name'),
          value: 'name',
          align: 'left',
          sortable: true,
          class: 'portlet-instance-name-header',
          width: '20%'
        },
        {
          text: this.$t('portlets.label.description'),
          value: 'description',
          align: 'center',
          sortable: false,
          class: 'portlet-instance-description-header',
          width: '70%'
        },
        {
          text: this.$t('portlets.label.status'),
          value: 'disabled',
          align: 'center',
          sortable: true,
          class: 'portlet-instance-status-header',
          width: '60px'
        },
        {
          text: this.$t('portlets.label.actions'),
          value: 'actions',
          align: 'center',
          sortable: false,
          class: 'portlet-instance-actions-header',
          width: '50px'
        },
      ];
    },
    noEmptyPortletInstances() {
      const portletInstances = this.portletInstances?.filter?.(t => t.name) || [];
      portletInstances.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return portletInstances;
    },
    filteredPortletInstances() {
      return this.keyword?.length && this.noEmptyPortletInstances.filter(t => {
        const name = this.$te(t.name) ? this.$t(t.name) : t.name;
        const description = this.$te(t.description) ? this.$t(t.description) : t.description;
        return name?.toLowerCase?.()?.includes(this.keyword.toLowerCase())
          || this.$utils.htmlToText(description)?.toLowerCase?.()?.includes(this.keyword.toLowerCase());
      }) || this.noEmptyPortletInstances;
    },
    nameToDelete() {
      return this.portletInstanceToDelete && this.$te(this.portletInstanceToDelete?.name) ? this.$t(this.portletInstanceToDelete?.name) : this.portletInstanceToDelete?.name;
    },
  },
  created() {
    this.$root.$on('portlets-instance-deleted', this.refreshPortletInstances);
    this.$root.$on('portlets-instance-created', this.refreshPortletInstances);
    this.$root.$on('portlets-instance-updated', this.refreshPortletInstances);
    this.$root.$on('portlets-instance-enabled', this.refreshPortletInstances);
    this.$root.$on('portlets-instance-disabled', this.refreshPortletInstances);
    this.$root.$on('portlets-instance-saved', this.refreshPortletInstances);
    this.$root.$on('portlets-instance-delete', this.deletePortletInstanceConfirm);
    this.$root.$on('portlets-instance-category-selected', this.selectCategoryId);
    this.refreshPortletInstances();
  },
  beforeDestroy() {
    this.$root.$off('portlets-instance-deleted', this.refreshPortletInstances);
    this.$root.$off('portlets-instance-created', this.refreshPortletInstances);
    this.$root.$off('portlets-instance-updated', this.refreshPortletInstances);
    this.$root.$off('portlets-instance-enabled', this.refreshPortletInstances);
    this.$root.$off('portlets-instance-disabled', this.refreshPortletInstances);
    this.$root.$off('portlets-instance-saved', this.refreshPortletInstances);
    this.$root.$off('portlets-instance-delete', this.deletePortletInstanceConfirm);
  },
  methods: {
    applySortOnItems(portletInstances, sortFields, sortDescendings) {
      for (let i = 0; i < sortFields.length; i++) {
        portletInstances = this.applySortOnItemsUsingField(portletInstances, sortFields[i], sortDescendings[i]);
      }
      return portletInstances;
    },
    applySortOnItemsUsingField(portletInstances, field, desc) {
      if (field === 'name') {
        portletInstances.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      } else if (field === 'disabled') {
        portletInstances.sort((a, b) => (a.disabled ? 0 : 1) - (b.disabled ? 0 : 1));
      }
      if (desc) {
        portletInstances.reverse();
      }
      return portletInstances;
    },
    deletePortletInstanceConfirm(portletInstance) {
      this.portletInstanceToDelete = portletInstance;
      if (this.portletInstanceToDelete) {
        this.$refs.deleteConfirmDialog.open();
      }
    },
    selectCategoryId(id) {
      if (this.categoryId !== id) {
        this.categoryId = id;
        this.refreshPortletInstances();
      }
    },
    refreshPortletInstances() {
      this.loading = true;
      return this.$portletInstanceService.getPortletInstances(this.categoryId)
        .then(portletInstances => this.portletInstances = portletInstances || [])
        .finally(() => this.loading = false);
    },
    deletePortletInstance(portletInstance) {
      this.loading = true;
      this.$portletInstanceService.deletePortletInstance(portletInstance.id)
        .then(() => {
          this.$root.$emit('portlets-instance-deleted', portletInstance);
          this.$root.$emit('alert-message', this.$t('portlets.delete.success'), 'success');
        })
        .catch(() => this.$root.$emit('alert-message', this.$t('portlets.delete.error'), 'error'))
        .finally(() => this.loading = false);
    },
  },
};
</script>
