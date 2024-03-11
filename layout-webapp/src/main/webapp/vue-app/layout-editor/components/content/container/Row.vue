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
    ref="section"
    class="position-relative layout-section pb-5">
    <layout-editor-section-grid
      :rows="rowsCount"
      :cols="colsCount"
      class="position-absolute full-width full-height grid-gap-cols-5 grid-gap-rows-5 pb-5" />
    <layout-editor-container-container-base
      ref="container"
      :container="container"
      :index="index"
      :length="length"
      :context="context"
      :moving="movingChildren"
      :cell-height="cellHeight"
      :cell-width="cellWidth"
      :rows-count="rowsCount"
      :cols-count="colsCount"
      class="position-relative z-index-one"
      type="section"
      force-draggable
      @hovered="hoverSection = $event"
      @move-start="movingChildren = true" />
    <v-hover v-if="!context">
      <div
        v-if="!context"
        slot-scope="{ hover }"
        class="layout-section-border">
        <div class="position-relative full-height full-width">
          <layout-editor-section-border-menu
            :hover="hover || hoverSection || movingSection"
            :index="index"
            :length="length"
            :moving="movingSection"
            @move-start="movingSection = true" />
        </div>
      </div>
    </v-hover>
  </div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    index: {
      type: Number,
      default: null,
    },
    length: {
      type: Number,
      default: null,
    },
    context: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    hoverSection: false,
    movingSection: false,
    movingChildren: false,
    resizingCell: null,
    mouseX: 0,
    mouseY: 0,
    sectionWidth: 0,
    sectionX: 0,
    sectionY: 0,
  }),
  computed: {
    cellWidth() {
      return this.sectionWidth && this.colsCount && (this.sectionWidth / this.colsCount + 1);
    },
    cellHeight() {
      return this.cellWidth;
    },
    colsCount() {
      return this.container?.colsCount;
    },
    rowsCount() {
      return this.container?.rowsCount;
    },
    resizingCellColIndex() {
      return this.resizingCell?.colIndex;
    },
    resizingCellRowIndex() {
      return this.resizingCell?.rowIndex;
    },
    mouseCellRowIndex() {
      return Math.max(
        Math.min(
          Math.ceil((this.mouseY - this.sectionY) / this.cellHeight) - 1,
          this.rowsCount - 1
        ),
        this.resizingCellRowIndex
      );
    },
    mouseCellColIndex() {
      return Math.max(
        Math.min(
          Math.ceil((this.mouseX - this.sectionX) / this.cellWidth) - 1,
          this.colsCount - 1
        ),
        this.resizingCellColIndex
      );
    },
  },
  watch: {
    movingSection() {
      this.$root.draggedContainerType = this.movingSection && 'section' || null;
    },
    colsCount() {
      this.refreshDimensions();
    },
    rowsCount() {
      this.refreshDimensions();
    },
    mouseCellRowIndex() {
      this.$root.mouseCellRowIndex = this.mouseY ? this.mouseCellRowIndex : -1;
      this.$root.mouseCellColIndex = this.mouseX ? this.mouseCellColIndex : -1;
    },
    mouseCellColIndex() {
      this.$root.mouseCellRowIndex = this.mouseY ? this.mouseCellRowIndex : -1;
      this.$root.mouseCellColIndex = this.mouseX ? this.mouseCellColIndex : -1;
    },
  },
  created() {
    this.$root.$on('layout-move-container', this.handleMoving);
    this.$root.$on('layout-cell-resize-start', this.handleCellResizeStart);
    this.$root.$on('layout-cell-resize-end', this.handleCellResizeEnd);
  },
  mounted() {
    if (!this.context) {
      window.addEventListener('resize', this.refreshDimensions);
      document.querySelector('.page-scroll-content').addEventListener('scroll', this.refreshDimensions);
    }
    this.refreshDimensions();
  },
  beforeDestroy() {
    this.$root.$off('layout-move-container', this.handleMoving);
    this.$root.$off('layout-cell-resize-start', this.handleCellResizeStart);
    this.$root.$off('layout-cell-resize-end', this.handleCellResizeEnd);
    if (!this.context) {
      window.removeEventListener('resize', this.refreshDimensions);
      document.querySelector('.page-scroll-content').removeEventListener('scroll', this.refreshDimensions);
    }
  },
  methods: {
    handleMoving(moving, storageId, sectionId) {
      if (this.movingSection && !moving) {
        this.movingSection = false;
      } else if (sectionId === this.container.storageId) {
        window.setTimeout(() => {
          this.$layoutUtils.refreshCellIndexes(this.container);
        }, 300);
      }
    },
    refreshDimensions() {
      window.setTimeout(() => {
        if (!this.$refs.section) {
          return;
        }
        const dimensions = this.$refs.section.getBoundingClientRect();
        this.sectionWidth = dimensions.width;
        this.sectionX = dimensions.x;
        this.sectionY = dimensions.y;
      }, 300);
    },
    handleCellResizeStart(sectionId, cell) {
      if (sectionId !== this.container.storageId) {
        return;
      }
      this.$refs.container.$el.addEventListener('mousemove', this.handleMouseMove);
      this.resizingCell = cell;
    },
    handleCellResizeEnd(sectionId) {
      if (sectionId !== this.container.storageId) {
        return;
      }
      this.$layoutUtils.resizeCell(this.container, this.resizingCell, this.mouseCellRowIndex, this.mouseCellColIndex);
      this.$refs.container.$el.removeEventListener('mousemove', this.handleMouseMove);
      this.resizingCell = null;
      this.mouseX = 0;
      this.mouseY = 0;
    },
    handleMouseMove(event) {
      this.mouseX = event.x;
      this.mouseY = event.y;
    },
  },
};
</script>
