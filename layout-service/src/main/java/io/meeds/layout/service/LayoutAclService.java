/**
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.layout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;

@Service
public class LayoutAclService {

  @Autowired
  private UserACL          userAcl;

  @Autowired
  private LayoutService    layoutService;

  @Autowired
  private Authenticator    authenticator;

  private IdentityRegistry identityRegistry;

  @PostConstruct
  public void init() {
    // Can't be autowired from Kernel IoC, thus inject it once Spring Bean
    // initialized
    identityRegistry = ExoContainerContext.getService(IdentityRegistry.class);
  }

  public boolean canAddSite(String username) {
    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.hasCreatePortalPermission();
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public boolean canEditSite(SiteKey siteKey, String username) {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      return false;
    }
    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.hasEditPermission(portalConfig);
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public boolean canEditNavigation(SiteKey siteKey, String username) {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      return false;
    }

    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.hasEditPermission(portalConfig) || userAcl.hasEditPermissionOnNavigation(siteKey);
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public boolean canViewNavigation(SiteKey siteKey, PageKey pageKey, String username) {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      return false;
    }
    Page page = pageKey == null ? null : layoutService.getPage(pageKey);
    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.hasAccessPermission(portalConfig) && (page == null || userAcl.hasPermission(page));
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public boolean canViewPage(PageKey pageKey, String username) {
    Page page = layoutService.getPage(pageKey);
    if (page == null) {
      return false;
    }

    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.hasPermission(page);
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public boolean canEditPage(PageKey pageKey, String username) {
    Page page = layoutService.getPage(pageKey);
    if (page == null) {
      return false;
    }

    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.hasEditPermission(page);
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public boolean isAdministrator(String username) {
    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(getConversationState(username));
    try {
      return userAcl.isSuperUser() || userAcl.isUserInGroup(getAdministratorsGroup());
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  public String getAdministratorsGroup() {
    return userAcl.getAdminGroups();
  }

  private ConversationState getConversationState(String username) {
    return new ConversationState(getUserIdentity(username));
  }

  @SneakyThrows
  private Identity getUserIdentity(String username) {
    Identity identity = identityRegistry.getIdentity(username);
    if (identity != null) {
      return identity;
    } else {
      return authenticator.createIdentity(username);
    }
  }

}
