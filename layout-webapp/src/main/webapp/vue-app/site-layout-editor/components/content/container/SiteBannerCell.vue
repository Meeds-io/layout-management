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
  <v-hover v-model="hover">
    <layout-editor-container-base
      ref="container"
      :container="container"
      :parent-id="parentId"
      :hide-children="moving"
      :class="{
        'position-relative': hasApplication,
        'z-index-two': hover && !$root.drawerOpened,
      }"
      class="full-height flex-grow-1 flex-shrink-1">
      <template v-if="application" #header>
        <v-hover v-model="hoverMenu">
          <layout-editor-application-menu
            ref="menu"
            :container="application"
            :section="container"
            :parent-id="storageId"
            :application-title="applicationTitle"
            :application-category-title="applicationCategoryTitle"
            @move-start="moveStart"
            @move-end="moveEnd" />
        </v-hover>
        <div
          v-if="!editablePortlet"
          v-show="hover"
          class="full-width full-height overflow-hidden position-absolute z-index-two">
          <v-expand-transition>
            <v-card
              v-if="hover"
              :class="isDynamicSection && 'mb-5'"
              :height="isDynamicSection && 'calc(100% - 20px)' || '100%'"
              class="d-flex align-center justify-center full-width transition-fast-in-fast-out mask-color darken-2 v-card--reveal white--text">
              <v-icon size="22" class="white--text me-2 mt-1">fab fa-readme</v-icon>
              <span>{{ $t('layout.readonlyPortletContent') }}</span>
            </v-card>
          </v-expand-transition>
        </div>
      </template>
      <template #footer>
        <div v-if="$root.desktopDisplayMode && displayResizeButton" class="position-absolute full-height t-0">
          <layout-editor-cell-resize-button
            :container="container"
            :parent-id="parentId"
            :hover="hover"
            :moving="moving"
            class="layout-column-resize"
            spacing-class="me-n3"
            dynamic-section
            @move-start="moveStart" />
        </div>
        <v-hover v-if="$root.desktopDisplayMode && !hasApplication" v-model="hoverAddApplication">
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
            @click="$root.$emit('layout-add-application-category-drawer', storageId, container)">
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
    hoverMenu: false,
    hoverAddApplication: false,
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
    displayResizeButton() {
      return this.index > 0;
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
    application() {
      return this.children?.[0];
    },
    applicationId() {
      return this.application?.storageId;
    },
    portletInstance() {
      return this.$root.portletInstances?.find?.(p => p.contentId === this.application?.contentId);
    },
    applicationTitle() {
      return this.portletInstance?.name || this.application?.title || '';
    },
    applicationCategory() {
      return this.portletInstance?.id && this.$root.portletInstanceCategories?.find?.(c => c?.applications?.find?.(a => a?.id === this.portletInstance?.id));
    },
    applicationCategoryTitle() {
      return this.applicationCategory?.name || '';
    },
    editablePortlet() {
      return this.portletInstance?.editable || false;
    },
    hoverApp() {
      return !!(this.hoverMenu || this.hover);
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
    hoverApp() {
      if (this.$refs?.menu) {
        if (this.hoverApp) {
          this.$refs.menu.displayMenu();
        } else {
          this.$refs.menu.hideMenu();
        }
      }
    },
  },
  methods: {
    moveStart(event, moveType) {
      this.$root.moveType = moveType;
      if (moveType === 'resize') {
        this.$root.movingParentId = this.parentId;
        this.$root.movingParentDynamic = true;
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
    moveEnd() {
      this.$emit('move-end');
    },
  },
};
</script>