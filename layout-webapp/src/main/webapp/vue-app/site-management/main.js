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
  const components = extensionRegistry.loadComponents('siteManagement');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

Vue.use(Vuetify);
const vuetify = new Vuetify(eXo.env.portal.vuetifyPreset);

const appId = 'siteManagement';

//getting language of the PLF
const lang = eXo?.env.portal.language || 'en';

//should expose the locale ressources as REST API
const urls = [`${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.layout.SiteManagementPortlet-${lang}.json`,
  `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.layout.SiteNavigationPortlet-${lang}.json`];

export function init() {
  exoi18n.loadLanguageAsync(lang, urls).then(i18n => {
    // init Vue app when locale ressources are ready
    Vue.createApp({
      template: `<site-management id="${appId}"/>`,
      vuetify,
      i18n},
    `#${appId}`, 'site-management');
  });
}