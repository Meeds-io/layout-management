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
  <v-fade-transition>
    <div
      ref="menu"
      v-show="menu"
      class="layout-no-multi-select absolute-horizontal-center z-index-drawer t-0 mt-n4">
      <v-chip color="white" class="elevation-2 no-border">
        <v-btn
          :title="$t('layout.moveCell')"
          :width="iconSize"
          :height="iconSize"
          class="ms-2 draggable-cell"
          icon
          @mousedown="dragStart"
          @mouseup="dragEnd">
          <v-icon :size="iconSize" class="icon-default-color">fa-arrows-alt</v-icon>
        </v-btn>
        <v-btn
          :title="$t('layout.editApplication')"
          :width="iconSize"
          :height="iconSize"
          class="mx-4"
          icon
          @click.prevent.stop="$root.$emit('layout-edit-application', sectionId, container, applicationCategoryTitle, applicationTitle)">
          <v-icon :size="iconSize" class="icon-default-color">fa-edit</v-icon>
        </v-btn>
        <v-btn
          :title="$t('layout.deleteApplication')"
          :width="iconSize"
          :height="iconSize"
          class="me-2"
          icon
          @click.prevent.stop="$root.$emit('layout-delete-application', sectionId, container)">
          <v-icon :size="iconSize" class="icon-default-color">fa-trash</v-icon>
        </v-btn>
      </v-chip>
    </div>
  </v-fade-transition>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    section: {
      type: Object,
      default: null,
    },
    parentId: {
      type: String,
      default: null,
    },
    applicationTitle: {
      type: String,
      default: null,
    },
    applicationCategoryTitle: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    iconSize: 20,
    menu: false,
  }),
  computed: {
    sectionId() {
      return this.section?.storageId;
    },
    isDynamicSection() {
      return this.section?.template === this.$layoutUtils.flexTemplate;
    },
  },
  methods: {
    displayMenu() {
      if (this.$root.movingParentId) {
        return;
      }
      this.$root.hoveredSection = this.section;
      this.$root.hoveredApplication = this.container;
      this.$root.hoveredParentId = this.parentId;
      this.menu = true;
    },
    hideMenu() {
      if (this.$root.movingParentId) {
        return;
      }
      this.menu = false;
    },
    dragEnd() {
      if (this.isDynamicSection) {
        this.$emit('move-end');
      }
    },
    dragStart(event) {
      if (event.button !== 0) {
        return;
      }
      if (this.isDynamicSection) {
        this.$emit('move-start', event, 'drag', this.container);
      } else {
        event.preventDefault();
        event.stopPropagation();
        this.$root.$emit('layout-editor-application-move-start', event, 'drag', this.container);
      }
    },
  },
};
</script>
