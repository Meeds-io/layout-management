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
import ManagePermissionsDrawer from './components/manage-permissions/ManagePermissionsDrawer.vue';
import ManageEditPermission from './components/manage-permissions/ManageEditPermission.vue';
import ManageAccessPermission from './components/manage-permissions/ManageAccessPermission.vue';
import ManagePermissionItem from './components/manage-permissions/ManagePermissionItem.vue';
import PermissionTypeSelector from './components/manage-permissions/PermissionTypeSelector.vue';
import SiteNavigationDrawer from './components/site-navigation/SiteNavigationDrawer.vue';
import SiteNavigationNodesList from './components/site-navigation/NodesList.vue';
import SiteNavigationNodeItem from './components/site-navigation/NodeItem.vue';
import SiteNavigationNodeItemMenu from './components/site-navigation/NodeItemMenu.vue';
import SiteNavigationNodeDrawer from './components/site-navigation/SiteNavigationNodeDrawer.vue';
import SiteNavigationElementDrawer from './components/site-navigation/SiteNavigationElementDrawer.vue';
import SiteNavigationPageElement from './components/site-navigation/SiteNavigationPageElement.vue';
import SiteNavigationNewPageElement from './components/site-navigation/SiteNavigationNewPageElement.vue';
import SiteNavigationScheduleDatePickers from './components/site-navigation/SiteNavigationScheduleDatePickers.vue';
import SiteNavigationExistingPageElement from './components/site-navigation/SiteNavigationExistingPageElement.vue';
import SiteNavigationPageSuggester from './components/site-navigation/SiteNavigationPageSuggester.vue';
import SiteNavigationSiteSuggester from './components/site-navigation/SiteNavigationSiteSuggester.vue';
import NodeIconPickerDrawer from './components/site-navigation/NodeIconPickerDrawer.vue';

const components = {
  'manage-permissions-drawer': ManagePermissionsDrawer,
  'manage-edit-permission': ManageEditPermission,
  'manage-access-permission': ManageAccessPermission,
  'manage-permission-item': ManagePermissionItem,
  'permission-type-selector': PermissionTypeSelector,
  'site-navigation-drawer': SiteNavigationDrawer,
  'site-navigation-nodes-list': SiteNavigationNodesList,
  'site-navigation-node-item': SiteNavigationNodeItem,
  'site-navigation-node-item-menu': SiteNavigationNodeItemMenu,
  'site-navigation-node-drawer': SiteNavigationNodeDrawer,
  'site-navigation-element-drawer': SiteNavigationElementDrawer,
  'site-navigation-page-element': SiteNavigationPageElement,
  'site-navigation-new-page-element': SiteNavigationNewPageElement,
  'site-navigation-schedule-date-pickers': SiteNavigationScheduleDatePickers,
  'site-navigation-existing-page-element': SiteNavigationExistingPageElement,
  'site-navigation-page-suggester': SiteNavigationPageSuggester,
  'site-navigation-site-suggester': SiteNavigationSiteSuggester,
  'node-icon-picker-drawer': NodeIconPickerDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

import * as siteNavigationService from './js/siteNavigationService.js';

if (!Vue.prototype.$siteNavigationService) {
  window.Object.defineProperty(Vue.prototype, '$siteNavigationService', {
    value: siteNavigationService,
  });
}