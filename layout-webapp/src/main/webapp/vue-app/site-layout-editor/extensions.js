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

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 400,
  type: 'Site',
  isValid: container => container?.template === 'system:/groovy/portal/webui/container/UISiteLayout.gtmpl',
  containerType: 'site-layout-editor-container-site',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 500,
  type: 'PageBody',
  isValid: container => container?.template === 'PageBody',
  containerType: 'site-layout-editor-container-page-body',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 600,
  type: 'Sibebar',
  isValid: container => container?.template === 'Sidebar',
  containerType: 'site-layout-editor-container-site-sidebar-section',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 700,
  type: 'SidebarCell',
  isValid: container => container?.template === 'SidebarCell',
  containerType: 'site-layout-editor-container-site-sidebar-cell',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 800,
  type: 'Banner',
  isValid: container => container?.template === 'Banner',
  containerType: 'site-layout-editor-container-site-banner-section',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 900,
  type: 'BannerCell',
  isValid: container => container?.template === 'BannerCell',
  containerType: 'site-layout-editor-container-site-banner-cell',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 1000,
  type: 'SiteMiddleBody',
  isValid: container => container?.template === 'SiteMiddleBody',
  containerType: 'site-layout-editor-container-site-middle',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 1100,
  type: 'SiteMiddleCenterBody',
  isValid: container => container?.template === 'SiteMiddleCenterBody',
  containerType: 'site-layout-editor-container-site-middle-center',
});
