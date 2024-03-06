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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.Preference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class LayoutModel {

  protected String                  id;

  protected String                  storageId;

  protected String                  storageName;

  protected String                  name;

  protected String                  icon;

  protected String                  template;

  protected String                  factoryId;

  protected String                  title;

  protected String                  description;

  protected String                  width;

  protected String                  height;

  protected String[]                accessPermissions;

  // Specific to container
  protected String                  cssClass;

  protected String                  profiles;

  protected String[]                moveAppsPermissions;

  protected String[]                moveContainersPermissions;

  protected List<LayoutModel>       children;

  // Specific to applications
  private String                    contentId;

  private Map<String, List<String>> preferences;

  private boolean                   showInfoBar;

  private boolean                   showApplicationState = true;

  private boolean                   showApplicationMode  = true;

  // Specific to page
  private String                    editPermission;

  private PageKey                   pageKey;

  private String                    ownerType;

  private String                    ownerId;

  private boolean                   showMaxWindow;

  private boolean                   hideSharedLayout;

  private String                    type;

  private String                    link;

  public LayoutModel(ModelObject model) {
    init(model);
  }

  private void init(ModelObject model) {
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
      this.showInfoBar = application.getShowInfoBar();
      this.showApplicationState = application.getShowApplicationState();
      this.showApplicationMode = application.getShowApplicationMode();
      this.accessPermissions = application.getAccessPermissions();
      @SuppressWarnings("unchecked")
      TransientApplicationState<Portlet> state = (TransientApplicationState<Portlet>) application.getState();
      this.contentId = state.getContentId();
      Portlet portlet = state.getContentState();
      if (portlet != null) {
        this.preferences = new HashMap<>();
        for (Preference preference : portlet) {
          this.preferences.put(preference.getName(), preference.getValues());
        }
      } else {
        this.preferences = Collections.emptyMap();
      }
    }
  }

}
