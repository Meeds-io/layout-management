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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.gatein.pc.api.PortletInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.config.serialize.PortletApplication;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.portal.pom.spi.portlet.PortletBuilder;

import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.model.PortletInstancePreference;

/**
 * A plugin that is used to display a selected portlet instance in the context
 * of the PortletEditor page until. This should be changed to use
 * {@link PortletInvoker} to process 'view' & 'edit' & 'serveResource' switch
 * JSR-168 & JSR-286 requirements. But for now, to kkep WebUI based portlets
 * working (which doesn't implement the JSRs portlet bridge), we will use this
 * trick to allow displaying a portlet instance inside WebUI dynamic container.
 */
@Service
public class PortletInstanceRenderService {

  private static final String    PORTLET_EDITOR_PORTLET_CONTENT_ID = "layout/PortletEditor";

  private static final String    PORTLET_EDITOR_SYSTEM_PAGE        = "_portletEditor";

  private static final PageKey   PORTLET_EDITOR_SYSTEM_PAGE_KEY    =
                                                                new PageKey(SiteKey.portal("global"), PORTLET_EDITOR_SYSTEM_PAGE);

  private static final Context   CONTEXT                           = Context.GLOBAL.id("PORTLET_INSTANCE");

  private static final Scope     SCOPE                             = Scope.APPLICATION.id("PORTLET_INSTANCE_APPLICATION");

  @Autowired
  private SettingService         settingService;

  @Autowired
  private PortletInstanceService portletInstanceService;

  @Autowired
  private LayoutService          layoutService;

  private Application<Portlet>   portletEditorApplication;

  public Application<?> getPortletInstanceApplication(String username, // NOSONAR
                                                      String portletInstanceId,
                                                      String applicationStorageId) throws IllegalAccessException,
                                                                                   ObjectNotFoundException {
    if (StringUtils.isNotBlank(portletInstanceId)) {
      // Display the portlet instance by id
      return getOrCreatePortletInstanceApplication(portletInstanceId, username);
    } else if (StringUtils.isNotBlank(applicationStorageId)) {
      // Display the app by storage id
      return layoutService.getApplicationModel(applicationStorageId);
    } else {
      // Display the editor
      return getPortletEditorApplication();
    }
  }

  private Application<?> getOrCreatePortletInstanceApplication(String portletInstanceId,
                                                               String userName) throws IllegalAccessException,
                                                                                ObjectNotFoundException {
    PortletInstance portletInstance = portletInstanceService.getPortletInstance(Long.parseLong(portletInstanceId),
                                                                                userName,
                                                                                Locale.ENGLISH,
                                                                                false);
    long applicationId = getPortletInstanceApplicationId(portletInstanceId);
    if (applicationId == 0) {
      return createPortletInstanceApplication(portletInstance);
    } else {
      try {
        return layoutService.getApplicationModel(String.valueOf(applicationId));
      } catch (Exception e) {
        return createPortletInstanceApplication(portletInstance);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private synchronized Application<Portlet> createPortletInstanceApplication(PortletInstance portletInstance) {
    TransientApplicationState<Portlet> state = new TransientApplicationState<>(portletInstance.getContentId());

    List<String> permissions = portletInstance.getPermissions();
    List<PortletInstancePreference> preferences = portletInstance.getPreferences();
    if (CollectionUtils.isNotEmpty(preferences)) {
      PortletBuilder builder = new PortletBuilder();
      preferences.stream().forEach(pref -> builder.add(pref.getName(), pref.getValue()));
      state.setContentState(builder.build());
    }

    PortletApplication portletApplication = new PortletApplication();
    portletApplication.setState(state);
    portletApplication.setAccessPermissions(CollectionUtils.isEmpty(permissions) ? new String[] { UserACL.EVERYONE } :
                                                                                 permissions.toArray(new String[0]));

    Page page = getPortletInstanceSystemPage();
    Container container = (Container) page.getChildren().get(0);
    ArrayList<ModelObject> children = container.getChildren();
    int index;
    if (CollectionUtils.isEmpty(children)) {
      index = 0;
      children = new ArrayList<>();
    } else {
      index = children.size();
      children = new ArrayList<>(children);
    }
    children.add(portletApplication);
    container.setChildren(children);
    page.setChildren(new ArrayList<>(Collections.singletonList(container)));
    layoutService.save(page);

    container = getPortletInstanceSystemContainer();
    Application<Portlet> application = (Application<Portlet>) container.getChildren().get(index);
    savePortletInstanceApplicationId(application.getStorageId(),
                                     portletInstance.getId());
    return application;
  }

  private long getPortletInstanceApplicationId(String portletInstanceId) {
    SettingValue<?> settingValue = settingService.get(CONTEXT, SCOPE, portletInstanceId);
    if (settingValue != null && settingValue.getValue() != null && StringUtils.isNotBlank(settingValue.getValue().toString())) {
      return Long.parseLong(settingValue.getValue().toString());
    } else {
      return 0;
    }
  }

  private void savePortletInstanceApplicationId(String applicationStorageId, long portletInstanceId) {
    settingService.set(CONTEXT,
                       SCOPE,
                       String.valueOf(portletInstanceId),
                       SettingValue.create(applicationStorageId));
  }

  private Container getPortletInstanceSystemContainer() {
    return (Container) getPortletInstanceSystemPage().getChildren().get(0);
  }

  private Page getPortletInstanceSystemPage() {
    Page page = layoutService.getPage(PORTLET_EDITOR_SYSTEM_PAGE_KEY);
    if (page == null) {
      page = new Page();
      page.setTitle("Portlet Editor Working Page");
      page.setEditPermission("manager:/platform/administrators");
      page.setPageId(PORTLET_EDITOR_SYSTEM_PAGE_KEY.format());

      Container container = new Container();
      container.setTemplate("nop");
      page.setChildren(new ArrayList<>(Collections.singletonList(container)));

      PageState pageState = new PageState(page.getTitle(),
                                          page.getDescription(),
                                          false,
                                          null,
                                          Arrays.asList(UserACL.EVERYONE),
                                          page.getEditPermission(),
                                          Arrays.asList(UserACL.EVERYONE),
                                          Arrays.asList(UserACL.EVERYONE));
      layoutService.save(new PageContext(PORTLET_EDITOR_SYSTEM_PAGE_KEY, pageState), page);
      page = layoutService.getPage(PORTLET_EDITOR_SYSTEM_PAGE_KEY);
    }
    return page;
  }

  private Application<Portlet> getPortletEditorApplication() {
    if (portletEditorApplication == null) {
      portletEditorApplication = new PortletApplication();
      portletEditorApplication.setAccessPermissions(new String[] { UserACL.EVERYONE });
      portletEditorApplication.setState(new TransientApplicationState<>(PORTLET_EDITOR_PORTLET_CONTENT_ID));
    }
    return portletEditorApplication;
  }

}
