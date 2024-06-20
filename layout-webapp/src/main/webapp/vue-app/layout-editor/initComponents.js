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

import LayoutEditor from './components/LayoutEditor.vue';

import Toolbar from './components/toolbar/Toolbar.vue';
import SaveButton from './components/toolbar/actions/SaveButton.vue';
import SaveDraftButton from './components/toolbar/actions/SaveDraftButton.vue';
import SaveAsTemplateButton from './components/toolbar/actions/SaveAsTemplateButton.vue';
import SaveTemplateButton from './components/toolbar/actions/SaveTemplateButton.vue';
import HistoryButtons from './components/toolbar/actions/HistoryButtons.vue';
import MobilePreviewButton from './components/toolbar/actions/MobilePreviewButton.vue';
import PagePreviewButton from './components/toolbar/actions/PagePreviewButton.vue';
import PagePropertiesButton from './components/toolbar/actions/PagePropertiesButton.vue';

import BorderRadiusSelector from './components/form/BorderRadiusSelector.vue';
import NumberInput from './components/form/NumberInput.vue';
import ColorPicker from './components/form/ColorPicker.vue';
import BackgroundImageAttachment from './components/form/BackgroundImageAttachment.vue';
import BackgroundInput from './components/form/BackgroundInput.vue';
import TextColorInput from './components/form/TextColorInput.vue';

import Content from './components/content/Content.vue';

import CellsDropBox from './components/content/base/CellsDropBox.vue';
import CellsSelectionBox from './components/content/base/CellsSelectionBox.vue';
import ContainerExtension from './components/content/base/ContainerExtension.vue';
import ContainerBase from './components/content/base/ContainerBase.vue';

import SectionMenu from './components/content/common/SectionMenu.vue';
import SectionSelectionGrid from './components/content/common/SectionSelectionGrid.vue';
import SectionSelectionGridCell from './components/content/common/SectionSelectionGridCell.vue';
import SectionGridEditor from './components/content/common/SectionGridEditor.vue';
import SectionFlexEditor from './components/content/common/SectionFlexEditor.vue';
import ApplicationCategoryCard from './components/content/common/ApplicationCategoryCard.vue';
import ApplicationCard from './components/content/common/ApplicationCard.vue';
import ApplicationMenu from './components/content/common/ApplicationMenu.vue';
import CellResizeButton from './components/content/common/CellResizeButton.vue';

import Container from './components/content/container/Container.vue';
import Section from './components/content/container/Section.vue';
import Cell from './components/content/container/Cell.vue';
import Application from './components/content/container/Application.vue';

import EditSectionDrawer from './components/drawer/EditSectionDrawer.vue';
import AddSectionDrawer from './components/drawer/AddSectionDrawer.vue';
import SelectApplicationCategoryDrawer from './components/drawer/SelectApplicationCategoryDrawer.vue';
import AddApplicationDrawer from './components/drawer/AddApplicationDrawer.vue';
import EditApplicationDrawer from './components/drawer/EditApplicationDrawer.vue';
import EditPageDrawer from './components/drawer/EditPageDrawer.vue';

import EditPortletDialog from './components/dialog/EditPortletDialog.vue';

import Coediting from './components/coediting/Coediting.vue';

const components = {
  'layout-editor': LayoutEditor,
  'layout-editor-toolbar': Toolbar,
  'layout-editor-toolbar-save-button': SaveButton,
  'layout-editor-toolbar-save-draft-button': SaveDraftButton,
  'layout-editor-toolbar-save-as-template-button': SaveAsTemplateButton,
  'layout-editor-toolbar-save-template-button': SaveTemplateButton,
  'layout-editor-toolbar-history-buttons': HistoryButtons,
  'layout-editor-toolbar-page-preview-button': PagePreviewButton,
  'layout-editor-toolbar-page-properties-button': PagePropertiesButton,
  'layout-editor-toolbar-mobile-preview-button': MobilePreviewButton,
  'layout-editor-content': Content,
  'layout-editor-color-picker': ColorPicker,
  'layout-editor-border-radius-selector': BorderRadiusSelector,
  'layout-editor-number-input': NumberInput,
  'layout-editor-container': Container,
  'layout-editor-container-extension': ContainerExtension,
  'layout-editor-container-base': ContainerBase,
  'layout-editor-container-section': Section,
  'layout-editor-container-cell': Cell,
  'layout-editor-container-application': Application,
  'layout-editor-section-selection-grid': SectionSelectionGrid,
  'layout-editor-section-selection-grid-cell': SectionSelectionGridCell,
  'layout-editor-section-grid-editor': SectionGridEditor,
  'layout-editor-section-flex-editor': SectionFlexEditor,
  'layout-editor-section-add-drawer': AddSectionDrawer,
  'layout-editor-section-edit-drawer': EditSectionDrawer,
  'layout-editor-section-menu': SectionMenu,
  'layout-editor-application-category-select-drawer': SelectApplicationCategoryDrawer,
  'layout-editor-application-add-drawer': AddApplicationDrawer,
  'layout-editor-application-edit-drawer': EditApplicationDrawer,
  'layout-editor-page-edit-drawer': EditPageDrawer,
  'layout-editor-portlet-edit-dialog': EditPortletDialog,
  'layout-editor-background-image-attachment': BackgroundImageAttachment,
  'layout-editor-background-input': BackgroundInput,
  'layout-editor-text-color-input': TextColorInput,
  'layout-editor-application-card': ApplicationCard,
  'layout-editor-application-category-card': ApplicationCategoryCard,
  'layout-editor-application-menu': ApplicationMenu,
  'layout-editor-cell-resize-button': CellResizeButton,
  'layout-editor-cells-selection-box': CellsSelectionBox,
  'layout-editor-cells-drop-box': CellsDropBox,
  // TODO : to define in social to be a reusable component
  'coediting': Coediting,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
