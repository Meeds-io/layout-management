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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.gatein.api.Portal;
import org.gatein.api.page.PageQuery;
import org.picocontainer.Startable;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.PageType;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.State;
import org.exoplatform.portal.mop.Utils;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.portal.mop.service.DescriptionService;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.mop.service.NavigationService;
import org.exoplatform.portal.mop.storage.utils.MOPUtils;
import org.exoplatform.portal.page.PageTemplateService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.http.PATCH;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.webui.core.model.SelectItemOption;

import io.meeds.layout.rest.model.NodeLabelRestEntity;
import io.meeds.layout.utils.SiteNavigationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Path("v1/siteNavigation")
@Tag(name = "v1/siteNavigation", description = "Managing site navigation")
public class SiteNavigationRestService implements ResourceContainer, Startable {
  private static final Log         LOG                         = ExoLogger.getLogger(SiteNavigationRestService.class);

  private final NavigationService  navigationService;

  private ScheduledExecutorService scheduledExecutor;

  private final PortalContainer    container;

  private LayoutService            layoutService;

  private PageTemplateService      pageTemplateService;

  private Portal                   portal;

  private UserPortalConfigService  userPortalConfigService;

  private DescriptionService       descriptionService;

  private final Map<Long, String>  navigationNodeToDeleteQueue = new HashMap<>();

  public SiteNavigationRestService(NavigationService navigationService,
                                   PortalContainer container,
                                   LayoutService layoutService,
                                   PageTemplateService pageTemplateService,
                                   Portal portal,
                                   UserPortalConfigService userPortalConfigService,
                                   DescriptionService descriptionService) {
    this.navigationService = navigationService;
    this.container = container;
    this.layoutService = layoutService;
    this.pageTemplateService = pageTemplateService;
    this.portal = portal;
    this.userPortalConfigService = userPortalConfigService;
    this.descriptionService = descriptionService;
  }

