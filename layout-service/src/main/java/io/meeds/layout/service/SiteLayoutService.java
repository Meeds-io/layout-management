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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.ObjectAlreadyExistsException;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.service.LayoutService;

import io.meeds.layout.model.PermissionUpdateModel;
import io.meeds.layout.model.SiteCreateModel;
import io.meeds.layout.model.SiteUpdateModel;

import lombok.SneakyThrows;

@Service
public class SiteLayoutService {

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private UserPortalConfigService portalConfigService;

  @Autowired
  private LayoutAclService        aclService;

  public PortalConfig getSite(long siteId, String username) throws ObjectNotFoundException, IllegalAccessException {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteId);
    if (portalConfig == null) {
      throw new ObjectNotFoundException(String.format("Site with id %s doesn't exists", siteId));
    } else if (!aclService.canViewSite(new SiteKey(portalConfig.getType(), portalConfig.getName()), username)) {
      throw new IllegalAccessException();
    }
    return portalConfig;
  }

  public PortalConfig getSite(SiteKey siteKey, String username) throws ObjectNotFoundException, IllegalAccessException {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      throw new ObjectNotFoundException(String.format("Site with key %s doesn't exists", siteKey));
    } else if (!aclService.canViewSite(siteKey, username)) {
      throw new IllegalAccessException();
    }
    return portalConfig;
  }

  @SneakyThrows
  public void createSite(SiteCreateModel createModel, String username) throws IllegalAccessException,
                                                                       ObjectAlreadyExistsException {
    if (!aclService.canAddSite(username)) {
      throw new IllegalAccessException();
    } else if (layoutService.getPortalConfig(createModel.getPortalConfig().getName()) != null) {
      throw new ObjectAlreadyExistsException(String.format("Site with name %s already exists",
                                                           createModel.getPortalConfig().getName()));
    }
    PortalConfig portalConfigToCreate = createModel.getPortalConfig();
    String siteTemplate = createModel.getSiteTemplate();
    portalConfigService.createUserPortalConfig(PortalConfig.PORTAL_TYPE, portalConfigToCreate.getName(), siteTemplate);

    String[] accessPermissions = portalConfigToCreate.getAccessPermissions() == null ?
                                                                                     new String[] { getAdministratorsPermission() } :
                                                                                     portalConfigToCreate.getAccessPermissions();
    String editPermission = portalConfigToCreate.getEditPermission() == null ?
                                                                             getAdministratorsPermission() :
                                                                             portalConfigToCreate.getEditPermission();

    PortalConfig createdPortalConfig = layoutService.getPortalConfig(portalConfigToCreate.getName());
    createdPortalConfig.setDescription(portalConfigToCreate.getDescription());
    createdPortalConfig.setLabel(portalConfigToCreate.getLabel());
    createdPortalConfig.setDisplayed(portalConfigToCreate.isDisplayed());
    createdPortalConfig.setDisplayOrder(portalConfigToCreate.isDisplayed() ? portalConfigToCreate.getDisplayOrder() : 0);
    createdPortalConfig.setAccessPermissions(accessPermissions);
    createdPortalConfig.setEditPermission(editPermission);
    if (StringUtils.isNotBlank(portalConfigToCreate.getBannerUploadId())) {
      createdPortalConfig.setBannerUploadId(portalConfigToCreate.getBannerUploadId());
    }
    layoutService.save(createdPortalConfig);
  }

  public void updateSite(SiteKey siteKey, SiteUpdateModel updateModel, String username) throws IllegalAccessException,
                                                                                        ObjectNotFoundException {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      throw new ObjectNotFoundException(String.format("Site with key %s doesn't exist", siteKey));
    } else if (!aclService.canEditSite(siteKey, username)) {
      throw new IllegalAccessException(String.format("Site with key %s can't be edited by user %s", siteKey, username));
    }
    portalConfig.setDescription(updateModel.getSiteDescription());
    portalConfig.setLabel(updateModel.getSiteLabel());
    portalConfig.setDisplayed(updateModel.isDisplayed());
    portalConfig.setDisplayOrder(updateModel.isDisplayed() ? updateModel.getDisplayOrder() : 0);
    if (updateModel.isBannerRemoved() && portalConfig.getBannerFileId() != 0) {
      layoutService.removeSiteBanner(siteKey.getName());
      portalConfig.setBannerFileId(0);
    } else if (StringUtils.isNotBlank(updateModel.getBannerUploadId())) {
      portalConfig.setBannerUploadId(updateModel.getBannerUploadId());
    }
    layoutService.save(portalConfig);
  }

  public void deleteSite(SiteKey siteKey, String username) throws IllegalAccessException, ObjectNotFoundException {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      throw new ObjectNotFoundException(String.format("Site with key %s doesn't exist", siteKey));
    } else if (!aclService.canEditSite(siteKey, username)) {
      throw new IllegalAccessException(String.format("Site with key %s can't be deleted by user %s", siteKey, username));
    }
    layoutService.remove(portalConfig);
  }

  public void updateSitePermissions(SiteKey siteKey,
                                    PermissionUpdateModel permissionUpdateModel,
                                    String username) throws IllegalAccessException, ObjectNotFoundException {
    PortalConfig portalConfig = layoutService.getPortalConfig(siteKey);
    if (portalConfig == null) {
      throw new ObjectNotFoundException(String.format("Site %s doesn't exist", siteKey));
    } else if (!aclService.canEditSite(siteKey, username)) {
      throw new IllegalAccessException(String.format("Site permissions with key %s can't be edited by user %s",
                                                     siteKey,
                                                     username));
    }
    if (!StringUtils.isBlank(permissionUpdateModel.getEditPermission())) {
      portalConfig.setEditPermission(permissionUpdateModel.getEditPermission());
    }
    if (!StringUtils.isBlank(permissionUpdateModel.getAccessPermissions())) {
      String[] accessPermissions = List.of(permissionUpdateModel.getAccessPermissions().split(","))
                                       .stream()
                                       .distinct()
                                       .toArray(String[]::new);
      portalConfig.setAccessPermissions(accessPermissions);
    }
    layoutService.save(portalConfig);
  }

  private String getAdministratorsPermission() {
    return "*:" + aclService.getAdministratorsGroup();
  }

}
