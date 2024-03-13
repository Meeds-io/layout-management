<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

-->
<template>
  <div
    v-show="movingCell"
    ref="movingBox"
    :class="!validSelection && 'secondary-border-color'"
    :style="boxStyle"
    class="layout-selecting-container d-flex position-absolute elevation-2 z-index-modal">
  </div>
</template>
<script>
export default {
  props: {
    section: {
      type: Object,
      default: null,
    },
    sectionX: {
      type: Number,
      default: null,
    },
    sectionY: {
      type: Number,
      default: null,
    },
  },
  data: () => ({
    containerElement: null,
    interceptEvents: false,
    parentAppDimensions: null,
    computingDisplayInterval: null,
    boxHeight: null,
    boxWidth: null,
  }),
  computed: {
    movingCell() {
      return this.$root.movingCell;
    },
    mouseCellRowIndex() {
      return this.$root.mouseCellRowIndex;
    },
    mouseCellColIndex() {
      return this.$root.mouseCellColIndex;
    },
    movingCellRowIndex() {
      return this.movingCell?.rowIndex || -1;
    },
    movingCellColIndex() {
      return this.movingCell?.colIndex || -1;
    },
    movingCellRowsCount() {
      return this.movingCell?.rowsCount || -1;
    },
    movingCellColsCount() {
      return this.movingCell?.colsCount || -1;
    },
    mouseCellEndRowIndex() {
      return this.mouseCellRowIndex + this.movingCellRowsCount;
    },
    mouseCellEndColIndex() {
      return this.mouseCellColIndex + this.movingCellColsCount;
    },
    validSelection() {
      return this.$layoutUtils.isValidTargetMovingCell(
        this.section,
        this.movingCell,
        this.mouseCellRowIndex,
        this.mouseCellColIndex);
    },
    movingStartX() {
      return this.$root.movingStartX;
    },
    movingStartY() {
      return this.$root.movingStartY;
    },
    movingX() {
      return this.$root.movingX;
    },
    movingY() {
      return this.$root.movingY;
    },
    parentAppX() {
      return this.$root.parentAppDimensions?.x || 0;
    },
    parentAppY() {
      return this.$root.parentAppDimensions?.y || 0;
    },
    boxStyle() {
      return {
        top: `${this.movingY - 24}px`,
        left: `${this.movingX - this.sectionX - this.boxWidth / 2 + 36}px`,
        height: `${this.boxHeight + 3}px`,
        'max-height': `${this.boxHeight + 3}px`,
        width: `${this.boxWidth + 3}px`,
        'max-width': `${this.boxWidth + 3}px`,
      };
    },
  },
  watch: {
    interceptEvents() {
      if (this.interceptEvents) {
        document.addEventListener('mousemove', this.updateMoving);
        document.querySelector('.page-scroll-content').addEventListener('scroll', this.updateScrollPosition);
      } else {
        document.removeEventListener('mousemove', this.updateMoving);
        document.querySelector('.page-scroll-content').removeEventListener('scroll', this.updateScrollPosition);
        this.$emit('hide');
      }
    },
    parentAppX(newVal, oldVal) {
      if (this.interceptEvents && this.$root.movingX && this.$root.movingY) {
        this.$root.movingX = this.$root.movingX - newVal + oldVal;
      }
    },
    parentAppY(newVal, oldVal) {
      if (this.interceptEvents && this.$root.movingX && this.$root.movingY) {
        this.$root.movingY = this.$root.movingY - newVal + oldVal;
      }
    },
    containerElement(newVal, oldVal) {
      if (!oldVal && newVal) {
        window.setTimeout(() => {
          this.$refs.movingBox.append(newVal);
        }, 100);
      }
    },
  },
  created() {
    this.$root.$on('layout-cell-moving-start', this.startMoving);
    this.$root.$on('layout-cell-moving-end', this.endMoving);
  },
  beforeDestroy() {
    this.$root.$off('layout-cell-moving-start', this.startMoving);
    this.$root.$off('layout-cell-moving-end', this.endMoving);
  },
  methods: {
    updateScrollPosition() {
      if (this.interceptEvents) {
        this.$root.updateParentAppDimensions();
        this.$nextTick(() => {
          this.diffScrollX = this.$root.parentAppDimensions.x - this.startScrollX;
          this.diffScrollY = this.$root.parentAppDimensions.y - this.startScrollY;
        });
      }
    },
    startMoving(event) {
      if (!this.interceptEvents
          && event?.target?.closest?.('#layoutEditor')
          && event?.target?.tagName !== 'BUTTON'
          && event?.target?.tagName !== 'A') {
        const containerDimensions = event.containerElement.getBoundingClientRect();
        this.boxHeight = containerDimensions.height;
        this.boxWidth = containerDimensions.width;

        this.$root.updateParentAppDimensions();
        this.$root.movingX = this.$layoutUtils.getX(event) - this.$root.parentAppDimensions.x;
        this.$root.movingY = this.$layoutUtils.getY(event) - this.$root.parentAppDimensions.y;
        this.$root.movingStartX = this.$root.movingX;
        this.$root.movingStartY = this.$root.movingY;
        this.startScrollX = this.$root.parentAppDimensions.x;
        this.startScrollY = this.$root.parentAppDimensions.y;
        this.diffScrollX = 0;
        this.diffScrollY = 0;
        this.containerElement = event.containerElement.cloneNode(true);
        this.$root.movingCell = event.cell;

        this.interceptEvents = false;
        this.$nextTick().then(() => this.interceptEvents = true);
      }
    },
    updateMoving(event) {
      if (this.interceptEvents) {
        this.$root.movingX = this.$layoutUtils.getX(event) - this.parentAppX;
        this.$root.movingY = this.$layoutUtils.getY(event) - this.parentAppY;
      }
    },
    endMoving() {
      if (this.interceptEvents) {
        this.interceptEvents = false;
        this.$root.movingCell = null;
      }
    },
  },
};
</script>