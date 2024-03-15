
<template>
  <div :class="gridClass">
    <layout-editor-section-selection-grid-cell
      v-for="i in length"
      :id="`grid-cell-${i}`"
      :key="i"
      :index="i"
      :cols="cols" />
  </div>
</template>
<script>
export default {
  props: {
    section: {
      type: Object,
      default: null,
    },
  },
  computed: {
    rows() {
      return this.section.rowsCount;
    },
    cols() {
      return this.section.colsCount;
    },
    gridClass() {
      return `grid-cols-${this.cols} grid-rows-${this.rows}`;
    },
    length() {
      return this.rows * this.cols;
    },
  },
};
</script>