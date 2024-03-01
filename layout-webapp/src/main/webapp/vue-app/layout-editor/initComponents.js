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

import Content from './components/content/Content.vue';

import SectionBorderMenu from './components/content/common/SectionBorderMenu.vue';
import ContainerExtension from './components/content/common/ContainerExtension.vue';
import ContainerBase from './components/content/common/ContainerBase.vue';

import Page from './components/content/container/Page.vue';
import Container from './components/content/container/Container.vue';
import Row from './components/content/container/Row.vue';
import Column from './components/content/container/Column.vue';

import EditSectionDrawer from './components/content/drawer/EditSectionDrawer.vue';

const components = {
  'layout-editor': LayoutEditor,
  'layout-editor-toolbar': Toolbar,
  'layout-editor-toolbar-save-button': SaveButton,
  'layout-editor-content': Content,
  'layout-editor-container-container-extension': ContainerExtension,
  'layout-editor-container-container-base': ContainerBase,
  'layout-editor-container-page': Page,
  'layout-editor-container-container': Container,
  'layout-editor-container-row': Row,
  'layout-editor-container-column': Column,
  'layout-editor-section-border-menu': SectionBorderMenu,
  'layout-editor-section-edit-drawer': EditSectionDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
