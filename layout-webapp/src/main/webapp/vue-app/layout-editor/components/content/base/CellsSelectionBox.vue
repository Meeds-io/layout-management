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
    v-show="multiCellsSelect"
    :style="boxStyle"
    class="layout-selecting-container grey opacity-5 elevation-2 position-absolute z-index-modal">
  </div>
</template>
<script>
export default {
  data: () => ({
    interceptEvents: false,
    parentAppDimensions: null,
    computingDisplayInterval: null,
  }),
  computed: {
    multiCellsSelect() {
      return this.$root.multiCellsSelect;
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
    boxHeight() {
      return this.movingY - this.movingStartY;
    },
    boxWidth() {
      return this.movingX - this.movingStartX;
    },
    boxStyle() {
      const boxStyle = {};
      if (this.boxHeight > 0) {
        boxStyle.top = `${this.movingStartY}px`;
      } else {
        boxStyle.top = `${this.movingStartY + this.boxHeight}px`;
      }
      if (this.boxWidth > 0) {
        boxStyle.left = `${this.movingStartX}px`;
      } else {
        boxStyle.left = `${this.movingStartX + this.boxWidth}px`;
      }
      boxStyle.height = `${Math.abs(this.boxHeight)}px`;
      boxStyle.width = `${Math.abs(this.boxWidth)}px`;
      return boxStyle;
    },
  },
  watch: {
    multiCellsSelect() {
      if (this.multiCellsSelect) {
        this.$root.$emit('layout-cells-selection-start');
      }
    },
    interceptEvents() {
      if (this.interceptEvents) {
        document.addEventListener('mousemove', this.updateSelection);
        document.addEventListener('mouseup', this.endSelection);
        document.querySelector('.page-scroll-content').addEventListener('scroll', this.updateScrollPosition);
      } else {
        document.removeEventListener('mousemove', this.updateSelection);
        document.removeEventListener('mouseup', this.endSelection);
        document.querySelector('.page-scroll-content').removeEventListener('scroll', this.updateScrollPosition);
        this.reset();
      }
    },
    movingX() {
      this.updateDisplay();
    },
    movingY() {
      this.updateDisplay();
    },
    parentAppX(newVal, oldVal) {
      if (this.interceptEvents) {
        this.$root.movingX = this.$root.movingX - newVal + oldVal;
      }
    },
    parentAppY(newVal, oldVal) {
      if (this.interceptEvents) {
        this.$root.movingY = this.$root.movingY - newVal + oldVal;
      }
    },
  },
  created() {
    document.addEventListener('mousedown', this.startSelection);
  },
  methods: {
    reset() {
      this.$root.movingX = 0;
      this.$root.movingY = 0;
      this.$root.movingStartX = 0;
      this.$root.movingStartY = 0;
      this.startScrollX = 0;
      this.startScrollY = 0;
      this.$root.diffScrollX = 0;
      this.$root.diffScrollY = 0;
      this.$root.parentAppDimensions = null;
    },
    updateDisplay() {
      if (this.interceptEvents) {
        if (!this.computingDisplayInterval && !this.$root.multiCellsSelect) {
          this.computingDisplayInterval = window.setTimeout(() => {
            this.$root.multiCellsSelect =
              Math.abs(this.movingX - this.movingStartX) > 10
              || Math.abs(this.movingY - this.movingStartY) > 10;
            this.computingDisplayInterval = null;
          }, 50);
        }
      } else {
        this.$root.multiCellsSelect = false;
      }
    },
    updateScrollPosition() {
      if (this.interceptEvents) {
        this.$root.updateParentAppDimensions();
        this.$nextTick(() => {
          this.$root.diffScrollX = this.$root.parentAppDimensions.x - this.startScrollX;
          this.$root.diffScrollY = this.$root.parentAppDimensions.y - this.startScrollY;
          this.updateSelection();
        });
      }
    },
    startSelection(event) {
      if (event.button !== 0) {
        return;
      }
      if (event?.target?.closest?.('#layoutEditor')
          && !event?.target?.closest?.('.layout-no-multi-select')
          && event?.target?.tagName !== 'BUTTON'
          && event?.target?.tagName !== 'A') {
        this.$root.updateParentAppDimensions();
        this.$root.movingX = this.$layoutUtils.getX(event) - this.$root.parentAppDimensions.x;
        this.$root.movingY = this.$layoutUtils.getY(event) - this.$root.parentAppDimensions.y;
        this.$root.movingStartX = this.$root.movingX;
        this.$root.movingStartY = this.$root.movingY;
        this.startScrollX = this.$root.parentAppDimensions.x;
        this.startScrollY = this.$root.parentAppDimensions.y;
        this.$root.diffScrollX = 0;
        this.$root.diffScrollY = 0;
        this.interceptEvents = false;
        this.$nextTick().then(() => this.interceptEvents = true);
      }
    },
    updateSelection(event) {
      if (this.interceptEvents && event) {
        this.$root.movingX = this.$layoutUtils.getX(event) - this.parentAppX;
        this.$root.movingY = this.$layoutUtils.getY(event) - this.parentAppY;
      }
    },
    endSelection() {
      if (this.interceptEvents) {
        if (this.multiCellsSelect) {
          this.$root.$emit('layout-cells-selection-end');
        }
        this.interceptEvents = false;
      }
    },
  },
};
</script>