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
    :id="id"
    :class="cssClass"
    :style="cssStyle">
    <slot name="header"></slot>
    <slot v-if="$slots.content" name="content"></slot>
    <template v-else-if="hasChildren">
      <page-layout-container-extension
        v-for="child in children"
        :key="child.storageId"
        :container="child"
        :parent-id="storageId" />
    </template>
    <slot name="footer"></slot>
  </div>
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
    noBackgrounStyle: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    id() {
      return this.container?.id || `Container${this.storageId}`;
    },
    storageId() {
      return this.container?.storageId;
    },
    children() {
      return this.container.children;
    },
    hasChildren() {
      return !!this.children?.length;
    },
    height() {
      return this.container.height;
    },
    width() {
      return this.container.width;
    },
    borderColor() {
      return this.container.borderColor;
    },
    cssStyle() {
      if (!this.height && !this.width && !this.borderColor) {
        return null;
      } else {
        const style = {};
        if (this.height) {
          style['--appHeight'] = this.hasUnit(this.height) ? this.height : `${this.height}px`;
        }
        if (this.width === 'fullWindow') {
          style['--allPagesSinglePageApplicationWidth'] = 'calc(100% - 40px)';
        } else if (this.width) {
          style['--appWidth'] = this.hasUnit(this.width) ? this.width : `${this.width}px`;
        }
        if (this.borderColor) {
          style['--appBorderColor'] = this.borderColor;
        }
        if (this.container.borderSize) {
          style['--appBorderSize'] = `${this.container.borderSize}px`;
        }
        if (this.container.boxShadow === 'true') {
          style['--appBoxShadow'] = '0px 3px 3px -2px rgba(0, 0, 0, 0.2), 0px 3px 4px 0px rgba(0, 0, 0, 0.14), 0px 1px 8px 0px rgba(0, 0, 0, 0.12)';
        }
        if (!this.noBackgrounStyle && (this.container.backgroundImage || this.container.backgroundColor)) {
          if (this.container.backgroundColor) {
            style['background-color'] = this.container.backgroundColor;
          }
          if (this.container.backgroundEffect) {
            style['background-image'] = `${this.container.backgroundEffect},url(${this.container.backgroundImage})`;
          } else {
            style['background-image'] = `url(${this.container.backgroundImage})`;
          }
          if (this.container.backgroundRepeat) {
            style['background-repeat'] = this.container.backgroundRepeat;
          }
          if (this.container.backgroundSize) {
            style['background-size'] = this.container.backgroundSize;
          }
        }
        return style;
      }
    },
    cssClass() {
      return this.container.cssClass || '';
    },
  },
  methods: {
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
  },
};
</script>
