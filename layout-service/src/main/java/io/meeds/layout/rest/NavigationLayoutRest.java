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

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.mop.navigation.NodeData;

import io.meeds.layout.model.NavigationCreateModel;
import io.meeds.layout.model.NavigationUpdateModel;
import io.meeds.layout.model.NodeLabel;
import io.meeds.layout.service.NavigationLayoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("navigations")
@Tag(name = "navigations", description = "Managing site navigation")
public class NavigationLayoutRest {

  @Autowired
  private NavigationLayoutService navigationLayoutService;

  @PostMapping
  @Secured("users")
  @Operation(summary = "Create a navigation node", method = "POST", description = "This creates the given navigation node")
  @ApiResponses(value = {
                         @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                         @ApiResponse(responseCode = "400", description = "Invalid request input"),
                         @ApiResponse(responseCode = "403", description = "Forbidden"),
                         @ApiResponse(responseCode = "404", description = "Not found"),
 })
  public NodeData createNode(
                             HttpServletRequest request,
                             @RequestBody
                             NavigationCreateModel createModel) {
    try {
      return navigationLayoutService.createNode(createModel, request.getRemoteUser());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PostMapping("{nodeId}/draft")
  @Secured("users")
  @Operation(summary = "Creates a draft navigation node",
             method = "POST",
             description = "This creates a daft node with page based on an existing navigation node")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "403", description = "Forbidden"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public NodeData createDraftNode(
                                  HttpServletRequest request,
                                  @Parameter(description = "navigation node id")
                                  @PathVariable("nodeId")
                                  Long nodeId) {
    try {
      return navigationLayoutService.createDraftNode(nodeId, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PutMapping("{nodeId}")
  @Secured("users")
  @Operation(summary = "Update a navigation node", method = "PUT", description = "This updates the given navigation node")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "400", description = "Invalid request input"),
                          @ApiResponse(responseCode = "403", description = "Forbidden"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public void updateNode(
                         HttpServletRequest request,
                         @Parameter(description = "navigation node id")
                         @PathVariable("nodeId")
                         Long nodeId,
                         @RequestBody
                         NavigationUpdateModel updateModel) {
    try {
      navigationLayoutService.updateNode(nodeId, updateModel, request.getRemoteUser());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @DeleteMapping("{nodeId}")
  @Secured("users")
  @Operation(summary = "Delete a navigation node ", method = "DELETE", description = "This deletes the given navigation node")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "403", description = "Forbidden"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public void deleteNode(
                         HttpServletRequest request,
                         @Parameter(description = "Node id", required = true)
                         @PathVariable("nodeId")
                         Long nodeId,
                         @Parameter(description = "Time to effectively delete navigation node", required = false)
                         @RequestParam("delay")
                         long delay) {
    try {
      navigationLayoutService.deleteNode(nodeId, delay, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PostMapping("{nodeId}/undoDelete")
  @Secured("users")
  @Operation(summary = "Undo delete a navigation node if not yet effectively deleted", method = "POST",
             description = "This undo deletes the given navigation node if not yet effectively deleted")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public void undoDeleteNode(
                             HttpServletRequest request,
                             @Parameter(description = "Node identifier", required = true)
                             @PathVariable("nodeId")
                             Long nodeId) {
    try {
      navigationLayoutService.undoDeleteNode(nodeId, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PatchMapping(value = "/{nodeId}/move", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  @Secured("users")
  @Operation(summary = "Move a navigation node", method = "PATCH", description = "This moves the given navigation node")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "403", description = "User not authorized to move the navigation node"),
                          @ApiResponse(responseCode = "404", description = "Node not found") })
  public void moveNode(
                       HttpServletRequest request,
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
      navigationLayoutService.moveNode(nodeId, destinationParentId, previousNodeId, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @GetMapping("{nodeId}")
  @Operation(summary = "Retrieve node labels", method = "GET", description = "This retrieves node labels")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "403", description = "Forbidden"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public NodeData getNode(
                          HttpServletRequest request,
                          @Parameter(description = "Node id", required = true)
                          @PathVariable("nodeId")
                          Long nodeId) {
    try {
      return navigationLayoutService.getNode(nodeId, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @GetMapping("{nodeId}/uri")
  @Operation(summary = "Retrieve node uri", method = "GET",
             description = "This retrieves node Uri that will allow to access the page")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "403", description = "Forbidden"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public String getNodeUri(
                           HttpServletRequest request,
                           @Parameter(description = "Node id", required = true)
                           @PathVariable("nodeId")
                           Long nodeId) {
    try {
      return navigationLayoutService.getNodeUri(nodeId, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @GetMapping("{nodeId}/labels")
  @Operation(summary = "Retrieve node labels", method = "GET", description = "This retrieves node labels")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "403", description = "Forbidden"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
  })
  public NodeLabel getNodeLabels(
                                 HttpServletRequest request,
                                 @Parameter(description = "Node id", required = true)
                                 @PathVariable("nodeId")
                                 Long nodeId) {
    try {
      return navigationLayoutService.getNodeLabels(nodeId, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

}
