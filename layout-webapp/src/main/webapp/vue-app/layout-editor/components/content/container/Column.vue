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
    :index="index"
    :length="length"
    :context="context"
    :hide-children="moving"
    :cell-height="targetCellHeight"
    :cell-width="targetCellWidth"
    :class="{
      'z-index-two': hover && !$root.drawerOpened,
      'elevation-1': hasApplication && hover,
      'z-index-modal': resize,
    }"
    class="position-relative d-flex flex-column border-radius"
    @hovered="hover = $event"
    @initialized="computeHasContent">
    <template #content>
      <template v-if="hasApplication">
        <v-btn
          v-if="hover && !resize && !moving && !$root.drawerOpened"
          ref="resizeButton"
          :title="$t('layout.resizeCell')"
          :width="iconSize"
          :height="iconSize"
          :class="{
            'l-0': $vuetify.rtl,
            'r-0': !$vuetify.rtl,
            'fa-rotate-90': !$vuetify.rtl
          }"
          class="position-absolute z-index-two b-0 mb-n3 me-n3"
          icon
          @mousedown.prevent.stop="resizeStart">
          <v-icon :size="iconSize" class="icon-default-color">fa-expand-alt</v-icon>
        </v-btn>
        <v-fade-transition>
          <div
            v-show="hover && !moving && !$root.drawerOpened"
            class="layout-no-multi-select absolute-horizontal-center z-index-drawer mt-n4">
            <v-chip color="white" class="elevation-2">
              <v-btn
                :title="$t('layout.moveCell')"
                :width="iconSize"
                :height="iconSize"
                class="draggable ms-2"
                icon
                @mousedown.prevent.stop="dragStart">
                <v-icon :size="iconSize" class="icon-default-color">fa-arrows-alt</v-icon>
              </v-btn>
              <v-btn
                :title="$t('layout.editApplication')"
                :width="iconSize"
                :height="iconSize"
                class="mx-4"
                icon
                @click.prevent.stop="$emit('layout-edit-application', parentId, container)">
                <v-icon :size="iconSize" class="icon-default-color">fa-edit</v-icon>
              </v-btn>
              <v-btn
                :title="$t('layout.deleteApplication')"
                :width="iconSize"
                :height="iconSize"
                class="me-2"
                icon
                @click.prevent.stop="$root.$emit('layout-delete-application', parentId, container)">
                <v-icon :size="iconSize" class="icon-default-color">fa-trash</v-icon>
              </v-btn>
            </v-chip>
          </div>
        </v-fade-transition>
      </template>
      <v-hover v-else>
        <v-card
          slot-scope="hoverScope"
          :class="{
            'grey-background opacity-5': hoverScope.hover && !isSelectedCell,
            'grey': isSelectedCell,
            'grey-background': !hoverScope.hover && !isSelectedCell,
          }"
          class="full-width full-height"
          flat
          v-on="!$root.multiCellsSelect && {
            click: () => $root.$emit('layout-cell-add-application', parentId, container)
          }" />
      </v-hover>
    </template>
    <template v-if="hasApplication && !moving" #bottom>
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
    index: {
      type: Number,
      default: null,
    },
    length: {
      type: Number,
      default: null,
    },
    context: {
      type: String,
      default: null,
    },
    cellHeight: {
      type: String,
      default: null,
    },
    cellWidth: {
      type: String,
      default: null,
    },
    rowsCount: {
      type: Number,
      default: null,
    },
    colsCount: {
      type: Number,
      default: null,
    },
  },
  data: () => ({
    hover: false,
    cellCols: 0,
    cellRows: 0,
    dimensions: null,
    isInMultiSelection: false,
    hasContent: true,
    iconSize: 20,
  }),
  computed: {
    moving() {
      return this.storageId && this.$root.movingCell?.storageId === this.storageId;
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
    multiCellsSelect() {
      return this.$root.multiCellsSelect;
    },
    dimensionsX0() {
      return this.dimensions?.x || 0;
    },
    dimensionsX1() {
      return this.dimensionsX0 + this.dimensions?.width || 0;
    },
    dimensionsY0() {
      return this.dimensions?.y || 0;
    },
    dimensionsY1() {
      return this.dimensionsY0 + this.dimensions.height || 0;
    },
    selectedCells() {
      return this.$root.selectedCells;
    },
    isSelectedCell() {
      return !!this.selectedCells.find(c => c.storageId === this.container.storageId);
    },
    selectMouseX1() {
      return this.$root.selectMouseX1;
    },
    selectMouseY1() {
      return this.$root.selectMouseY1;
    },
  },
  watch: {
    multiCellsSelect(val) {
      if (val) {
        this.dimensions = this.$refs.container.$el.getBoundingClientRect();
      }
    },
    dimensions() {
      return this.computeIsInMultiSelection();
    },
    selectMouseX1() {
      return this.computeIsInMultiSelection();
    },
    selectMouseY1() {
      return this.computeIsInMultiSelection();
    },
    hasApplication() {
      this.computeHasContent();
    },
    isInMultiSelection() {
      if (this.multiCellsSelect) {
        if (this.isInMultiSelection) {
          if (this.$root.selectedCells?.length === 0) {
            this.$root.selectedSectionId = this.parentId;
          }
          if (this.$root.selectedSectionId === this.parentId) {
            this.$root.selectedCells.push(this.container);
          }
        } else if (this.$root.selectedSectionId === this.parentId) {
          const index = this.$root.selectedCells?.indexOf(this.container);
          if (index === 0 || index) {
            this.$root.selectedCells.splice(index, 1);
          }
        }
      }
    },
  },
  mounted() {
    this.computeHasContent();
  },
  methods: {
    dragStart(event) {
      this.moveStart(event, 'drag');
    },
    resizeStart(event) {
      this.moveStart(event, 'resize');
    },
    moveStart(event, moveType) {
      this.$root.$emit('layout-section-history-add', this.parentId);
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
    computeIsInMultiSelection() {
      this.isInMultiSelection =
        this.multiCellsSelect
        && this.dimensions
        && ((
          (this.dimensionsX0 > this.$root.selectMouseX0 && this.dimensionsX0 < this.$root.selectMouseX1)
           || (this.dimensionsX1 > this.$root.selectMouseX0 && this.dimensionsX1 < this.$root.selectMouseX1)
        ) && (
          (this.dimensionsY0 > this.$root.selectMouseY0 && this.dimensionsY0 < this.$root.selectMouseY1)
           || (this.dimensionsY1 > this.$root.selectMouseY0 && this.dimensionsY1 < this.$root.selectMouseY1)
        ) || (
          (this.dimensionsX0 < this.$root.selectMouseX0 && this.dimensionsX1 > this.$root.selectMouseX1)
           && (this.dimensionsY0 > this.$root.selectMouseY0 && this.dimensionsY0 < this.$root.selectMouseY1)
        ) || (
          (this.dimensionsY0 < this.$root.selectMouseY0 && this.dimensionsY1 > this.$root.selectMouseY1)
           && (this.dimensionsX0 > this.$root.selectMouseX0 && this.dimensionsX0 < this.$root.selectMouseX1)
        ) || (
          (this.dimensionsY0 < this.$root.selectMouseY0 && this.dimensionsY1 > this.$root.selectMouseY0)
           && (this.dimensionsX0 < this.$root.selectMouseX0 && this.dimensionsX1 > this.$root.selectMouseX0)
        ));
    },
  },
};
</script>
