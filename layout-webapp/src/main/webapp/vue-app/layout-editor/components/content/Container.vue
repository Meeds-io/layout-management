<template>
  <v-col :cols="cols">
    <v-card
      class="d-flex full-width full-height grey-background aspect-ratio-1 no-border"
      outlined
      hover />
  </v-col>
</template>
<script>
export default {
  props: {
    cols: {
      type: Number,
      default: () => 3,
    },
  },
};
</script>
