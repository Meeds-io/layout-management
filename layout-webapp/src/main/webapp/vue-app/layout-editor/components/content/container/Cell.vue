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
    :parent-id="parentId"
    :draggable="isDynamicSection"
    :hide-children="moving"
    :class="{
      'z-index-two': hover && !$root.drawerOpened,
      'pb-12': movingChildren,
    }"
    :style="cssStyle"
    class="position-relative d-flex flex-column"
    @hovered="hover = $event"
    @initialized="computeHasContent"
    @move-start="moveStart">
    <template #footer>
      <div
        v-if="$root.desktopDisplayMode && !isDynamicSection && hasApplication && !moving"
        ref="placeholder"
        :class="{
          'linear-gradient-grey-background': hasContent,
          'white': !hasContent,
        }"
        class="position-relative flex-grow-1 flex-shrink-1 overflow-hidden">
        <div
          v-if="!hasContent"
          class="absolute-all-center d-flex flex-column align-center justify-center">
          <v-icon size="40" class="icon-default-color mb-5">far fa-hourglass</v-icon>
          <span class="font-weight-bold">{{ $t('layout.inSearchOfData') }}</span>
          <span v-if="applicationTitle" class="caption">{{ applicationTitle }}</span>
          <span v-if="applicationCategoryTitle" class="caption">({{ applicationCategoryTitle }})</span>
        </div>
      </div>
      <div
        v-if="$root.desktopDisplayMode"
        :class="{
          'mt-n5': isDynamicSection,
        }">
        <layout-editor-cell-resize-button
          v-if="displayResizeButton"
          :container="container"
          :parent-id="parentId"
          :dynamic-section="isDynamicSection"
          :hover="hover"
          :moving="moving"
          @move-start="moveStart" />
      </div>
      <v-hover v-if="$root.desktopDisplayMode && (!hasApplication || isDynamicSection)">
        <v-card
          slot-scope="hoverScope"
          v-show="!movingChildren"
          :class="{
            'full-height': !hasApplication,
            'mt-n5': hasApplication,
            'grey-background': !isSelectedCell && !hoverScope.hover && (!$root.movingCell || $root.selectedSectionId !== parentId),
            'light-grey-background': !isSelectedCell && hoverScope.hover && (!$root.movingCell || $root.selectedSectionId !== parentId),
            'transparent': $root.movingCell && $root.selectedSectionId === parentId && !isSelectedCell,
            'crosshair-cursor': !isDynamicSection,
            'grey': isSelectedCell,
            'invisible': moving,
          }"
          :title="!isDynamicSection && $t('layout.cellHoverTooltip')"
          class="full-width"
          flat
          v-on="!multiSelectEnabled && {
            click: () => $root.$emit('layout-cell-add-application', parentId, container)
          }">
          <v-card
            v-if="isDynamicSection"
            :class="{
              'invisible': !hoverScope.hover,
            }"
            :aria-label="$t('layout.addApplicationButton')"
            :title="$t('layout.addApplicationButton')"
            :height="hasApplication && 50 || '50%'"
            max-height="200"
            color="transparent"
            class="full-width d-flex flex-wrap align-center justify-center px-2"
            flat>
            <div class="d-flex flex-wrap align-center justify-center">
              <v-icon class="icon-default-color pe-2">fa-plus</v-icon>
              <span class="text-no-wrap">{{ $t('layout.addApp') }}</span>
            </div>
          </v-card>
          <div
            v-else
            :aria-label="$t('layout.cellHoverTooltip')"
            class="d-flex align-center justify-center full-width full-height">
            <v-icon
              class="icon-default-color"
              size="20">fa-vector-square</v-icon>
          </div>
        </v-card>
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
      default: () => 0,
    },
    length: {
      type: Number,
      default: () => 0,
    },
  },
  data: () => ({
    hover: false,
    hasContent: true,
  }),
  computed: {
    moving() {
      return this.storageId && this.$root.movingCell?.storageId === this.storageId;
    },
    movingChildren() {
      return this.storageId && this.isDynamicSection && this.$root.movingParentId === this.parentId;
    },
    sectionType() {
      return this.$layoutUtils.getSection(this.$root.layout, this.parentId)?.template;
    },
    isDynamicSection() {
      return this.sectionType === this.$layoutUtils.flexTemplate;
    },
    multiSelectEnabled() {
      return this.$root.isMultiSelect
        && this.$root.multiCellsSelect;
    },
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
    applicationTitle() {
      return !this.isDynamicSection && this.container?.children?.[0]?.title || '';
    },
    applicationCategory() {
      return this.applicationTitle && this.$root.applicationCategories?.find?.(c => c?.applications?.find?.(a => a?.displayName === this.applicationTitle));
    },
    applicationCategoryTitle() {
      return this.applicationCategory?.displayName || '';
    },
    isSelectedCell() {
      return this.$root.isMultiSelect
        && this.$root.selectedSectionId === this.parentId
        && !!this.$root.selectedCellCoordinates.find(c => c.rowIndex === this.container.rowIndex && c.colIndex === this.container.colIndex);
    },
    isNextCellOfMovedCell() {
      return this.$root.movingCell && this.storageId === this.$root.nextCellStorageId || false;
    },
    displayResizeButton() {
      return this.hasApplication || (this.isDynamicSection && this.index < (this.length - 1) && this.length < 12);
    },
    displayToMenu() {
      return this.hasApplication && !this.isDynamicSection;
    },
    cssStyle() {
      if (this.isDynamicSection) {
        const cssStyle = {};
        if (this.$root.desktopDisplayMode) {
          cssStyle['min-height'] = this.hasApplication && '100px' || '150px';
        }
        if (this.isNextCellOfMovedCell) {
          if (this.$vuetify.$rtl) {
            cssStyle['margin-right'] = `${this.$root.nextCellDiffWidth}px`;
          } else {
            cssStyle['margin-left'] = `${this.$root.nextCellDiffWidth}px`;
          }
        }
        return cssStyle;
      } else {
        return null;
      }
    },
  },
  watch: {
    hasApplication() {
      this.computeHasContent();
    },
    hover() {
      if (this.hover) {
        this.$root.hoveredParentId = this.storageId;
      } else if (this.$root.hoveredParentId === this.storageId) {
        this.$root.hoveredParentId = null;
      }
    },
  },
  mounted() {
    this.computeHasContent();
  },
  methods: {
    moveStart(event, moveType) {
      this.$root.moveType = moveType;
      if (!this.isDynamicSection || moveType === 'resize') {
        this.$root.movingParentId = this.parentId;
        this.$nextTick().then(() => {
          this.$root.$emit('layout-cell-moving-start', {
            target: event.target,
            x: event.x,
            y: event.y,
            sectionId: this.parentId,
            cell: this.container,
            containerElement: this.$refs.container.$el,
            moveType,
          });
        });
      }
    },
    computeHasContent() {
      if (this.isDynamicSection) {
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
        let hasContent = false;
        if (this.$refs.placeholder) {
          const placeholderHeight = this.$refs.placeholder.getBoundingClientRect().height;
          const containerHeight = this.$refs.container.$el.getBoundingClientRect().height;
          hasContent = containerHeight - placeholderHeight > 10;
        }
        this.hasContent = hasContent;
        if (!hasContent) {
          this.computeHasContentAsync();
        }
      }, 200);
    },
  },
};
</script>