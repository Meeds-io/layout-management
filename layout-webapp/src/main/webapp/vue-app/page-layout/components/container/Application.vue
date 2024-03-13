<template>
  <v-card
    :width="appWidth || ''"
    :max-width="appWidth || ''"
    :min-height="appHeight || ''"
    :max-height="appHeight || ''"
    :height="appHeight || ''"
    class="overflow-hidden"
    color="transparent"
    flat>
    <div ref="content"></div>
  </v-card>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    parentId: {
      type: String,
      default: null,
    },
  },
  computed: {
    nodeUri() {
      return this.$root.nodeUri;
    },
    storageId() {
      return this.container?.storageId;
    },
    appHeight() {
      return this.cellHeight - (this.container?.gap?.v || 20);
    },
    appWidth() {
      return this.cellWidth - (this.container?.gap?.h || 20) - 8;
    },
  },
  mounted() {
    this.installApplication();
  },
  methods: {
    installApplication() {
      if (this.$refs.content
          && this.nodeUri
          && this.storageId) {
        this.$applicationUtils.installApplication(this.nodeUri, this.storageId, this.$refs.content);
      }
    }
  }
};
</script>