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
            this.newParent(layout);
          }
          this.layoutToEdit = layout;
          this.addSection(0, 12);
        } else {
          this.layoutToEdit = null;
        }
      },
    },
  },
  created() {
    this.$root.$on('layout-save-page', this.save);
    this.$root.$on('layout-add-section', this.addSection);
  },
  methods: {
    save() {
      // TODO
    },
    newParent(layout) {
      const vuetifyAppContainer = this.newContainer('system:/groovy/portal/webui/container/UIContainer.gtmpl', 'VuetifyApp', layout);
      this.newContainer('system:/groovy/portal/webui/container/UIContainer.gtmpl', 'v-application v-application--is-ltr v-application--wrap singlePageApplication', vuetifyAppContainer);
    },
    addSection(index, cols) {
      const parentContainer = this.layoutToEdit?.children?.[0]?.children?.[0];
      if (!parentContainer?.cssClass?.includes?.('v-application')) {
        return;
      }
      const row = this.newContainer('system:/groovy/portal/webui/container/UIVRowContainer.gtmpl', null, parentContainer, index);
      for (let i = 0; i < cols; i++) {
        this.newContainer('system:/groovy/portal/webui/container/UIVColContainer.gtmpl', 'col-12 col-sm-6 col-md-4 col-lg-3 py-0 aspect-ratio-1', row);
      }
    },
    newContainer(template, cssClass, parentContainer, index) {
      const container = JSON.parse(JSON.stringify(this.containerModel));
      container.template = template;
      container.cssClass = cssClass;
      container.storageId = parseInt(Math.random() * 10000);
      container.id = container.storageId;
      container.name = container.storageId;
      if (parentContainer) {
        container.parentId = parentContainer.storageId;
        parentContainer.children.splice(index || 0, 0, container);
      }
      return container;
    },
  },
};
</script>
