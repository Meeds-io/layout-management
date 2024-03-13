<template>
  <div :class="gridClass">
    <div
      v-for="i in length"
      :key="i"
      :id="`grid-cell-${i}`"
      :class="cellClass"
      class="grey-background opacity-5"></div>
  </div>
</template>
<script>
export default {
  props: {
    rows: {
      type: Number,
      default: null,
    },
    cols: {
      type: Number,
      default: null,
    },
    cellClass: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    updateGrid: false,
  }),
  computed: {
    gridClass() {
      return `grid-cols-${this.cols} grid-rows-${this.rows}`;
    },
    length() {
      return this.rows * this.cols;
    },
  },
};
</script>