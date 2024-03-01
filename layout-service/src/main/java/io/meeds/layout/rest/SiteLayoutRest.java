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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.mop.SiteKey;

import io.meeds.layout.model.PermissionUpdateModel;
import io.meeds.layout.model.SiteCreateModel;
import io.meeds.layout.model.SiteUpdateModel;
import io.meeds.layout.service.SiteLayoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("sites")
@Tag(name = "sites", description = "Managing sites")
public class SiteLayoutRest {

  @Autowired
  private SiteLayoutService siteLayoutService;

  @DeleteMapping("{siteType}/{siteName}")
  @Secured("users")
  @Operation(summary = "Delete a site", method = "GET", description = "This deletes the given site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void deleteSite(
                         HttpServletRequest request,
                         @Parameter(description = "site type")
                         @PathVariable("siteType")
                         String siteType,
                         @Parameter(description = "site name")
                         @PathVariable("siteName")
                         String siteName) {
    try {
      siteLayoutService.deleteSite(new SiteKey(siteType, siteName), request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PutMapping("{siteType}/{siteName}")
  @Secured("users")
  @Operation(summary = "update a site", method = "PUT", description = "This updates the given site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void updateSite(
                         HttpServletRequest request,
                         @Parameter(description = "site type")
                         @PathVariable("siteType")
                         String siteType,
                         @Parameter(description = "site name")
                         @PathVariable("siteName")
                         String siteName,
                         @RequestBody
                         SiteUpdateModel updateModel) {
    try {
      siteLayoutService.updateSite(new SiteKey(siteType, siteName), updateModel, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PatchMapping(value = "{siteType}/{siteName}/permissions", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @Secured("users")
  @Operation(summary = "Update a page access and edit permission", method = "PATCH",
             description = "This updates the given page access and edit permission")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Page permissions updated"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Page not found"),
                          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void updateSitePermissions(
                                    HttpServletRequest request,
                                    @Parameter(description = "site type")
                                    @PathVariable("siteType")
                                    String siteType,
                                    @Parameter(description = "site name")
                                    @PathVariable("siteName")
                                    String siteName,
                                    @Parameter(description = "Site permission model", required = true)
                                    @RequestBody
                                    PermissionUpdateModel permissionUpdateModel) {
    try {
      siteLayoutService.updateSitePermissions(new SiteKey(siteType, siteName), permissionUpdateModel, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PostMapping
  @Secured("users")
  @Operation(summary = "create a site", method = "POST", description = "This create a new site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void createSite(
                         HttpServletRequest request,
                         @Parameter(description = "site to create", required = true)
                         @RequestBody
                         SiteCreateModel createModel) {
    try {
      siteLayoutService.createSite(createModel, request.getRemoteUser());
    } catch (ObjectAlreadyExistsException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

}
