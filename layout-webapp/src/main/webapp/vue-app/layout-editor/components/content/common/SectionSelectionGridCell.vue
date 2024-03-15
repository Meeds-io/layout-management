<template>
  <div>
    <div
      :class="selected && 'grey lighten-2'"
      class="border-grey-dashed full-width full-height"></div>
  </div>
</template>
<script>
export default {
  props: {
    cols: {
      type: Number,
      default: null,
    },
    index: {
      type: Number,
      default: null,
    },
  },
  computed: {
    rowIndex() {
      return this.cols && parseInt((this.index - 1) / this.cols);
    },
    colIndex() {
      return this.cols && (this.index - 1) % this.cols;
    },
    selected() {
      return !!this.$root.selectedCellCoordinates.find(c => c.rowIndex === this.rowIndex && c.colIndex === this.colIndex);
    },
  },
};
</script>