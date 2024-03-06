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
package io.meeds.layout.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.StatefulPortletContext;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.pc.portlet.impl.spi.AbstractClientContext;
import org.gatein.pc.portlet.impl.spi.AbstractPortalContext;
import org.gatein.pc.portlet.impl.spi.AbstractSecurityContext;
import org.gatein.pc.portlet.impl.spi.AbstractWindowContext;
import org.gatein.portal.controller.resource.ResourceId;
import org.gatein.portal.controller.resource.ResourceRequestHandler;
import org.gatein.portal.controller.resource.ResourceScope;
import org.gatein.portal.controller.resource.script.ScriptResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.pc.ExoPortletState;
import org.exoplatform.portal.pc.ExoPortletStateType;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;
import org.exoplatform.portal.resource.SkinConfig;
import org.exoplatform.portal.resource.SkinService;
import org.exoplatform.portal.webui.application.ExoPortletInstanceContext;
import org.exoplatform.portal.webui.application.ExoServerContext;
import org.exoplatform.services.portletcontainer.PortletContainerException;
import org.exoplatform.web.ControllerContext;
import org.exoplatform.web.PortalHttpServletResponseWrapper;
import org.exoplatform.web.application.javascript.JavascriptConfigService;

import io.meeds.layout.model.ApplicationRenderResponse;
import io.meeds.layout.model.PortletInvocationUserContext;
import io.meeds.layout.model.PortletRenderInvocationContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

@Service
public class ApplicationLayoutService {

  @Autowired
  private LayoutService              layoutService;

  @Autowired
  private LayoutAclService           layoutAclService;

  @Autowired
  private PageLayoutService          pageLayoutService;

  @Autowired
  private NavigationLayoutService    navigationLayoutService;

  @Autowired
  private ApplicationRegistryService applicationRegistryService;

  @Autowired
  private SkinService                skinService;

  @Autowired
  private JavascriptConfigService    javascriptConfigService;

  private PortletInvoker             portletInvoker;

  @SneakyThrows
  public ApplicationRenderResponse getApplicationRenderContent(long nodeId,
                                                               String portletName,
                                                               String portletStorageId,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) throws IllegalAccessException,
                                                                                             ObjectNotFoundException {
    String username = request.getRemoteUser();
    Locale locale = request.getLocale();

    Application application = applicationRegistryService.getApplicationCategories()
                                                        .stream()
                                                        .flatMap(c -> c.getApplications().stream())
                                                        .filter(app -> StringUtils.equals(app.getApplicationName(),
                                                                                          portletName))
                                                        .findFirst()
                                                        .orElseThrow(() -> new ObjectNotFoundException(String.format("Application with name %s wasn't found",
                                                                                                                     portletName)));

    if (nodeId > 0) {
      NodeData node = navigationLayoutService.getNode(nodeId, username);
      PageContext page = pageLayoutService.getPage(node.getState().getPageRef(), username);
      if (page == null) {
        return null;
      }
    } else if (!layoutAclService.canViewApplication(application, username)) {
      throw new IllegalAccessException(String.format("Not allowed to access application with name %s",
                                                     portletName));
    }

    String contentId = application.getContentId();

    ApplicationState<Portlet> applicationState;
    if (StringUtils.isBlank(portletStorageId)) {
      Portlet preferences = new Portlet();
      applicationState = new TransientApplicationState<>(contentId, preferences);
    } else {
      org.exoplatform.portal.config.model.Application<Portlet> applicationModel =
                                                                                layoutService.getApplicationModel(portletStorageId);
      if (applicationModel == null) {
        throw new ObjectNotFoundException(String.format("Application with name %s and id %s wasn't found",
                                                        portletName,
                                                        portletStorageId));
      }
      applicationState = applicationModel.getState();
    }

    RenderInvocation invocation = buildInvocation(request, response, applicationState, contentId);
    try {
      setupContext();
      PortletInvocationResponse renderResponse = getPortletInvoker().invoke(invocation);
      String markup = generateRenderMarkup(renderResponse);
      ScriptResource jsModule = javascriptConfigService.getResource(new ResourceId(ResourceScope.PORTLET, contentId));
      SkinConfig skin = skinService.getSkin(contentId, skinService.getDefaultSkin());
      return new ApplicationRenderResponse(markup,
                                           jsModule == null ? null : contentId,
                                           createSkinUrl(skin));
    } finally {

    }
  }

