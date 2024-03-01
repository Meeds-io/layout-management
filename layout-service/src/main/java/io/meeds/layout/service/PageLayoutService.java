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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.PageType;
import org.exoplatform.portal.mop.QueryResult;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.Utils;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.page.PageTemplateService;
import org.exoplatform.webui.core.model.SelectItemOption;

import io.meeds.layout.model.PageCreateModel;
import io.meeds.layout.model.PermissionUpdateModel;

import lombok.SneakyThrows;

@Service
public class PageLayoutService {

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private LayoutAclService        aclService;

  @Autowired
  private PageTemplateService     pageTemplateService;

  @Autowired
  private UserPortalConfigService userPortalConfigService;

  public List<PageContext> getPages(String siteTypeName,
                                    String siteName,
                                    String pageDisplayName,
                                    int offset,
                                    int limit,
                                    String username) {
    SiteType siteType = null;
    if (StringUtils.isNotBlank(siteTypeName)) {
      siteType = SiteType.valueOf(siteTypeName.toUpperCase());
    }
    QueryResult<PageContext> queryResult = layoutService.findPages(offset, limit, siteType, siteName, null, pageDisplayName);
    List<PageContext> pages = new ArrayList<>();
    queryResult.iterator().forEachRemaining(pages::add);
    return pages.stream()
                .filter(p -> aclService.canViewPage(p.getKey(), username))
                .toList();
  }

  public PageContext getPage(PageKey pageKey,
                             String username) throws ObjectNotFoundException, IllegalAccessException {
    PageContext page = layoutService.getPageContext(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format("Page with ref %s isn't accessible for user %s", pageKey, username));
    } else if (!aclService.canViewPage(pageKey, username)) {
      throw new IllegalAccessException(String.format("Page with ref %s isn't accessible for user %s", pageKey, username));
    }
    return page;
  }

  public Container getPageLayout(PageKey pageKey,
                                 String username) throws ObjectNotFoundException, IllegalAccessException {
    Page page = layoutService.getPage(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format("Page with ref %s isn't accessible for user %s", pageKey, username));
    } else if (!aclService.canViewPage(pageKey, username)) {
      throw new IllegalAccessException(String.format("Page with ref %s isn't accessible for user %s", pageKey, username));
    }
    return page;
  }

  @SneakyThrows
  public PageContext createPage(PageCreateModel pageModel,
                                String username) throws ObjectNotFoundException,
                                                 IllegalAccessException,
                                                 IllegalArgumentException {
    SiteKey siteKey = new SiteKey(pageModel.getPageSiteType(), pageModel.getPageSiteName());
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      throw new ObjectNotFoundException(String.format("Site with key %s doesn't exist", siteKey));
    } else if (!aclService.canEditNavigation(siteKey, username)) {
      throw new IllegalAccessException();
    }

    String pageName = pageModel.getPageName() == null ? UUID.randomUUID().toString() :
                                                      pageModel.getPageName() + "_" + UUID.randomUUID();

    Page page = createPageInstance(pageModel.getPageSiteType(), // NOSONAR
                                   pageModel.getPageSiteName(),
                                   pageName,
                                   getPageType(pageModel.getPageType()),
                                   pageModel.getPageTemplate(),
                                   pageModel.getLink());

    page.setTitle(pageModel.getPageTitle());
    String[] accessPermissions = pageModel.getAccessPermissions() == null ?
                                                                          portalConfig.getAccessPermissions() :
                                                                          pageModel.getAccessPermissions();
    page.setAccessPermissions(accessPermissions);
    String editPermission = pageModel.getEditPermission() == null ?
                                                                  portalConfig.getEditPermission() :
                                                                  pageModel.getEditPermission();
    page.setEditPermission(editPermission);
    layoutService.save(new PageContext(page.getPageKey(), Utils.toPageState(page)), page);
    return layoutService.getPageContext(page.getPageKey());
  }

  public void updatePageLink(PageKey pageKey,
                             String link,
                             String username) throws ObjectNotFoundException,
                                              IllegalAccessException,
                                              IllegalStateException {
    PageContext pageContext = layoutService.getPageContext(pageKey);
    if (pageContext == null) {
      throw new ObjectNotFoundException(String.format("Page with key %s doesn't exist", pageKey));
    } else if (!aclService.canEditPage(pageKey, username)) {
      throw new IllegalAccessException();
    }
    PageState pageState = pageContext.getState();
    PageType pageType = getPageType(pageState.getType());
    if (pageType != PageType.LINK) {
      throw new IllegalStateException(String.format("Page %s isn't of type 'LINK'", pageKey));
    }
    pageContext.setState(new PageState(pageState.getDisplayName(),
                                       pageState.getDescription(),
                                       pageState.getShowMaxWindow(),
                                       pageState.getFactoryId(),
                                       pageState.getAccessPermissions(),
                                       pageState.getEditPermission(),
                                       pageState.getMoveAppsPermissions(),
                                       pageState.getMoveContainersPermissions(),
                                       pageState.getType(),
                                       link));
    layoutService.save(pageContext);
  }

  public void updatePagePermissions(PageKey pageKey,
                                    PermissionUpdateModel permissionUpdateModel,
                                    String username) throws ObjectNotFoundException,
                                                     IllegalAccessException {
    PageContext pageContext = layoutService.getPageContext(pageKey);
    if (pageContext == null) {
      throw new ObjectNotFoundException(String.format("Page with key %s doesn't exist", pageKey));
    } else if (!aclService.canEditPage(pageKey, username)) {
      throw new IllegalAccessException();
    }
    PageState pageState = pageContext.getState();
    List<String> accessPermissionsList = List.of(permissionUpdateModel.getAccessPermissions().split(","))
                                             .stream()
                                             .distinct()
                                             .toList();
    String editPermission = permissionUpdateModel.getEditPermission();

    pageContext.setState(new PageState(pageState.getDisplayName(),
                                       pageState.getDescription(),
                                       pageState.getShowMaxWindow(),
                                       pageState.getFactoryId(),
                                       accessPermissionsList,
                                       editPermission,
                                       pageState.getMoveAppsPermissions(),
                                       pageState.getMoveContainersPermissions(),
                                       pageState.getType(),
                                       pageState.getLink()));
    layoutService.save(pageContext);
  }

  public List<SelectItemOption<String>> getPageTemplates() {
    return pageTemplateService.getPageTemplates();
  }

  @SneakyThrows
  private Page createPageInstance(String siteType,
                                  String siteName,
                                  String pageName,
                                  PageType pageType,
                                  String pageTemplate,
                                  String pageLink) throws IllegalArgumentException {
    Page page;
    if (pageType == PageType.PAGE) {
      if (StringUtils.isBlank(pageTemplate)) {
        throw new IllegalArgumentException("pageTemplate is mandatory");
      }
      page = userPortalConfigService.createPageTemplate(pageTemplate,
                                                        siteType,
                                                        siteName);
      page.setName(pageName);
    } else if (pageType == PageType.LINK) {
      page = new Page(siteType, siteName, pageName);
      page.setLink(pageLink);
    } else {
      throw new IllegalArgumentException("pageType is mandatory");
    }
    page.setType(pageType.name());
    return page;
  }

  private PageType getPageType(String pageType) {
    return StringUtils.isBlank(pageType) ? null : PageType.valueOf(pageType.toUpperCase());
  }

}
