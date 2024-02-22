/*
 * Copyright (C) 2023 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import SiteManagement from './components/SiteManagement.vue';
import SitesList from './components/SitesList.vue';
import SiteCard from './components/SiteCard.vue';
import SiteCardMenu from './components/SiteCardMenu.vue';
import SitePropertiesDrawer from './components/SitePropertiesDrawer.vue';
import SiteBannerSelector from './components/SiteBannerSelector.vue';
import SiteToolbar from './components/SiteToolbar.vue';
import SiteTemplateDrawer from './components/SiteTemplateDrawer.vue';


const components = {
  'site-management': SiteManagement,
  'site-management-sites-list': SitesList,
  'site-management-site-card': SiteCard,
  'site-management-site-card-menu': SiteCardMenu,
  'site-properties-drawer': SitePropertiesDrawer ,
  'site-management-banner-selector': SiteBannerSelector ,
  'site-management-toolbar': SiteToolbar ,
  'site-template-drawer': SiteTemplateDrawer ,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

import * as siteManagementService from './siteManagementService.js';

if (!Vue.prototype.$siteManagementService) {
  window.Object.defineProperty(Vue.prototype, '$siteManagementService', {
    value: siteManagementService,
  });
}