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

import SectionFlexEditor from './components/SectionFlexEditor.vue';
import SectionGridEditor from './components/SectionGridEditor.vue';

import SectionTemplatePreview from './components/SectionTemplatePreview.vue';

import SectionTemplateDrawer from './components/SectionTemplateDrawer.vue';
import SectionTemplateSaveAsDrawer from './components/SectionTemplateSaveAsDrawer.vue';

const components = {
  'section-template-flex-editor': SectionFlexEditor,
  'section-template-grid-editor': SectionGridEditor,
  'section-template-preview': SectionTemplatePreview,

  'section-template-drawer': SectionTemplateDrawer,
  'section-template-save-as-drawer': SectionTemplateSaveAsDrawer,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
