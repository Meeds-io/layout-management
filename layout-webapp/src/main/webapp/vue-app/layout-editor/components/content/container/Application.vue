<template>
  <v-card
    :class="{
      'position-absolute z-index-one': !!cellHeight,
    }"
    :width="appWidth || ''"
    :max-width="appWidth || ''"
    :min-height="appHeight || ''"
    :max-height="appHeight || ''"
    :height="appHeight || ''"
    class="layout-application"
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
    cellHeight: {
      type: Number,
      default: () => 0,
    },
    cellWidth: {
      type: Number,
      default: () => 0,
    },
  },
  data: () => ({
    applicationInstalled: false,
  }),
  computed: {
    storageId() {
      return this.container?.storageId;
    },
    nodeId() {
      return this.$root.draftNodeId;
    },
    nodeUri() {
      return this.$root.draftNodeUri;
    },
    appHeight() {
      return this.cellHeight - (this.container?.gap?.v || 20);
    },
    appWidth() {
      return this.cellWidth - (this.container?.gap?.h || 20) - 8;
    },
  },
  watch: {
    storageId(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.installApplication();
      }
    },
    nodeUri(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.installApplication();
      }
    },
  },
  mounted() {
    this.installApplication();
  },
  updated() {
    this.installApplication();
  },
  methods: {
    installApplication() {
      if (!this.applicationInstalled
          && this.$refs.content
          && this.nodeUri
          && this.storageId) {
        this.$applicationUtils.installApplication(this.nodeUri, this.storageId, this.$refs.content)
          .then(() => this.applicationInstalled = true);
      }
    }
  }
};
</script>