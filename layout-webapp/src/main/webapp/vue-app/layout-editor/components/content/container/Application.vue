<template>
  <div ref="content" class="layout-application"></div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
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