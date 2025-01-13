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

import SiteTemplatePreview from './components/SiteTemplatePreview.vue';

import SiteTemplateDrawer from './components/SiteTemplateDrawer.vue';
import SiteTemplateSaveAsDrawer from './components/SiteTemplateSaveAsDrawer.vue';

const components = {
  'site-template-preview': SiteTemplatePreview,

  'site-template-drawer': SiteTemplateDrawer,
  'site-template-save-as-drawer': SiteTemplateSaveAsDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
