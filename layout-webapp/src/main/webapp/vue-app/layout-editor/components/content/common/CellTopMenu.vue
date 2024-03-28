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
      v-show="hover && !moving && !$root.drawerOpened"
      class="layout-no-multi-select absolute-horizontal-center z-index-drawer t-0 mt-n4">
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
          @click.prevent.stop="$root.$emit('layout-edit-application', parentId, container, applicationCategory, applicationTitle)">
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
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    dynamicSection: {
      type: Boolean,
      default: false,
    },
    applicationCategory: {
      type: String,
      default: null,
    },
    applicationTitle: {
      type: String,
      default: null,
    },
    parentId: {
      type: String,
      default: null,
    },
    hover: {
      type: Boolean,
      default: null,
    },
    moving: {
      type: Boolean,
      default: null,
    },
  },
  data: () => ({
    iconSize: 20,
  }),
  methods: {
    dragStart(event) {
      this.$emit('move-start', event, 'drag');
    },
  },
};
</script>
