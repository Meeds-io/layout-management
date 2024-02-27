<template>
  <div class="position-relative">
    <div
      v-show="hover"
      :style="{
        top: '-8px',
        right: '-20px',
        bottom: '-8px',
        left: '-20px',
      }"
      class="position-absolute border-color-grey-lighten border-radius"></div>
    <v-row class="my-n3">
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
      <layout-editor-container cols="3" />
    </v-row>
  </div>
</template>
<script>
export default {
  props: {
    page: {
      type: Object,
      default: null,
    },
    hover: {
      type: Boolean,
      default: false,
    },
  },
};
</script>
