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
  <div class="position-relative layout-section">
    <layout-editor-container-container-base
      :container="container"
      :index="index"
      :length="length"
      :preview="preview"
      type="section"
      class="row mx-n3 border-box-sizing"
      force-draggable
      @hovered="hoverSection = $event" />
    <v-hover v-if="!preview">
      <div
        v-if="!preview"
        slot-scope="{ hover }"
        class="layout-section-border">
        <div class="position-relative full-height full-width">
          <layout-editor-section-border-menu
            :hover="hover || hoverSection || movingSection"
            :index="index"
            :length="length"
            :moving="movingSection"
            @move-start="movingSection = true" />
        </div>
      </div>
    </v-hover>
  </div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
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
    preview: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    hoverSection: false,
    movingSection: false,
  }),
  watch: {
    movingSection() {
      this.$root.draggedContainerType = this.movingSection && 'section' || null;
    },
  },
  created() {
    this.$root.$on('layout-move-container', this.handleMoving);
  },
  beforeDestroy() {
    this.$root.$off('layout-move-container', this.handleMoving);
  },
  methods: {
    handleMoving(moving) {
      if (this.movingSection && !moving) {
        this.movingSection = false;
      }
    },
  },
};
</script>
