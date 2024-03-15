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
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.TransientApplicationState;
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
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.webui.core.model.SelectItemOption;

import io.meeds.layout.model.PageCreateModel;
import io.meeds.layout.model.PermissionUpdateModel;

import lombok.SneakyThrows;

@Service
public class PageLayoutService {

  private static final String     PAGE_NOT_EXISTS_MESSAGE     = "Page with key %s doesn't exist";

  private static final String     PAGE_NOT_ACCESSIBLE_MESSAGE = "Page with ref %s isn't accessible for user %s";

  private static final String     PAGE_NOT_EDITABLE_MESSAGE   = "Page with ref %s isn't editable for user %s";

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
      throw new ObjectNotFoundException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    } else if (!aclService.canViewPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    }
    return page;
  }

  public Page getPageLayout(PageKey pageKey,
                            String username) throws ObjectNotFoundException, IllegalAccessException {
    Page page = getPageLayout(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    } else if (!aclService.canViewPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    }
    return page;
  }

  public Page getPageLayout(PageKey pageKey) {
    return layoutService.getPage(pageKey);
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

  public PageKey clonePage(PageKey pageKey,
                           String username) throws IllegalAccessException,
                                            ObjectNotFoundException {
    Page page = layoutService.getPage(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_EXISTS_MESSAGE, pageKey.format()));
    } else if (!aclService.canEditPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_EDITABLE_MESSAGE, pageKey.format(), username));
    }
    // Copy applications into a dedicated application container
    // to seperate lifecycle of original page and cloned page
    // thus generate a dedicated storage identifier for
    // applications and thus a dedicated portlet preferences
    convertApplications(page);

    String draftPageName = page.getName() + "_draft_" + username;
    page.setName(draftPageName);
    page.setTitle(page.getTitle() + " Draft " + username);

    PageKey clonedPageKey = page.getPageKey();
    layoutService.save(new PageContext(clonedPageKey, Utils.toPageState(page)), page);
    return clonedPageKey;
  }

  public PageContext updatePageLayout(String pageRef, Page page, String username) throws IllegalAccessException,
                                                                                  ObjectNotFoundException {
    // Security and existence check
    PageKey pageKey = PageKey.parse(pageRef);
    Page existingPage = layoutService.getPage(pageKey);
    if (existingPage == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_EXISTS_MESSAGE, pageKey.format()));
    } else if (!aclService.canEditPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_EDITABLE_MESSAGE, pageKey.format(), username));
    }

    // Update Page Layout only
    existingPage.setChildren(page.getChildren());
    layoutService.save(existingPage);
    return layoutService.getPageContext(existingPage.getPageKey());
  }

  public void updatePageLink(PageKey pageKey,
                             String link,
                             String username) throws ObjectNotFoundException,
                                              IllegalAccessException,
                                              IllegalStateException {
    PageContext pageContext = layoutService.getPageContext(pageKey);
    if (pageContext == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_EXISTS_MESSAGE, pageKey));
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
      throw new ObjectNotFoundException(String.format(PAGE_NOT_EXISTS_MESSAGE, pageKey));
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

  @SuppressWarnings("unchecked")
  private void convertApplications(ModelObject object) {
    if (object instanceof Container container) {
      if (container.getChildren() != null) {
        container.getChildren().forEach(this::convertApplications);
      }
    } else if (object instanceof Application application) { // NOSONAR
      convertApplication(application);
    }
  }

  private void convertApplication(Application<Portlet> application) {
    ApplicationState<Portlet> state = application.getState();

    // Marshal application state
    if (!(state instanceof TransientApplicationState)) { // NOSONAR
      state = new TransientApplicationState<>(layoutService.getId(state),
                                              layoutService.load(state, ApplicationType.PORTLET));
      application.setState(state);
    }
  }

}
