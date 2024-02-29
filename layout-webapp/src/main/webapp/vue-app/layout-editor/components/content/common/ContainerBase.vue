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
  <v-hover v-model="hover">
    <div
      v-if="children.length <= 1"
      :id="id"
      :class="cssClass"
      :style="cssStyle">
      <slot v-if="$slots.content" name="content"></slot>
      <div
        v-if="noChildren"
        class="d-flex align-center justify-center full-width full-height position-relative grey-background mb-5">
        <template>{{ id }}</template>
      </div>
      <layout-editor-container-container-extension
        v-for="(child, i) in children"
        :key="child.storageId"
        :container="child"
        :parent-id="storageId"
        :index="i"
        :length="children.length" />
    </div>
    <draggable
      v-else
      v-model="children"
      :id="id"
      :class="cssClass"
      :style="cssStyle"
      :animation="200"
      :options="{group: !disableGroup}"
      handle=".draggHandler"
      ghost-class="ghost-card"
      item-key="storageId"
      class="z-index-two"
      @start="startMoving"
      @end="endMoving">
      <layout-editor-container-container-extension
        v-for="(child, i) in children"
        :key="child.storageId"
        :container="child"
        :parent-id="container.storageId"
        :index="i"
        :length="children.length" />
    </draggable>
  </v-hover>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    type: {
      type: String,
      default: () => 'base',
    },
    index: {
      type: Number,
      default: () => 0,
    },
    length: {
      type: Number,
      default: () => 0,
    },
    moving: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    hover: false,
    dragged: false,
    children: [],
  }),
  computed: {
    id() {
      return this.container?.id || this.storageId;
    },
    storageId() {
      return this.container?.storageId;
    },
    cssClass() {
      return `${this.container.cssClass} ${this.children.length > 1 && 'v-draggable position-relative' || ''}`;
    },
    height() {
      return this.container.height;
    },
    width() {
      return this.container.width;
    },
    cssStyle() {
      if (!this.height && !this.width) {
        return null;
      } else {
        const style = {};
        if (this.height) {
          style.height = this.hasUnit(this.height) ? this.height : `${this.height}px`;
        }
        if (this.width) {
          style.width = this.hasUnit(this.width) ? this.width : `${this.width}px`;
        }
        return style;
      }
    },
    containerChildren() {
      return this.container.children;
    },
    disableGroup() {
      return this.$root.draggedContainerType === 'section';
    },
    noChildren() {
      return !this.children.length;
    },
  },
  watch: {
    hover() {
      this.$emit('hovered', this.hover);
    },
    dragged() {
      this.$root.draggedContainer = this.dragged && this.storageId || null;
      this.$root.$emit('layout-move-container', this.dragged, this.storageId);
    },
    containerChildren() {
      if (JSON.stringify(this.containerChildren) !== JSON.stringify(this.children)) {
        this.refreshChildren();
      }
    },
  },
  created() {
    this.refreshChildren();
  },
  methods: {
    startMoving() {
      this.dragged = true;
    },
    endMoving() {
      this.dragged = false;
    },
    refreshChildren() {
      this.children = this.container?.children;
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
  },
};
</script>
