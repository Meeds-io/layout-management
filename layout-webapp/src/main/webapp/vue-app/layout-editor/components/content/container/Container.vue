<template>
  <layout-editor-container-container-base
    :container="container" />
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
  },
};
</script>
