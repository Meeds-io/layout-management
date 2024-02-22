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

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.gatein.api.Portal;
import org.gatein.api.Util;
import org.gatein.api.site.Site;
import org.gatein.api.site.SiteId;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.http.PATCH;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.rest.api.EntityBuilder;

import io.meeds.layout.utils.SiteManagementUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Path("v1/sites")
@Tag(name = "v1/sites", description = "Managing sites")
public class SiteManagementRestService implements ResourceContainer {

  private static final Log LOG = ExoLogger.getLogger(SiteManagementRestService.class);

  private static final String[] DEFAULT_PORTAL_ACCESS_PERMISSIONS = {"*:/platform/administrators"};

  private static final String DEFAULT_PORTAL_EDIT_PERMISSIONS = "manager:/platform/administrators";

  private Portal           portal;

  private LayoutService    layoutService;
  private UserPortalConfigService userPortalConfigService;

  public SiteManagementRestService(Portal portal, LayoutService layoutService, UserPortalConfigService userPortalConfigService) {
    this.portal = portal;
    this.layoutService = layoutService;
    this.userPortalConfigService = userPortalConfigService;
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Operation(summary = "Delete a site", method = "GET", description = "This deletes the given site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response deleteSite(@Parameter(description = "site type")
  @QueryParam("siteType")
  String siteType,
                             @Parameter(description = "site name")
                             @QueryParam("siteName")
                             String siteName) {
    try {
      SiteId siteId = Util.from(new SiteKey(siteType, siteName));
      Site site = portal.getSite(siteId);
      if (!SiteManagementUtils.canEditSite(site)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      portal.removeSite(siteId);
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error("Error when deleting the site with name {} and type {}", siteName, siteType, e);
      return Response.serverError().build();
    }
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Operation(summary = "update a site", method = "PUT", description = "This updates the given site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateSite(@Context
  HttpServletRequest request,
                             @Parameter(description = "site type")
                             @QueryParam("siteType")
                             String siteType,
                             @Parameter(description = "site name")
                             @QueryParam("siteName")
                             String siteName,
                             @Parameter(description = "site Label")
                             @QueryParam("siteLabel")
                             String siteLabel,
                             @Parameter(description = "site description")
                             @QueryParam("siteDescription")
                             String siteDescription,
                             @Parameter(description = "site displayed in meta site")
                             @QueryParam("displayed")
                             boolean displayed,
                             @Parameter(description = "site display order")
                             @QueryParam("displayOrder")
                             int displayOrder,
                             @Parameter(description = "site banner UploadId")
                             @QueryParam("bannerUploadId")
                             String bannerUploadId,
                             @Parameter(description = "if site banner has been removed")
                             @DefaultValue("false")
                             @QueryParam("bannerRemoved")
                             boolean bannerRemoved,
                             @Parameter(description = "Used to retrieve the site label and description in the requested language")
                             @QueryParam("lang")
                             String lang) {
    try {
      SiteId siteId = Util.from(new SiteKey(siteType, siteName));
      Site site = portal.getSite(siteId);
      if (!SiteManagementUtils.canEditSite(site)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      PortalConfig portalConfig = layoutService.getPortalConfig(new SiteKey(siteType, siteName));
      portalConfig.setDescription(siteDescription);
      portalConfig.setLabel(siteLabel);
      portalConfig.setDisplayed(displayed);
      portalConfig.setDisplayOrder(displayed ? displayOrder : 0);
      if (bannerRemoved && portalConfig.getBannerFileId() != 0) {
        layoutService.removeSiteBanner(siteName);
        portalConfig.setBannerFileId(0);
      } else if (StringUtils.isNotBlank(bannerUploadId)) {
        portalConfig.setBannerUploadId(bannerUploadId);
      }
      layoutService.save(portalConfig);
      return Response.ok(EntityBuilder.buildSiteEntity(portalConfig, request, false, null, false, false, false, getLocale(lang)))
                     .build();
    } catch (Exception e) {
      LOG.error("Error when updating the site with name {} and type {}", siteName, siteType, e);
      return Response.serverError().build();
    }
  }

  @Path("/permissions")
  @PATCH
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Operation(summary = "Update a page access and edit permission", method = "PATCH", description = "This updates the given page access and edit permission")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Page permissions updated"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Page not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updateSitePermissions(@Context
  HttpServletRequest request,
                                        @Parameter(description = "Site type", required = true)
                                        @QueryParam("siteType")
                                        String siteType,
                                        @Parameter(description = "Site name", required = true)
                                        @QueryParam("siteName")
                                        String siteName,
                                        @Parameter(description = "Site new edit permission", required = true)
                                        @QueryParam("editPermission")
                                        String editPermission,
                                        @Parameter(description = "Site new access permissions", required = true)
                                        @QueryParam("accessPermissions")
                                        String accessPermissions,
                                        @Parameter(description = "Used to retrieve the site label and description in the requested language")
                                        @QueryParam("lang")
                                        String lang) {
    try {
      if (StringUtils.isBlank(siteName) || StringUtils.isBlank(siteType)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
      }
      SiteId siteId = Util.from(new SiteKey(siteType, siteName));
      Site site = portal.getSite(siteId);
      if (site == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      if (!SiteManagementUtils.canEditSite(site)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      if (!StringUtils.isBlank(editPermission)) {
        site.setEditPermission(Util.from(editPermission));
      }
      if (!StringUtils.isBlank(accessPermissions)) {
        List<String> accessPermissionsList = List.of(accessPermissions.split(",")).stream().distinct().toList();
        site.setAccessPermission(Util.from(accessPermissionsList));
      }
      portal.saveSite(site);
      PortalConfig portalConfig = layoutService.getPortalConfig(new SiteKey(siteType, siteName));
      return Response.ok(EntityBuilder.buildSiteEntity(portalConfig, request, false, null, false, false, false, getLocale(lang)))
                     .build();
    } catch (Exception e) {
      LOG.error("Error when updating site permissions with name {} and type {}", siteName, siteType, e);
      return Response.serverError().build();
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Operation(summary = "create a site", method = "POST", description = "This create a new site")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response createSite(@Context
  HttpServletRequest request, @Parameter(description = "site to create", required = true)
  PortalConfig portalConfig,
                             @Parameter(description = "site layout template", required = true)
                             @QueryParam("siteTemplate")
                             String siteTemplate,
                             @Parameter(description = "Used to retrieve the site label and description in the requested language")
                             @QueryParam("lang")
                             String lang) {
    try {
      userPortalConfigService.createUserPortalConfig(PortalConfig.PORTAL_TYPE, portalConfig.getName(), siteTemplate);
      PortalConfig createdPortalConfig = layoutService.getPortalConfig(portalConfig.getName());
      createdPortalConfig.setDescription(portalConfig.getDescription());
      createdPortalConfig.setLabel(portalConfig.getLabel());
      createdPortalConfig.setDisplayed(portalConfig.isDisplayed());
      createdPortalConfig.setDisplayOrder(portalConfig.isDisplayed() ? portalConfig.getDisplayOrder() : 0);
      createdPortalConfig.setAccessPermissions(DEFAULT_PORTAL_ACCESS_PERMISSIONS);
      createdPortalConfig.setEditPermission(DEFAULT_PORTAL_EDIT_PERMISSIONS);
      if (StringUtils.isNotBlank(portalConfig.getBannerUploadId())) {
        createdPortalConfig.setBannerUploadId(portalConfig.getBannerUploadId());
      }
      layoutService.save(createdPortalConfig);
      return Response.ok(EntityBuilder.buildSiteEntity(layoutService.getPortalConfig(portalConfig.getName()),
                                                       request,
                                                       false,
                                                       null,
                                                       false,
                                                       false,
                                                       false,
                                                       getLocale(lang)))
                     .build();
    } catch (Exception e) {
      LOG.error("Error when creating the site with name {} and type {}", portalConfig.getName(), e);
      return Response.serverError().build();
    }
  }

    private Locale getLocale(String lang) {
        return StringUtils.isBlank(lang) ? null : Locale.forLanguageTag(lang);
    }
}