  private void setupContext() {
//    PortalHttpServletResponseWrapper httpServletResponseWrapper = new PortalHttpServletResponseWrapper(res);
//    processed = handler.execute(new ControllerContext(this, router, req, httpServletResponseWrapper, parameters));
  }

  private void clearContext() {
    
  }

  private RenderInvocation buildInvocation(HttpServletRequest request,
                                           HttpServletResponse response,
                                           ApplicationState<Portlet> applicationState,
                                           String contentId) {
    String username = request.getRemoteUser();
    Locale locale = request.getLocale();

    StatefulPortletContext<ExoPortletState> portletContext = getPortletContext(contentId, applicationState);
    PortletInvocationContext invocationContext = new PortletRenderInvocationContext();
    RenderInvocation invocation = new RenderInvocation(invocationContext);
    invocation.setMode(Mode.VIEW);
    invocation.setWindowState(org.gatein.pc.api.WindowState.MAXIMIZED);
    invocation.setResponse(response);
    invocation.setRequest(request);
    invocation.setClientContext(new AbstractClientContext(request));
    invocation.setTarget(portletContext);
    invocation.setInstanceContext(new ExoPortletInstanceContext(portletContext.getId()));
    invocation.setServerContext(new ExoServerContext(request, response));
    invocation.setUserContext(new PortletInvocationUserContext(username, locale));
    invocation.setWindowContext(new AbstractWindowContext(UUID.randomUUID().toString()));
    invocation.setPortalContext(new AbstractPortalContext(Collections.singletonMap("javax.portlet.markup.head.element.support",
                                                                                   "true")));
    invocation.setSecurityContext(new AbstractSecurityContext(request));
    return invocation;
  }

  public String generateRenderMarkup(PortletInvocationResponse invocationResponse) throws PortletContainerException {
    if (invocationResponse instanceof ContentResponse contentResponse) {
      switch (contentResponse.getType()) {
      case ContentResponse.TYPE_CHARS:
        return contentResponse.getContent();
      case ContentResponse.TYPE_BYTES:
        return new String(contentResponse.getBytes(), StandardCharsets.UTF_8);
      default:
        return "";
      }
    } else if (invocationResponse instanceof ErrorResponse errorResponse) {
      throw new PortletContainerException(errorResponse.getMessage(), errorResponse.getCause());
    } else {
      throw new PortletContainerException("Unknown invocation response type [" + invocationResponse.getClass() +
          "]. Expected a FragmentResponse or an ErrorResponse");
    }
  }

  private StatefulPortletContext<ExoPortletState> getPortletContext(String portletFqn,
                                                                    ApplicationState<Portlet> applicationState) {
    PortletContext producerOfferedPortletContext = getProducerOfferedPortletContext(portletFqn);
    ExoPortletState map = new ExoPortletState(producerOfferedPortletContext.getId());
    Portlet preferences = layoutService.load(applicationState, ApplicationType.PORTLET);
    if (preferences != null) {
      for (Preference pref : preferences) {
        map.getState().put(pref.getName(), pref.getValues());
      }
    }
    return StatefulPortletContext.create(PortletContext.LOCAL_CONSUMER_CLONE.getId(), ExoPortletStateType.getInstance(), map);
  }

  private PortletContext getProducerOfferedPortletContext(String portletFqn) {
    String[] portletFqnParts = portletFqn.split("/");
    return PortletContext.reference(PortletInvoker.LOCAL_PORTLET_INVOKER_ID,
                                    PortletContext.createPortletContext(portletFqnParts[0], portletFqnParts[1]));
  }

  private String createSkinUrl(SkinConfig skin) {
    if (skin == null || StringUtils.isBlank(skin.getCSSPath())) {
      return null;
    }
    return String.format("/portal/skins/%s%s%s-lt.css",
                         ResourceRequestHandler.VERSION,
                         skin.getCSSPath().replace(".css", ""),
                         PropertyManager.isDevelopping() ? "" : "-min");
  }

  public PortletInvoker getPortletInvoker() {
    if (portletInvoker == null) {
      portletInvoker = ExoContainerContext.getService(PortletInvoker.class);
    }
    return portletInvoker;
  }
}
