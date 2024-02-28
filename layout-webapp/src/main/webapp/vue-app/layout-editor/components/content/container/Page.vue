<template>
  <div :id="`UIPage-${container.storageId}`">
    <div class="UIComponentBlock">
      <div class="VIEW-PAGE">
        <div id="UIPage">
          <layout-editor-container-container-children
            :container="container"
            class="UIRowContainer" />
        </div>
      </div>
    </div>
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
};
</script>
