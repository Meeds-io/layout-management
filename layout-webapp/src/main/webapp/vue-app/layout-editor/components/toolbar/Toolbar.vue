<template>
  <v-card
    class="d-flex align-center no-border-radius px-4"
    height="57"
    flat>
    <v-icon class="icon-default-color">fa-pager</v-icon>
    <span class="px-2">{{ $t('layout.editPageName', {0: pageName}) }}</span>
    <v-spacer />
    <layout-editor-toolbar-save-button />
  </v-card>
</template>
<script>
export default {
  props: {
    page: {
      type: Object,
      default: null,
    },
    node: {
      type: Object,
      default: null,
    },
    nodeLabels: {
      type: Object,
      default: null,
    },
  },
  computed: {
    pageName() {
      return this.nodeLabels?.labels && (this.nodeLabels.labels[eXo.env.portal.language] || this.nodeLabels.labels[this.defaultLanguage]) || this.page?.title;
    },
  }
};
</script>
