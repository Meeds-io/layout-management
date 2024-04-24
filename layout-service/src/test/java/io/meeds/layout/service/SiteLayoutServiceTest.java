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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

@SpringBootTest(classes = {
                            SiteLayoutService.class,
})
@ExtendWith(MockitoExtension.class)
public class SiteLayoutServiceTest {

  private static final String     TEST_USER = "testuser";

  private static final SiteKey    SITE_KEY  = SiteKey.portal("test");

  @MockBean
  private LayoutService           layoutService;

  @MockBean
  private UserPortalConfigService portalConfigService;

  @MockBean
  private LayoutAclService        aclService;

  @Autowired
  private SiteLayoutService       siteLayoutService;

  @Mock
  private PortalConfig            portalConfig;

  @Test
  @SneakyThrows
  public void getSiteById() {
    assertThrows(ObjectNotFoundException.class, () -> siteLayoutService.getSite(2l, TEST_USER));
    when(layoutService.getPortalConfig(2l)).thenReturn(portalConfig);
    when(portalConfig.getType()).thenReturn(SITE_KEY.getTypeName());
    when(portalConfig.getName()).thenReturn(SITE_KEY.getName());
    assertThrows(IllegalAccessException.class, () -> siteLayoutService.getSite(2l, TEST_USER));
    when(aclService.canViewSite(SITE_KEY, TEST_USER)).thenReturn(true);
    assertEquals(portalConfig, siteLayoutService.getSite(2l, TEST_USER));
  }

  @Test
  @SneakyThrows
  public void getSiteByKey() {
    assertThrows(ObjectNotFoundException.class, () -> siteLayoutService.getSite(SITE_KEY, TEST_USER));
    when(layoutService.getPortalConfig(SITE_KEY)).thenReturn(portalConfig);
    assertThrows(IllegalAccessException.class, () -> siteLayoutService.getSite(SITE_KEY, TEST_USER));
    when(aclService.canViewSite(SITE_KEY, TEST_USER)).thenReturn(true);
    assertEquals(portalConfig, siteLayoutService.getSite(SITE_KEY, TEST_USER));
  }

  @Test
  @SneakyThrows
  public void createSite() {
    SiteCreateModel createModel = mock(SiteCreateModel.class);
    assertThrows(IllegalAccessException.class, () -> siteLayoutService.createSite(createModel, TEST_USER));

    when(aclService.canAddSite(TEST_USER)).thenReturn(true);

    String siteTemplate = "siteTemplate";
    when(createModel.getSiteTemplate()).thenReturn(siteTemplate);
    when(createModel.getPortalConfig()).thenReturn(portalConfig);
    when(portalConfig.getName()).thenReturn(SITE_KEY.getName());
    when(layoutService.getPortalConfig(SITE_KEY.getName())).thenReturn(portalConfig);
    assertThrows(ObjectAlreadyExistsException.class, () -> siteLayoutService.createSite(createModel, TEST_USER));
    when(layoutService.getPortalConfig(SITE_KEY.getName())).thenReturn(null);

    String description = "description";
    String uploadId = "56632";
    when(portalConfig.getLabel()).thenReturn(SITE_KEY.getTypeName());
    when(portalConfig.getDescription()).thenReturn(description);
    when(portalConfig.isDisplayed()).thenReturn(true);
    when(portalConfig.getDisplayOrder()).thenReturn(22);
    when(portalConfig.getBannerUploadId()).thenReturn(uploadId);
    when(aclService.getAdministratorsGroup()).thenReturn("administrators");
    doAnswer(invocation -> {
      when(layoutService.getPortalConfig(invocation.getArgument(1, String.class))).thenReturn(portalConfig);
      return null;
    }).when(portalConfigService)
      .createUserPortalConfig(PortalConfig.PORTAL_TYPE, SITE_KEY.getName(), siteTemplate);

    siteLayoutService.createSite(createModel, TEST_USER);
    verify(layoutService).save(portalConfig);
    verify(portalConfig).setDescription(description);
    verify(portalConfig).setDisplayed(true);
    verify(portalConfig).setDisplayOrder(22);
    verify(portalConfig).setBannerUploadId(uploadId);
    verify(portalConfig).setEditPermission("*:administrators");
    verify(portalConfig).setAccessPermissions(argThat(p -> p[0].equals("*:administrators")));

    when(portalConfig.isDisplayed()).thenReturn(false);
    when(portalConfig.getBannerUploadId()).thenReturn(null);
    when(portalConfig.getAccessPermissions()).thenReturn(new String[] { "accessPermission" });
    when(portalConfig.getEditPermission()).thenReturn("editPermission");
    when(layoutService.getPortalConfig(SITE_KEY.getName())).thenReturn(null);
    doAnswer(invocation -> {
      when(layoutService.getPortalConfig(invocation.getArgument(1, String.class))).thenReturn(portalConfig);
      return null;
    }).when(portalConfigService)
      .createUserPortalConfig(PortalConfig.PORTAL_TYPE, SITE_KEY.getName(), siteTemplate);
    siteLayoutService.createSite(createModel, TEST_USER);
    verify(portalConfig).setAccessPermissions(argThat(p -> p[0].equals("accessPermission")));
    verify(portalConfig).setEditPermission("editPermission");
    verify(portalConfig).setDisplayed(false);
    verify(portalConfig).setDisplayOrder(0);
  }

