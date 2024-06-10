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
  data: () => ({
    applicationContent: null,
    contentRetrieved: false,
  }),
  computed: {
    nodeUri() {
      return this.$root.nodeUri;
    },
    storageId() {
      return this.container?.storageId;
    },
    portletId() {
      return this.storageId || this.container?.id;
    },
    contentId() {
      return this.container?.contentId;
    },
    id() {
      return `UIPortlet-${this.container?.id || this.storageId || `${this.parentId}-${parseInt(Math.random() * 10000)}`}`;
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
    borderSize() {
      return this.container.borderSize;
    },
    boxShadow() {
      return this.container.boxShadow;
    },
    backgroundColor() {
      return this.container.backgroundColor;
    },
    cssStyle() {
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
      if (this.borderSize) {
        style['--appBorderSize'] = `${this.borderSize}px`;
      }
      if (this.boxShadow === 'true') {
        style['--appBoxShadow'] = '0px 3px 3px -2px rgba(0, 0, 0, 0.2), 0px 3px 4px 0px rgba(0, 0, 0, 0.14), 0px 1px 8px 0px rgba(0, 0, 0, 0.12)';
      }
      if (this.backgroundColor) {
        style['background-color'] = this.backgroundColor;
      }
      return style;
    },
    cssClass() {
      return this.container.cssClass || '';
    },
  },
  watch: {
    portletId() {
      this.retrieveData();
    },
    nodeUri() {
      this.retrieveData();
    },
    applicationContent() {
      if (this.applicationContent) {
        this.installApplication();
      }
    },
  },
  created() {
    this.retrieveData();
  },
  mounted() {
    this.installApplication();
  },
  methods: {
    installApplication() {
      if (this.$refs.content && this.nodeUri) {
        if (!this.portletId) {
          console.warn(`Application '${this.contentId}' doesn't have a storageId neither and id`); // eslint-disable-line no-console
        } else if (this.applicationContent) {
          this.$applicationUtils.handleApplicationContent(this.applicationContent, this.$refs.content);
          this.applicationContent = null;
        }
      }
    },
    retrieveData() {
      if (this.portletId && this.nodeUri && !this.contentRetrieved) {
        this.contentRetrieved = true;
        this.$applicationUtils.getApplicationContent(this.nodeUri, this.portletId)
          .then(applicationContent => this.applicationContent = applicationContent);
      }
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
  }
};
</script>