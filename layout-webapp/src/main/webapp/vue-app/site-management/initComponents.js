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

import SiteManagement from './components/SiteManagement.vue';

import SiteToolbar from './components/header/SiteToolbar.vue';

import SiteList from './components/main/List.vue';
import SiteItem from './components/main/Item.vue';
import SiteItemMenu from './components/main/Menu.vue';

import SiteFormDrawer from './components/drawer/SiteFormDrawer.vue';
import SiteTemplateDrawer from './components/drawer/SiteTemplateDrawer.vue';

import SiteBanner from './components/form/SiteBanner.vue';
import SiteTemplate from './components/form/SiteTemplate.vue';

const components = {
  'site-management': SiteManagement,

  'site-management-toolbar': SiteToolbar ,

  'site-management-list': SiteList,
  'site-management-item': SiteItem,
  'site-management-item-menu': SiteItemMenu,

  'site-form-drawer': SiteFormDrawer,
  'site-template-drawer': SiteTemplateDrawer,

  'site-management-banner': SiteBanner,
  'site-management-template': SiteTemplate,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
