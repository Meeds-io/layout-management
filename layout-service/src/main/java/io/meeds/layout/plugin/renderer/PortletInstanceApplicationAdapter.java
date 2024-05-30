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
package io.meeds.layout.plugin.renderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.ModelStyle;
import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.portal.config.serialize.PortletApplication;
import org.exoplatform.portal.pom.data.ApplicationData;
import org.exoplatform.portal.pom.spi.portlet.Portlet;

import io.meeds.layout.service.PortletInstanceRenderService;

import lombok.SneakyThrows;

@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PortletInstanceApplicationAdapter extends PortletApplication {

  @Autowired
  private PortletInstanceRenderService portletInstanceRenderService;

  private ThreadLocal<Application>     application = new ThreadLocal<>();

  @Override
  public ModelStyle getCssStyle() {
    return getApplication().getCssStyle();
  }

  @Override
  public void setCssStyle(ModelStyle cssStyle) {
    getApplication().setCssStyle(cssStyle);
  }

  @Override
  public ApplicationType<Portlet> getType() {
    return ApplicationType.PORTLET;
  }

  @Override
  public String getWidth() {
    return getApplication().getWidth();
  }

  @Override
  public void setWidth(String s) {
    getApplication().setWidth(s);
  }

  @Override
  public String getHeight() {
    return getApplication().getHeight();
  }

  @Override
  public void setHeight(String s) {
    getApplication().setHeight(s);
  }

  @Override
  public String getId() {
    return getApplication().getId();
  }

  @Override
  public void setId(String value) {
    getApplication().setId(value);
  }

  @Override
  public String[] getAccessPermissions() {
    return getApplication().getAccessPermissions();
  }

  @Override
  public void setAccessPermissions(String[] accessPermissions) {
    getApplication().setAccessPermissions(accessPermissions);
  }

  @Override
  public boolean isModifiable() {
    return getApplication().isModifiable();
  }

  @Override
  public void setModifiable(boolean modifiable) {
    getApplication().setModifiable(modifiable);
  }

  @Override
  public ApplicationState<Portlet> getState() {
    return getApplication().getState();
  }

  @Override
  public void setState(ApplicationState<Portlet> value) {
    getApplication().setState(value);
  }

  @Override
  public boolean getShowInfoBar() {
    return getApplication().getShowInfoBar();
  }

  @Override
  public void setShowInfoBar(boolean b) {
    getApplication().setShowInfoBar(b);
  }

  @Override
  public boolean getShowApplicationState() {
    return getApplication().getShowApplicationState();
  }

  @Override
  public void setShowApplicationState(boolean b) {
    getApplication().setShowApplicationState(b);
  }

  @Override
  public boolean getShowApplicationMode() {
    return getApplication().getShowApplicationMode();
  }

  @Override
  public void setShowApplicationMode(boolean b) {
    getApplication().setShowApplicationMode(b);
  }

  @Override
  public String getIcon() {
    return getApplication().getIcon();
  }

  @Override
  public void setIcon(String value) {
    getApplication().setIcon(value);
  }

  @Override
  public String getDescription() {
    return getApplication().getDescription();
  }

  @Override
  public void setDescription(String des) {
    getApplication().setDescription(des);
  }

  @Override
  public String getTitle() {
    return getApplication().getTitle();
  }

  @Override
  public void setTitle(String value) {
    getApplication().setTitle(value);
  }

  @Override
  public Properties getProperties() {
    return getApplication().getProperties();
  }

  @Override
  public void setProperties(Properties properties) {
    getApplication().setProperties(properties);
  }

  @Override
  public String getTheme() {
    return getApplication().getTheme();
  }

  @Override
  public void setTheme(String theme) {
    getApplication().setTheme(theme);
  }

  @Override
  public String getCssClass() {
    return getApplication().getCssClass();
  }

  @Override
  public String getBorderColor() {
    return getApplication().getBorderColor();
  }

  @Override
  public ApplicationData build() {
    return getApplication().build();
  }

  @Override
  public void resetStorage() {
    getApplication().resetStorage();
  }

  @Override
  public void setCssClass(String cssClass) {
    getApplication().setCssClass(cssClass);
  }

  @Override
  public void setBorderColor(String borderColor) {
    getApplication().setBorderColor(borderColor);
  }

  @Override
  public String getStorageId() {
    return getApplication().getStorageId();
  }

  @Override
  public String getStorageName() {
    return getApplication().getStorageName();
  }

  @Override
  public void setStorageName(String storageName) {
    getApplication().setStorageName(storageName);
  }

  @SneakyThrows
  public Application getApplication() { // NOSONAR
    Application<?> portletApplication = application.get();
    if (portletApplication == null) {
      portletApplication = portletInstanceRenderService.getPortletInstanceApplication(getCurrentUserName(),
                                                                                      getPortletInstanceId(),
                                                                                      getApplicationStorageId());
      manageRequestCache(portletApplication);
    }
    return portletApplication;
  }

  private void manageRequestCache(Application<?> portletApplication) {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    if (requestContext != null) {
      application.set(portletApplication);
      requestContext.addOnRequestEnd(() -> application.remove());
    }
  }

  private String getPortletInstanceId() {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    return requestContext == null ? null : requestContext.getRequest().getParameter("portletInstanceId");
  }

  private String getApplicationStorageId() {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    return requestContext == null ? null : requestContext.getRequest().getParameter("portal:componentId");
  }

  private String getCurrentUserName() {
    PortalRequestContext requestContext = PortalRequestContext.getCurrentInstance();
    return requestContext == null ? null : requestContext.getRequest().getRemoteUser();
  }

}
