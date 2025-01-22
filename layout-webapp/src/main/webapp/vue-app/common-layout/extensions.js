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

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 1500,
  type: 'default',
  isValid: container => container?.template === 'Container',
  containerType: 'layout-editor-container',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 300,
  type: 'grid-section',
  isValid: container => container?.template === 'GridContainer' || container?.template === 'FlexContainer',
  containerType: 'layout-editor-container-section',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 400,
  type: 'cell',
  isValid: container => container?.template === 'CellContainer',
  containerType: 'layout-editor-container-cell',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 500,
  type: 'Site',
  isValid: container => container?.template === 'Site',
  containerType: 'layout-editor-container-site',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 500,
  type: 'PageBody',
  isValid: container => container?.template === 'PageBody',
  containerType: 'layout-editor-container-page-body',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 600,
  type: 'Sibebar',
  isValid: container => container?.template === 'Sidebar',
  containerType: 'layout-editor-container-site-sidebar-section',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 700,
  type: 'SidebarCell',
  isValid: container => container?.template === 'SidebarCell',
  containerType: 'layout-editor-container-site-sidebar-cell',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 800,
  type: 'Banner',
  isValid: container => container?.template === 'Banner',
  containerType: 'layout-editor-container-site-banner-section',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 900,
  type: 'BannerCell',
  isValid: container => container?.template === 'BannerCell',
  containerType: 'layout-editor-container-site-banner-cell',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 1000,
  type: 'SiteMiddleBody',
  isValid: container => container?.template === 'SiteMiddleBody',
  containerType: 'layout-editor-container-site-middle-section',
});

extensionRegistry.registerExtension('layout-editor', 'container', {
  rank: 1500,
  type: 'application',
  isValid: container => !container.type && !container.template,
  containerType: 'layout-editor-container-application',
});
