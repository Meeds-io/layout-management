<template>
  <div :class="gridClass" class="grid-gap-1">
    <div
      v-for="i in length"
      :key="i"
      :id="`grid-cell-${i}`"
      :class="cellClass"
      class="grey-background grid-cell-lg-colspan-1 grid-cell-lg-rowspan-1 opacity-5"></div>
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
      return `d-md-grid grid-cols-md-${this.cols} grid-rows-md-${this.rows}`;
    },
    length() {
      return this.rows * this.cols;
    },
  },
};
</script>