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
package io.meeds.layout.plugin.upgrade;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.mop.dao.WindowDAO;
import org.exoplatform.portal.mop.storage.cache.CacheLayoutStorage;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import io.meeds.common.ContainerTransactional;
import io.meeds.layout.model.ApplicationReferenceUpgrade;
import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.service.PortletInstanceService;

import lombok.SneakyThrows;

public class LayoutApplicationReferenceUpgradePlugin extends UpgradeProductPlugin {

  private static final String               ENABLED_PARAM = "enabled";

  private static final Log                  LOG           = ExoLogger.getLogger(LayoutApplicationReferenceUpgradePlugin.class);

  private CacheService                      cacheService;

  private PortletInstanceService            portletInstanceService;

  private WindowDAO                         windowDAO;

  private List<ApplicationReferenceUpgrade> upgrades;

  private boolean                           enabled;

  public LayoutApplicationReferenceUpgradePlugin(CacheService cacheService,
                                                 SettingService settingService,
                                                 PortletInstanceService portletInstanceService,
                                                 WindowDAO windowDAO,
                                                 InitParams initParams) {
    super(settingService, initParams);
    this.portletInstanceService = portletInstanceService;
    this.windowDAO = windowDAO;
    this.cacheService = cacheService;
    this.upgrades = initParams.getObjectParamValues(ApplicationReferenceUpgrade.class);
    this.enabled = !initParams.containsKey(ENABLED_PARAM)
                   || Boolean.parseBoolean(initParams.getValueParam(ENABLED_PARAM).getValue());
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    long start = System.currentTimeMillis();
    LOG.info("Start:: Upgrade Application ContentId {}", getName());
    try {
      upgradeApplications();
      LOG.info("End:: Upgrade Application ContentId {} in {} ms", getName(), System.currentTimeMillis() - start);
    } finally {
      this.cacheService.getCacheInstance(CacheLayoutStorage.PORTLET_PREFERENCES_CACHE_NAME).clearCache();
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return enabled && CollectionUtils.isNotEmpty(upgrades);
  }

  @SneakyThrows
  @ContainerTransactional
  public void upgradeApplications() { // NOSONAR
    List<PortletInstance> portletInstances = portletInstanceService.getPortletInstances();

    for (ApplicationReferenceUpgrade applicationModification : upgrades) {
      String oldContentId = applicationModification.getOldContentId();
      String newContentId = applicationModification.getNewContentId();
      int modifiedLines = 0;
      if (applicationModification.isModification()) {
        if (applicationModification.isUpgradePages()) {
          modifiedLines = windowDAO.updateContentId(oldContentId, newContentId);
        }
        if (applicationModification.isUpgradePortletInstance()) {
          PortletInstance portletInstance = portletInstances.stream()
                                                            .filter(p -> StringUtils.equals(oldContentId, p.getContentId()))
                                                            .findFirst()
                                                            .orElse(null);
          if (portletInstance != null) {
            portletInstance.setContentId(newContentId);
            portletInstanceService.updatePortletInstance(portletInstance);
          }
        }
      } else if (applicationModification.isRemoval()) {
        if (applicationModification.isUpgradePages()) {
          modifiedLines = windowDAO.deleteByContentId(oldContentId);
        }
        if (applicationModification.isUpgradePortletInstance()) {
          PortletInstance portletInstance = portletInstances.stream()
                                                            .filter(p -> StringUtils.equals(oldContentId, p.getContentId()))
                                                            .findFirst()
                                                            .orElse(null);
          if (portletInstance != null) {
            portletInstanceService.deletePortletInstance(portletInstance.getId());
          }
        }
      }

      if (modifiedLines > 0) {
        LOG.info("| -- UPDATE::Application Reference: {} items migrated from '{}' to '{}' successfully",
                 modifiedLines,
                 oldContentId,
                 newContentId);
      }
    }
  }

}
