<template>
  <v-card
    class="d-flex flex-row align-center justify-center me-n3"
    max-width="88"
    flat>
    <v-btn
      icon
      @click="decrementBorder">
      <v-icon>fa-minus fa-sm</v-icon>
    </v-btn>
    <div>{{ border }}</div>
    <v-btn
      icon
      @click="incrementBorder">
      <v-icon>fa-plus fa-sm</v-icon>
    </v-btn>
  </v-card>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    diff: {
      type: Number,
      default: () => 0,
    },
  },
  data: () => ({
    border: 20,
  }),
  watch: {
    border: {
      immediate: true,
      handler() {
        this.$emit('input', this.border + this.diff);
      },
    },
  },
  created() {
    this.border = (this.value || 0) - this.diff;
  },
  methods: {
    decrementBorder() {
      if (this.border > 0) {
        this.border -= 4;
      }
    },
    incrementBorder() {
      if (this.border < 20) {
        this.border += 4;
      }
    },
  },
};
</script>
