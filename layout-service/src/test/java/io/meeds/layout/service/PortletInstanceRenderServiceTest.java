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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.pom.spi.portlet.Portlet;

import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.model.PortletInstancePreference;
import io.meeds.layout.plugin.PortletInstancePreferencePlugin;

import lombok.SneakyThrows;

@SpringBootTest(classes = {
                            PortletInstanceRenderService.class,
})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PortletInstanceRenderServiceTest {

  private static final String             CONTENT_ID = "test/content";

  private static final String             USERNAME   = "test";

  @MockBean
  private LayoutAclService                layoutAclService;

  @MockBean
  private SettingService                  settingService;

  @MockBean
  private LayoutService                   layoutService;

  @MockBean
  private PortletInstanceService          portletInstanceService;

  @Autowired
  private PortletInstanceRenderService    portletInstanceRenderService;

  @Mock
  private Application                     application;

  @Mock
  private PortletInstance                 portletInstance;

  @Mock
  private Page                            page;

  @Mock
  private Container                       container;

  @Mock
  private PortletInstancePreferencePlugin plugin;

  @Test
  @SneakyThrows
  public void getPortletInstanceApplicationByInstanceId() {
    assertThrows(ObjectNotFoundException.class,
                 () -> portletInstanceRenderService.getPortletInstanceApplication(USERNAME, "2", null));
    when(settingService.get(any(), any(), eq("2"))).thenReturn(new SettingValue("3"));

    assertThrows(ObjectNotFoundException.class,
                 () -> portletInstanceRenderService.getPortletInstanceApplication(USERNAME, "2", null));
    when(portletInstanceService.getPortletInstance(2,
                                                   USERNAME,
                                                   Locale.ENGLISH,
                                                   false)).thenReturn(portletInstance);
    when(layoutService.getApplicationModel("3")).thenReturn(application);
    when(portletInstance.getId()).thenReturn(2l);

    Application<?> portletInstanceApplication = portletInstanceRenderService.getPortletInstanceApplication(USERNAME, "2", null);
    assertEquals(application, portletInstanceApplication);
  }

  @Test
  @SneakyThrows
  public void getPortletInstanceApplicationPlaceholder() {
    assertNotNull(portletInstanceRenderService.getPortletInstanceApplication(null, null, null));
  }

  @Test
  @SneakyThrows
  public void getPortletInstancePreferencesWhenNoPlugin() {
    when(portletInstanceService.getPortletInstance(2, USERNAME, null, false)).thenReturn(portletInstance);
    when(portletInstance.getContentId()).thenReturn(CONTENT_ID);
    when(portletInstance.getId()).thenReturn(2l);
    when(settingService.get(any(), any(), eq("2"))).thenReturn(new SettingValue("3"));
    when(layoutService.getApplicationModel("3")).thenReturn(application);
    Portlet portlet = new Portlet();
    when(layoutService.load(any(), any())).thenReturn(portlet);
    portlet.setValue("test", "testValue");

    List<PortletInstancePreference> portletInstancePreferences =
                                                               portletInstanceRenderService.getPortletInstancePreferences(2,
                                                                                                                          USERNAME);
    assertNotNull(portletInstancePreferences);
    assertEquals(1, portletInstancePreferences.size());
    assertEquals("test", portletInstancePreferences.get(0).getName());
    assertEquals("testValue", portletInstancePreferences.get(0).getValue());
  }

  @Test
  @SneakyThrows
  public void getPortletInstancePreferencesWithPlugin() {
    when(portletInstanceService.getPortletInstance(2, USERNAME, null, false)).thenReturn(portletInstance);
    when(portletInstance.getContentId()).thenReturn(CONTENT_ID);
    when(portletInstance.getId()).thenReturn(2l);
    when(settingService.get(any(), any(), eq("2"))).thenReturn(new SettingValue("3"));
    when(layoutService.getApplicationModel("3")).thenReturn(application);
    when(plugin.getPortletName()).thenReturn("content");
    portletInstanceRenderService.addPortletInstancePreferencePlugin(plugin);
    try {
      when(plugin.generatePreferences(any(), any())).thenReturn(Collections.singletonList(new PortletInstancePreference("test",
                                                                                                                        "value")));

      List<PortletInstancePreference> portletInstancePreferences =
                                                                 portletInstanceRenderService.getPortletInstancePreferences(2,
                                                                                                                            USERNAME);
      assertNotNull(portletInstancePreferences);
      assertEquals(1, portletInstancePreferences.size());
      assertEquals("test", portletInstancePreferences.get(0).getName());
      assertEquals("value", portletInstancePreferences.get(0).getValue());
    } finally {
      portletInstanceRenderService.removePortletInstancePreferencePlugin(plugin.getPortletName());
    }
  }

  @Test
  @SneakyThrows
  public void getPortletInstancePreferencesWhenNoPluginNoPreferences() {
    assertThrows(ObjectNotFoundException.class, () -> portletInstanceRenderService.getPortletInstancePreferences(2, USERNAME));

    when(portletInstanceService.getPortletInstance(2, USERNAME, null, false)).thenReturn(portletInstance);
    when(portletInstance.getContentId()).thenReturn(CONTENT_ID);
    when(portletInstance.getId()).thenReturn(2l);
    assertThrows(ObjectNotFoundException.class, () -> portletInstanceRenderService.getPortletInstancePreferences(2, USERNAME));

    when(settingService.get(any(), any(), eq("2"))).thenReturn(new SettingValue("3"));
    assertThrows(ObjectNotFoundException.class, () -> portletInstanceRenderService.getPortletInstancePreferences(2, USERNAME));

    when(layoutService.getApplicationModel("3")).thenReturn(application);
    assertNotNull(portletInstanceRenderService.getPortletInstancePreferences(2, USERNAME));
    assertEquals(0, portletInstanceRenderService.getPortletInstancePreferences(2, USERNAME).size());
  }

  @Test
  @SneakyThrows
  public void getPortletInstanceApplicationByInstanceIdAndCreateApplication() {
    assertThrows(ObjectNotFoundException.class,
                 () -> portletInstanceRenderService.getPortletInstanceApplication(USERNAME, "2", null));
    when(portletInstanceService.getPortletInstance(2,
                                                   USERNAME,
                                                   Locale.ENGLISH,
                                                   false)).thenReturn(portletInstance);

    PortletInstancePreference preference = new PortletInstancePreference();
    preference.setName("prefName");
    preference.setValue("prefValue");
    when(portletInstance.getPreferences()).thenReturn(Collections.singletonList(preference));
    when(portletInstance.getContentId()).thenReturn(CONTENT_ID);

    doAnswer(invocation -> {
      when(layoutService.getPage(any(PageKey.class))).thenReturn(page);
      ArrayList list = new ArrayList<>();
      list.add(container);
      when(page.getChildren()).thenReturn(list);

      ArrayList apps = new ArrayList<>();
      apps.add(application);
      when(container.getChildren()).thenReturn(apps);
      return null;
    }).when(layoutService).save(any(PageContext.class), any(Page.class));

    doAnswer(invocation -> {
      ArrayList<ModelObject> children = invocation.getArgument(0);
      children.set(children.size() - 1, application);
      when(container.getChildren()).thenReturn(children);
      when(application.getStorageId()).thenReturn("3");
      return null;
    }).when(container).setChildren(any());

    Application<?> portletInstanceApplication = portletInstanceRenderService.getPortletInstanceApplication(USERNAME, "2", null);
    assertEquals(application, portletInstanceApplication);
  }

  @Test
  @SneakyThrows
  public void getPortletInstanceApplicationByApplicationId() {
    when(settingService.get(any(), any(), eq("2"))).thenReturn(new SettingValue("3"));

    when(layoutService.getApplicationModel("3")).thenReturn(application);
    Application<?> portletInstanceApplication = portletInstanceRenderService.getPortletInstanceApplication(USERNAME, null, "3");
    assertEquals(application, portletInstanceApplication);
  }

}
