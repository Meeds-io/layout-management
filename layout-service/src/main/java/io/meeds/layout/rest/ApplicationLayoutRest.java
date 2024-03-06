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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.model.ApplicationRenderResponse;
import io.meeds.layout.service.ApplicationLayoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("applications")
@Tag(name = "applications", description = "Managing page applications")
public class ApplicationLayoutRest {

  @Autowired
  private ApplicationLayoutService applicationLayoutService;

  @GetMapping("{portletName}/preview")
  @Secured("users")
  @Operation(summary = "Get application content in HTML/JS/CSS",
             method = "GET",
             description = "This method will return the application content with HTML/JS/CSS")
  @ApiResponses(value = {
                          @ApiResponse(responseCode = "200", description = "Request fulfilled"),
                          @ApiResponse(responseCode = "404", description = "Not found"),
                          @ApiResponse(responseCode = "403", description = "Forbidden operation")
  })
  public ApplicationRenderResponse getApplicationRenderContent(
                                                               HttpServletRequest request, 
                                                               HttpServletResponse response,
                                                               @Parameter(description = "Portlet Application name", required = true)
                                                               @PathVariable("portletName")
                                                               String portletName,
                                                               @Parameter(description = "Navigation node id. Used to apply permissions of page rather than app registry permissions", required = false)
                                                               @RequestParam(name = "nodeId", required = false)
                                                               long nodeId,
                                                               @Parameter(description = "Portlet Storage identifier", required = false)
                                                               @RequestParam(name = "storageId", required = false)
                                                               String storageId) {
    try {
      return applicationLayoutService.getApplicationRenderContent(nodeId,
                                                                  portletName,
                                                                  storageId,
                                                                  request,
                                                                  response);
    } catch (ObjectNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

}
