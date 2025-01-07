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

import SectionTemplateManagement from './components/SectionTemplateManagement.vue';

import SectionTemplateToolbar from './components/header/Toolbar.vue';
import SectionTemplateList from './components/instances/List.vue';
import SectionTemplateItem from './components/instances/Item.vue';
import SectionTemplateMenu from './components/instances/Menu.vue';
import AddSectionDrawer from './components/instances/drawer/AddSectionDrawer.vue';
import SectionTemplateDrawer from './components/instances/drawer/SectionTemplateDrawer.vue';
import SectionTemplatePreview from './components/instances/form/SectionTemplatePreview.vue';

import SectionFlexEditor from '../layout-editor/components/content/common/SectionFlexEditor.vue';
import SectionGridEditor from '../layout-editor/components/content/common/SectionGridEditor.vue';

const components = {
  'section-template-management': SectionTemplateManagement,
  'section-template-toolbar': SectionTemplateToolbar,
  'section-template-list': SectionTemplateList,
  'section-template-item': SectionTemplateItem,
  'section-template-menu': SectionTemplateMenu,
  'section-template-add-drawer': AddSectionDrawer,
  'section-template-drawer': SectionTemplateDrawer,
  'section-template-preview': SectionTemplatePreview,
  'section-template-flex-editor': SectionFlexEditor,
  'section-template-grid-editor': SectionGridEditor,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