  @Test
  @SneakyThrows
  public void updateSite() {
    SiteUpdateModel updateModel = mock(SiteUpdateModel.class);
    when(updateModel.getSiteType()).thenReturn(SITE_KEY.getTypeName());
    when(updateModel.getSiteName()).thenReturn(SITE_KEY.getName());
    assertThrows(ObjectNotFoundException.class, () -> siteLayoutService.updateSite(updateModel, TEST_USER));

    when(layoutService.getPortalConfig(SITE_KEY)).thenReturn(portalConfig);
    assertThrows(IllegalAccessException.class, () -> siteLayoutService.updateSite(updateModel, TEST_USER));

    when(aclService.canEditSite(SITE_KEY, TEST_USER)).thenReturn(true);

    when(updateModel.getSiteLabel()).thenReturn("label");
    when(updateModel.getSiteDescription()).thenReturn("description");
    when(updateModel.isDisplayed()).thenReturn(true);
    when(updateModel.getDisplayOrder()).thenReturn(22);
    when(updateModel.getBannerUploadId()).thenReturn("56632");
    siteLayoutService.updateSite(updateModel, TEST_USER);
    verify(layoutService).save(portalConfig);
    verify(layoutService, never()).removeSiteBanner(any());
    verify(portalConfig).setDisplayOrder(22);

    when(updateModel.isDisplayed()).thenReturn(false);
    when(updateModel.isBannerRemoved()).thenReturn(true);
    when(portalConfig.getBannerFileId()).thenReturn(336l);
    siteLayoutService.updateSite(updateModel, TEST_USER);
    verify(layoutService, times(2)).save(portalConfig);
    verify(layoutService).removeSiteBanner(SITE_KEY.getName());
    verify(portalConfig).setDisplayOrder(0);
  }

  @Test
  @SneakyThrows
  public void deleteSite() {
    assertThrows(ObjectNotFoundException.class, () -> siteLayoutService.deleteSite(SITE_KEY, TEST_USER));
    when(layoutService.getPortalConfig(SITE_KEY)).thenReturn(portalConfig);
    assertThrows(IllegalAccessException.class, () -> siteLayoutService.deleteSite(SITE_KEY, TEST_USER));
    when(aclService.canEditSite(SITE_KEY, TEST_USER)).thenReturn(true);
    siteLayoutService.deleteSite(SITE_KEY, TEST_USER);
    verify(layoutService).remove(portalConfig);
  }

  @Test
  @SneakyThrows
  public void updateSitePermissions() {
    String accessPermissions = "access, permissions";
    String editPermission = "edit permission";
    PermissionUpdateModel permissionModel = new PermissionUpdateModel(SITE_KEY.getTypeName(),
                                                                      SITE_KEY.getName(),
                                                                      accessPermissions,
                                                                      editPermission);
    assertThrows(ObjectNotFoundException.class,
                 () -> siteLayoutService.updateSitePermissions(permissionModel, TEST_USER));
    when(layoutService.getPortalConfig(SITE_KEY)).thenReturn(portalConfig);
    assertThrows(IllegalAccessException.class,
                 () -> siteLayoutService.updateSitePermissions(permissionModel, TEST_USER));
    when(aclService.canEditSite(SITE_KEY, TEST_USER)).thenReturn(true);
    siteLayoutService.updateSitePermissions(permissionModel, TEST_USER);
    verify(layoutService).save(portalConfig);
    portalConfig.setAccessPermissions(argThat(p -> Arrays.asList("access", "permissions").equals(Arrays.asList(p))));
    portalConfig.setEditPermission(argThat(editPermission::equals));
  }

}
