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
  <div>
    <layout-editor-container-container-extension
      :container="layoutToEdit" />
    <layout-editor-section-add-drawer
      ref="sectionAddDrawer" />
    <layout-editor-section-edit-drawer
      ref="sectionEditDrawer" />
  </div>
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
    isCompatible: false,
  }),
  watch: {
    layout: {
      immediate: true,
      handler() {
        if (this.layout) {
          const layout = JSON.parse(JSON.stringify(this.layout));
          if (!layout.children?.length) {
            this.newParent(layout);
            this.layoutToEdit = layout;
            this.initSection(0, 3, 4);
          } else {
            this.layoutToEdit = layout;
          }
          this.isCompatible = this.$layoutUtils.parseLayout(layout);
        } else {
          this.layoutToEdit = null;
        }
      },
    },
  },
  created() {
    this.$root.$on('layout-save-page', this.save);
    this.$root.$on('layout-add-section', this.addSection);
    this.$root.$on('layout-edit-section', this.editSection);
    this.$root.$on('layout-remove-section', this.removeSection);
    this.$root.$on('layout-replace-section', this.replaceSection);
    this.$root.$on('layout-children-size-updated', this.handleSectionUpdated);
  },
  methods: {
    save() {
      // TODO
    },
    handleSectionUpdated(container, children, index, type) {
      container.children = children;
      if (type === 'section' && !container.children?.length) {
        window.setTimeout(() => this.removeSection(index), 500);
      }
    },
    newParent(layout) {
      const vuetifyAppContainer = this.$layoutUtils.newContainer(this.$layoutUtils.simpleContainerTemplate, 'VuetifyApp', layout);
      this.$layoutUtils.newContainer(this.$layoutUtils.simpleContainerTemplate, 'v-application v-application--is-ltr v-application--wrap singlePageApplication', vuetifyAppContainer);
    },
    initSection(index, rows, cols) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        this.$layoutUtils.newSection(parentContainer, index, rows, cols);
      }
    },
    addSection(index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        this.$refs.sectionAddDrawer.open(parentContainer, index);
      }
    },
    removeSection(index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        parentContainer.children.splice(index, 1);
      }
    },
    replaceSection(index, section) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        parentContainer.children.splice(index, 1, section);
      }
    },
    editSection(index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        this.$refs.sectionEditDrawer.open(parentContainer.children[index], index, parentContainer.children.length);
      }
    },
  },
};
</script>
