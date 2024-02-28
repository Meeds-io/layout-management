<template>
  <component
    v-if="containerType"
    :is="containerType"
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
  computed: {
    containerType() {
      const extension = this.container && this.$root.containerTypes.find(ext => ext?.isValid?.(this.container));
      return extension?.containerType;
    },
    params() {
      return {
        container: this.container,
      };
    },
  },
};
</script>