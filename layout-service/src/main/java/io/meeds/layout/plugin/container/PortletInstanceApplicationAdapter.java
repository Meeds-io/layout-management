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
package io.meeds.layout.plugin.container;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.ModelStyle;
import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.config.serialize.PortletApplication;
import org.exoplatform.portal.pom.data.ApplicationData;
import org.exoplatform.portal.pom.spi.portlet.Portlet;

import io.meeds.layout.service.PortletInstanceService;

import lombok.SneakyThrows;

@Component
public class PortletInstanceApplicationAdapter extends PortletApplication {

  private static final String                             PLACEHOLDER_ID    = "PortletInstanceApplicationAdapter";

  private static final TransientApplicationState<Portlet> PLACEHOLDER_STATE =
                                                                            new TransientApplicationState<>("layout/PortletEditor");

  @Autowired
  private PortletInstanceService                          portletInstanceService;

  private ThreadLocal<Application<Portlet>>               application       = new ThreadLocal<>();

  @Override
  public ModelStyle getCssStyle() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getCssStyle();
  }

  @Override
  public void setCssStyle(ModelStyle cssStyle) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setCssStyle(cssStyle);
    }
  }

  @Override
  public ApplicationType<Portlet> getType() {
    return ApplicationType.PORTLET;
  }

  @Override
  public String getWidth() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getWidth();
  }

  @Override
  public void setWidth(String s) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setWidth(s);
    }
  }

  @Override
  public String getHeight() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getHeight();
  }

  @Override
  public void setHeight(String s) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setHeight(s);
    }
  }

  @Override
  public String getId() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? PLACEHOLDER_ID : portletApplication.getId();
  }

  @Override
  public void setId(String value) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setId(value);
    }
  }

  @Override
  public String[] getAccessPermissions() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getAccessPermissions();
  }

  @Override
  public void setAccessPermissions(String[] accessPermissions) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setAccessPermissions(accessPermissions);
    }
  }

  @Override
  public boolean isModifiable() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication != null && portletApplication.isModifiable();
  }

  @Override
  public void setModifiable(boolean modifiable) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setModifiable(modifiable);
    }
  }

  @Override
  public ApplicationState<Portlet> getState() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? PLACEHOLDER_STATE : portletApplication.getState();
  }

  @Override
  public void setState(ApplicationState<Portlet> value) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setState(value);
    }
  }

  @Override
  public boolean getShowInfoBar() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication != null && portletApplication.getShowInfoBar();
  }

  @Override
  public void setShowInfoBar(boolean b) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setShowInfoBar(b);
    }
  }

  @Override
  public boolean getShowApplicationState() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication != null && portletApplication.getShowApplicationState();
  }

  @Override
  public void setShowApplicationState(boolean b) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setShowApplicationState(b);
    }
  }

  @Override
  public boolean getShowApplicationMode() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication != null && portletApplication.getShowApplicationMode();
  }

  @Override
  public void setShowApplicationMode(boolean b) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setShowApplicationMode(b);
    }
  }

  @Override
  public String getIcon() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getIcon();
  }

  @Override
  public void setIcon(String value) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setIcon(value);
    }
  }

  @Override
  public String getDescription() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getDescription();
  }

  @Override
  public void setDescription(String des) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setDescription(des);
    }
  }

  @Override
  public String getTitle() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getTitle();
  }

  @Override
  public void setTitle(String value) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setTitle(value);
    }
  }

  @Override
  public Properties getProperties() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getProperties();
  }

  @Override
  public void setProperties(Properties properties) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setProperties(properties);
    }
  }

  @Override
  public String getTheme() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getTheme();
  }

  @Override
  public void setTheme(String theme) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setTheme(theme);
    }
  }

  @Override
  public String getCssClass() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getCssClass();
  }

  @Override
  @SuppressWarnings("unchecked")
  public ApplicationData<Portlet> build() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.build();
  }

  @Override
  public void checkStorage() throws ObjectNotFoundException {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.checkStorage();
    }
  }

  @Override
  public void resetStorage() throws ObjectNotFoundException {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.resetStorage();
    }
  }

  @Override
  public void setCssClass(String cssClass) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setCssClass(cssClass);
    }
  }

  @Override
  public String getStorageId() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getStorageId();
  }

  @Override
  public String getStorageName() {
    Application<Portlet> portletApplication = getApplication();
    return portletApplication == null ? null : portletApplication.getStorageName();
  }

  @Override
  public void setStorageName(String storageName) {
    Application<Portlet> portletApplication = getApplication();
    if (portletApplication != null) {
      portletApplication.setStorageName(storageName);
    }
  }

  @SneakyThrows
  public Application<Portlet> getApplication() { // NOSONAR
    Application<Portlet> portletApplication = application.get();
    if (portletApplication == null) {
      portletApplication = portletInstanceService.getPortletInstanceApplication(getPortletInstanceId(),
                                                                                getApplicationStorageId(),
                                                                                getCurrentUserName());
      manageRequestCache(portletApplication);
    }
    return portletApplication;
  }

  public void cleanApplication() {
    application.remove();
  }

  private void manageRequestCache(Application<Portlet> portletApplication) {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    if (requestContext != null) {
      application.set(portletApplication);
      requestContext.addOnRequestEnd(this::cleanApplication);
    }
  }

  private long getPortletInstanceId() {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    return requestContext == null ? 0 : getParameterLong(requestContext, "portletInstanceId");
  }

  private long getApplicationStorageId() {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    return requestContext == null ? 0 : getParameterLong(requestContext, "portal:componentId");
  }

  private String getCurrentUserName() {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    return requestContext == null ? null : requestContext.getRequest().getRemoteUser();
  }

  private long getParameterLong(PortalRequestContext requestContext, String paramName) {
    String value = requestContext.getRequest().getParameter(paramName);
    return StringUtils.isBlank(value) || !StringUtils.isNumeric(value) ? 0 : Long.parseLong(value);
  }

}
