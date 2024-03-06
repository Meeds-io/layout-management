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

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.webui.core.model.SelectItemOption;

import io.meeds.layout.model.PageCreateModel;
import io.meeds.layout.model.PermissionUpdateModel;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.rest.model.PageTemplateModel;
import io.meeds.layout.rest.util.EntityBuilder;
import io.meeds.layout.service.LayoutI18NService;
import io.meeds.layout.service.PageLayoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("pages")
@Tag(name = "pages", description = "Managing site pages")
public class PageLayoutRest {

  @Autowired
  private PageLayoutService pageLayoutService;

  @Autowired
  private LayoutI18NService layoutI18NService;

  @Autowired
  private LayoutService     layoutService;

  @GetMapping
  @Secured("users")
  @Operation(summary = "Retrieve pages", method = "GET", description = "This retrieves pages")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled") })
  public List<PageContext> getPages(
                                    HttpServletRequest request,
                                    @Parameter(description = "Portal site type, possible values: PORTAL, GROUP or USER",
                                               required = false)
                                    @RequestParam(name = "siteType", required = false)
                                    String siteType,
                                    @Parameter(description = "Portal site name", required = false)
                                    @RequestParam(name = "siteName", required = false)
                                    String siteName,
                                    @Parameter(description = "page display name", required = false)
                                    @RequestParam(name = "pageDisplayName", required = false)
                                    String pageDisplayName,
                                    @Parameter(description = "Results offset", required = false)
                                    @RequestParam(name = "offset", required = false, defaultValue = "0")
                                    int offset,
                                    @Parameter(description = "Results limit", required = false)
                                    @RequestParam(name = "limit", required = false, defaultValue = "10")
                                    int limit) {
    return pageLayoutService.getPages(siteType, siteName, pageDisplayName, offset, limit, request.getRemoteUser());
  }

  @GetMapping("{pageRef}/layout")
  @Secured("users")
  @Operation(summary = "Retrieve page layout by reference", method = "GET", description = "This retrieves page by reference")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public LayoutModel getPageLayout(
                                   HttpServletRequest request,
                                   @Parameter(description = "page reference", required = true)
                                   @PathVariable("pageRef")
                                   String pageRef) {
    try {
      Page page = pageLayoutService.getPageLayout(PageKey.parse(pageRef), request.getRemoteUser());
      return EntityBuilder.toLayoutModel(page, layoutService);
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
      return pageLayoutService.getPage(PageKey.parse(pageRef), request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PostMapping
  @Secured("users")
  @Operation(summary = "Create a page", method = "POST", description = "This creates the page")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "page created"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "500", description = "Internal server error") })
  public PageContext createPage(
                                HttpServletRequest request,
                                @RequestBody
                                PageCreateModel createModel) {
    try {
      return pageLayoutService.createPage(createModel, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
      pageLayoutService.updatePageLink(PageKey.parse(pageRef), link, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
      pageLayoutService.updatePagePermissions(PageKey.parse(pageRef), permissionUpdateModel, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @GetMapping("/templates")
  @Secured("users")
  @Operation(summary = "Retrieve page templates", method = "GET", description = "This retrieves page templates")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public List<PageTemplateModel> getPageTemplates(HttpServletRequest request) {
    Locale locale = request.getLocale();
    List<SelectItemOption<String>> pageTemplates = pageLayoutService.getPageTemplates();
    return EntityBuilder.toPageTemplateModel(pageTemplates, layoutI18NService, locale);
  }

}
