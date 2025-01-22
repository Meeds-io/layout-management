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
    :parent-id="parentId"
    :hide-children="moving"
    :class="{
      'z-index-two': hover && !$root.drawerOpened,
    }"
    :style="cssStyle"
    class="d-flex flex-column full-height flex-grow-1 flex-shrink-1 me-5"
    @hovered="hover = $event"
    @initialized="computeHasContent"
    @move-start="moveStart">
    <template #footer>
      <div v-if="$root.desktopDisplayMode && displayResizeButton">
        <layout-editor-cell-resize-button
          :container="container"
          :parent-id="parentId"
          :hover="hover"
          :moving="moving"
          dynamic-section
          @move-start="moveStart" />
      </div>
      <v-hover v-if="$root.desktopDisplayMode && !hasApplication">
        <v-card
          slot-scope="hoverScope"
          v-show="!movingChildren"
          :class="{
            'grey-background': !isSelectedCell && !hoverScope.hover && (!$root.movingCell || $root.selectedSectionId !== parentId),
            'light-grey-background': !isSelectedCell && hoverScope.hover && (!$root.movingCell || $root.selectedSectionId !== parentId),
            'transparent': $root.movingCell && $root.selectedSectionId === parentId && !isSelectedCell,
            'grey': isSelectedCell,
            'invisible': moving,
          }"
          class="full-width full-height layout-add-application-button"
          flat
          v-on="!multiSelectEnabled && {
            click: () => $root.$emit('layout-add-application-category-drawer', parentId, container)
          }">
          <v-card
            :class="{
              'invisible': !hoverScope.hover,
            }"
            :aria-label="$t('layout.addApplicationButton')"
            :title="$t('layout.addApplicationButton')"
            min-height="100%"
            height="100%"
            color="transparent"
            class="full-width full-height d-flex flex-wrap align-center justify-center"
            flat>
            <div class="d-flex flex-wrap align-center justify-center">
              <v-icon class="icon-default-color pe-2">fa-plus</v-icon>
              <span class="text-no-wrap">{{ $t('layout.addApp') }}</span>
            </div>
          </v-card>
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
      return this.storageId
        && this.$root.movingParentId
        && this.$root.movingParentDynamic;
    },
    sectionType() {
      return this.$layoutUtils.getSection(this.$root.layout, this.parentId)?.template;
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
      return this.container?.children?.[0]?.title || '';
    },
    applicationCategory() {
      return this.applicationTitle && this.$root.portletInstanceCategories?.find?.(c => c?.applications?.find?.(a => a?.name === this.applicationTitle));
    },
    applicationCategoryTitle() {
      return this.applicationCategory?.name || '';
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
      return this.index < (this.length - 1);
    },
    cssStyle() {
      const cssStyle = {};
      if (this.isNextCellOfMovedCell) {
        if (this.$vuetify.$rtl) {
          cssStyle['margin-right'] = `${this.$root.nextCellDiffWidth}px`;
        } else {
          cssStyle['margin-left'] = `${this.$root.nextCellDiffWidth}px`;
        }
      }
      return cssStyle;
    },
  },
  watch: {
    hover() {
      if (this.hover) {
        this.$root.hoveredParentId = this.storageId;
      } else if (this.$root.hoveredParentId === this.storageId) {
        this.$root.hoveredParentId = null;
      }
    },
  },
  methods: {
    moveStart(event, moveType) {
      this.$root.moveType = moveType;
      if (moveType === 'resize') {
        this.$root.movingParentId = this.parentId;
        const section = this.$layoutUtils.getContainerById(this.$root.layout, this.parentId);
        this.$root.movingParentDynamic = section?.template === this.$layoutUtils.flexTemplate;
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
  },
};
</script>