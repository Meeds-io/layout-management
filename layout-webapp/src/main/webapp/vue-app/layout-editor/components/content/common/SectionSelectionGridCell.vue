<template>
  <div>
    <div
      :class="{
        'border-grey-dashed': !$root.isMove || valid,
        'border-error-dashed-1': $root.isMove && !valid && !firstCell,
        'border-error-dashed-2': $root.isMove && !valid && firstCell,
      }"
      class="full-width full-height">
      <div
        :class="{
          'grey lighten-2': selected && (!firstCell || !$root.isMove),
          'success-color-background opacity-5': $root.isMove && selected && firstCell && valid,
          'error-color-background opacity-5': $root.isMove && selected && firstCell && !valid,
        }"
        class="full-width full-height">
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    cellsValidity: {
      type: Object,
      default: null,
    },
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
    valid() {
      return this.cellsValidity?.[this.rowIndex]?.[this.colIndex] !== false;
    },
    firstCell() {
      return this.rowIndex === this.$root.movingSelectedFirstRowIndex && this.colIndex === this.$root.movingSelectedFirstColIndex;
    },
  },
};
</script>