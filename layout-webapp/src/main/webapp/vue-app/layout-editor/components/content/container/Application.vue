<template>
  <v-hover v-model="hover">
    <div
      ref="content"
      :id="id"
      :class="`${cssClass}${hover && isDynamicSection && ' position-relative' || ''}${hidden && ' d-none' || ''}`"
      :style="cssStyle"
      :data-storage-id="storageId"
      class="layout-application">
      <v-hover v-model="hoverMenu">
        <layout-editor-application-menu
          ref="menu"
          :container="container"
          :section="section"
          :parent-id="parentId"
          :application-title="applicationTitle"
          :application-category-title="applicationCategoryTitle"
          @move-start="moveStart"
          @move-end="moveEnd" />
      </v-hover>
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
  },
  data: () => ({
    applicationInstalled: false,
    section: null,
    height: null,
    width: null,
    borderColor: null,
    cssClass: null,
    hover: false,
    hoverMenu: false,
  }),
  computed: {
    storageId() {
      return this.container?.storageId;
    },
    nodeId() {
      return this.$root.draftNodeId;
    },
    nodeUri() {
      return this.$root.draftNodeUri;
    },
    id() {
      return this.container?.id || `Container${this.storageId}`;
    },
    cssStyle() {
      if (!this.height && !this.width && !this.borderColor) {
        return null;
      } else {
        const style = {};
        if (this.height) {
          style['--appHeight'] = this.hasUnit(this.height) ? this.height : `${this.height}px`;
          style['--appHeightScroll'] = 'auto';
        }
        if (this.width) {
          style['--appWidth'] = this.hasUnit(this.width) ? this.width : `${this.width}px`;
          style['--appWidthScroll'] = 'auto';
        }
        if (this.borderColor) {
          style['--appBorderColor'] = this.borderColor;
        }
        return style;
      }
    },
    hidden() {
      return this.$root.mobileDisplayMode && this.container?.cssClass?.includes?.('hidden-sm-and-down');
    },
    isDynamicSection() {
      return this.section?.template === this.$layoutUtils.flexTemplate;
    },
    applicationTitle() {
      return this.container?.title || '';
    },
    applicationCategory() {
      return this.applicationTitle && this.$root.applicationCategories?.find?.(c => c?.applications?.find?.(a => a?.displayName === this.applicationTitle));
    },
    applicationCategoryTitle() {
      return this.applicationCategory?.displayName || '';
    },
    hoverApp() {
      return this.hoverMenu || this.hover;
    },
  },
  watch: {
    applicationInstalled() {
      this.$emit('initialized');
    },
    storageId(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.installApplication();
      }
    },
    nodeUri(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.installApplication();
      }
    },
    hoverApp() {
      if (this.hoverApp) {
        this.$refs.menu.displayMenu();
      } else {
        this.$refs.menu.hideMenu();
      }
    },
  },
  created() {
    this.$root.$on('layout-section-application-update-style', this.updateStyle);
    this.initStyle();
    this.section = this.$layoutUtils.getSectionByContainer(this.$root.layout, this.parentId);
  },
  mounted() {
    this.installApplication();
  },
  updated() {
    this.installApplication();
  },
  beforeDestroy() {
    this.$root.$off('layout-section-application-update-style', this.updateStyle);
  },
  methods: {
    installApplication() {
      if (!this.applicationInstalled
          && this.$refs.content
          && this.nodeUri
          && this.storageId) {
        this.$applicationUtils.installApplication(this.nodeUri, this.storageId, this.$refs.content)
          .then(() => this.applicationInstalled = true);
      }
    },
    updateStyle(container) {
      if (this.container.storageId === container.storageId) {
        this.initStyle();
      }
    },
    initStyle() {
      this.height = this.container.height;
      this.width = this.container.width;
      this.borderColor = this.container.borderColor;
      this.cssClass = this.container.cssClass || '';
    },
    moveEnd() {
      this.$emit('move-end');
    },
    moveStart(event, moveType) {
      this.$emit('move-start', event, moveType, this.container);
    },
    hasUnit(length) {
      return Number.isNaN(Number(length));
    },
  },
};
</script>