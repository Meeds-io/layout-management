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
import PageSpacePreviewButton from './components/toolbar/actions/PageSpacePreviewButton.vue';
import PagePropertiesButton from './components/toolbar/actions/PagePropertiesButton.vue';

import Content from './components/content/Content.vue';

import EditSectionDrawer from './components/drawer/EditSectionDrawer.vue';
import AddSectionDrawer from './components/drawer/AddSectionDrawer.vue';
import EditPageDrawer from './components/drawer/EditPageDrawer.vue';

const components = {
  'layout-editor': LayoutEditor,
  'layout-editor-toolbar': Toolbar,
  'layout-editor-toolbar-save-button': SaveButton,
  'layout-editor-toolbar-save-draft-button': SaveDraftButton,
  'layout-editor-toolbar-save-as-template-button': SaveAsTemplateButton,
  'layout-editor-toolbar-save-template-button': SaveTemplateButton,
  'layout-editor-toolbar-history-buttons': HistoryButtons,
  'layout-editor-toolbar-page-preview-button': PagePreviewButton,
  'layout-editor-toolbar-page-space-preview-button': PageSpacePreviewButton,
  'layout-editor-toolbar-page-properties-button': PagePropertiesButton,
  'layout-editor-toolbar-mobile-preview-button': MobilePreviewButton,
  'layout-editor-content': Content,
  'layout-editor-section-add-drawer': AddSectionDrawer,
  'layout-editor-section-edit-drawer': EditSectionDrawer,
  'layout-editor-page-edit-drawer': EditPageDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
