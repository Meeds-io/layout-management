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
  <v-hover v-model="hover">
    <div
      ref="content"
      :id="id"
      :class="`${cssClass}${isDynamicSection && (hover || !hasContent) && ' position-relative' || ''}${displayNoContent && ' z-index-one' || ''}`"
      :style="cssStyle"
      :data-storage-id="storageId"
      class="layout-application">
      <v-hover v-model="hoverMenu">
        <layout-editor-application-menu
          ref="menu"
          :container="container"
          :section="section"
          :parent-id="parentId"
          :application-title="applicationTitle"
          :application-category-title="applicationCategoryTitle"
          @move-start="moveStart"
          @move-end="moveEnd" />
      </v-hover>
      <div v-if="displayNoContent" class="full-width text-no-wrap border-color-grey-lighten ms-1">
        <div class="absolute-vertical-center d-flex ms-n1">
          <div class="light-black-background white--text px-2">
            {{ applicationTitle }}
          </div>
          <v-icon size="35" class="layout-no-content-caret icon-default-color my-n2">{{ $vuetify.rtl && 'fa-caret-left' || 'fa-caret-right' }}</v-icon>
        </div>
      </div>
    </div>
  </v-hover>
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
    applicationInstalled: false,
    section: null,
    height: null,
    width: null,
    borderColor: null,
    cssClass: null,
    hover: false,
    hoverMenu: false,
    hoverGridCell: false,
    hasContentCheckCount: 0,
    hasContent: true,
  }),
  computed: {
    storageId() {
      return this.container?.storageId;
    },
    hoveredParentId() {
      return this.$root.hoveredParentId;
    },
    applicationId() {
      return this.container?.children?.[0]?.storageId || this.container?.storageId;
    },
    nodeId() {
      return this.$root.draftNodeId;
    },
    nodeUri() {
      return this.$root.draftNodeUri;
    },
    id() {
      return `UIPortlet-${this.container?.id || this.storageId}`;
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
    isDynamicSection() {
      return this.section?.template === this.$layoutUtils.flexTemplate;
    },
    applicationTitle() {
      return this.container?.title || '';
    },
    applicationCategory() {
      return this.applicationTitle && this.$root.applicationCategories?.find?.(c => c?.applications?.find?.(a => a?.displayName === this.applicationTitle));
    },
    applicationCategoryTitle() {
      return this.applicationCategory?.displayName || '';
    },
    hoverApp() {
      return this.hoverMenu || this.hover || this.hoverGridCell;
    },
    displayNoContent() {
      return this.isDynamicSection && !this.hasContent && this.$root.desktopDisplayMode;
    },
  },
  watch: {
    applicationInstalled() {
      this.$emit('initialized');
      this.computeHasContent();
    },
    storageId(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.installApplication();
      }
    },
    nodeUri(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.installApplication();
      }
    },
    hoveredParentId() {
      if (!this.isDynamicSection) {
        this.hoverGridCell = this.hoveredParentId === this.parentId;
      }
    },
    hoverApp() {
      if (this.hoverApp) {
        this.$refs.menu.displayMenu();
      } else {
        this.$refs.menu.hideMenu();
      }
    },
  },
  created() {
    this.$root.$on('layout-section-application-update-style', this.updateStyle);
    this.$root.$on('layout-editor-portlet-properties-updated', this.updateApplication);
    this.initStyle();
    this.section = this.$layoutUtils.getSectionByContainer(this.$root.layout, this.parentId);
  },
  mounted() {
    this.installApplication();
  },
  updated() {
    this.installApplication();
  },
  beforeDestroy() {
    this.$root.$off('layout-section-application-update-style', this.updateStyle);
    this.$root.$off('layout-editor-portlet-properties-updated', this.updateApplication);
  },
  methods: {
    updateApplication(applicationId) {
      if (applicationId !== this.applicationId) {
        return;
      }
      this.applicationInstalled = false;
      this.installApplication();
    },
    installApplication() {
      if (!this.applicationInstalled
          && this.$refs.content
          && this.nodeUri
          && this.storageId) {
        this.$applicationUtils.installApplication(this.nodeUri, this.storageId, this.$refs.content)
          .then(() => this.applicationInstalled = true);
      }
    },
    updateStyle(container) {
      if (this.container.storageId === container.storageId) {
        this.initStyle();
      }
    },
    initStyle() {
      this.height = this.container.height;
      this.width = this.container.width;
      this.borderColor = this.container.borderColor;
      this.cssClass = this.container.cssClass || '';
    },
    moveEnd() {
      this.$emit('move-end');
    },
    moveStart(event, moveType) {
      this.$emit('move-start', event, moveType, this.container);
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
    computeHasContent() {
      if (!this.isDynamicSection) {
        return;
      }
      this.hasContentCheckCount = 0;
      this.hasContent = true;
      this.computeHasContentAsync();
    },
    computeHasContentAsync() {
      if (this.hasContentCheckCount > 10) {
        this.hasContent = false;
        return;
      }
      this.hasContentCheckCount++;
      window.setTimeout(() => {
        if (this.$refs.content) {
          this.hasContent = this.$refs.content.getBoundingClientRect().height > 10;
          if (!this.hasContent) {
            this.computeHasContentAsync();
          }
        }
      }, 200);
    },
  },
};
</script>