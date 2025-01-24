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
      <v-hover v-if="$root.desktopDisplayMode" v-model="hoverAddApplication">
        <v-card
          v-show="!movingChildren"
          :class="{
            'grey-background': opaqueBackground,
            'light-grey-background': !opaqueBackground,
            'transparent': $root.movingCell && $root.selectedSectionId === parentId,
            'invisible': moving,
          }"
          class="full-width full-height layout-add-application-button"
          flat
          @click="$root.$emit('layout-add-application-category-drawer', storageId)">
          <v-card
            :class="{
              'invisible': !hoverAddApplication,
            }"
            :aria-label="$t('layout.addApplicationButton')"
            :title="$t('layout.addApplicationButton')"
            min-height="100%"
            height="100%"
            color="transparent"
            class="full-width full-height d-flex flex-wrap align-center justify-center"
            flat>
            <component
              :is="hasBackground && 'v-btn' || 'div'"
              :class="{
                'btn white': hasBackground,
                'd-flex flex-wrap align-center justify-center': !hasBackground,
              }"
              class="position-absolute">
              <v-icon class="icon-default-color pe-2">fa-plus</v-icon>
              <span class="text-no-wrap">{{ $t('layout.addApp') }}</span>
            </component>
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
    hoverAddApplication: false,
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
    displayResizeButton() {
      return this.index < (this.length - 1);
    },
    parentContainer() {
      return this.$layoutUtils.getContainerById(this.$root.layout, this.parentId);
    },
    hasBackground() {
      return this.parentContainer.backgroundImage
        || this.parentContainer.backgroundColor
        || this.parentContainer.backgroundGradientFrom;
    },
    opaqueBackground() {
      return !this.hoverAddApplication
        && !this.hasBackground
        && (!this.$root.movingCell || this.$root.selectedSectionId !== this.parentId);
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