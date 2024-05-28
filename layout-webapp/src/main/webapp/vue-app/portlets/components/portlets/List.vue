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
  computed: {
    portlets() {
      return this.$root.portlets;
    },
    loading() {
      return !!this.$root.loading;
    },
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
      portlets.sort((a, b) => this.$root.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
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
        portlets.sort((a, b) => this.$root.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      }
      if (desc) {
        portlets.reverse();
      }
      return portlets;
    },
  },
};
</script>
