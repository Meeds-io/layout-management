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

import PortletsManagement from './components/PortletsManagement.vue';

import Toolbar from './components/header/Toolbar.vue';

import InstanceMain from './components/instances/Main.vue';
import InstanceCategories from './components/instances/Categories.vue';
import InstanceCategoryMenu from './components/instances/CategoryMenu.vue';
import InstanceCategoryItem from './components/instances/CategoryItem.vue';

import InstanceList from './components/instances/List.vue';
import InstanceItem from './components/instances/Item.vue';
import InstanceMenu from './components/instances/Menu.vue';

import PortletList from './components/portlets/List.vue';
import PortletItem from './components/portlets/Item.vue';
import PortletMenu from './components/portlets/Menu.vue';

const components = {
  'portlets-management': PortletsManagement,

  'portlets-toolbar': Toolbar,

  'portlets-instance-main': InstanceMain,

  'portlets-instance-categories': InstanceCategories,
  'portlets-instance-category': InstanceCategoryItem,
  'portlets-instance-category-menu': InstanceCategoryMenu,

  'portlets-instance-list': InstanceList,
  'portlets-instance-item': InstanceItem,
  'portlets-instance-item-menu': InstanceMenu,

  'portlets-list': PortletList,
  'portlets-item': PortletItem,
  'portlets-item-menu': PortletMenu,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
