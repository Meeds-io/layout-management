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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;

import io.meeds.layout.model.PageCreateModel;
import io.meeds.layout.model.PermissionUpdateModel;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.rest.util.RestEntityBuilder;
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

  @GetMapping("layout")
  @Secured("users")
  @Operation(summary = "Retrieve page layout by reference", method = "GET", description = "This retrieves page by reference")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public LayoutModel getPageLayout(
                                   HttpServletRequest request,
                                   @Parameter(description = "page reference", required = true)
                                   @RequestParam("pageRef")
                                   String pageRef) {
    try {
      Page page = pageLayoutService.getPageLayout(PageKey.parse(pageRef), request.getRemoteUser());
      return RestEntityBuilder.toLayoutModel(page);
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @GetMapping("byRef")
  @Secured("users")
  @Operation(summary = "Retrieve page by reference", method = "GET", description = "This retrieves page by reference")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public PageContext getPage(
                             HttpServletRequest request,
                             @Parameter(description = "page reference", required = true)
                             @RequestParam("pageRef")
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

  @PutMapping("layout")
  @Secured("users")
  @Operation(summary = "Updates an existing page layout", method = "PUT", description = "This updates the designated page layout")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "page created"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "500", description = "Internal server error") })
  public LayoutModel updatePageLayout(
                                      HttpServletRequest request,
                                      @Parameter(description = "page display name", required = true)
                                      @RequestParam("pageRef")
                                      String pageRef,
                                      @Parameter(description = "Whether the page layout update is a draft page publication or not", required = false)
                                      @RequestParam(name = "publish", required = false)
                                      Optional<Boolean> publish,
                                      @RequestBody
                                      LayoutModel layoutModel) {
    try {
      pageLayoutService.updatePageLayout(pageRef,
                                         RestEntityBuilder.fromLayoutModel(layoutModel),
                                         publish.orElse(false).booleanValue(),
                                         request.getRemoteUser());
      return getPageLayout(request, pageRef);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PatchMapping(name = "link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @Secured("users")
  @Operation(summary = "Update page link", method = "GET", description = "This updates page link")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void updatePageLink(
                             HttpServletRequest request,
                             @Parameter(description = "page display name", required = true)
                             @RequestParam("pageRef")
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

  @PatchMapping("permissions")
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
                                    @RequestParam("pageRef")
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

}
