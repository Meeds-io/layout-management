/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2025 Meeds Association contact@meeds.io
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

import io.meeds.layout.service.PortletInstanceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.jdbc.entity.PageEntity;
import org.exoplatform.portal.jdbc.entity.WindowEntity;
import org.exoplatform.portal.mop.dao.PageDAO;
import org.exoplatform.portal.mop.dao.WindowDAO;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpaceBannerHomePageUpgradePlugin extends LayoutApplicationReferenceUpgradePlugin {

  private static final Log log = ExoLogger.getLogger(SpaceBannerHomePageUpgradePlugin.class);

  private final EntityManagerService entityManagerService;

  private final PageDAO     pageDAO;

  private final WindowDAO   windowDAO;

  public SpaceBannerHomePageUpgradePlugin(SettingService settingService,
                                          InitParams initParams,
                                          EntityManagerService entityManagerService,
                                          PageDAO pageDAO,
                                          WindowDAO windowDAO, CacheService cacheService, PortletInstanceService portletInstanceService) {
    super(cacheService, settingService,portletInstanceService,windowDAO, initParams);
    this.entityManagerService = entityManagerService;
    this.pageDAO = pageDAO;
    this.windowDAO = windowDAO;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    long start = System.currentTimeMillis();
    log.info("Start:: Update Space Home Page banner content id");

    List<Long> spaceHomePagesId = getSpaceHomePages();
    spaceHomePagesId.forEach(pageId -> {

      PageEntity page = pageDAO.find(pageId);
      JSONArray jsonArray = new JSONArray(page.getPageBody());
      for (int i = 0; i < jsonArray.length(); i++) {
        checkChildren(jsonArray.getJSONObject(i));
      }
    });

    super.processUpgrade(oldVersion, newVersion);

    log.info("End:: Update Space Home Page banner content id in {} ms", System.currentTimeMillis() - start);
  }

  private void checkChildren(JSONObject element) {
    long id = element.getLong("id");
    String type = element.getString("type");
    if (type.equals("WINDOW")) {
      WindowEntity windowEntity = windowDAO.find(id);
      if (windowEntity.getContentId().equals("social-portlet/SpaceMenuPortlet")) {
        updateWindow(id);
      }
    } else {
      if (element.has("children")) {
        JSONArray children = element.getJSONArray("children");
        for (int i = 0; i < children.length(); i++) {
          checkChildren(children.getJSONObject(i));
        }
      }
    }
  }

  private List<Long> getSpaceHomePages() {
    RequestLifeCycle.begin(this.entityManagerService);
    EntityManager entityManager = this.entityManagerService.getEntityManager();
    List<Long> results = new ArrayList<>();
    try {
      String sqlString = "SELECT pnn.PAGE_ID FROM PORTAL_NAVIGATION_NODES pnn WHERE pnn.PARENT_ID in ("
          + "SELECT pn.NODE_ID FROM PORTAL_NAVIGATIONS pn WHERE pn.SITE_ID in ("
          + "SELECT ps.ID FROM PORTAL_SITES ps WHERE ps.TYPE=1"
          + ")"
          + ")";
      Query query = entityManager.createNativeQuery(sqlString, Long.class);
      results = query.getResultList();
    } catch (Exception e) {
      log.error("Error when getting Space Home Pages", e);
    } finally {
      RequestLifeCycle.end();
    }
    return results;
  }

  private void updateWindow(long id) {
    RequestLifeCycle.begin(this.entityManagerService);
    EntityManager entityManager = this.entityManagerService.getEntityManager();
    if (!entityManager.getTransaction().isActive()) {
      entityManager.getTransaction().begin();
    }
    String sqlString = "UPDATE PORTAL_WINDOWS SET CONTENT_ID='social/SpaceBannerPortlet' WHERE ID=" + id;
    try {
      Query nativeQuery = entityManager.createNativeQuery(sqlString);
      nativeQuery.executeUpdate();
    } finally {
      RequestLifeCycle.end();
    }
  }
}
