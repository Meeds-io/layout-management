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

import io.meeds.layout.model.ApplicationReferenceUpgrade;
import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.service.PortletInstanceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import lombok.SneakyThrows;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.jdbc.entity.PageEntity;
import org.exoplatform.portal.jdbc.entity.WindowEntity;
import org.exoplatform.portal.mop.dao.PageDAO;
import org.exoplatform.portal.mop.dao.WindowDAO;
import org.exoplatform.portal.mop.storage.PageStorage;
import org.exoplatform.portal.mop.storage.cache.CacheLayoutStorage;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpaceBannerHomePageUpgradePluginTest {

  @Mock
  private CacheService                   cacheService;

  @Mock
  private SettingService                 settingService;

  @Mock
  private PortletInstanceService         portletInstanceService;

  @Mock
  private EntityManagerService entityManagerService;

  @Mock
  private PageStorage pageStorage;

  @Mock
  private WindowDAO                      windowDAO;

  @Mock
  private PageDAO                      pageDAO;

  @Mock
  private InitParams                     initParams;

  @Mock
  private EntityManager entityManager;

  @Mock
  private ApplicationReferenceUpgrade    applicationModification;

  @Mock
  private PortletInstance                portletInstance;

  @Mock
  private ExoCache<Serializable, Object> portletPreferencesCache;

  @Mock
  private Query query;

  private String                         oldContentId = "oldApp/oldContentId";

  private String                         newContentId = "newApp/newContentId";

  @BeforeEach
  public void before() {
    when(cacheService.getCacheInstance(CacheLayoutStorage.PORTLET_PREFERENCES_CACHE_NAME)).thenReturn(portletPreferencesCache);

  }

  @Test
  @SneakyThrows
  public void processUpgrade() {

    ArrayList pageIdList = new ArrayList();
    pageIdList.add(1L);
    pageIdList.add(2L);
    when(query.getResultList()).thenReturn(pageIdList);
    when(entityManager.createNativeQuery(anyString(), ArgumentMatchers.<Class<Long>>any())).thenReturn(query);
    when(entityManager.createNativeQuery(anyString())).thenReturn(query);
    when(entityManagerService.getEntityManager()).thenReturn(entityManager);

    EntityTransaction transaction = mock(EntityTransaction.class);
    when(transaction.isActive()).thenReturn(true);
    when(entityManager.getTransaction()).thenReturn(transaction);

    PageEntity pageEntity1 = mock(PageEntity.class);
    PageEntity pageEntity2 = mock(PageEntity.class);
    when(pageEntity1.getPageBody()).thenReturn("[{\"id\":1,\"type\":\"WINDOW\"}]");
    when(pageEntity2.getPageBody()).thenReturn("[{\"id\":2,\"type\":\"WINDOW\"}]");
    when(pageDAO.find(1L)).thenReturn(pageEntity1);
    when(pageDAO.find(2L)).thenReturn(pageEntity2);
    WindowEntity windowEntity1 = mock(WindowEntity.class);
    WindowEntity windowEntity2 = mock(WindowEntity.class);
    when(windowEntity1.getContentId()).thenReturn("social-portlet/SpaceMenuPortlet");
    when(windowEntity2.getContentId()).thenReturn("social-portlet/SpaceMenuPortlet");

    when(windowDAO.find(1L)).thenReturn(windowEntity1);
    when(windowDAO.find(2L)).thenReturn(windowEntity2);



    SpaceBannerHomePageUpgradePlugin spaceBannerHomePageUpgradePlugin = new SpaceBannerHomePageUpgradePlugin(settingService,
                                                                                                             initParams,
                                                                                                             entityManagerService,
                                                                                                             pageStorage,
                                                                                                             pageDAO,
                                                                                                             windowDAO,
                                                                                                             cacheService,
                                                                                                             portletInstanceService);
    spaceBannerHomePageUpgradePlugin.processUpgrade(null, null);
    verify(query, times(2)).executeUpdate();
  }

}
