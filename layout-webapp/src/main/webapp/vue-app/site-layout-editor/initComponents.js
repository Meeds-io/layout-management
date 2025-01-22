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

import SiteLayoutEditor from './components/SiteLayoutEditor.vue';

import Toolbar from './components/toolbar/Toolbar.vue';
import SaveButton from './components/toolbar/actions/SaveButton.vue';
import HistoryButtons from './components/toolbar/actions/HistoryButtons.vue';
import MobilePreviewButton from './components/toolbar/actions/MobilePreviewButton.vue';
import SitePropertiesButton from './components/toolbar/actions/SitePropertiesButton.vue';
import SiteEditSectionsButton from './components/toolbar/actions/SiteEditSectionsButton.vue';

import Content from './components/content/Content.vue';
import SidebarSectionMenu from './components/content/common/SidebarSectionMenu.vue';
import BannerSectionMenu from './components/content/common/BannerSectionMenu.vue';

const components = {
  'site-layout-editor': SiteLayoutEditor,

  'site-layout-editor-toolbar': Toolbar,
  'site-layout-editor-toolbar-save-button': SaveButton,
  'site-layout-editor-toolbar-history-buttons': HistoryButtons,
  'site-layout-editor-toolbar-properties-button': SitePropertiesButton,
  'site-layout-editor-toolbar-edit-sections-button': SiteEditSectionsButton,
  'site-layout-editor-toolbar-mobile-preview-button': MobilePreviewButton,

  'site-layout-editor-content': Content,
  'site-layout-editor-sidebar-section-menu': SidebarSectionMenu,
  'site-layout-editor-banner-section-menu': BannerSectionMenu,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
