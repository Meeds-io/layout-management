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
      class="position-absolute full-width full-height pb-5" />
    <layout-editor-container-base
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
      :class="zIndexClass"
      class="position-relative"
      type="section"
      no-draggable
      @hovered="hoverSection = $event && !drawerOpened"
      @move-start="movingChildren = true">
      <template v-if="$root.movingParentId === storageId" #content>
        <layout-editor-cells-drop-box
          :section="container"
          :section-x="sectionX"
          :section-y="sectionY"
          @hide="$root.movingParentId = null" />
      </template>
    </layout-editor-container-base>
    <v-hover>
      <div
        v-if="!context"
        slot-scope="{ hover }"
        class="layout-section-border">
        <div class="position-relative full-height full-width">
          <layout-editor-section-border-menu
            :hover="!drawerOpened && (hover || hoverSection || movingSection)"
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
    sourceCell: null,
    mouseX: 0,
    mouseY: 0,
    sectionWidth: 0,
    sectionX: 0,
    sectionY: 0,
  }),
  computed: {
    drawerOpened() {
      return this.$root.drawerOpened;
    },
    zIndexClass() {
      return !this.drawerOpened && 'z-index-one';
    },
    cellWidth() {
      return this.sectionWidth && this.colsCount && ((this.sectionWidth + this.$root.gap) / this.colsCount);
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
    storageId() {
      return this.container?.storageId;
    },
    movingCell() {
      return this.$root.movingCell;
    },
    sourceCellRowIndex() {
      return this.sourceCell?.rowIndex;
    },
    sourceCellColIndex() {
      return this.sourceCell?.colIndex;
    },
    mouseCellRowIndex() {
      return Math.max(
        Math.min(
          Math.ceil((this.mouseY - this.sectionY) / this.cellHeight) - 1,
          this.rowsCount - 1
        ),
        this.$root.movingCell ? 0 : this.sourceCellRowIndex
      );
    },
    mouseCellColIndex() {
      const diffWidth = this.$root.movingCell ? (this.cellWidth * this.$root.movingCell.colsCount) / 2 : 0;
      const diffX = this.$root.movingCell ? 0 : 1;
      return Math.max(
        Math.min(
          Math.ceil((this.mouseX - this.sectionX - diffWidth) / this.cellWidth) - diffX,
          this.colsCount - 1
        ),
        this.$root.movingCell ? 0 : this.sourceCellColIndex
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
    this.$root.$on('layout-cell-resize-start', this.handleCellResizeStart);
    this.$root.$on('layout-cell-moving-start', this.handleCellMoveStart);
    this.$root.$on('layout-cell-resize-end', this.handleCellResizeEnd);
    this.$root.$on('layout-cell-moving-end', this.handleCellMoveEnd);
  },
  mounted() {
    if (!this.context) {
      window.addEventListener('resize', this.refreshDimensions);
      document.querySelector('.page-scroll-content').addEventListener('scroll', this.refreshDimensions);
    }
    this.refreshDimensions();
  },
  beforeDestroy() {
    this.$root.$off('layout-cell-resize-start', this.handleCellResizeStart);
    this.$root.$off('layout-cell-moving-start', this.handleCellMoveStart);
    this.$root.$off('layout-cell-resize-end', this.handleCellResizeEnd);
    this.$root.$off('layout-cell-moving-end', this.handleCellMoveEnd);
    if (!this.context) {
      window.removeEventListener('resize', this.refreshDimensions);
      document.querySelector('.page-scroll-content').removeEventListener('scroll', this.refreshDimensions);
    }
  },
  methods: {
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
      this.handleCellMotionStart(sectionId, cell);
    },
    handleCellResizeEnd(sectionId) {
      if (sectionId !== this.container.storageId) {
        return;
      }
      this.$layoutUtils.resizeCell(this.container, this.sourceCell, this.mouseCellRowIndex, this.mouseCellColIndex);
      this.handleCellMotionEnd();
    },
    handleCellMoveStart(event) {
      this.handleCellMotionStart(event.sectionId, event.cell);
    },
    handleCellMoveEnd(event) {
      if (event.sectionId !== this.container.storageId) {
        return;
      }
      this.$layoutUtils.moveCell(this.container, event.cell, this.mouseCellRowIndex, this.mouseCellColIndex);
      this.handleCellMotionEnd();
    },
    handleCellMotionStart(sectionId, cell) {
      if (sectionId !== this.container.storageId) {
        return;
      }
      this.$refs.container.$el.addEventListener('mousemove', this.handleMouseMove);
      this.sourceCell = cell;
    },
    handleCellMotionEnd() {
      this.$refs.container.$el.removeEventListener('mousemove', this.handleMouseMove);
      this.sourceCell = null;
      this.mouseX = 0;
      this.mouseY = 0;
    },
    handleMouseMove(event) {
      if (event.x && event.y) {
        this.mouseX = event.x;
        this.mouseY = event.y;
      }
    },
  },
};
</script>
