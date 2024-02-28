<template>
  <div>
    <layout-editor-container-container-extension
      v-for="child in children"
      :key="computeId(child)"
      :container="child" />
  </div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
  },
  computed: {
    children() {
      return this.container?.children || [];
    },
  },
  methods: {
    computeId(container) {
      if (!container.storageId) {
        container.storageId = parseInt(Math.random() * 10000);
      }
      return container.storageId;
    },
  }
};
</script>
