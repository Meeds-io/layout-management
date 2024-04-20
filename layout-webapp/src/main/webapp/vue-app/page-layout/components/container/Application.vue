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
    ref="content"
    :id="id"
    :class="cssClass"
    :style="cssStyle"
    class="layout-application"></div>
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
  },
  computed: {
    nodeUri() {
      return this.$root.nodeUri;
    },
    storageId() {
      return this.container?.storageId;
    },
    id() {
      return `UIPortlet-${this.container?.id || this.storageId}`;
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
          style['--appHeightScroll'] = 'auto';
        }
        if (this.width) {
          style['--appWidth'] = this.hasUnit(this.width) ? this.width : `${this.width}px`;
          style['--appWidthScroll'] = 'auto';
        }
        if (this.borderColor) {
          style['--appBorderColor'] = this.borderColor;
        }
        return style;
      }
    },
    cssClass() {
      return this.container.cssClass || '';
    },
  },
  mounted() {
    this.installApplication();
  },
  methods: {
    installApplication() {
      if (this.$refs.content
          && this.nodeUri
          && this.storageId) {
        this.$applicationUtils.installApplication(this.nodeUri, this.storageId, this.$refs.content);
      }
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
  }
};
</script>