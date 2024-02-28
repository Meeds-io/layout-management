<template>
  <layout-editor-container-container-extension
    :container="layoutToEdit" />
</template>
<script>
export default {
  props: {
    page: {
      type: Object,
      default: null,
    },
    layout: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    layoutToEdit: null,
    containerModel: {
      // Used to add a specific id to the container on display
      // to be able to apply a specific CSS
      // but generally not useful
      id: null,
      // Not used for containers
      name: null,
      // Not used for containers
      icon: null,
      // Used by extensionRegistry mechanism to now which
      // Vue component to use
      template: null,
      // Used for now only for dynamic containers
      factoryId: null,
      // Not used for containers
      title: null,
      // Not used for containers
      description: null,
      // Used to specify the width of the container
      width: null,
      // Used to specify the height of the container
      height: null,
      // Used to specify some special CSS Classes to be used on container parent
      cssClass: null,
      // Used to specify whether the block should be displayed or not
      // when an addon is present
      profiles: null,
      // Generally kept to be accessible to everyone to make
      // the parent page access permissions applied globally 
      accessPermissions: ['Everyone'],
      // Deprecated, not used for containers
      moveAppsPermissions: ['Everyone'],
      // Deprecated, not used for containers
      moveContainersPermissions: ['Everyone'],
      // List of children which can be of type:
      // 1. Container
      //   or
      // 2. Application
      children: [],
    },
  }),
  watch: {
    layout: {
      immediate: true,
      handler() {
        if (this.layout) {
          const layout = JSON.parse(JSON.stringify(this.layout));
          if (!layout.children?.length) {
            layout.children = [this.newParent()];
            this.addSection(layout);
          }
          this.layoutToEdit = layout;
        } else {
          this.layoutToEdit = null;
        }
      },
    },
  },
  created() {
    this.$root.$on('layout-save-page', this.save);
  },
  methods: {
    save() {
      // TODO
    },
    newParent() {
      const vuetifyAppContainer = this.newContainer('system:/groovy/portal/webui/container/UIContainer.gtmpl', 'VuetifyApp');
      const vAppContainer = this.newContainer('system:/groovy/portal/webui/container/UIContainer.gtmpl', 'v-application v-application--is-ltr v-application--wrap singlePageApplication');
      vuetifyAppContainer.children.push(vAppContainer);
      return vuetifyAppContainer;
    },
    addSection(layout) {
      const parentContainer = layout?.children?.[0]?.children?.[0];
      if (!parentContainer?.cssClass?.includes?.('v-application')) {
        return;
      }
      const row = this.newContainer('system:/groovy/portal/webui/container/UIVRowContainer.gtmpl', null, parentContainer);
      for (let index = 0; index < 12; index++) {
        this.newContainer('system:/groovy/portal/webui/container/UIVColContainer.gtmpl', 'col-12 col-sm-6 col-md-4 col-lg-3 py-0 aspect-ratio-1', row);
      }
    },
    newContainer(template, cssClass, parentContainer) {
      const container = JSON.parse(JSON.stringify(this.containerModel));
      container.template = template;
      container.cssClass = cssClass;
      if (parentContainer) {
        parentContainer.children.push(container);
      }
      return container;
    },
  },
};
</script>
