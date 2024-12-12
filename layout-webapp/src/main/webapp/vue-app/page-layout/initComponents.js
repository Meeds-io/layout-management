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

import PageLayout from './components/PageLayout.vue';

import ContainerExtension from './components/base/ContainerExtension.vue';
import ContainerBase from './components/base/ContainerBase.vue';

import PageContainer from './components/container/PageContainer.vue';
import DynamicSection from './components/container/DynamicSection.vue';
import FixedSection from './components/container/FixedSection.vue';
import CellContainer from './components/container/CellContainer.vue';
import Container from './components/container/Container.vue';
import Application from './components/container/Application.vue';

const components = {
  'page-layout': PageLayout,

  'page-layout-container-extension': ContainerExtension,
  'page-layout-container-base': ContainerBase,

  'page-layout-page-container': PageContainer,
  'page-layout-dynamic-section': DynamicSection,
  'page-layout-fixed-section': FixedSection,
  'page-layout-cell-container': CellContainer,
  'page-layout-container': Container,
  'page-layout-application': Application,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
