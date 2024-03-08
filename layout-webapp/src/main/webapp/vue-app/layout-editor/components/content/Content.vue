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
    <layout-editor-application-drawer
      ref="applicationDrawer" />
  </div>
</template>
<script>
export default {
  props: {
    page: {
      type: Object,
      default: null,
    },
    node: {
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
    layoutToEdit() {
      this.$root.layout = this.layoutToEdit;
    },
    layout: {
      immediate: true,
      handler() {
        if (this.layout) {
          const layout = JSON.parse(JSON.stringify(this.layout));
          if (!layout.children?.length) {
            this.$layoutUtils.newParentContainer(layout);
            this.saveDraft(layout);
          } else {
            this.setLayout(layout);
          }
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
    this.$root.$on('layout-cell-resize', this.handleCellMerge);
    this.$root.$on('layout-cell-add-application', this.handleOpenAddApplicationDrawer);
    this.$root.$on('layout-add-application', this.handleAddApplication);
  },
  methods: {
    save() {
      // TODO
    },
    saveDraft(layout) {
      const layoutToUpdate = JSON.parse(JSON.stringify(layout || this.layoutToEdit));
      this.cleanStorageId(layoutToUpdate);
      return this.$pageLayoutService.updatePageLayout(this.$root.draftPageRef, layoutToUpdate)
        .then(layout => this.setLayout(layout));
    },
    handleOpenAddApplicationDrawer(sectionId, container) {
      this.$root.selectedSectionId = sectionId;
      this.$root.selectedCells = [container];
      this.$refs.applicationDrawer.open();
    },
    handleAddApplication(application) {
      const firstCell = this.$root.selectedCells[0];
      try {
        if (this.$root.selectedCells.length > 1) {
          const lastCell = this.$root.selectedCells[this.$root.selectedCells.length - 1];
          this.handleCellMerge(this.$root.selectedSectionId, firstCell, lastCell.rowIndex, lastCell.colIndex);
        }
        const cell = this.$layoutUtils.getCell(this.layoutToEdit, firstCell.storageId);
        this.$layoutUtils.newApplication(cell, application);
        this.saveDraft();
      } finally {
        this.$root.selectedSectionId = null;
        this.$root.selectedCells = null;
      }
    },
    handleCellMerge(parentId, container, targetCellRowIndex, targetCellColIndex) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      const section = parentContainer.children.find(c => c.storageId === parentId);
      if (section) {
        this.$layoutUtils.mergeCell(section, container, targetCellRowIndex, targetCellColIndex);
      } else {
        console.warn(`Can't find section with id ${parentId}`); // eslint-disable-line no-console
      }
    },
    handleSectionUpdated(container, children, index, type) {
      container.children = children;
      if (type === 'section' && !container.children?.length) {
        window.setTimeout(() => this.removeSection(index), 500);
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
    cleanStorageId(container) {
      if (container.randomId) {
        container.storageId = null;
      }
      if (container.children?.length) {
        container.children.forEach(this.cleanStorageId);
      }
    },
    setLayout(layout) {
      this.initContainer(layout);
      this.isCompatible = this.$layoutUtils.parseSections(layout);
      if (this.layoutToEdit) {
        Object.assign(this.layoutToEdit, layout);
      } else {
        this.layoutToEdit = layout;
      }
    },
    initContainer(container) {
      if (container.children) {
        container.children.forEach(this.initContainer);
      } else {
        container.children = [];
      }
    },
  },
};
</script>
