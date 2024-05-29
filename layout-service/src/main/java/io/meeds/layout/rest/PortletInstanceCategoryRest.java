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
package io.meeds.layout.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.model.PortletInstanceCategory;
import io.meeds.layout.service.PortletInstanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/portlet/instance/categories")
@Tag(name = "/layout/rest/portlet/instance/categories", description = "Managing portlet instance categorys")
public class PortletInstanceCategoryRest {

  @Autowired
  private PortletInstanceService portletInstanceService;

  @GetMapping
  @Secured("users")
  @Operation(summary = "Retrieve portlet instance categorys", method = "GET",
             description = "This retrieves portlet instance categorys")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"), })
  public List<PortletInstanceCategory> getPortletInstanceCategorys(HttpServletRequest request) {
    return portletInstanceService.getPortletInstanceCategories(request.getRemoteUser(), request.getLocale(), true);
  }

  @GetMapping("{id}")
  @Secured("users")
  @Operation(summary = "Retrieve a portlet instance category designated by its id", method = "GET",
             description = "This will retrieve a portlet instance category designated by its id")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Request fulfilled"), })
  public PortletInstanceCategory getPortletInstanceCategory(
                                                            HttpServletRequest request,
                                                            @Parameter(description = "Portlet instance category identifier")
                                                            @PathVariable("id")
                                                            long id) {
    try {
      return portletInstanceService.getPortletInstanceCategory(id, request.getRemoteUser(), request.getLocale(), true);
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping
  @Secured("users")
  @Operation(summary = "Create a portlet instance category", method = "POST",
             description = "This creates a new portlet instance category")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "portlet instance category created"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input") })
  public PortletInstanceCategory createPortletInstanceCategory(
                                                               HttpServletRequest request,
                                                               @RequestBody
                                                               PortletInstanceCategory portletInstanceCategory) {
    try {
      return portletInstanceService.createPortletInstanceCategory(portletInstanceCategory, request.getRemoteUser());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PutMapping("{id}")
  @Secured("users")
  @Operation(summary = "Update a portlet instance category", method = "PUT",
             description = "This updates an existing portlet instance category")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "portlet instance category updated"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Object Not found") })
  public void updatePortletInstanceCategory(
                                            HttpServletRequest request,
                                            @Parameter(description = "Portlet instance category identifier")
                                            @PathVariable("id")
                                            long id,
                                            @RequestBody
                                            PortletInstanceCategory portletInstanceCategory) {
    try {
      portletInstanceCategory.setId(id);
      portletInstanceService.updatePortletInstanceCategory(portletInstanceCategory, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @DeleteMapping("{id}")
  @Secured("users")
  @Operation(summary = "Deletes a portlet instance category", method = "DELETE",
             description = "This deletes an existing portlet instance category")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "portlet instance category deleted"),
                          @ApiResponse(responseCode = "400", description = "Invalid query input"),
                          @ApiResponse(responseCode = "404", description = "Object Not found") })
  public void deletePortletInstanceCategory(
                                            HttpServletRequest request,
                                            @Parameter(description = "Portlet instance category identifier")
                                            @PathVariable("id")
                                            long id) {
    try {
      portletInstanceService.deletePortletInstanceCategory(id, request.getRemoteUser());
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

}
