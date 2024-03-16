
<template>
  <div :class="gridClass">
    <layout-editor-section-selection-grid-cell
      v-for="i in length"
      :id="`grid-cell-${i}`"
      :key="i"
      :index="i"
      :cols="cols"
      :cells-validity="cellsValidity" />
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
  data: () => ({
    cellsValidity: {},
  }),
  computed: {
    isMove() {
      return this.$root.isMove;
    },
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
  watch: {
    isMove() {
      this.computeCellsValidity();
    },
  },
  created() {
    this.computeCellsValidity();
  },
  methods: {
    computeCellsValidity() {
      if (this.isMove) {
        window.setTimeout(() => {
          const cellsValidity = {};
          for (let i = 0; i < this.section.rowsCount; i++) {
            cellsValidity[i] = {};
            for (let j = 0; j < this.section.colsCount; j++) {
              cellsValidity[i][j] = this.$layoutUtils.isValidTargetMovingCell(this.section, this.$root.movingCell, i, j);
            }
          }
          this.cellsValidity = cellsValidity;
        }, 50);
      } else {
        this.cellsValidity = {};
      }
    },
  },
};
</script>