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
  <!-- eslint-disable-next-line vuejs-accessibility/no-static-element-interactions -->
  <div
    v-if="childrenSize"
    ref="section"
    :id="id"
    :data-storage-id="storageId"
    :style="cssStyle"
    class="position-relative flex-grow-1 flex-shrink-1">
    <v-hover :disabled="$root.mobileDisplayMode">
      <div
        slot-scope="{ hover }"
        class="full-width">
        <site-layout-editor-banner-section-menu
          :container="container"
          :hover="!drawerOpened && (hover || hoverSection || movingSection)"
          :index="index"
          :length="length"
          :moving="movingSection"
          @move-start="movingSection = true"
          @move-end="movingSection = false" />
      </div>
    </v-hover>
    <layout-editor-container-base
      ref="container"
      :container="container"
      :parent-id="parentId"
      :index="index"
      class="position-relative overflow-initial layout-section-content full-height d-flex flex-row me-n5"
      type="section"
      no-background-style
      @hovered="hoverSection = $event && !drawerOpened" />
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
    hoverSection: false,
    movingSection: false,
    sectionWidth: 0,
  }),
  computed: {
    storageId() {
      return this.container?.storageId;
    },
    zIndexClass() {
      return !this.drawerOpened && 'z-index-one';
    },
    drawerOpened() {
      return this.$root.drawerOpened;
    },
    cssStyle() {
      return {
        ...this.$applicationUtils.getStyle(this.container, {
          onlyBackgroundStyle: true,
          sectionStyle: true,
        }),
        'height': `${this.container?.height || 57}px`,
        'max-height': `${this.container?.height || 57}px`,
      };
    },
    childrenSize() {
      return this.container?.children?.length;
    },
  },
};
</script>