/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.layout.utils;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.Identity;

import java.util.Locale;
import java.util.ResourceBundle;

public class SiteNavigationUtils {

  private static final String EDITOR_MEMBERSHIP_NAME          = "editor";

  private static final String MANAGER_MEMBERSHIP_NAME         = "manager";

  private static final String PLATFORM_WEB_CONTRIBUTORS_GROUP = "/platform/web-contributors";

  private static final String PLATFORM_ADMINISTRATORS_GROUP   = "/platform/administrators";

  private SiteNavigationUtils() {
  }

  public static boolean canEditNavigation(Identity currentIdentity, NodeData nodeData) {
    SiteKey siteKey = nodeData.getSiteKey();
    LayoutService layoutService = CommonsUtils.getService(LayoutService.class);
    PortalConfig sitePortalConfig = layoutService.getPortalConfig(siteKey);
    UserACL userACL = CommonsUtils.getService(UserACL.class);
    return (currentIdentity.isMemberOf(PLATFORM_WEB_CONTRIBUTORS_GROUP, EDITOR_MEMBERSHIP_NAME)
        || currentIdentity.isMemberOf(PLATFORM_ADMINISTRATORS_GROUP, MANAGER_MEMBERSHIP_NAME))
        && (userACL.hasEditPermissionOnPortal(siteKey.getTypeName(), siteKey.getName(), sitePortalConfig.getEditPermission())
            || userACL.hasEditPermissionOnNavigation(siteKey));
  }

  public static boolean canViewNavigation(NodeData nodeData) {
    SiteKey siteKey = nodeData.getSiteKey();
    LayoutService layoutService = CommonsUtils.getService(LayoutService.class);
    PortalConfig sitePortalConfig = layoutService.getPortalConfig(siteKey);
    Page page = null;
    if (nodeData.getState().getPageRef() != null) {
      page = layoutService.getPage(nodeData.getState().getPageRef());
    }
    UserACL userACL = CommonsUtils.getService(UserACL.class);
    return userACL.hasAccessPermission(sitePortalConfig)
           && (page == null || userACL.hasPermission(page));
  }

  public static boolean canEditPage(PageContext pageContext) {
    UserACL userACL = CommonsUtils.getService(UserACL.class);
    return userACL.hasEditPermission(pageContext);
  }

  public static boolean isAdministrator() {
    UserACL userACL = CommonsUtils.getService(UserACL.class);
    return userACL.isSuperUser() || userACL.isUserInGroup(PLATFORM_ADMINISTRATORS_GROUP);
  }

  public static String getI18NLabel(Locale userLocale, String label) {
    ResourceBundleService resourceBundleService = CommonsUtils.getService(ResourceBundleService.class);
    if (userLocale == null) {
      userLocale = Locale.ENGLISH;
    }
    try {
      ResourceBundle resourceBundle =
                                    resourceBundleService.getResourceBundle(resourceBundleService.getSharedResourceBundleNames(),
                                                                            userLocale);
      String key = "UIWizardPageSelectLayoutForm.label." + label;
      if (resourceBundle != null && label != null && resourceBundle.containsKey(key)) {
        return resourceBundle.getString(key);
      }
      return label;
    } catch (Exception e) {
      return label;
    }
  }

}
