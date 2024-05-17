<template>
  <v-data-table
    :headers="headers"
    :items="filteredPageTemplates"
    :loading="loading"
    :disable-sort="$root.isMobile"
    :hide-default-header="$root.isMobile"
    disable-pagination
    hide-default-footer
    class="pageTemplatesTable px-5">
    <template slot="item" slot-scope="props">
      <page-templates-management-item
        :key="props.item.name"
        :page-template="props.item" />
    </template>
  </v-data-table>
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
    pageTemplates: [],
    loading: false,
  }),
  computed: {
    headers() {
      return [
        {
          text: '',
          value: 'illustrationId',
          align: 'center',
          sortable: false,
          class: 'page-template-illustration-header',
          width: '60px'
        },
        {
          text: this.$t('pageTemplates.label.name'),
          value: 'name',
          align: 'left',
          sortable: true,
          class: 'page-template-name-header',
          width: '20%'
        },
        {
          text: this.$t('pageTemplates.label.description'),
          value: 'description',
          align: 'center',
          sortable: false,
          class: 'page-template-description-header',
          width: '70%'
        },
        {
          text: this.$t('pageTemplates.label.category'),
          value: 'category',
          align: 'center',
          sortable: true,
          class: 'page-template-category-header',
          width: '120px'
        },
        {
          text: this.$t('pageTemplates.label.status'),
          value: 'disabled',
          align: 'center',
          sortable: true,
          class: 'page-template-category-header',
          width: '120px'
        },
      ];
    },
    filteredPageTemplates() {
      return this.keyword?.length && this.pageTemplates.filter(t => {
        const name = this.$te(t.name) ? this.$t(t.name) : t.name;
        const description = this.$te(t.description) ? this.$t(t.description) : t.description;
        return name?.toLowerCase?.()?.includes(this.keyword.toLowerCase())
          || this.$utils.htmlToText(description)?.toLowerCase?.()?.includes(this.keyword.toLowerCase());
      }) || this.pageTemplates;
    },
  },
  created() {
    this.$root.$on('page-templates-refresh', this.refreshPageTemplates);
    this.refreshPageTemplates();
  },
  beforeDestroy() {
    this.$root.$off('page-templates-refresh', this.refreshPageTemplates);
  },
  methods: {
    refreshPageTemplates() {
      this.loading = true;
      return this.$pageTemplateService.getPageTemplates()
        .then(pageTemplates => this.pageTemplates = pageTemplates || [])
        .finally(() => this.loading = false);
    },
  },
};
</script>
