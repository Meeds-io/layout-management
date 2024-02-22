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

import './initComponents.js';

// get overridden components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('siteNavigation');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

extensionRegistry.registerComponent('manageSpaceActions', 'manage-space-actions', {
  id: 'manage-space-actions',
  vueComponent: Vue.options.components['site-navigation-button'],
  rank: 20,
});

extensionRegistry.registerComponent('manageSpaceDrawers', 'manage-space-drawers', {
  id: 'manage-space-drawers',
  vueComponent: Vue.options.components['site-navigation-drawers-actions'],
  rank: 20,
});

Vue.use(Vuetify);
const vuetify = new Vuetify(eXo.env.portal.vuetifyPreset);

const appId = 'siteNavigation';

//getting language of the PLF
const lang = eXo && eXo.env.portal.language || 'en';

//should expose the locale ressources as REST API
const urls = [`${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.layout.SiteNavigationPortlet-${lang}.json`];

exoi18n.loadLanguageAsync(lang, urls);

export function init(canManageSiteNavigation) {
  exoi18n.loadLanguageAsync(lang, urls).then(i18n => {
    // init Vue app when locale ressources are ready
    Vue.createApp({
      template: `<site-navigation id="${appId}" :can-manage-site-navigation="${canManageSiteNavigation}"/>`,
      vuetify,
      i18n},
    `#${appId}`, 'site-navigation');
  });
}