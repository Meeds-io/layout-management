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
    :hide-children="moving"
    :class="{
      'z-index-two': hover && !$root.drawerOpened,
      'elevation-1': hasApplication && hover && !$root.movingCell,
    }"
    class="position-relative d-flex flex-column border-radius"
    @hovered="hover = $event"
    @initialized="computeHasContent">
    <template #header>
      <layout-editor-cell-top-menu
        v-if="hasApplication"
        :container="container"
        :parent-id="parentId"
        :hover="hover"
        :moving="moving"
        @move-start="moveStart" />
      <v-hover v-else>
        <v-card
          slot-scope="hoverScope"
          :class="{
            'opacity-5': hoverScope.hover && !isSelectedCell,
            'grey-background': !isSelectedCell && (!$root.movingCell || $root.selectedSectionId !== parentId),
            'transparent': $root.movingCell && $root.selectedSectionId === parentId && !isSelectedCell,
            'grey': isSelectedCell,
          }"
          class="full-width full-height"
          flat
          v-on="!multiSelectEnabled && {
            click: () => $root.$emit('layout-cell-add-application', parentId, container)
          }" />
      </v-hover>
    </template>
    <template v-if="hasApplication && !moving" #footer>
      <div
        ref="placeholder"
        :class="hasContent && 'linear-gradient-grey-background' || 'white'"
        class="position-relative flex-grow-1 flex-shrink-1 border-radius overflow-hidden">
        <div
          v-if="!hasContent"
          class="absolute-all-center d-flex flex-column align-center justify-center">
          <v-icon size="40" class="icon-default-color mb-5">far fa-hourglass</v-icon>
          <span class="font-weight-bold">{{ $t('layout.inSearchOfData') }}</span>
          <span v-if="applicationTitle" class="caption">{{ applicationTitle }}</span>
          <span v-if="applicationCategoryTitle" class="caption">({{ applicationCategoryTitle }})</span>
        </div>
      </div>
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
  },
  data: () => ({
    hover: false,
    hasContent: true,
  }),
  computed: {
    moving() {
      return this.storageId && this.$root.movingCell?.storageId === this.storageId;
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
  },
  watch: {
    hasApplication() {
      this.computeHasContent();
    },
  },
  mounted() {
    this.computeHasContent();
  },
  methods: {
    moveStart(event, moveType) {
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
    },
    computeHasContent() {
      this.hasContentCheckCount = 0;
      this.computeHasContentAsync();
    },
    computeHasContentAsync() {
      if (this.hasContentCheckCount > 3) {
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
        if (hasContent) {
          this.hasContent = true;
        } else {
          this.computeHasContentAsync();
        }
      }, 200);
    },
  },
};
</script>