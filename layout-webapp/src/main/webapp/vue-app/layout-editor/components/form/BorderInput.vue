<template>
  <v-card
    max-width="70"
    class="my-auto"
    flat>
    <v-text-field
      v-model="border"
      class="border-box-sizing pt-0"
      type="number"
      max="20"
      min="0"
      step="4"
      hide-details
      outlined
      dense />
  </v-card>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    border: 20,
  }),
  watch: {
    border: {
      immediate: true,
      handler() {
        this.$emit('input', this.border - 20);
      },
    },
  },
  created() {
    this.border = (this.value || 0) + 20;
  }
};
</script>
