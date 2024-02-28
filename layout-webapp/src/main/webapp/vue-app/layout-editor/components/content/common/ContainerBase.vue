<template>
  <div
    :id="id"
    :class="cssClass"
    :style="cssStyle">
    <div
      v-if="$root.editPage"
      :class="noChildren && 'grey-background mb-5'"
      class="full-width full-height position-relative">
    </div>
    <slot v-if="$slots.default" name="default"></slot>
    <template v-else>
      <slot name="content"></slot>
      <layout-editor-container-container-extension
        v-for="child in children"
        :key="computeId(child)"
        :container="child" />
    </template>
  </div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
  },
  computed: {
    id() {
      return this.container.id || this.container.storageId;
    },
    cssClass() {
      return this.container.cssClass;
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
    children() {
      return this.container?.children || [];
    },
    noChildren() {
      return !this.children.length;
    },
  },
  methods: {
    computeId(container) {
      if (!container.storageId) {
        container.storageId = parseInt(Math.random() * 10000);
      }
      return container.storageId;
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    }
  },
};
</script>
