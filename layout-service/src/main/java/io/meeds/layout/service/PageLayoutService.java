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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.javascript.jscomp.jarjar.com.google.re2j.Pattern;

import org.exoplatform.commons.addons.AddOnService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.ModelStyle;
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
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;

import io.meeds.layout.model.PageCreateModel;
import io.meeds.layout.model.PageTemplate;
import io.meeds.layout.model.PermissionUpdateModel;
import io.meeds.layout.model.PortletInstancePreference;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.util.JsonUtils;

import lombok.SneakyThrows;

@Service
public class PageLayoutService {

  public static final String      EMPTY_PAGE_TEMPLATE             = "empty";

  private static final Pattern    GENERIC_STYLE_MATCHER_VALIDATOR = Pattern.compile("[#0-9a-zA-Z\\(\\),\\./\"'\\-%_]+");

  private static final String     PAGE_NOT_EXISTS_MESSAGE         = "Page with key %s doesn't exist";

  private static final String     PAGE_NOT_ACCESSIBLE_MESSAGE     = "Page with ref %s isn't accessible for user %s";

  private static final String     PAGE_NOT_EDITABLE_MESSAGE       = "Page with ref %s isn't editable for user %s";

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private LayoutAclService        aclService;

  @Autowired
  private PageTemplateService     pageTemplateService;

  @Autowired
  private PortletInstanceService  portletInstanceService;

  @Autowired
  private UserPortalConfigService userPortalConfigService;

  @Autowired
  private AddOnService            addOnService;

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

  public PageContext getPage(PageKey pageKey, String username) throws ObjectNotFoundException, IllegalAccessException {
    PageContext page = layoutService.getPageContext(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    } else if (!aclService.canViewPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    }
    return page;
  }

  public Application<Portlet> getPageApplicationLayout(PageKey pageKey,
                                                       long applicationId,
                                                       String username) throws ObjectNotFoundException,
                                                                        IllegalAccessException {
    Page page = getPageLayout(pageKey, username);
    Application<Portlet> application = findApplication(page, applicationId);
    if (application == null) {
      throw new ObjectNotFoundException(String.format("Application with id %s wasn't found in page %s",
                                                      applicationId,
                                                      pageKey));
    }
    computeApplicationPreferences(application, username);
    return application;
  }

