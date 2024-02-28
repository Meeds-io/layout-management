<template>
  <div class="absolute-full-size border-color-thin-grey-opacity2 border-radius">
    <div class="position-relative full-width full-height">
      <v-btn
        class="absolute-horizontal-center t-0 z-index-two mt-n4"
        height="32"
        width="32"
        icon
        @click="$root.$emit('layout-add-section-before', index)">
        <v-icon size="24">fa-plus</v-icon>
      </v-btn>
      <v-btn
        class="absolute-horizontal-center b-0 z-index-two mb-n4"
        height="32"
        width="32"
        icon
        @click="$root.$emit('layout-add-section-after', index)">
        <v-icon size="32">fa-plus</v-icon>
      </v-btn>
      <v-btn
        class="absolute-vertical-center r-0 z-index-two mr-n5"
        height="32"
        width="32"
        icon
        @click="$root.$emit('layout-add-section-edit', index)">
        <v-icon size="24">fa-edit</v-icon>
      </v-btn>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    hover: {
      type: Boolean,
      default: false,
    },
    container: {
      type: Boolean,
      default: false,
    },
    index: {
      type: Boolean,
      default: false,
    },
  },
};
</script>
