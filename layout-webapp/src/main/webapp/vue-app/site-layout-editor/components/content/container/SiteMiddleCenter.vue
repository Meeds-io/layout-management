<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io

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
    :style="cssStyle"
    class="d-flex flex-row mx-auto z-index-one site-middle-center-container"
    type="site-middle-center-container"
    no-application-style
    no-background-style />
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
  },
  data: () => ({
    height: 225,
  }),
  computed: {
    id() {
      return this.container.id || this.storageId;
    },
    storageId() {
      return this.container?.storageId;
    },
    middleContainerMinHeight() {
      return this.$root.middleContainer?.children?.map?.(c => c.height && Number(c.height) || 57)?.reduce?.((acc, v) => acc + v, 0) || 0;
    },
    sidebarsContainerMinWidth() {
      return this.$root.layout?.children
        ?.filter?.(c => c.template === this.$layoutUtils.sidebarTemplate
          && c.children?.length
          && (
            !this.$root.mobileDisplayMode
            || !c?.children?.[0].cssClass?.includes?.('hidden-sm-and-down')
          )
        )
        ?.map?.(c => c.width
          && Number(c.width)
          || 310
        )
        ?.reduce?.((acc, v) => acc + v, 0) || 0;
    },
    minHeight() {
      return `calc(100vh - ${this.middleContainerMinHeight}px`;
    },
    width() {
      return this.container.width || 1320;
    },
    maxWidth() {
      return `min(100vw - ${this.sidebarsContainerMinWidth}px, ${this.width}px)`;
    },
    cssStyle() {
      return {
        'height': `${this.height}px`,
        'min-height': this.minHeight,
        'width': `${this.width}px`,
        'max-width': this.maxWidth,
      };
    },
  },
};
</script>