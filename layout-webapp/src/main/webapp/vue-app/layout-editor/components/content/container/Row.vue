<template>
  <layout-editor-container-container-base
    :container="container"
    :class="cssClass"
    class="row mx-n3 border-box-sizing">
    <template #content>
      <v-hover>
        <div
          v-if="$root.editPage"
          slot-scope="{hover}"
          :style="{
            top: '-22px',
            right: '-12px',
            bottom: '-4px',
            left: '-12px',
          }"
          class="position-absolute pa-3">
          <div class="position-relative full-height full-width">
            <layout-editor-section-border-menu v-show="hover" />
          </div>
        </div>
      </v-hover>
    </template>
  </layout-editor-container-container-base>
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
    cssClass() {
      return this.$root.editPage && 'position-relative' || null;
    },
  },
};
</script>
