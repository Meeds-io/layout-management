<template>
  <div
    ref="content"
    :id="id"
    :class="cssClass"
    :style="cssStyle"
    class="layout-application"></div>
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
  },
  computed: {
    nodeUri() {
      return this.$root.nodeUri;
    },
    storageId() {
      return this.container?.storageId;
    },
    id() {
      return this.container?.id || `Container${this.storageId}`;
    },
    height() {
      return this.container.height;
    },
    width() {
      return this.container.width;
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
    cssClass() {
      return this.container.cssClass || '';
    },
  },
  mounted() {
    this.installApplication();
  },
  methods: {
    installApplication() {
      if (this.$refs.content
          && this.nodeUri
          && this.storageId) {
        this.$applicationUtils.installApplication(this.nodeUri, this.storageId, this.$refs.content);
      }
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
  }
};
</script>