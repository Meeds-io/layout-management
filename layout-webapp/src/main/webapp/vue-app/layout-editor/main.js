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
import './extensions.js';
import './services.js';

// get overridden components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('layoutEditor');
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

document.dispatchEvent(new CustomEvent('displayTopBarLoading'));

export function init() {
  exoi18n.loadLanguageAsync(lang, url)
    .then(i18n =>
      // init Vue app when locale ressources are ready
      Vue.createApp({
        template: `<layout-editor id="${appId}"/>`,
        vuetify: Vue.prototype.vuetifyOptions,
        i18n,
        data: () => ({
          containerTypes: extensionRegistry.loadExtensions('layout-editor', 'container'),
          editPage: true,
          layout: null,
          pageRef: null,
          draftPageRef: null,
          draftNode: null,
          draftNodeId: null,
          draftNodeUri: null,
          draggedContainer: null,
          draggedSection: null,
          resizeDimensions: null,
          resizeParentId: null,
          movingParentId: null,
          drawerOpened: 0,
          selectedSectionId: null,
          selectedCellCoordinates: [],
          selectedCells: [],
          // Resize mouse Grid indexes
          mouseCellRowIndex: -1,
          mouseCellColIndex: -1,
          // Grid cells selection
          parentAppDimensions: false,
          multiCellsSelect: false,
          sectionHistory: null,
          sectionRedo: null,
          movingCell: null,
          movingCellRowIndex: 0,
          movingCellColIndex: 0,
          movingCellRowSpan: 0,
          movingCellColSpan: 0,
          sectionX: 0,
          sectionY: 0,
          cellX: 0,
          cellY: 0,
          startScrollX: 0,
          startScrollY: 0,
          diffScrollX: 0,
          diffScrollY: 0,
          movingStartX: 0,
          movingStartY: 0,
          diffMovingX: 0,
          diffMovingY: 0,
          movingX: 0,
          movingY: 0,
          gap: 20,
        }),
        computed: {
          resizeMouseX() {
            return this.resizeDimensions && (this.resizeDimensions.x + this.resizeDimensions.width);
          },
          resizeMouseY() {
            return this.resizeDimensions && (this.resizeDimensions.y + this.resizeDimensions.height);
          },
          selectMouseX0() {
            return Math.min(this.movingStartX - this.diffScrollX, this.movingX - this.diffScrollX) + this.parentAppX;
          },
          selectMouseX1() {
            return Math.max(this.movingStartX - this.diffScrollX, this.movingX - this.diffScrollX) + this.parentAppX;
          },
          selectMouseY0() {
            return Math.min(this.movingStartY - this.diffScrollY, this.movingY - this.diffScrollY) + this.parentAppY;
          },
          selectMouseY1() {
            return Math.max(this.movingStartY - this.diffScrollY, this.movingY - this.diffScrollY) + this.parentAppY;
          },
          parentAppX() {
            return this.$root.parentAppDimensions?.x || 0;
          },
          parentAppY() {
            return this.$root.parentAppDimensions?.y || 0;
          },
          isMovingCell() {
            return this.$root.movingCell && this.$root.movingX && this.$root.movingY && true || false;
          },
          defaultContainer() {
            return this.containerTypes.find(extension => extension.type === 'container');
          },
        },
        created() {
          document.addEventListener('extension-layout-editor-container-updated', this.refreshContainerTypes);
          document.addEventListener('drawerOpened', this.setDrawerOpened);
          document.addEventListener('drawerClosed', this.setDrawerClosed);
        },
        methods: {
          setDrawerOpened() {
            this.drawerOpened++;
          },
          setDrawerClosed() {
            this.drawerOpened--;
          },
          refreshContainerTypes() {
            this.containerTypes = extensionRegistry.loadExtensions('layout-editor', 'container');
          },
          updateParentAppDimensions() {
            this.parentAppDimensions = document.querySelector('#layoutEditor').getBoundingClientRect();
          },
        },
      }, `#${appId}`, 'Layout Editor')
    )
    .finally(() => Vue.prototype.$utils.includeExtensions('LayoutEditorExtension'));
}
