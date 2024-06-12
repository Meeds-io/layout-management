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

import '../common-page-layout/main.js';
import './initComponents.js';
import './extensions.js';

// get overridden components if exists
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('PageLayout');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}

const appId = 'PageLayout';
export function init(pageRef) {
  const containerTypes = extensionRegistry.loadExtensions('page-layout', 'container');
  Vue.createApp({
    template: `<page-layout id="${appId}"/>`,
    vuetify: Vue.prototype.vuetifyOptions,
    data: () => ({
      pageRef,
      containerTypes,
      nodeUri: window.location.pathname.replace('/portal/', '/'),
    }),
    computed: {
      defaultContainer() {
        return this.containerTypes.find(extension => extension.type === 'default');
      },
    },
    created() {
      document.addEventListener('extension-page-layout-container-updated', this.refreshContainerTypes);
    },
    mounted() {
      this.$el?.closest?.('.PORTLET-FRAGMENT')?.classList?.remove?.('PORTLET-FRAGMENT');
    },
    methods: {
      refreshContainerTypes() {
        this.containerTypes = extensionRegistry.loadExtensions('page-layout', 'container');
      },
    },
  }, `#${appId}`, 'Page Layout');
  Vue.prototype.$utils.includeExtensions('PageLayoutExtension');
}
