/*
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
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

import SectionEditor from './components/SectionEditor.vue';

import Toolbar from './components/toolbar/Toolbar.vue';
import SaveButton from './components/toolbar/actions/SaveButton.vue';
import HistoryButtons from './components/toolbar/actions/HistoryButtons.vue';
import MobilePreviewButton from './components/toolbar/actions/MobilePreviewButton.vue';

import Content from './components/content/Content.vue';

import SectionTemplateDrawer from '../section-templates/components/main/drawer/SectionTemplateDrawer.vue';
import SectionTemplatePreview from '../section-templates/components/main/form/SectionTemplatePreview.vue';

const components = {
  'section-editor': SectionEditor,
  'section-editor-toolbar': Toolbar,
  'section-editor-toolbar-save-button': SaveButton,
  'section-editor-toolbar-history-buttons': HistoryButtons,
  'section-editor-toolbar-mobile-preview-button': MobilePreviewButton,
  'section-editor-content': Content,
  'section-template-drawer': SectionTemplateDrawer,
  'section-template-preview': SectionTemplatePreview,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
