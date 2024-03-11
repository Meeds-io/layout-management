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
    loading: false,
  }),
  watch: {
    layoutToEdit() {
      this.$root.layout = this.layoutToEdit;
    },
    loading() {
      if (this.loading) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
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
    this.$root.$on('layout-add-section-drawer', this.addSection);
    this.$root.$on('layout-edit-section-drawer', this.editSection);
    this.$root.$on('layout-cell-add-application', this.addApplication);
    this.$root.$on('layout-cells-selection-start', this.initCellsSelection);
    this.$root.$on('layout-cells-selection-end', this.addApplicationOnCells);
    this.$root.$on('layout-add-section', this.handleAddSection);
    this.$root.$on('layout-remove-section', this.handleRemoveSection);
    this.$root.$on('layout-replace-section', this.handleReplaceSection);
    this.$root.$on('layout-children-size-updated', this.handleSectionUpdated);
    this.$root.$on('layout-cell-resize', this.handleCellMerge);
    this.$root.$on('layout-add-application', this.handleAddApplication);
    this.$root.$on('layout-application-drawer-closed', this.resetCellsSelection);
  },
  methods: {
    save() {
      // TODO
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
    addSection(index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        this.$refs.sectionAddDrawer.open(parentContainer, index);
      }
    },
    editSection(index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        this.$refs.sectionEditDrawer.open(parentContainer.children[index], index, parentContainer.children.length);
      }
    },
    initCellsSelection() {
      this.$root.selectedSectionId = null;
      this.$root.selectedCells = [];
    },
    addApplication(sectionId, container) {
      this.$root.selectedSectionId = sectionId;
      this.$root.selectedCells = [container];
      this.$refs.applicationDrawer.open();
    },
    resetCellsSelection() {
      window.setTimeout(() => this.initCellsSelection(), 300);
    },
    addApplicationOnCells() {
      if (this.$root.selectedSectionId
          && this.$root.selectedCells?.length) {
        this.$refs.applicationDrawer.open();
      }
    },
    handleAddApplication(application) {
      const selectedCells = this.$root.selectedCells.slice();
      const selectedSectionId = this.$root.selectedSectionId;
      const firstCellRowIndex = Math.min(...selectedCells.map(c => c.rowIndex));
      const firstCellColIndex = Math.min(...selectedCells.map(c => c.colIndex));
      const lastCellRowIndex = Math.max(...selectedCells.map(c => c.rowIndex));
      const lastCellColIndex = Math.max(...selectedCells.map(c => c.colIndex));

      try {
        const firstCell = selectedCells.find(c => c.rowIndex === firstCellRowIndex && c.colIndex === firstCellColIndex);
        const lastCell = selectedCells.find(c => c.rowIndex === lastCellRowIndex && c.colIndex === lastCellColIndex);
        if (!firstCell) {
          console.error('Can not find the first cell to add an application into it', selectedCells, firstCellRowIndex, firstCellColIndex); // eslint-lint-disable no-console
          return;
        } else if (!lastCell) {
          console.error('Can not find the last cell to add an application into it', selectedCells, lastCellRowIndex, lastCellColIndex); // eslint-lint-disable no-console
          return;
        } else if (selectedCells.length > 1) {
          this.mergeCell(selectedSectionId, firstCell, lastCell.rowIndex, lastCell.colIndex);
        }
        const cell = this.$layoutUtils.getCell(this.layoutToEdit, firstCell.storageId);
        this.$layoutUtils.newApplication(cell, application);
        this.saveDraft();
      } finally {
        this.initCellsSelection();
      }
    },
    handleCellMerge(parentId, container, targetCellRowIndex, targetCellColIndex) {
      this.mergeCell(parentId, container, targetCellRowIndex, targetCellColIndex);
      this.saveDraft();
    },
    mergeCell(parentId, container, targetCellRowIndex, targetCellColIndex) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      const section = parentContainer.children.find(c => c.storageId === parentId);
      if (section) {
        this.$layoutUtils.resizeCell(section, container, targetCellRowIndex, targetCellColIndex);
      } else {
        console.warn(`Can't find section with id ${parentId}`); // eslint-disable-line no-console
      }
    },
    handleSectionUpdated(container, children, index, type) {
      container.children = children;
      if (type === 'section' && !container.children?.length) {
        window.setTimeout(() => this.handleRemoveSection(index), 500);
      }
    },
    handleAddSection(section, index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      parentContainer.children.splice(index || 0, 0, section);
      this.saveDraft();
    },
    handleRemoveSection(index) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        parentContainer.children.splice(index, 1);
      }
      this.saveDraft();
    },
    handleReplaceSection(index, section) {
      const parentContainer = this.$layoutUtils.getParentContainer(this.layoutToEdit);
      if (parentContainer) {
        parentContainer.children.splice(index, 1, section);
      }
      this.saveDraft();
    },
    saveDraft(layout) {
      const layoutToUpdate = this.$layoutUtils.cleanAttributes(layout || this.layoutToEdit);
      this.loading = true;
      return this.$pageLayoutService.updatePageLayout(this.$root.draftPageRef, layoutToUpdate)
        .then(layout => this.setLayout(layout))
        .finally(() => this.loading = false);
    },
  },
};
</script>
