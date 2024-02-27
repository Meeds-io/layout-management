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

import org.apache.commons.lang3.StringUtils;
import org.gatein.api.Portal;
import org.gatein.api.Util;
import org.gatein.api.site.Site;
import org.gatein.api.site.SiteId;
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

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.entity.SiteEntity;

import io.meeds.layout.rest.model.PermissionUpdateModel;
import io.meeds.layout.rest.model.SiteCreateModel;
import io.meeds.layout.rest.model.SiteUpdateModel;
import io.meeds.layout.utils.SiteManagementUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("sites")
@Tag(name = "sites", description = "Managing sites")
public class SiteManagementRestService {

  private static final Log        LOG = ExoLogger.getLogger(SiteManagementRestService.class);

  @Autowired
  private Portal                  portal;

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private UserPortalConfigService userPortalConfigService;

  @Autowired
  private UserACL                 userAcl;

  @DeleteMapping("{siteType}/{siteName}")
  @Secured("users")
  @Operation(summary = "Delete a site", method = "GET", description = "This deletes the given site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void deleteSite(
                         @Parameter(description = "site type")
                         @PathVariable("siteType")
                         String siteType,
                         @Parameter(description = "site name")
                         @PathVariable("siteName")
                         String siteName) {
    try {
      SiteId siteId = Util.from(new SiteKey(siteType, siteName));
      Site site = portal.getSite(siteId);
      if (!SiteManagementUtils.canEditSite(site)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      portal.removeSite(siteId);
    } catch (Exception e) {
      LOG.error("Error when deleting the site with name {} and type {}", siteName, siteType, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PutMapping("{siteType}/{siteName}")
  @Secured("users")
  @Operation(summary = "update a site", method = "PUT", description = "This updates the given site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public SiteEntity updateSite(
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
      SiteId siteId = Util.from(new SiteKey(siteType, siteName));
      Site site = portal.getSite(siteId);
      if (!SiteManagementUtils.canEditSite(site)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      PortalConfig portalConfig =
                                layoutService.getPortalConfig(new SiteKey(siteType, siteName));
      portalConfig.setDescription(updateModel.getSiteDescription());
      portalConfig.setLabel(updateModel.getSiteLabel());
      portalConfig.setDisplayed(updateModel.isDisplayed());
      portalConfig.setDisplayOrder(updateModel.isDisplayed() ? updateModel.getDisplayOrder() : 0);
      if (updateModel.isBannerRemoved() && portalConfig.getBannerFileId() != 0) {
        layoutService.removeSiteBanner(siteName);
        portalConfig.setBannerFileId(0);
      } else if (StringUtils.isNotBlank(updateModel.getBannerUploadId())) {
        portalConfig.setBannerUploadId(updateModel.getBannerUploadId());
      }
      layoutService.save(portalConfig);
      return EntityBuilder.buildSiteEntity(portalConfig, request, false, null, false, false, false, getLocale(request));
    } catch (Exception e) {
      LOG.warn("Error when updating the site {}", updateModel, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
  public SiteEntity updateSitePermissions(
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
      SiteId siteId = Util.from(new SiteKey(siteType, siteName));
      Site site = portal.getSite(siteId);
      if (site == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      if (!SiteManagementUtils.canEditSite(site)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      if (!StringUtils.isBlank(permissionUpdateModel.getEditPermission())) {
        site.setEditPermission(Util.from(permissionUpdateModel.getEditPermission()));
      }
      if (!StringUtils.isBlank(permissionUpdateModel.getAccessPermissions())) {
        List<String> accessPermissionsList = List.of(permissionUpdateModel.getAccessPermissions().split(","))
                                                 .stream()
                                                 .distinct()
                                                 .toList();
        site.setAccessPermission(Util.from(accessPermissionsList));
      }
      portal.saveSite(site);
      PortalConfig portalConfig = layoutService.getPortalConfig(new SiteKey(siteType, siteName));
      return EntityBuilder.buildSiteEntity(portalConfig, request, false, null, false, false, false, getLocale(request));
    } catch (Exception e) {
      LOG.error("Error when updating site permissions {}", permissionUpdateModel, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping
  @Secured("users")
  @Operation(summary = "create a site", method = "POST", description = "This create a new site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public SiteEntity createSite(
                               HttpServletRequest request,
                               @Parameter(description = "site to create", required = true)
                               @RequestBody
                               SiteCreateModel createModel) {
    if (!SiteManagementUtils.canAddSite()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    PortalConfig portalConfig = createModel.getPortalConfig();
    String siteTemplate = createModel.getSiteTemplate();
    try {
      userPortalConfigService.createUserPortalConfig(PortalConfig.PORTAL_TYPE, portalConfig.getName(), siteTemplate);
      PortalConfig createdPortalConfig = layoutService.getPortalConfig(portalConfig.getName());
      createdPortalConfig.setDescription(portalConfig.getDescription());
      createdPortalConfig.setLabel(portalConfig.getLabel());
      createdPortalConfig.setDisplayed(portalConfig.isDisplayed());
      createdPortalConfig.setDisplayOrder(portalConfig.isDisplayed() ? portalConfig.getDisplayOrder() : 0);
      createdPortalConfig.setAccessPermissions(new String[] { userAcl.getAdminGroups() });
      createdPortalConfig.setEditPermission(userAcl.getAdminGroups());
      if (StringUtils.isNotBlank(portalConfig.getBannerUploadId())) {
        createdPortalConfig.setBannerUploadId(portalConfig.getBannerUploadId());
      }
      layoutService.save(createdPortalConfig);
      return EntityBuilder.buildSiteEntity(layoutService.getPortalConfig(portalConfig.getName()),
                                           request,
                                           false,
                                           null,
                                           false,
                                           false,
                                           false,
                                           getLocale(request));
    } catch (Exception e) {
      LOG.error("Error when creating the site with name {} and type {}", portalConfig.getName(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private Locale getLocale(HttpServletRequest request) {
    return request.getLocale();
  }

}