  public Page getPageLayout(PageKey pageKey, String username) throws ObjectNotFoundException, IllegalAccessException {
    Page page = getPageLayout(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    } else if (!aclService.canViewPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_ACCESSIBLE_MESSAGE, pageKey, username));
    }
    return page;
  }

  public Page getPageLayout(PageKey pageKey) {
    Page page = layoutService.getPage(pageKey);
    if (page != null) {
      expandAddonContainerChildren(page);
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

    Page page = createPageInstance(pageModel.getPageSiteType(),
                                   pageModel.getPageSiteName(),
                                   pageName,
                                   getPageType(pageModel.getPageType()),
                                   pageModel.getPageTemplateId(),
                                   pageModel.getLink());

    page.setName(pageName);
    page.setTitle(pageModel.getPageTitle());
    String[] accessPermissions = pageModel.getAccessPermissions() == null ?
                                                                          portalConfig.getAccessPermissions() :
                                                                          pageModel.getAccessPermissions();
    page.setAccessPermissions(accessPermissions);
    String editPermission = pageModel.getEditPermission() == null ?
                                                                  portalConfig.getEditPermission() :
                                                                  pageModel.getEditPermission();
    page.setEditPermission(editPermission);
    validateCSSInputs(page);
    layoutService.save(new PageContext(page.getPageKey(), Utils.toPageState(page)), page);
    return layoutService.getPageContext(page.getPageKey());
  }

  public PageKey clonePage(PageKey pageKey,
                           String username) throws IllegalAccessException,
                                            ObjectNotFoundException {
    Page page = getPageLayout(pageKey);
    if (page == null) {
      throw new ObjectNotFoundException(String.format(PAGE_NOT_EXISTS_MESSAGE, pageKey.format()));
    } else if (!aclService.canEditPage(pageKey, username)) {
      throw new IllegalAccessException(String.format(PAGE_NOT_EDITABLE_MESSAGE, pageKey.format(), username));
    }
    // Copy applications into a dedicated application container
    // to seperate lifecycle of original page and cloned page
    // thus generate a dedicated storage identifier for
    // applications and thus a dedicated portlet preferences
    page.resetStorage();
    page.setName(page.getName() + "_draft_" + username);
    page.setTitle(page.getTitle() + " Draft " + username);
    replaceAddonContainerChildren(page);

    layoutService.save(new PageContext(page.getPageKey(), Utils.toPageState(page)), page);
    return page.getPageKey();
  }

  public PageContext updatePageLayout(String pageRef,
                                      Page page,
                                      boolean publish,
                                      String username) throws IllegalAccessException,
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
    if (publish) {
      page.resetStorage();
    }
    validateCSSInputs(page);
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
    List<String> accessPermissionsList = permissionUpdateModel.getAccessPermissions();
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

  @SneakyThrows
  private Page createPageInstance(String siteType,
                                  String siteName,
                                  String pageName,
                                  PageType pageType,
                                  Long pageTemplateId,
                                  String pageLink) throws IllegalArgumentException {
    return switch (pageType) {
    case PAGE: {
      if (pageTemplateId == null) {
        throw new IllegalArgumentException("pageTemplateId is mandatory");
      }
      PageTemplate pageTemplate = pageTemplateService.getPageTemplate(pageTemplateId);
      if (pageTemplate == null) {
        throw new ObjectNotFoundException("pageTemplate not found");
      }
      if (pageTemplate.isDisabled()) {
        throw new IllegalArgumentException("pageTemplate with designated Id is disabled");
      }
      Page page = userPortalConfigService.createPageTemplate(EMPTY_PAGE_TEMPLATE,
                                                             siteType,
                                                             siteName);
      Page pageLayout = JsonUtils.fromJsonString(pageTemplate.getContent(), LayoutModel.class)
                                 .toPage();
      page.setChildren(pageLayout.getChildren());
      page.resetStorage();
      page.setName(pageName);
      page.setType(pageType.name());
      yield page;
    }
    case LINK: {
      Page page = new Page(siteType, siteName, pageName);
      page.setLink(pageLink);
      page.setType(pageType.name());
      yield page;
    }
    default:
      // Unreachable but kept in case of behavior changes
      throw new IllegalArgumentException("pageType is mandatory");
    };
  }

  private PageType getPageType(String pageType) {
    return StringUtils.isBlank(pageType) ? PageType.PAGE : PageType.valueOf(pageType.toUpperCase());
  }

  private void expandAddonContainerChildren(Container container) {
    if (StringUtils.equals(container.getFactoryId(), "addonContainer")) {
      List<Application<Portlet>> applications = addOnService.getApplications(container.getName());
      if (CollectionUtils.isNotEmpty(applications)) {
        container.setChildren(new ArrayList<>(applications));
      }
    } else if (container.getChildren() != null) {
      container.getChildren()
               .stream()
               .filter(Objects::nonNull)
               .filter(Container.class::isInstance)
               .map(Container.class::cast)
               .forEach(this::expandAddonContainerChildren);
    }
  }

  private void replaceAddonContainerChildren(Container container) {
    ArrayList<ModelObject> subContainers = container.getChildren();
    if (subContainers == null) {
      return;
    }
    LinkedHashMap<Integer, List<Application<Portlet>>> addonContainerChildren = new LinkedHashMap<>();
    for (int i = subContainers.size() - 1; i >= 0; i--) {
      ModelObject modelObject = subContainers.get(i);
      if (modelObject instanceof Container subContainer) {
        if (StringUtils.equals(subContainer.getFactoryId(), "addonContainer")) {
          List<Application<Portlet>> applications = addOnService.getApplications(subContainer.getName());
          if (CollectionUtils.isNotEmpty(applications)) {
            addonContainerChildren.put(i, applications);
          }
        } else {
          replaceAddonContainerChildren(subContainer);
        }
      }
    }
    if (!addonContainerChildren.isEmpty()) {
      addonContainerChildren.forEach((index, applications) -> {
        subContainers.remove(index.intValue());
        subContainers.addAll(index, applications);
      });
      container.setChildren(subContainers);
    }
  }

  private void validateCSSInputs(ModelObject modelObject) {
    ModelStyle cssStyle = modelObject.getCssStyle();
    Arrays.asList(modelObject.getHeight(),
                  modelObject.getWidth(),
                  cssStyle == null ? null : cssStyle.getBorderColor(),
                  cssStyle == null ? null : cssStyle.getBorderSize(),
                  cssStyle == null ? null : cssStyle.getBoxShadow(),
                  cssStyle == null ? null : cssStyle.getBackgroundColor(),
                  cssStyle == null ? null : cssStyle.getBackgroundImage(),
                  cssStyle == null ? null : cssStyle.getBackgroundEffect(),
                  cssStyle == null ? null : cssStyle.getBackgroundSize(),
                  cssStyle == null ? null : cssStyle.getBackgroundRepeat())
          .forEach(this::validateCSSStyleValue);
    if (modelObject instanceof Container container && !CollectionUtils.isEmpty(container.getChildren())) {
      container.getChildren().forEach(this::validateCSSInputs);
    }
  }

  private void validateCSSStyleValue(String value) {
    if (StringUtils.isNotBlank(value)
        && (!GENERIC_STYLE_MATCHER_VALIDATOR.matches(value)
            || value.contains("javascript")
            || value.contains("eval"))) {
      throw new IllegalArgumentException(String.format("Invalid css value input %s",
                                                       value));
    }
  }

  private void computeApplicationPreferences(Application<Portlet> application, String username) throws IllegalAccessException,
                                                                                                ObjectNotFoundException {
    String portletContentId = layoutService.getId(application.getState());
    Portlet portletPreferences = getApplicationPreferences(Long.parseLong(application.getStorageId()), username);
    TransientApplicationState<Portlet> applicationState = new TransientApplicationState<>(portletContentId, portletPreferences);
    application.setState(applicationState);
  }

  private Portlet getApplicationPreferences(long applicationId, String username) throws IllegalAccessException,
                                                                                 ObjectNotFoundException {
    List<PortletInstancePreference> preferences = portletInstanceService.getApplicationPreferences(applicationId, username);
    Map<String, Preference> preferencesMap = preferences.stream()
                                                        .collect(Collectors.toMap(PortletInstancePreference::getName,
                                                                                  p -> new Preference(p.getName(),
                                                                                                      p.getValue(),
                                                                                                      false)));
    return new Portlet(preferencesMap);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Application<Portlet> findApplication(ModelObject modelObject, long applicationId) {
    return switch (modelObject) {
    case Container container -> {
      if (!CollectionUtils.isEmpty(container.getChildren())) {
        yield container.getChildren()
                       .stream()
                       .map(m -> findApplication(m, applicationId))
                       .filter(Objects::nonNull)
                       .findFirst()
                       .orElse(null);
      }
      yield null;
    }
    case Application application -> {
      if (StringUtils.equals(application.getStorageId(), String.valueOf(applicationId))) {
        yield application;
      }
      yield null;
    }
    default -> null;
    };
  }

}
