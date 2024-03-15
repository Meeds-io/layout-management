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
      class="position-relative"
      @start="startMoving"
      @end="endMoving">
      <layout-editor-container-extension
        v-for="(child, i) in children"
        :key="child.storageId"
        :container="child"
        :parent-id="storageId"
        :index="i"
        :length="children.length"
        :class="draggableContainerClass"
        :cell-height="cellHeight"
        :cell-width="cellWidth"
        @move-start="$emit('move-start')" />
    </draggable>
    <div
      v-else
      :id="id"
      :class="cssClass"
      :style="cssStyle">
      <slot name="content"></slot>
      <layout-editor-container-extension
        v-for="(child, i) in children"
        v-show="!hideChildren"
        :key="child.storageId"
        :container="child"
        :parent-id="storageId"
        :index="i"
        :length="children.length"
        :cell-height="cellHeight"
        :cell-width="cellWidth" />
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
    noDraggable: {
      type: Boolean,
      default: false,
    },
    hideChildren: {
      type: Boolean,
      default: false,
    },
    context: {
      type: String,
      default: null,
    },
    cellHeight: {
      type: Number,
      default: () => 0,
    },
    cellWidth: {
      type: Number,
      default: () => 0,
    },
    rowsCount: {
      type: Number,
      default: () => 0,
    },
    colsCount: {
      type: Number,
      default: () => 0,
    },
  },
  data: () => ({
    hover: false,
    children: [],
    dragged: false,
  }),
  computed: {
    storageId() {
      return this.container.storageId;
    },
    id() {
      return this.container.id || this.storageId;
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
    height() {
      return this.container.height === 'unset' ? null : this.container.height;
    },
    width() {
      return this.container.width === 'unset' ? null : this.container.width;
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
      return `${this.container.cssClass || ''} ${this.draggable && 'v-draggable' || ''} ${this.noChildren && 'position-relative' || ''}`;
    },
    draggable() {
      return !this.context && !this.noDraggable && this.childrenSize > 1;
    },
    draggableContainerClass() {
      return `draggable-container-${this.storageId}`;
    },
    dragOptions() {
      return {
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
    refreshChildren() {
      if (!this.context) {
        this.children = this.container?.children || [];
      } else if (this.container?.children?.length || this.children?.length) {
        window.setTimeout(() => {
          this.children = this.container?.children || [];
        }, 50);
      }
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
    startMoving() {
      this.dragged = true;
    },
    endMoving() {
      this.dragged = false;
    },
  },
};
</script>
