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
  <layout-editor-container-container-base
    ref="container"
    :container="container"
    :index="index"
    :length="length"
    :context="context"
    :cell-height="cellHeight"
    :cell-width="cellWidth"
    :style="cellStyle"
    class="px-3"
    @hovered="hover = $event">
    <template #content>
      <div
        v-if="isResizeHover"
        class="position-absolute full-width full-height test-dimensions">
        <div class="position-relative full-width full-height">
          <div class="position-absolute t-0 l-0 mt-n6 display-1">
            <span class="green--text">{{ parseInt(dimensionsX0) }}</span>,
            <span class="red--text">{{ parseInt(dimensionsY0) }}</span>
          </div>
        </div>
      </div>
      <div
        v-if="resize"
        :style="resizeCellStyle"
        class="position-absolute secondary-border-color"></div>
      <div
        v-if="noChildren"
        ref="resizeContent"
        :style="resizeStyle"
        :class="resizeClass">
        <div
          v-if="!context && length > 1"
          class="position-relative d-flex align-center justify-center full-width full-height">
          <v-fade-transition>
            <div v-show="hover || resize">
              <v-btn
                :title="$t('layout.addApplication')"
                :width="iconSize"
                :height="iconSize"
                class="me-3"
                icon
                @click="$emit('add-application')">
                <v-icon :size="iconSize" class="icon-default-color">fa-plus</v-icon>
              </v-btn>
              <v-btn
                :title="$t('layout.moveCell')"
                :width="iconSize"
                :height="iconSize"
                class="draggable ms-3"
                icon
                @click="$emit('move-start')">
                <v-icon :size="iconSize" class="icon-default-color">fa-arrows-alt</v-icon>
              </v-btn>
              <v-btn
                ref="resizeButton"
                :title="$t('layout.resizeCell')"
                :width="iconSize"
                :height="iconSize"
                :class="{
                  'l-0': $vuetify.rtl,
                  'r-0': !$vuetify.rtl,
                  'fa-rotate-90': !$vuetify.rtl
                }"
                class="position-absolute b-0"
                icon
                @mousedown="resizeStart">
                <v-icon :size="iconSize" class="icon-default-color">fa-expand-alt</v-icon>
              </v-btn>
              <div
                v-if="resize && resizeMouseX"
                class="position-absolute z-index-two resize-dimensions r-0 b-0 mb-10 mr-10">
                <div class="position-relative full-width full-height">
                  <div class="position-absolute b-0 r-0 mr-10 display-1">
                    <span class="green--text">{{ parseInt(resizeMouseX) }}</span>,
                    <span class="red--text">{{ parseInt(resizeMouseY) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </v-fade-transition>
        </div>
      </div>
    </template>
  </layout-editor-container-container-base>
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
    targetCellRowIndex: 0,
    targetCellColIndex: 0,
    targetCellHeight: 0,
    targetCellWidth: 0,
    resizeInterval: null,
    dimensions: null,
    sectionDimensions: null,
  }),
  computed: {
    children() {
      return this.container.children;
    },
    childrenSize() {
      return this.children.length;
    },
    noChildren() {
      return !this.childrenSize;
    },
    storageId() {
      return this.container?.storageId;
    },
    iconSize() {
      return 24;
    },
    resizeCellStyle() {
      return {
        'height': this.resize && `${this.targetCellHeight}px` || '100%',
        'width': this.resize && `${this.targetCellWidth}px` || '100%',
        'z-index': this.resize && '1050' || '0',
      };
    },
    resizeStyle() {
      return {
        'background-color': this.container.color,
        'box-sizing': this.resize && 'content-box' || 'border-box',
        'border': this.resize && '2px solid var(--allPagesPrimaryColor)' || 'none',
        'opacity': this.resize && '0.7' || '1',
        'min-height': `${this.cellHeight - 20}px`,
        'min-width': `${this.cellWidth - 24}px`,
        'height': this.resize && `${this.resizeHeight - 4}px` || '100%',
        'width': this.resize && `${this.resizeWidth - 4}px` || '100%',
        'z-index': this.resize && '1000' || '0',
        'user-select': 'none',
      };
    },
    resizeClass() {
      return {
        'position-absolute': this.resize,
        'position-relative': !this.resize,
      };
    },
    resizeMouseX() {
      return this.$root.resizeMouseX;
    },
    resizeMouseY() {
      return this.$root.resizeMouseY;
    },
    dimensionsX0() {
      return this.dimensions.x;
    },
    dimensionsX1() {
      return this.dimensions.x + this.dimensions.width;
    },
    dimensionsY0() {
      return this.dimensions.y;
    },
    dimensionsY1() {
      return this.dimensions.y + this.dimensions.height;
    },
    colSpan() {
      return this.container.colsCount;
    },
    rowSpan() {
      return this.container.rowsCount;
    },
    minHeight() {
      return (this.cellHeight * this.rowSpan) - 20;
    },
    cellStyle() {
      return {
        'min-height': `${this.minHeight - 20}px`,
        'min-width': `${this.cellWidth - 24}px`,
      };
    },
  },
  watch: {
    resize() {
      if (this.resize) {
        this.$root.$emit('layout-cell-resize-start', this.parentId, this.container);
      } else {
        this.$root.$emit('layout-cell-resize-end', this.parentId, this.container);
      }
    },
    resizeX() {
      this.resizeWidth = this.originalWidth + this.resizeX - this.originalX;
    },
    resizeY() {
      this.resizeHeight = this.originalHeight + this.resizeY - this.originalY;
    },
  },
  methods: {
    resizeStart(event) {
      if (!this.resize) {
        this.originalX = event.x;
        this.originalY = event.y;
        this.originalHeight = this.$refs.resizeContent.getBoundingClientRect().height;
        this.originalWidth = this.$refs.resizeContent.getBoundingClientRect().width;
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
  },
};
</script>
