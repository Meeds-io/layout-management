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

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 1500,
  type: 'default',
  isValid: container => container?.template === 'Container',
  containerType: 'page-layout-container',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 100,
  type: 'cell',
  isValid: container => container.template === 'CellContainer',
  containerType: 'page-layout-cell-container',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 200,
  type: 'section',
  isValid: container => container.template === 'FlexContainer',
  containerType: 'page-layout-dynamic-section',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 300,
  type: 'section',
  isValid: container => container.template === 'GridContainer',
  containerType: 'page-layout-fixed-section',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 400,
  type: 'application',
  isValid: container => !container.template,
  containerType: 'page-layout-application',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 500,
  type: 'PageBody',
  isValid: container => container?.template === 'PageBody' || container?.template === 'system:/groovy/portal/webui/container/UIPageLayout.gtmpl',
  containerType: 'page-layout-page-body-container',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 600,
  type: 'Sibebar',
  isValid: container => container?.template === 'Sidebar',
  containerType: 'page-layout-sidebar-section',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 700,
  type: 'SidebarCell',
  isValid: container => container?.template === 'SidebarCell',
  containerType: 'page-layout-sidebar-cell',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 800,
  type: 'Banner',
  isValid: container => container?.template === 'Banner',
  containerType: 'page-layout-banner-section',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 900,
  type: 'BannerCell',
  isValid: container => container?.template === 'BannerCell',
  containerType: 'page-layout-banner-cell',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 1000,
  type: 'SiteMiddleBody',
  isValid: container => container?.template === 'SiteMiddleBody',
  containerType: 'page-layout-site-middle-container',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 1100,
  type: 'SiteMiddleCenterBody',
  isValid: container => container?.template === 'SiteMiddleCenterBody',
  containerType: 'page-layout-site-middle-center-container',
});

extensionRegistry.registerExtension('page-layout', 'container', {
  rank: 1200,
  type: 'Site',
  isValid: container => container?.template === 'system:/groovy/portal/webui/container/UISiteLayout.gtmpl',
  containerType: 'page-layout-site-container',
});
