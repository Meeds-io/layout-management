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
  <v-hover v-model="hover" :disabled="$root.mobileDisplayMode">
    <draggable
      v-if="draggable"
      v-model="children"
      :id="id"
      :data-storage-id="storageId"
      :class="cssClass"
      :style="cssStyle"
      :options="dragOptions"
      item-key="storageId"
      class="position-relative"
      @start="startMoving"
      @end="endMoving">
      <slot name="header"></slot>
      <slot v-if="$slots.content" name="content"></slot>
      <template v-else-if="hasChildren">
        <template v-for="(child, i) in children">
          <layout-editor-container-extension
            v-if="child"
            :key="child.storageId"
            :container="child"
            :application-title="applicationTitle"
            :application-category="applicationCategoryTitle"
            :parent-id="storageId"
            :index="i"
            :length="childrenSize"
            :class="`${draggableContainerClass} ${hideChildren && 'invisible' || ''}`"
            @initialized="$emit('initialized', child)"
            @move-start="moveStart"
            @move-end="moveEnd" />
        </template>
      </template>
      <slot name="footer"></slot>
    </draggable>
    <div
      v-else
      :id="id"
      :class="cssClass"
      :style="cssStyle">
      <slot name="header"></slot>
      <slot v-if="$slots.content" name="content"></slot>
      <template v-else-if="hasChildren">
        <layout-editor-container-extension
          v-for="(child, i) in children"
          :key="child.storageId"
          :container="child"
          :application-title="applicationTitle"
          :application-category="applicationCategoryTitle"
          :parent-id="storageId"
          :index="i"
          :length="childrenSize"
          :class="{
            'invisible': hideChildren
          }"
          @initialized="$emit('initialized', child)"
          @move-start="moveStart"
          @move-end="moveEnd" />
      </template>
      <slot name="footer"></slot>
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
    parentId: {
      type: String,
      default: null,
    },
    type: {
      type: String,
      default: () => 'default',
    },
    index: {
      type: Number,
      default: () => 0,
    },
    length: {
      type: Number,
      default: () => 0,
    },
    draggable: {
      type: Boolean,
      default: false,
    },
    hideChildren: {
      type: Boolean,
      default: false,
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
    hasChildren() {
      return !!this.childrenSize;
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
    borderColor() {
      return this.container.borderColor;
    },
    cssStyle() {
      if (!this.height && !this.width && !this.borderColor) {
        return null;
      } else {
        const style = {};
        if (this.height) {
          style.height = this.hasUnit(this.height) ? this.height : `${this.height}px`;
        }
        if (this.width) {
          style.width = this.hasUnit(this.width) ? this.width : `${this.width}px`;
        }
        if (this.borderColor) {
          style['--appBorderColor'] = this.borderColor;
        }
        return style;
      }
    },
    containerCssClass() {
      return this.$root.mobileDisplayMode ? this.container.cssClass?.replace?.('d-md-grid', '') : this.container.cssClass;
    },
    cssClass() {
      return `${this.containerCssClass || ''} ${this.draggable && 'v-draggable' || ''} ${this.noChildren && 'position-relative' || ''}`;
    },
    isCell() {
      return this.container.template === this.$layoutUtils.cellTemplate;
    },
    draggableContainerClass() {
      return this.isCell && `draggable-container-${this.parentId}` || `draggable-container-${this.storageId}`;
    },
    dragOptions() {
      const dragOptions = {
        group: `${this.container.template}-${this.parentId}`,
        draggable: `.${this.draggableContainerClass}`,
        animation: 200,
        ghostClass: 'layout-moving-ghost-container',
        chosenClass: 'layout-moving-chosen-container',
        handle: this.isCell && '.draggable-cell' || '.draggable',
      };
      return dragOptions;
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
      if (!this.isCell && JSON.stringify(this.container.children) !== JSON.stringify(this.children)) {
        this.$root.$emit('layout-children-size-updated', this.container, this.children, this.index, this.type);
      }
    },
  },
  created() {
    this.refreshChildren();
  },
  methods: {
    refreshChildren() {
      this.children = this.container?.children?.filter(c => !!c) || [];
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
    moveStart(event, moveType) {
      this.$emit('move-start', event, moveType);
    },
    moveEnd() {
      this.$emit('move-end');
      this.$root.movingParentId = null;
    },
    startMoving() {
      this.dragged = true;
      this.$root.movingParentId = this.parentId;
    },
    endMoving(event) {
      this.dragged = false;
      this.$root.movingParentId = null;

      const fromCell = this.$layoutUtils.getContainerById(this.$root.layout, event.from.getAttribute('data-storage-id'));
      const toCell = this.$layoutUtils.getContainerById(this.$root.layout, event.to.getAttribute('data-storage-id'));
      const application = this.$layoutUtils.getContainerById(this.$root.layout, event.item.getAttribute('data-storage-id'));

      if (fromCell && toCell && application) {
        if (this.isCell) {
          const section = this.$layoutUtils.getSectionByContainer(this.$root.layout, event.item.getAttribute('data-storage-id'));
          this.$root.$emit('layout-section-history-add', section?.storageId);
        } else {
          this.$root.$emit('layout-modified');
        }

        const fromIndex = fromCell.children.findIndex(c => c?.storageId === application.storageId);
        if (fromIndex >= 0) {
          fromCell.children.splice(fromIndex, 1);
        }
        const toIndex = toCell.children.findIndex(c => c?.storageId === application.storageId);
        if (toIndex < 0) {
          toCell.children.splice(event.newIndex, 0, application);
        }
      }
    },
  },
};
</script>
