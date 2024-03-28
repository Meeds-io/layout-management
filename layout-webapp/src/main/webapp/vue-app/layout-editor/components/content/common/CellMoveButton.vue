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
  <v-btn
    v-if="hover && !moving && !$root.drawerOpened"
    ref="resizeButton"
    :title="dynamicSection && $t('layout.updateWidth') || $t('layout.resizeCell')"
    :width="iconSize"
    :height="iconSize"
    :class="{
      'l-0': $vuetify.rtl,
      'r-0': !$vuetify.rtl,
      'b-0 mb-n3 me-n3': !dynamicSection,
      'absolute-vertical-center me-n5': dynamicSection,
      'fa-rotate-90': !dynamicSection && !$vuetify.rtl
    }"
    class="position-absolute z-index-two"
    icon
    @mousedown.prevent.stop="resizeStart">
    <v-icon :size="iconSize" class="icon-default-color">
      {{ dynamicSection && 'fa-arrows-alt-h' || 'fa-expand-alt' }}
    </v-icon>
  </v-btn>
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
    resizeStart(event) {
      this.$emit('move-start', event, 'resize');
    },
  },
};
</script>