  @POST
  @Path("node")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Create a navigation node", method = "POST", description = "This creates the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "navigation node created"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Node not found"),
      @ApiResponse(responseCode = "401", description = "User not authorized to create the navigation node"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response createNode(@Parameter(description = "navigation node id")
  @QueryParam("parentNodeId")
  Long parentNodeId,
                             @Parameter(description = "previous node id")
                             @QueryParam("previousNodeId")
                             Long previousNodeId,
                             @Parameter(description = "node label")
                             @QueryParam("nodeLabel")
                             String nodeLabel,
                             @Parameter(description = "node id")
                             @QueryParam("nodeId")
                             String nodeId,
                             @Parameter(description = "isVisible")
                             @QueryParam("isVisible")
                             boolean isVisible,
                             @Parameter(description = "isScheduled")
                             @QueryParam("isScheduled")
                             boolean isScheduled,
                             @Parameter(description = "startScheduleDate")
                             @QueryParam("startScheduleDate")
                             Long startScheduleDate,
                             @Parameter(description = "endScheduleDate")
                             @QueryParam("endScheduleDate")
                             Long endScheduleDate,
                             @Parameter(description = "page reference")
                             @QueryParam("pageRef")
                             String pageRef,
                             @Parameter(description = "node target")
                             @QueryParam("target")
                             String target,
                             @RequestBody(description = "node labels", required = true)
                             NodeLabelRestEntity nodeLabelRestEntity,
                             @Parameter(description = "isPasteMode")
                             @QueryParam("isPasteMode")
                             boolean isPasteMode,
                             @Parameter(description = "node icon")
                             @QueryParam("icon")
                             String icon) {

    if (parentNodeId == null || StringUtils.isBlank(nodeLabel) || StringUtils.isBlank(nodeId) || nodeLabelRestEntity == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
    }
    try {
      NodeData parentNodeData = navigationService.getNodeById(parentNodeId);
      if (parentNodeData == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Node data with parent node id is not found").build();
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, parentNodeData)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      Map<Locale, State> nodeLabels = new HashMap<>();
      if (nodeLabelRestEntity.getLabels() != null) {
        nodeLabel = null;
        nodeLabelRestEntity.getLabels().entrySet().forEach(label -> {
          State state = new State(label.getValue(), null);
          nodeLabels.put(new Locale(label.getKey()), state);
        });
      }
      NodeState nodeState;
      long now = System.currentTimeMillis();
      if (isVisible && isScheduled && startScheduleDate != null && endScheduleDate != null) {
        if (startScheduleDate > endScheduleDate) {
          return Response.status(Response.Status.BAD_REQUEST)
                         .entity("end schedule date must be after start schedule date")
                         .build();
        } else if (now > startScheduleDate && !isPasteMode) {
          return Response.status(Response.Status.BAD_REQUEST).entity("start schedule date must be after current date").build();
        } else {
          nodeState = new NodeState(nodeLabel,
                                    icon,
                                    startScheduleDate,
                                    endScheduleDate,
                                    Visibility.TEMPORAL,
                                    StringUtils.isBlank(pageRef) ? null : PageKey.parse(pageRef),
                                    null,
                                    target,
                                    System.currentTimeMillis());
        }
      } else {
        nodeState = new NodeState(nodeLabel,
                                  icon,
                                  -1,
                                  -1,
                                  isVisible ? Visibility.DISPLAYED : Visibility.HIDDEN,
                                  StringUtils.isBlank(pageRef) ? null : PageKey.parse(pageRef),
                                  null,
                                  target,
                                  System.currentTimeMillis());
      }
      NodeData[] nodeData = navigationService.createNode(parentNodeId, previousNodeId, nodeId, nodeState);
      descriptionService.setDescriptions(nodeData[1].getId(), nodeLabels);
      return Response.ok().entity(nodeData).build();
    } catch (Exception e) {
      LOG.error("Error when creating a new navigation node", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("node")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Update a navigation node", method = "PUT", description = "This updates the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "navigation node updated"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Node not found"),
      @ApiResponse(responseCode = "401", description = "User not authorized to update the navigation node"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response updateNode(@Parameter(description = "navigation node id")
  @QueryParam("nodeId")
  Long nodeId,
                             @Parameter(description = "node label")
                             @QueryParam("nodeLabel")
                             String nodeLabel,
                             @Parameter(description = "pageRef")
                             @QueryParam("pageRef")
                             String pageRef,
                             @Parameter(description = "node target")
                             @QueryParam("target")
                             String target,
                             @Parameter(description = "isVisible")
                             @QueryParam("isVisible")
                             boolean isVisible,
                             @Parameter(description = "isScheduled")
                             @QueryParam("isScheduled")
                             boolean isScheduled,
                             @Parameter(description = "startScheduleDate")
                             @QueryParam("startScheduleDate")
                             Long startScheduleDate,
                             @Parameter(description = "endScheduleDate")
                             @QueryParam("endScheduleDate")
                             Long endScheduleDate,
                             @RequestBody(description = "node labels", required = true)
                             NodeLabelRestEntity nodeLabelRestEntity,
                             @Parameter(description = "node icon")
                             @QueryParam("icon")
                             String icon) {
    if (nodeId == null || StringUtils.isBlank(nodeLabel) || nodeLabelRestEntity == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
    }
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Node data with node id is not found").build();
      }
      PageKey pageKey = null;
      if (!StringUtils.isBlank(pageRef)) {
        PageContext pageContext = layoutService.getPageContext(PageKey.parse(pageRef));
        if (pageContext == null) {
          return Response.status(Response.Status.NOT_FOUND).entity("Page context with page reference is not found").build();
        } else {
          pageKey = pageContext.getKey();
        }
      }

      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      Map<Locale, State> nodeLabels = new HashMap<>();
      if (nodeLabelRestEntity.getLabels() != null) {
        nodeLabel = null;
        nodeLabelRestEntity.getLabels().entrySet().forEach(label -> {
          State state = new State(label.getValue(), null);
          nodeLabels.put(new Locale(label.getKey()), state);
        });
      }
      NodeState nodeState;
      long now = System.currentTimeMillis();
      if (isVisible && isScheduled && startScheduleDate != null && endScheduleDate != null) {
        if (startScheduleDate > endScheduleDate) {
          return Response.status(Response.Status.BAD_REQUEST)
                         .entity("end schedule date must be after start schedule date")
                         .build();
        } else if (now > startScheduleDate) {
          return Response.status(Response.Status.BAD_REQUEST).entity("start schedule date must be after current date").build();
        } else {
          nodeState = new NodeState(nodeLabel,
                                    icon,
                                    startScheduleDate,
                                    endScheduleDate,
                                    Visibility.TEMPORAL,
                                    pageKey,
                                    null,
                                    target,
                                    System.currentTimeMillis());
        }
      } else {
        nodeState = new NodeState(nodeLabel,
                                  icon,
                                  -1,
                                  -1,
                                  isVisible ? Visibility.DISPLAYED : Visibility.HIDDEN,
                                  pageKey,
                                  null,
                                  target,
                                  System.currentTimeMillis());
      }
      descriptionService.setDescriptions(String.valueOf(nodeId), nodeLabels);
      navigationService.updateNode(nodeId, nodeState);
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error("Error when updating a navigation node", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("node/{nodeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Delete a navigation node ", method = "DELETE", description = "This deletes the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "navigation node deleted"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Node not found"),
      @ApiResponse(responseCode = "401", description = "User not authorized to delete the navigation node"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response deleteNode(@Context
  HttpServletRequest request,
                             @Parameter(description = "Node id", required = true)
                             @PathParam("nodeId")
                             Long nodeId,
                             @Parameter(description = "Time to effectively delete navigation node", required = false)
                             @QueryParam("delay")
                             long delay) {
    try {
      if (nodeId == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Node id is mandatory").build();
      }
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Node data with node id is not found").build();
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      if (delay > 0) {
        navigationNodeToDeleteQueue.put(nodeId, currentIdentity.getUserId());
        scheduledExecutor.schedule(() -> {
          if (navigationNodeToDeleteQueue.containsKey(nodeId)) {
            ExoContainerContext.setCurrentContainer(container);
            RequestLifeCycle.begin(container);
            try {
              navigationNodeToDeleteQueue.remove(nodeId);
              navigationService.deleteNode(nodeId);
            } catch (Exception e) {
              LOG.warn("Error when deleting the navigation node with id {}", nodeId, e);
            } finally {
              RequestLifeCycle.end();
            }
          }
        }, delay, TimeUnit.SECONDS);
      } else {
        navigationNodeToDeleteQueue.remove(nodeId);
        navigationService.deleteNode(nodeId);
      }
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error("Error when deleting the navigation node with id {}", nodeId, e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @Path("node/{nodeId}/undoDelete")
  @POST
  @RolesAllowed("users")
  @Operation(summary = "Undo delete a navigation node if not yet effectively deleted", method = "POST", description = "This undo deletes the given navigation node if not yet effectively deleted")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Node not found"),
      @ApiResponse(responseCode = "403", description = "Forbidden operation"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response undoDeleteNode(@Context
  HttpServletRequest request,
                                 @Parameter(description = "Node identifier", required = true)
                                 @PathParam("nodeId")
                                 Long nodeId) {
    try {
      if (nodeId == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Node id is mandatory").build();
      }
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Node data with node id is not found").build();
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      if (navigationNodeToDeleteQueue.containsKey(nodeId)) {
        String authenticatedUser = request.getRemoteUser();
        String originalModifierUser = navigationNodeToDeleteQueue.get(nodeId);
        if (!originalModifierUser.equals(authenticatedUser)) {
          LOG.warn("User {} attempts to cancel deletion of navigation node deleted by user {}",
                   authenticatedUser,
                   originalModifierUser);
          return Response.status(Response.Status.FORBIDDEN).build();
        }
        navigationNodeToDeleteQueue.remove(nodeId);
        return Response.noContent().build();
      } else {
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Node with id {} was already deleted or isn't planned to be deleted" + nodeId)
                       .build();
      }
    } catch (Exception e) {
      LOG.error("Error when undo deleting the navigation node with id {}", nodeId, e);
      return Response.serverError().build();
    }
  }

  @Path("node/{nodeId}/labels")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieve node labels", method = "GET", description = "This retrieves node labels")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getNodeLabels(@Context
  HttpServletRequest request,
                                @Parameter(description = "Node id", required = true)
                                @PathParam("nodeId")
                                Long nodeId) {
    try {
      Map<Locale, State> nodeLabels = descriptionService.getDescriptions(String.valueOf(nodeId));
      NodeLabelRestEntity nodeLabelRestEntity = EntityBuilder.toNodeLabelRestEntity(nodeLabels);
      return Response.ok().entity(nodeLabelRestEntity).build();
    } catch (Exception e) {
      LOG.error("Error when retrieving node labels", e);
      return Response.serverError().build();
    }
  }

  @PATCH
  @Path("/node/move")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Move a navigation node", method = "PATCH", description = "This moves the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "401", description = "User not authorized to move the navigation node"),
      @ApiResponse(responseCode = "404", description = "Node not found") })
  public Response moveNode(@Parameter(description = "node id")
  @QueryParam("nodeId")
  Long nodeId,
                           @Parameter(description = "destination parent id")
                           @QueryParam("destinationParentId")
                           Long destinationParentId,
                           @Parameter(description = "previous id")
                           @QueryParam("previousNodeId")
                           Long previousNodeId) {
    try {
      if (nodeId == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Node id is mandatory").build();
      }
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Node data with node id is not found").build();
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      long parentId = Long.parseLong(nodeData.getParentId());
      if (destinationParentId == null) {
        destinationParentId = parentId;
      }
      navigationService.moveNode(nodeId, parentId, destinationParentId, previousNodeId);
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error("Error when moving the navigation node with id {}", nodeId, e);
      return Response.serverError().build();
    }
  }

  @Path("/page/permissions")
  @PATCH
  @RolesAllowed("users")
  @Operation(summary = "Update a page access and edit permission", method = "PATCH", description = "This updates the given page access and edit permission")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Page permissions updated"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "404", description = "Page not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updatePagePermissions(@Context
  HttpServletRequest request,
                                        @Parameter(description = "Page reference", required = true)
                                        @QueryParam("pageRef")
                                        String pageRef,
                                        @Parameter(description = "Page new edit permission", required = true)
                                        @QueryParam("editPermission")
                                        String editPermission,
                                        @Parameter(description = "Page new access permissions", required = true)
                                        @QueryParam("accessPermissions")
                                        String accessPermissions) {
    try {
      if (StringUtils.isBlank(pageRef) || StringUtils.isBlank(editPermission) || StringUtils.isBlank(accessPermissions)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
      }
      PageContext pageContext = layoutService.getPageContext(PageKey.parse(pageRef));
      if (pageContext == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      if (!SiteNavigationUtils.canEditPage(pageContext)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      PageState pageState = pageContext.getState();
      List<String> accessPermissionsList = List.of(accessPermissions.split(",")).stream().distinct().toList();
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
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error("Error when updating page permissions with reference {}", pageRef, e);
      return Response.serverError().build();
    }
  }

  @Path("/page/templates")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieve page templates", method = "GET", description = "This retrieves page templates")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getPageTemplates(@Context
  HttpServletRequest httpRequest) {
    try {
      Locale locale = httpRequest.getLocale();
      List<SelectItemOption<String>> pageTemplates = pageTemplateService.getPageTemplates();
      return Response.ok().entity(EntityBuilder.toPageTemplateRestEntities(pageTemplates, locale)).build();
    } catch (Exception e) {
      LOG.error("Error when retrieving page templates", e);
      return Response.serverError().build();
    }
  }

  @Path("/pages")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieve pages", method = "GET", description = "This retrieves pages")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getPages(@Context
  HttpServletRequest httpRequest,
                           @Parameter(description = "Portal site type, possible values: PORTAL, GROUP or USER")
                           @QueryParam("siteType")
                           String siteType,
                           @Parameter(description = "Portal site name")
                           @QueryParam("siteName")
                           String siteName,
                           @Parameter(description = "page display name")
                           @QueryParam("pageDisplayName")
                           String pageDisplayName) {
    try {
      org.gatein.api.site.SiteType selectedSiteType = null;
      if (!StringUtils.isBlank(siteType)) {
        selectedSiteType = MOPUtils.convertSiteType(SiteType.valueOf(siteType.toUpperCase()));
      }
      PageQuery pageQuery = new PageQuery.Builder().withSiteType(selectedSiteType)
                                                   .withSiteName(siteName)
                                                   .withDisplayName(pageDisplayName)
                                                   .build();
      List<org.gatein.api.page.Page> pages = portal.findPages(pageQuery);
      return Response.ok().entity(pages).build();
    } catch (Exception e) {
      LOG.error("Error when retrieving pages", e);
      return Response.serverError().build();
    }
  }

  @Path("/page")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Create a page", method = "POST", description = "This creates the page")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "page created"),
      @ApiResponse(responseCode = "400", description = "Invalid query input"),
      @ApiResponse(responseCode = "500", description = "Internal server error") })
  public Response createPage(@Parameter(description = "page name", required = true)
  @QueryParam("pageName")
  String pageName,
                             @Parameter(description = "page name", required = true)
                             @QueryParam("pageTitle")
                             String pageTitle,
                             @Parameter(description = "page site type", required = true)
                             @QueryParam("pageSiteType")
                             String pageSiteType,
                             @Parameter(description = "page site name", required = true)
                             @QueryParam("pageSiteName")
                             String pageSiteName,
                             @Parameter(description = "Page Type : GROUP, PAGE OR LINK", required = true)
                             @QueryParam("pageType")
                             String pageType,
                             @Parameter(description = "link")
                             @QueryParam("link")
                             String link,
                             @Parameter(description = "page template : blank , normal, analytics ...")
                             @QueryParam("pageTemplate")
                             String pageTemplate) {
    if (StringUtils.isBlank(pageName) || StringUtils.isBlank(pageTitle) || StringUtils.isBlank(pageType)
        || StringUtils.isBlank(pageSiteName) || StringUtils.isBlank(pageSiteType)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
    }
    try {
      pageName = pageName + "_" + UUID.randomUUID();
      Page page;
      if (PageType.PAGE.equals(PageType.valueOf(pageType))) {
        if (StringUtils.isBlank(pageTemplate)) {
          return Response.status(Response.Status.BAD_REQUEST).entity("pageTemplate param is mandatory for PAGE pageType").build();
        }
        page = userPortalConfigService.createPageTemplate(pageTemplate, pageSiteType, pageSiteName);
      } else {
        page = new Page(pageSiteType, pageSiteName, pageName);
      }
      page.setName(pageName);
      page.setTitle(pageTitle);
      page.setType(pageType);
      page.setLink(PageType.LINK.equals(PageType.valueOf(pageType)) ? link : null);
      setDefaultPermission(page, new SiteKey(pageSiteType, pageSiteName));
      PageState pageState = Utils.toPageState(page);
      layoutService.save(new PageContext(page.getPageKey(), pageState), page);
      PageContext createdPage = layoutService.getPageContext(page.getPageKey());
      return Response.ok().entity(createdPage).build();
    } catch (Exception e) {
      LOG.error("Error when creating a new page", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }
  
  @Path("/page")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Retrieve page by reference", method = "GET", description = "This retrieves page by reference")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response getPageByRef(@Context
  HttpServletRequest httpRequest,
                                @Parameter(description = "page reference", required = true)
                                @QueryParam("pageRef")
                                String pageRef) {
    try {
      if (StringUtils.isBlank(pageRef)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
      }
      return Response.ok().entity(layoutService.getPageContext(PageKey.parse(pageRef))).build();
    } catch (Exception e) {
      LOG.error("Error when retrieving page with reference {} ", pageRef, e);
      return Response.serverError().build();
    }
  }
  
  @Path("/page/link")
  @PATCH
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Operation(summary = "Update page link", method = "GET", description = "This updates page link")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
      @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public Response updatePageLink(@Context
  HttpServletRequest httpRequest,
                                 @Parameter(description = "page display name", required = true)
                                 @QueryParam("pageRef")
                                 String pageRef,
                                 @Parameter(description = "page new Link")
                                 @QueryParam("link")
                                 String link) {
    try {
      if (StringUtils.isBlank(pageRef) || StringUtils.isBlank(link)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("params are mandatory").build();
      }
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
      return Response.ok().build();
    } catch (Exception e) {
      LOG.error("Error when updating  page link with reference {} ", pageRef, e);
      return Response.serverError().build();
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
  @Override
  public void start() {
    scheduledExecutor = Executors.newScheduledThreadPool(1);
  }

  @Override
  public void stop() {
    if (scheduledExecutor != null) {
      scheduledExecutor.shutdown();
    }
  }
}
