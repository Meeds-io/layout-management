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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.exoplatform.portal.mop.State;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.DescriptionService;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.mop.service.NavigationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import io.meeds.common.ContainerTransactional;
import io.meeds.layout.rest.model.NavigationCreateModel;
import io.meeds.layout.rest.model.NavigationUpdateModel;
import io.meeds.layout.rest.model.NodeLabelRestEntity;
import io.meeds.layout.rest.util.EntityBuilder;
import io.meeds.layout.utils.SiteNavigationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("navigations")
@Tag(name = "navigations", description = "Managing site navigation")
public class SiteNavigationRestService {

  private static final String     NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND = "Node data with node id is not found";

  private static final Log        LOG                                 = ExoLogger.getLogger(SiteNavigationRestService.class);

  @Autowired
  private NavigationService       navigationService;

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private DescriptionService      descriptionService;

  private final Map<Long, String> navigationNodeToDeleteQueue         = new HashMap<>();

  @PostMapping
  @Secured("users")
  @Operation(summary = "Create a navigation node", method = "POST", description = "This creates the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "navigation node created"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Node not found"),
                          @ApiResponse(responseCode = "401", description = "User not authorized to create the navigation node"),
                          @ApiResponse(responseCode = "500", description = "Internal server error") })
  public NodeData createNode(// NOSONAR
                             @RequestBody
                             NavigationCreateModel createModel) {
    try {
      NodeData parentNodeData = navigationService.getNodeById(createModel.getParentNodeId());
      if (parentNodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Node data with parent node id is not found");
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, parentNodeData)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Map<Locale, State> nodeLabels = new HashMap<>();
      if (createModel.getLabels() != null) {
        createModel.getLabels().entrySet().forEach(label -> {
          State state = new State(label.getValue(), null);
          nodeLabels.put(new Locale(label.getKey()), state);
        });
      }
      NodeState nodeState;
      long now = System.currentTimeMillis();
      if (createModel.isVisible()
          && createModel.isScheduled()
          && createModel.getStartScheduleDate() != null
          && createModel.getEndScheduleDate() != null) {
        if (createModel.getStartScheduleDate() > createModel.getEndScheduleDate()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end schedule date must be after start schedule date");
        } else if (now > createModel.getStartScheduleDate() && !createModel.isPasteMode()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start schedule date must be after current date");
        } else {
          nodeState = new NodeState(createModel.getNodeLabel(),
                                    createModel.getIcon(),
                                    createModel.getStartScheduleDate(),
                                    createModel.getEndScheduleDate(),
                                    Visibility.TEMPORAL,
                                    StringUtils.isBlank(createModel.getPageRef()) ? null :
                                                                                  PageKey.parse(createModel.getPageRef()),
                                    null,
                                    createModel.getTarget(),
                                    System.currentTimeMillis());
        }
      } else {
        nodeState = new NodeState(createModel.getNodeLabel(),
                                  createModel.getIcon(),
                                  -1,
                                  -1,
                                  createModel.isVisible() ? Visibility.DISPLAYED : Visibility.HIDDEN,
                                  StringUtils.isBlank(createModel.getPageRef()) ? null : PageKey.parse(createModel.getPageRef()),
                                  null,
                                  createModel.getTarget(),
                                  System.currentTimeMillis());
      }
      NodeData[] nodeData = navigationService.createNode(createModel.getParentNodeId(),
                                                         createModel.getPreviousNodeId(),
                                                         createModel.getNodeId(),
                                                         nodeState);
      descriptionService.setDescriptions(nodeData[1].getId(), nodeLabels);
      return nodeData[1];
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      LOG.warn("Error when creating a new navigation node {}", createModel, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PutMapping("{nodeId}")
  @Secured("users")
  @Operation(summary = "Update a navigation node", method = "PUT", description = "This updates the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "navigation node updated"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Node not found"),
                          @ApiResponse(responseCode = "401", description = "User not authorized to update the navigation node"),
                          @ApiResponse(responseCode = "500", description = "Internal server error") })
  public void updateNode( // NOSONAR
                         @Parameter(description = "navigation node id")
                         @PathVariable("nodeId")
                         Long nodeId,
                         @RequestBody
                         NavigationUpdateModel updateModel) {
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND);
      }
      PageKey pageKey = null;
      if (!StringUtils.isBlank(updateModel.getPageRef())) {
        PageContext pageContext = layoutService.getPageContext(PageKey.parse(updateModel.getPageRef()));
        if (pageContext == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page context with page reference is not found");
        } else {
          pageKey = pageContext.getKey();
        }
      }

      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Map<Locale, State> nodeLabels = new HashMap<>();
      if (updateModel.getLabels() != null) {
        updateModel.getLabels().entrySet().forEach(label -> {
          State state = new State(label.getValue(), null);
          nodeLabels.put(new Locale(label.getKey()), state);
        });
      }
      NodeState nodeState;
      long now = System.currentTimeMillis();
      if (updateModel.isVisible() && updateModel.isScheduled()
          && updateModel.getStartScheduleDate() != null
          && updateModel.getEndScheduleDate() != null) {
        if (updateModel.getStartScheduleDate() > updateModel.getEndScheduleDate()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end schedule date must be after start schedule date");
        } else if (now > updateModel.getStartScheduleDate()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start schedule date must be after current date");
        } else {
          nodeState = new NodeState(updateModel.getNodeLabel(),
                                    updateModel.getIcon(),
                                    updateModel.getStartScheduleDate(),
                                    updateModel.getEndScheduleDate(),
                                    Visibility.TEMPORAL,
                                    pageKey,
                                    null,
                                    updateModel.getTarget(),
                                    System.currentTimeMillis());
        }
      } else {
        nodeState = new NodeState(updateModel.getNodeLabel(),
                                  updateModel.getIcon(),
                                  -1,
                                  -1,
                                  updateModel.isVisible() ? Visibility.DISPLAYED : Visibility.HIDDEN,
                                  pageKey,
                                  null,
                                  updateModel.getTarget(),
                                  System.currentTimeMillis());
      }
      descriptionService.setDescriptions(String.valueOf(nodeId), nodeLabels);
      navigationService.updateNode(nodeId, nodeState);
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      LOG.warn("Error when updating a navigation node", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @DeleteMapping("{nodeId}")
  @Secured("users")
  @Operation(summary = "Delete a navigation node ", method = "DELETE", description = "This deletes the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "navigation node deleted"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Node not found"),
                          @ApiResponse(responseCode = "401", description = "User not authorized to delete the navigation node"),
                          @ApiResponse(responseCode = "500", description = "Internal server error") })
  public void deleteNode(
                         HttpServletRequest request,
                         @Parameter(description = "Node id", required = true)
                         @PathVariable("nodeId")
                         Long nodeId,
                         @Parameter(description = "Time to effectively delete navigation node", required = false)
                         @RequestParam("delay")
                         long delay) {
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND);
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }

      navigationNodeToDeleteQueue.put(nodeId, currentIdentity.getUserId());
      if (delay > 0) {
        CompletableFuture.delayedExecutor(delay, TimeUnit.SECONDS).execute(() -> deleteNode(nodeId));
      } else {
        deleteNode(nodeId);
      }
    } catch (Exception e) {
      LOG.warn("Error when deleting the navigation node with id {}", nodeId, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping("{nodeId}/undoDelete")
  @Secured("users")
  @Operation(summary = "Undo delete a navigation node if not yet effectively deleted", method = "POST",
             description = "This undo deletes the given navigation node if not yet effectively deleted")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Node not found"),
                          @ApiResponse(responseCode = "403", description = "Forbidden operation"),
                          @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public void undoDeleteNode(
                             HttpServletRequest request,
                             @Parameter(description = "Node identifier", required = true)
                             @PathVariable("nodeId")
                             Long nodeId) {
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND);
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }

      if (navigationNodeToDeleteQueue.containsKey(nodeId)) {
        String authenticatedUser = request.getRemoteUser();
        String originalModifierUser = navigationNodeToDeleteQueue.get(nodeId);
        if (!originalModifierUser.equals(authenticatedUser)) {
          LOG.warn("User {} attempts to cancel deletion of navigation node deleted by user {}",
                   authenticatedUser,
                   originalModifierUser);
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        navigationNodeToDeleteQueue.remove(nodeId);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      LOG.warn("Error when undo deleting the navigation node with id {}", nodeId, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PatchMapping(value = "/{nodeId}/move", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @Secured("users")
  @Operation(summary = "Move a navigation node", method = "PATCH", description = "This moves the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "401", description = "User not authorized to move the navigation node"),
                          @ApiResponse(responseCode = "404", description = "Node not found") })
  public void moveNode(
                       @Parameter(description = "node id")
                       @PathVariable("nodeId")
                       Long nodeId,
                       @Parameter(description = "destination parent id")
                       @RequestParam(name = "destinationParentId", required = false)
                       Long destinationParentId,
                       @Parameter(description = "previous id")
                       @RequestParam(name = "previousNodeId", required = false)
                       Long previousNodeId) {
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND);
      }
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      if (!SiteNavigationUtils.canEditNavigation(currentIdentity, nodeData)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      long parentId = Long.parseLong(nodeData.getParentId());
      if (destinationParentId == null) {
        destinationParentId = parentId;
      }
      navigationService.moveNode(nodeId, parentId, destinationParentId, previousNodeId);
    } catch (Exception e) {
      LOG.warn("Error when moving the navigation node with id {}", nodeId, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("{nodeId}")
  @Secured("users")
  @Operation(summary = "Retrieve node labels", method = "GET", description = "This retrieves node labels")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public NodeData getNode(
                          HttpServletRequest request,
                          @Parameter(description = "Node id", required = true)
                          @PathVariable("nodeId")
                          Long nodeId) {
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      } else if (!SiteNavigationUtils.canViewNavigation(nodeData)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
      return nodeData;
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("Error when retrieving node {}", nodeId, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @GetMapping("{nodeId}/labels")
  @Secured("users")
  @Operation(summary = "Retrieve node labels", method = "GET", description = "This retrieves node labels")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "500", description = "Internal server error"), })
  public NodeLabelRestEntity getNodeLabels(
                                           HttpServletRequest request,
                                           @Parameter(description = "Node id", required = true)
                                           @PathVariable("nodeId")
                                           Long nodeId) {
    try {
      NodeData nodeData = navigationService.getNodeById(nodeId);
      if (nodeData == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      } else if (!SiteNavigationUtils.canViewNavigation(nodeData)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
      Map<Locale, State> nodeLabels = descriptionService.getDescriptions(String.valueOf(nodeId));
      return EntityBuilder.toNodeLabelRestEntity(nodeLabels);
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("Error when retrieving node {} labels", nodeId, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @ContainerTransactional
  public void deleteNode(long nodeId) {
    if (navigationNodeToDeleteQueue.containsKey(nodeId)) {
      try {
        navigationService.deleteNode(nodeId);
      } finally {
        navigationNodeToDeleteQueue.remove(nodeId);
      }
    }
  }

}
