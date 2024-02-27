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
package io.meeds.layout.rest;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.gatein.api.Portal;
import org.gatein.api.page.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.PageType;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.Utils;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.mop.storage.utils.MOPUtils;
import org.exoplatform.portal.page.PageTemplateService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.core.model.SelectItemOption;

import io.meeds.layout.rest.model.PageCreateModel;
import io.meeds.layout.rest.model.PageTemplateRestEntity;
import io.meeds.layout.rest.model.PermissionUpdateModel;
import io.meeds.layout.rest.util.EntityBuilder;
import io.meeds.layout.utils.SiteNavigationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("pages")
@Tag(name = "pages", description = "Managing site pages")
public class SitePageRestService {

  private static final Log        LOG = ExoLogger.getLogger(SitePageRestService.class);

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private PageTemplateService     pageTemplateService;

  @Autowired
  private UserPortalConfigService userPortalConfigService;

  @Autowired
  private Portal                  portal;

  @GetMapping
  @Secured("users")
  @Operation(summary = "Retrieve pages", method = "GET", description = "This retrieves pages")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public List<org.gatein.api.page.Page> getPages(
                                                 HttpServletRequest request,
                                                 @Parameter(description = "Portal site type, possible values: PORTAL, GROUP or USER")
                                                 @RequestParam(name = "siteType", required = false)
                                                 String siteType,
                                                 @Parameter(description = "Portal site name")
                                                 @RequestParam("siteName")
                                                 String siteName,
                                                 @Parameter(description = "page display name")
                                                 @RequestParam("pageDisplayName")
                                                 String pageDisplayName) {
    try {
      org.gatein.api.site.SiteType selectedSiteType = null;
      if (StringUtils.isNotBlank(siteType)) {
        selectedSiteType = MOPUtils.convertSiteType(SiteType.valueOf(siteType.toUpperCase()));
      }
      PageQuery pageQuery = new PageQuery.Builder().withSiteType(selectedSiteType)
                                                   .withSiteName(siteName)
                                                   .withDisplayName(pageDisplayName)
                                                   .build();
      return portal.findPages(pageQuery);
    } catch (Exception e) {
      LOG.warn("Error when retrieving pages", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("{pageRef}")
  @Secured("users")
  @Operation(summary = "Retrieve page by reference", method = "GET", description = "This retrieves page by reference")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public PageContext getPage(
                             HttpServletRequest request,
                             @Parameter(description = "page reference", required = true)
                             @PathVariable("pageRef")
                             String pageRef) {
    try {
      return layoutService.getPageContext(PageKey.parse(pageRef));
    } catch (Exception e) {
      LOG.warn("Error when retrieving page with reference {} ", pageRef, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping
  @Secured("users")
  @Operation(summary = "Create a page", method = "POST", description = "This creates the page")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "page created"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "500", description = "Internal server error") })
  public PageContext createPage(
                                @RequestBody
                                PageCreateModel createModel) {
    try {
      String pageName = createModel.getPageName() + "_" + UUID.randomUUID();
      Page page;
      if (PageType.PAGE.equals(PageType.valueOf(createModel.getPageType()))) {
        if (StringUtils.isBlank(createModel.getPageTemplate())) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pageTemplate param is mandatory for PAGE pageType");
        }
        page = userPortalConfigService.createPageTemplate(createModel.getPageTemplate(),
                                                          createModel.getPageSiteType(),
                                                          createModel.getPageSiteName());
      } else {
        page = new Page(createModel.getPageSiteType(), createModel.getPageSiteName(), pageName);
      }
      page.setName(pageName);
      page.setTitle(createModel.getPageTitle());
      page.setType(createModel.getPageType());
      page.setLink(PageType.LINK.equals(PageType.valueOf(createModel.getPageType())) ? createModel.getLink() : null);
      setDefaultPermission(page, new SiteKey(createModel.getPageSiteType(), createModel.getPageSiteName()));
      PageState pageState = Utils.toPageState(page);
      layoutService.save(new PageContext(page.getPageKey(), pageState), page);
      return layoutService.getPageContext(page.getPageKey());
    } catch (Exception e) {
      LOG.warn("Error when creating a new page", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PatchMapping(name = "{pageRef}/link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @Secured("users")
  @Operation(summary = "Update page link", method = "GET", description = "This updates page link")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void updatePageLink(
                             HttpServletRequest request,
                             @Parameter(description = "page display name", required = true)
                             @PathVariable("pageRef")
                             String pageRef,
                             @Parameter(description = "page new Link")
                             @RequestParam("link")
                             String link) {
    try {
      PageContext pageContext = layoutService.getPageContext(PageKey.parse(pageRef));
      PageState pageState = pageContext.getState();
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
    } catch (Exception e) {
      LOG.warn("Error when updating  page link with reference {} ", pageRef, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PatchMapping("{pageRef}/permissions")
  @Secured("users")
  @Operation(summary = "Update a page access and edit permission", method = "PATCH",
             description = "This updates the given page access and edit permission")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Page permissions updated"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Page not found"),
                          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void updatePagePermissions(
                                    HttpServletRequest request,
                                    @Parameter(description = "Page reference", required = true)
                                    @PathVariable("pageRef")
                                    String pageRef,
                                    @RequestBody
                                    PermissionUpdateModel permissionUpdateModel) {
    try {
      PageContext pageContext = layoutService.getPageContext(PageKey.parse(pageRef));
      if (pageContext == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      if (!SiteNavigationUtils.canEditPage(pageContext)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      PageState pageState = pageContext.getState();
      List<String> accessPermissionsList = List.of(permissionUpdateModel.getAccessPermissions().split(","))
                                               .stream()
                                               .distinct()
                                               .toList();
      pageContext.setState(new PageState(pageState.getDisplayName(),
                                         pageState.getDescription(),
                                         pageState.getShowMaxWindow(),
                                         pageState.getFactoryId(),
                                         accessPermissionsList,
                                         permissionUpdateModel.getEditPermission(),
                                         pageState.getMoveAppsPermissions(),
                                         pageState.getMoveContainersPermissions(),
                                         pageState.getType(),
                                         pageState.getLink()));
      layoutService.save(pageContext);
    } catch (Exception e) {
      LOG.warn("Error when updating page permissions with reference {}", pageRef, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("/templates")
  @Secured("users")
  @Operation(summary = "Retrieve page templates", method = "GET", description = "This retrieves page templates")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public List<PageTemplateRestEntity> getPageTemplates(HttpServletRequest request) {
    try {
      Locale locale = request.getLocale();
      List<SelectItemOption<String>> pageTemplates = pageTemplateService.getPageTemplates();
      return EntityBuilder.toPageTemplateRestEntities(pageTemplates, locale);
    } catch (Exception e) {
      LOG.warn("Error when retrieving page templates", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void setDefaultPermission(Page page, SiteKey siteKey) {
    if (SiteType.PORTAL.equals(siteKey.getType())) {
      page.setAccessPermissions(new String[] { UserACL.EVERYONE });
      page.setEditPermission("*:/platform/administrators");
    } else if (SiteType.GROUP.equals(siteKey.getType())) {
      String siteName = siteKey.getName().startsWith("/") ? siteKey.getName() : "/" + siteKey.getName();
      page.setAccessPermissions(new String[] { "*:" + siteName });
      page.setEditPermission("manager:" + siteName);
    }
  }

}
