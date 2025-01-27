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
import SaveDraftButton from './components/toolbar/actions/SaveDraftButton.vue';
import HistoryButtons from './components/toolbar/actions/HistoryButtons.vue';
import MobilePreviewButton from './components/toolbar/actions/MobilePreviewButton.vue';
import SitePropertiesButton from './components/toolbar/actions/SitePropertiesButton.vue';
import SiteEditSectionsButton from './components/toolbar/actions/SiteEditSectionsButton.vue';

import Site from './components/content/container/Site.vue';
import SiteBannerSection from './components/content/container/SiteBannerSection.vue';
import SiteBannerCell from './components/content/container/SiteBannerCell.vue';
import SiteSidebarSection from './components/content/container/SiteSidebarSection.vue';
import SiteSidebarCell from './components/content/container/SiteSidebarCell.vue';
import SiteMiddle from './components/content/container/SiteMiddle.vue';
import SiteMiddleCenter from './components/content/container/SiteMiddleCenter.vue';
import PageBody from './components/content/container/PageBody.vue';

import Content from './components/content/Content.vue';
import SidebarSectionMenu from './components/content/menu/SidebarSectionMenu.vue';
import BannerSectionMenu from './components/content/menu/BannerSectionMenu.vue';

import EditSiteSectionsDrawer from './components/drawer/EditSiteSectionsDrawer.vue';
import EditSiteSidebarDrawer from './components/drawer/EditSiteSidebarDrawer.vue';
import EditSiteBannerDrawer from './components/drawer/EditSiteBannerDrawer.vue';
import EditApplicationDrawer from './components/drawer/EditApplicationDrawer.vue';
import EditSitePagesDrawer from './components/drawer/EditSitePagesDrawer.vue';

const components = {
  'site-layout-editor': SiteLayoutEditor,

  'site-layout-editor-toolbar': Toolbar,
  'site-layout-editor-toolbar-save-button': SaveButton,
  'site-layout-editor-toolbar-save-draft-button': SaveDraftButton,
  'site-layout-editor-toolbar-history-buttons': HistoryButtons,
  'site-layout-editor-toolbar-properties-button': SitePropertiesButton,
  'site-layout-editor-toolbar-edit-sections-button': SiteEditSectionsButton,
  'site-layout-editor-toolbar-mobile-preview-button': MobilePreviewButton,

  'site-layout-editor-content': Content,
  'site-layout-editor-sidebar-section-menu': SidebarSectionMenu,
  'site-layout-editor-banner-section-menu': BannerSectionMenu,

  'site-layout-editor-container-page-body': PageBody,
  'site-layout-editor-container-site': Site,
  'site-layout-editor-container-site-banner-section': SiteBannerSection,
  'site-layout-editor-container-site-banner-cell': SiteBannerCell,
  'site-layout-editor-container-site-sidebar-section': SiteSidebarSection,
  'site-layout-editor-container-site-sidebar-cell': SiteSidebarCell,
  'site-layout-editor-container-site-middle': SiteMiddle,
  'site-layout-editor-container-site-middle-center': SiteMiddleCenter,

  'site-layout-editor-sidebar-section-drawer': EditSiteSidebarDrawer,
  'site-layout-editor-banner-section-drawer': EditSiteBannerDrawer,
  'site-layout-editor-application-edit-drawer': EditApplicationDrawer,
  'site-layout-editor-edit-sections-drawer': EditSiteSectionsDrawer,
  'site-layout-editor-edit-site-pages-drawer': EditSitePagesDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
