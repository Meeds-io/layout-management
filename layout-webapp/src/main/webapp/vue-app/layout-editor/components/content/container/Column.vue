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
  <layout-editor-container-base
    ref="container"
    :container="container"
    :index="index"
    :length="length"
    :context="context"
    :hide-children="moving"
    :cell-height="targetCellHeight"
    :cell-width="targetCellWidth"
    :class="(hover && !$root.drawerOpened && 'z-index-two') || (resize && 'z-index-modal')"
    class="position-relative"
    @hovered="hover = $event">
    <template #content>
      <template v-if="hasApplication">
        <div
          v-if="resize"
          :style="resizeStyle"
          class="d-flex position-absolute z-index-two"></div>
        <v-btn
          v-if="hover && !resize && !moving && !$root.drawerOpened"
          ref="resizeButton"
          :title="$t('layout.resizeCell')"
          :width="iconSize"
          :height="iconSize"
          :class="{
            'l-0': $vuetify.rtl,
            'r-0': !$vuetify.rtl,
            'fa-rotate-90': !$vuetify.rtl
          }"
          class="position-absolute z-index-two b-0 mb-n3 me-n3"
          icon
          @mousedown.prevent.stop="resizeStart">
          <v-icon :size="iconSize" class="icon-default-color">fa-expand-alt</v-icon>
        </v-btn>
        <v-fade-transition>
          <div
            v-show="hover && !resize && !moving && !$root.drawerOpened"
            class="layout-no-multi-select absolute-horizontal-center z-index-drawer mt-n4">
            <v-chip color="white" class="elevation-2">
              <v-btn
                :title="$t('layout.moveCell')"
                :width="iconSize"
                :height="iconSize"
                class="draggable ms-2"
                icon
                @mousedown.prevent.stop="moveStart">
                <v-icon :size="iconSize" class="icon-default-color">fa-arrows-alt</v-icon>
              </v-btn>
              <v-btn
                :title="$t('layout.editApplication')"
                :width="iconSize"
                :height="iconSize"
                class="mx-4"
                icon
                @click.prevent.stop="editApplication">
                <v-icon :size="iconSize" class="icon-default-color">fa-edit</v-icon>
              </v-btn>
              <v-btn
                :title="$t('layout.deleteApplication')"
                :width="iconSize"
                :height="iconSize"
                class="me-2"
                icon
                @click.prevent.stop="deleteApplication">
                <v-icon :size="iconSize" class="icon-default-color">fa-trash</v-icon>
              </v-btn>
            </v-chip>
          </div>
        </v-fade-transition>
      </template>
      <v-hover v-else>
        <v-card
          slot-scope="hoverScope"
          :class="{
            'transparent': hoverScope.hover && !isSelectedCell,
            'grey': isSelectedCell,
            'grey-background': !hoverScope.hover && !isSelectedCell,
          }"
          class="full-width full-height"
          flat
          v-on="!$root.multiCellsSelect && {
            click: () => $root.$emit('layout-cell-add-application', parentId, container)
          }" />
      </v-hover>
    </template>
  </layout-editor-container-base>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    parentId: {
      type: String,
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
    cellHeight: {
      type: String,
      default: null,
    },
    cellWidth: {
      type: String,
      default: null,
    },
    rowsCount: {
      type: Number,
      default: null,
    },
    colsCount: {
      type: Number,
      default: null,
    },
  },
  data: () => ({
    hover: false,
    resize: false,
    resizeX: 0,
    resizeY: 0,
    resizeHeight: 0,
    resizeWidth: 0,
    originalX: 0,
    originalY: 0,
    originalHeight: 0,
    originalWidth: 0,
    cellCols: 0,
    cellRows: 0,
    targetCellComputing: 0,
    targetCellHeight: 0,
    targetCellWidth: 0,
    resizeInterval: null,
    dimensions: null,
  }),
  computed: {
    children() {
      return this.container.children;
    },
    childrenSize() {
      return this.children?.length || 0;
    },
    hasApplication() {
      return this.childrenSize > 0;
    },
    storageId() {
      return this.container?.storageId;
    },
    rowIndex() {
      return this.container?.rowIndex;
    },
    colIndex() {
      return this.container?.colIndex;
    },
    iconSize() {
      return 24;
    },
    heightGap() {
      return this.container?.gap?.v || 0;
    },
    widthGap() {
      return this.container?.gap?.h || 0;
    },
    resizeStyle() {
      return {
        'background-color': this.container.color,
        'box-sizing': this.resize && 'content-box' || 'border-box',
        'border': this.resize && '2px solid var(--allPagesPrimaryColor)' || 'none',
        'height': this.resize && `${this.resizeHeight - 4}px` || '100%',
        'width': this.resize && `${this.resizeWidth - 4}px` || '100%',
        'user-select': 'none',
      };
    },
    resizeMouseX() {
      return this.$root.resizeMouseX;
    },
    resizeMouseY() {
      return this.$root.resizeMouseY;
    },
    multiCellsSelect() {
      return this.$root.multiCellsSelect;
    },
    dimensionsX0() {
      return this.dimensions?.x || 0;
    },
    dimensionsX1() {
      return this.dimensionsX0 + this.dimensions?.width || 0;
    },
    dimensionsY0() {
      return this.dimensions?.y || 0;
    },
    dimensionsY1() {
      return this.dimensionsY0 + this.dimensions.height || 0;
    },
    isInMultiSelection() {
      return this.multiCellsSelect
        && this.dimensions
        && ((
          (this.dimensionsX0 > this.$root.selectMouseX0 && this.dimensionsX0 < this.$root.selectMouseX1)
           || (this.dimensionsX1 > this.$root.selectMouseX0 && this.dimensionsX1 < this.$root.selectMouseX1)
        ) && (
          (this.dimensionsY0 > this.$root.selectMouseY0 && this.dimensionsY0 < this.$root.selectMouseY1)
           || (this.dimensionsY1 > this.$root.selectMouseY0 && this.dimensionsY1 < this.$root.selectMouseY1)
        ) || (
          (this.dimensionsX0 < this.$root.selectMouseX0 && this.dimensionsX1 > this.$root.selectMouseX1)
           && (this.dimensionsY0 > this.$root.selectMouseY0 && this.dimensionsY0 < this.$root.selectMouseY1)
        ) || (
          (this.dimensionsY0 < this.$root.selectMouseY0 && this.dimensionsY1 > this.$root.selectMouseY1)
           && (this.dimensionsX0 > this.$root.selectMouseX0 && this.dimensionsX0 < this.$root.selectMouseX1)
        ) || (
          (this.dimensionsY0 < this.$root.selectMouseY0 && this.dimensionsY1 > this.$root.selectMouseY0)
           && (this.dimensionsX0 < this.$root.selectMouseX0 && this.dimensionsX1 > this.$root.selectMouseX0)
        ));
    },
    moving() {
      return this.$root.movingCell
        && this.parentId === this.$root.movingParentId
        && this.rowIndex === this.$root.movingCellRowIndex
        && this.colIndex === this.$root.movingCellColIndex;
    },
    isTargetOfMoving() {
      return this.$root.movingCell
        && this.parentId === this.$root.movingParentId
        && this.$root.mouseCellRowIndex >= 0
        && this.$root.mouseCellColIndex >= 0
        && this.rowIndex >= this.$root.mouseCellRowIndex
        && this.rowIndex < (this.$root.mouseCellRowIndex + this.$root.movingCell.rowsCount)
        && this.colIndex >= this.$root.mouseCellColIndex
        && this.colIndex < (this.$root.mouseCellColIndex + this.$root.movingCell.colsCount)
        || false;
    },
    colSpan() {
      return this.container.colsCount;
    },
    rowSpan() {
      return this.container.rowsCount;
    },
    minWidth() {
      return (this.cellWidth * this.colSpan);
    },
    minHeight() {
      return (this.cellWidth * this.rowSpan);
    },
    selectedCells() {
      return this.$root.selectedCells;
    },
    isSelectedCell() {
      if (this.$root.isMovingCell) {
        return this.isTargetOfMoving;
      } else {
        return !!this.selectedCells.find(c => c.storageId === this.container.storageId);
      }
    },
    mouseCellRowIndex() {
      return this.$root.mouseCellRowIndex;
    },
    mouseCellColIndex() {
      return this.$root.mouseCellColIndex;
    },
  },
  watch: {
    resize() {
      if (this.resize) {
        this.$root.$emit('layout-cell-resize-start', this.parentId, this.container);
      } else {
        this.$root.$emit('layout-cell-resize-end', this.parentId, this.container);
      }
      window.setTimeout(() => this.refreshTargetCellDimensions(), 300);
    },
    resizeX() {
      this.resizeWidth = this.originalWidth + this.resizeX - this.originalX;
    },
    resizeY() {
      this.resizeHeight = this.originalHeight + this.resizeY - this.originalY;
    },
    mouseCellRowIndex(newVal, oldVal) {
      if (this.resize && newVal !== oldVal) {
        this.refreshTargetCellDimensions();
      }
    },
    mouseCellColIndex(newVal, oldVal) {
      if (this.resize && newVal !== oldVal) {
        this.refreshTargetCellDimensions();
      }
    },
    multiCellsSelect(val) {
      if (val) {
        this.dimensions = this.$refs.container.$el.getBoundingClientRect();
      }
    },
    isInMultiSelection() {
      if (this.multiCellsSelect) {
        if (this.isInMultiSelection) {
          if (this.$root.selectedCells?.length === 0) {
            this.$root.selectedSectionId = this.parentId;
          }
          if (this.$root.selectedSectionId === this.parentId) {
            this.$root.selectedCells.push(this.container);
          }
        } else if (this.$root.selectedSectionId === this.parentId) {
          const index = this.$root.selectedCells?.indexOf(this.container);
          if (index === 0 || index) {
            this.$root.selectedCells.splice(index, 1);
          }
        }
      }
    },
  },
  methods: {
    resizeStart(event) {
      if (!this.resize) {
        this.$root.$emit('layout-section-history-add', this.parentId);
        this.originalX = event.x;
        this.originalY = event.y;
        this.originalHeight = this.$refs.container.$el.getBoundingClientRect().height;
        this.originalWidth = this.$refs.container.$el.getBoundingClientRect().width;
        this.resizeX = this.originalX;
        this.resizeY = this.originalY;
        this.resizeHeight = this.originalHeight;
        this.resizeWidth = this.originalWidth;
        this.cellRows = this.container.colsCount;
        this.cellCols = this.container.rowsCount;
        this.resize = true;

        document.addEventListener('mousemove', this.resizeMove);
        document.addEventListener('mouseup', this.resizeEnd);
      }
    },
    resizeEnd() {
      if (this.resize) {
        this.resize = false;
        this.resizeX = 0;
        this.resizeY = 0;

        document.removeEventListener('mousemove', this.resizeMove);
        document.removeEventListener('mouseup', this.resizeEnd);
      }
    },
    resizeMove(event) {
      if (this.resize) {
        this.resizeX = event.x;
        this.resizeY = event.y;
      }
    },
    moveStart(event) {
      this.$root.$emit('layout-section-history-add', this.parentId);
      this.$root.mouseCellRowIndex = this.container.rowIndex;
      this.$root.mouseCellColIndex = this.container.colIndex;
      this.$root.movingCellRowIndex = this.container.rowIndex;
      this.$root.movingCellColIndex = this.container.colIndex;
      this.$root.movingCellRowSpan = this.container.rowsCount;
      this.$root.movingCellColSpan = this.container.colsCount;
      this.$root.movingParentId = this.parentId;
      this.$nextTick().then(() => {
        this.$root.$emit('layout-cell-moving-start', {
          target: event.target,
          x: event.x,
          y: event.y,
          sectionId: this.parentId,
          cell: this.container,
          containerElement: this.$refs.container.$el,
        });
        document.addEventListener('mouseup', this.moveEnd);
      });
    },
    moveEnd(event) {
      if (this.$root.movingCell) {
        this.$root.$emit('layout-cell-moving-end', {
          sectionId: this.parentId,
          cell: this.container,
          rowIndex: this.$root.mouseCellRowIndex,
          colIndex: this.$root.mouseCellColIndex,
          target: event.target,
        });
        this.$root.movingCellRowIndex = -1;
        this.$root.movingCellColIndex = -1;
        document.removeEventListener('mouseup', this.moveEnd);
      }
    },
    refreshTargetCellDimensions() {
      if (this.resize
          && this.$root.mouseCellRowIndex > -1
          && this.$root.mouseCellColIndex > -1) {
        window.setTimeout(() => {
          this.targetCellHeight = (this.$root.mouseCellRowIndex - this.rowIndex + 1) * this.cellHeight;
          this.targetCellWidth = (this.$root.mouseCellColIndex - this.colIndex + 1) * this.cellWidth + 8;
        }, 50);
      } else {
        this.targetCellHeight = 0;
        this.targetCellWidth = 0;
      }
    },
    editApplication() {
      this.$root.$emit('layout-edit-application', this.parentId, this.container);
    },
    deleteApplication() {
      this.$root.$emit('layout-delete-application', this.parentId, this.container);
    },
  },
};
</script>
