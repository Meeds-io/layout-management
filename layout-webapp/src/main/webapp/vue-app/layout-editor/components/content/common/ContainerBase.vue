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
    <draggable
      v-if="draggable"
      v-model="children"
      :id="id"
      :class="cssClass"
      :style="cssStyle"
      :options="dragOptions"
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
        :length="children.length"
        :class="draggableContainerClass" />
    </draggable>
    <div
      v-else
      :id="id"
      :class="cssClass"
      :style="cssStyle">
      <slot v-if="$slots.content" name="content"></slot>
      <div
        v-if="noChildren"
        class="d-flex align-center justify-center full-width full-height position-relative grey-background">
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
    preview: {
      type: Boolean,
      default: false,
    },
    forceDraggable: {
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
    containerChildren() {
      return this.container.children;
    },
    childrenSize() {
      return this.children.length;
    },
    noChildren() {
      return !this.childrenSize;
    },
    storageId() {
      return this.container?.storageId;
    },
    height() {
      return this.container.height;
    },
    width() {
      return this.container.width;
    },
    draggable() {
      return !this.preview && (this.children.length > 1 || this.forceDraggable);
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
    cssClass() {
      return `${this.container.cssClass} ${this.draggable && 'v-draggable position-relative' || ''} ${this.noChildren && 'draggable position-relative pb-5' || ''}`;
    },
    draggableContainerClass() {
      return `draggable-container-${this.storageId}`;
    },
    dragOptions() {
      return {
        group: {
          name: this.type,
        },
        draggable: `.${this.draggableContainerClass}`,
        animation: 200,
        ghostClass: 'layout-moving-ghost-container',
        chosenClass: 'layout-moving-chosen-container',
        handle: '.draggable',
      };
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
    children() {
      if (JSON.stringify(this.container.children) !== JSON.stringify(this.children)) {
        this.$root.$emit('layout-children-size-updated', this.container, this.children, this.index, this.type);
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
