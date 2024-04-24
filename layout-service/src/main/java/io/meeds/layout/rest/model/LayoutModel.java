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
package io.meeds.layout.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationState;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.CloneApplicationState;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PersistentApplicationState;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.pom.spi.portlet.Portlet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class LayoutModel {

  protected String            id;

  protected String            storageId;

  protected String            storageName;

  protected String            name;

  protected String            icon;

  protected String            template;

  protected String            factoryId;

  protected String            title;

  protected String            description;

  protected String            width;

  protected String            height;

  protected String            cssClass;

  protected String            borderColor;

  protected String[]          accessPermissions;

  // Specific to container
  protected String            profiles;

  protected String[]          moveAppsPermissions;

  protected String[]          moveContainersPermissions;

  protected List<LayoutModel> children;

  // Specific to applications
  private String              contentId;

  private boolean             showInfoBar;

  private boolean             showApplicationState = true;

  private boolean             showApplicationMode  = true;

  // Specific to page
  private String              editPermission;

  @JsonProperty(access = Access.READ_ONLY)
  private PageKey             pageKey;

  private String              ownerType;

  private String              ownerId;

  private boolean             showMaxWindow;

  private boolean             hideSharedLayout;

  private String              type;

  private String              link;

  public LayoutModel(ModelObject model) {
    init(model);
  }

  private void init(ModelObject model) { // NOSONAR
    if (model instanceof Container container) {
      this.id = container.getId();
      this.storageId = container.getStorageId();
      this.storageName = container.getStorageName();
      this.name = container.getName();
      this.icon = container.getIcon();
      this.template = container.getTemplate();
      this.factoryId = container.getFactoryId();
      this.title = container.getTitle();
      this.description = container.getDescription();
      this.width = container.getWidth();
      this.height = container.getHeight();
      this.cssClass = container.getCssClass();
      this.borderColor = container.getBorderColor();
      this.profiles = container.getProfiles();
      this.accessPermissions = container.getAccessPermissions();
      this.moveAppsPermissions = container.getMoveAppsPermissions();
      this.moveContainersPermissions = container.getMoveContainersPermissions();
      this.children = container.getChildren().stream().map(LayoutModel::new).toList();

      if (model instanceof Page page) {
        this.editPermission = page.getEditPermission();
        this.pageKey = page.getPageKey();
        this.ownerType = page.getOwnerType();
        this.ownerId = page.getOwnerId();
        this.showMaxWindow = page.isShowMaxWindow();
        this.hideSharedLayout = page.isHideSharedLayout();
        this.type = page.getType();
        this.link = page.getLink();
      }
    } else if (model instanceof Application application) { // NOSONAR
      this.id = application.getId();
      this.storageId = application.getStorageId();
      this.storageName = application.getStorageName();
      this.icon = application.getIcon();
      this.title = application.getTitle();
      this.description = application.getDescription();
      this.width = application.getWidth();
      this.height = application.getHeight();
      this.cssClass = application.getCssClass();
      this.borderColor = application.getBorderColor();
      this.showInfoBar = application.getShowInfoBar();
      this.showApplicationState = application.getShowApplicationState();
      this.showApplicationMode = application.getShowApplicationMode();
      this.accessPermissions = application.getAccessPermissions();

      @SuppressWarnings("unchecked")
      ApplicationState<Portlet> state = application.getState();
      if (state instanceof PersistentApplicationState<Portlet> persistentState) {
        this.storageId = persistentState.getStorageId();
      } else if (state instanceof CloneApplicationState<Portlet> persistentState) {
        this.storageId = persistentState.getStorageId();
      } else if (state instanceof TransientApplicationState<Portlet> transientState) {
        this.contentId = transientState.getContentId();
      } else {
        throw new IllegalStateException("Application should either has a persistent or transient state");
      }
    }
  }

  public Page toPage() {
    Page page = new Page(storageId);
    ArrayList<ModelObject> pageContainers = this.children == null ? new ArrayList<>() :
                                                                  this.children.stream()
                                                                               .map(LayoutModel::toModelObject)
                                                                               .collect(Collectors.toCollection(ArrayList::new));
    page.setChildren(pageContainers);
    return page;
  }

  public static ModelObject toModelObject(LayoutModel layoutModel) {
    if (StringUtils.isNotBlank(layoutModel.template)) {
      Container container = new Container(layoutModel.getStorageId());
      container.setId(layoutModel.getId());
      container.setStorageName(layoutModel.getStorageName());
      container.setName(layoutModel.getName());
      container.setIcon(layoutModel.getIcon());
      container.setTemplate(layoutModel.getTemplate());
      container.setFactoryId(layoutModel.getFactoryId());
      container.setTitle(layoutModel.getTitle());
      container.setDescription(layoutModel.getDescription());
      container.setWidth(layoutModel.getWidth());
      container.setHeight(layoutModel.getHeight());
      container.setCssClass(layoutModel.getCssClass());
      container.setBorderColor(layoutModel.getBorderColor());
      container.setProfiles(layoutModel.getProfiles());
      container.setAccessPermissions(layoutModel.getAccessPermissions());
      container.setMoveAppsPermissions(layoutModel.getMoveAppsPermissions());
      container.setMoveContainersPermissions(layoutModel.getMoveContainersPermissions());
      if (layoutModel.getChildren() != null) {
        container.setChildren(layoutModel.getChildren()
                                         .stream()
                                         .map(LayoutModel::toModelObject)
                                         .collect(Collectors.toCollection(ArrayList::new)));
      }
      return container;
    } else { // NOSONAR
      Application<Portlet> application = new Application<>(ApplicationType.PORTLET,
                                                           layoutModel.getStorageId());
      application.setId(layoutModel.getId());
      application.setStorageName(layoutModel.getStorageName());
      application.setIcon(layoutModel.getIcon());
      application.setTitle(layoutModel.getTitle());
      application.setDescription(layoutModel.getDescription());
      application.setWidth(layoutModel.getWidth());
      application.setHeight(layoutModel.getHeight());
      application.setCssClass(layoutModel.getCssClass());
      application.setBorderColor(layoutModel.getBorderColor());
      application.setShowInfoBar(layoutModel.isShowInfoBar());
      application.setShowApplicationState(layoutModel.isShowApplicationState());
      application.setShowApplicationMode(layoutModel.isShowApplicationMode());
      application.setAccessPermissions(layoutModel.getAccessPermissions());

      ApplicationState<Portlet> state;
      if (StringUtils.isNotBlank(layoutModel.getStorageId())) {
        state = new PersistentApplicationState<>(layoutModel.getStorageId());
      } else if (StringUtils.isNotBlank(layoutModel.getContentId())) {
        TransientApplicationState<Portlet> transientState = new TransientApplicationState<>(layoutModel.getContentId());
        transientState.setOwnerId(layoutModel.getOwnerId());
        transientState.setOwnerType(layoutModel.getOwnerType());
        state = transientState;
      } else {
        throw new IllegalStateException("Application should either has a storageId or a contentId");
      }
      application.setState(state);
      return application;
    }
  }

}
