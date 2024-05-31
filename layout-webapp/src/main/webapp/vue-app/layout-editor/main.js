/*
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import './initComponents.js';
import '../common-page-template/main.js';
import '../common-portlets/main.js';

import './extensions.js';
import './services.js';

// get overridden components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('LayoutEditor');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

const appId = 'layoutEditor';

//getting language of the PLF
const lang = eXo?.env.portal.language || 'en';

//should expose the locale ressources as REST API
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.LayoutEditor-${lang}.json`;

export function init() {
  exoi18n.loadLanguageAsync(lang, url)
    .then(i18n => {
      // init Vue app when locale ressources are ready
      Vue.createApp({
        template: `<layout-editor id="${appId}"/>`,
        vuetify: Vue.prototype.vuetifyOptions,
        i18n,
        data: () => ({
          containerTypes: extensionRegistry.loadExtensions('layout-editor', 'container'),
          hoveredParentId: null,
          hoveredSectionId: null,
          hoveredSection: null,
          hoveredApplication: null,
          portletInstanceCategories: null,
          portletInstances: null,
          loadingPortletInstances: false,
          branding: null,
          displayMode: 'desktop',
          layout: null,
          pageRef: null,
          pageTemplate: null,
          pageTemplateId: null,
          draftPageRef: null,
          draftNode: null,
          draftNodeId: null,
          draftNodeUri: null,
          movingParentId: null,
          movingParentDynamic: false,
          drawerOpened: 0,
          selectedSectionId: null,
          selectedCellCoordinates: [],
          selectedCells: [],
          nextCellStorageId: null,
          nextCellDiffWidth: null,
          parentAppDimensions: false,
          multiCellsSelect: false,
          sectionHistory: null,
          sectionRedo: null,
          movingCell: null,
          moveType: null,
          startScrollX: 0,
          startScrollY: 0,
          diffScrollX: 0,
          diffScrollY: 0,
          gap: 20,
          nodeUri: null,
        }),
        computed: {
          parentAppX() {
            return this.parentAppDimensions?.x || 0;
          },
          parentAppY() {
            return this.parentAppDimensions?.y || 0;
          },
          defaultContainer() {
            return this.containerTypes.find(extension => extension.type === 'default');
          },
          isResize() {
            return this.moveType === 'resize';
          },
          isMove() {
            return this.moveType === 'drag';
          },
          isMultiSelect() {
            return this.moveType === 'multiSelect';
          },
          selectedFirstRowIndex() {
            return Math.min(...this.selectedCellCoordinates.map(c => c.rowIndex));
          },
          selectedFirstColIndex() {
            return Math.min(...this.selectedCellCoordinates.map(c => c.colIndex));
          },
          mobileDisplayMode() {
            return this.$root.displayMode === 'mobile';
          },
          desktopDisplayMode() {
            return this.$root.displayMode === 'desktop';
          },
        },
        watch: {
          movingParentId() {
            if (this.movingParentId) {
              this.$root.$emit('layout-editor-moving-start', this.movingParentId);
            } else {
              this.$root.$emit('layout-editor-moving-end', this.movingParentId);
            }
          },
          nodeUri() {
            if (this.nodeUri) {
              eXo.env.portal.webPageUrl = `/portal${this.nodeUri}`;
            }
          },
          layout(newVal, oldVal) {
            if (!oldVal) {
              window.setTimeout(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading')), 200);
            }
          },
        },
        created() {
          document.addEventListener('extension-layout-editor-container-updated', this.refreshContainerTypes);
          this.$on('layout-editor-portlet-instances-refresh', this.refreshPortletInstances);
          document.addEventListener('drawerOpened', this.setDrawerOpened);
          document.addEventListener('drawerClosed', this.setDrawerClosed);
          this.refreshPortletInstances();
          this.$brandingService.getBrandingInformation()
            .then(data => this.branding = data);
        },
        methods: {
          setDrawerOpened() {
            this.drawerOpened++;
          },
          setDrawerClosed() {
            this.drawerOpened--;
          },
          refreshPortletInstances() {
            this.loadingPortletInstances = true;
            return this.$portletInstanceCategoryService.getPortletInstanceCategories()
              .then(categories => this.portletInstanceCategories = categories)
              .then(()  => this.$portletInstanceService.getPortletInstances())
              .then(applications => this.portletInstances = applications.filter(a => !a.disabled))
              .finally(() => this.loadingPortletInstances = false);
          },
          refreshContainerTypes() {
            this.containerTypes = extensionRegistry.loadExtensions('layout-editor', 'container');
          },
          updateParentAppDimensions() {
            this.parentAppDimensions = document.querySelector('#layoutEditor').getBoundingClientRect();
          },
          initScrollPosition() {
            this.updateParentAppDimensions();
            this.startScrollX = this.parentAppDimensions.x;
            this.startScrollY = this.parentAppDimensions.y;
            this.diffScrollX = 0;
            this.diffScrollY = 0;
          },
          updateScrollPosition() {
            this.updateParentAppDimensions();
            this.diffScrollX = this.parentAppDimensions.x - this.startScrollX;
            this.diffScrollY = this.parentAppDimensions.y - this.startScrollY;
          },
          initCellsSelection() {
            this.selectedSectionId = null;
            this.moveType = null;
            this.selectedCells = [];
            this.selectedCellCoordinates = [];
          },
          resetMoving() {
            this.parentAppDimensions = null;
            this.moveType = null;
            this.multiCellsSelect = false;
          },
        },
      }, `#${appId}`, 'Layout Editor');
    })
    .finally(() => {
      Vue.prototype.$utils.includeExtensions('LayoutEditorExtension');
      document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
    });
}
