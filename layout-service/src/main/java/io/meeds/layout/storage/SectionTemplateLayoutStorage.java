/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io
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
package io.meeds.layout.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageState;
import org.exoplatform.portal.mop.service.LayoutService;

import io.meeds.layout.model.SectionTemplate;

import lombok.Synchronized;

@Component
public class SectionTemplateLayoutStorage {

  private static final Context CONTEXT                        = Context.GLOBAL.id("SECTION_TEMPLATE");

  private static final Scope   SECTION_TEMPLATE_SCOPE         = Scope.APPLICATION.id("CONTAINER_ID_BY_SECTION_TEMPLATE_ID");

  private static final Scope   PAGE_CONTAINER_SCOPE           = Scope.APPLICATION.id("SECTION_TEMPLATE_ID_BY_CONTAINER_ID");

  private static final PageKey SECTION_EDITOR_SYSTEM_PAGE_KEY = new PageKey(SiteKey.portal("global"), "_sectionEditor");

  @Autowired
  private SettingService       settingService;

  @Autowired
  private LayoutService        layoutService;

  public Container getOrCreateSectionTemplateContainer(SectionTemplate sectionTemplate) { // NOSONAR
    if (getContainerId(sectionTemplate.getId()) == 0) {
      return createSectionTemplateContainer(sectionTemplate);
    } else {
      try {
        long sectionTemplateId = sectionTemplate.getId();
        return getContainerBySectionTemplateId(sectionTemplateId);
      } catch (Exception e) {
        return createSectionTemplateContainer(sectionTemplate);
      }
    }
  }

  public Container getContainerBySectionTemplateId(long sectionTemplateId) {
    long containerId = getContainerId(sectionTemplateId);
    return getContainerById(containerId);
  }

  public Container getContainerById(long containerId) {
    Page page = getSystemPage();
    Container parentContainer = (Container) page.getChildren().get(0);
    ArrayList<ModelObject> children = parentContainer.getChildren();
    String containerIdString = String.valueOf(getSectionTemplateId(containerId));
    return children.stream()
                   .filter(Container.class::isInstance)
                   .map(Container.class::cast)
                   .filter(c -> StringUtils.equals(c.getStorageId(), containerIdString))
                   .findFirst()
                   .orElse(null);
  }

  public long getSectionTemplateId(long applicationId) {
    return getSettingValue(SECTION_TEMPLATE_SCOPE, applicationId);
  }

  public long getContainerId(long sectionTemplateId) {
    return getSettingValue(PAGE_CONTAINER_SCOPE, sectionTemplateId);
  }

  @Synchronized
  private Container createSectionTemplateContainer(SectionTemplate sectionTemplate) {
    Container sectionContainer = new Container();
    sectionContainer.setAccessPermissions(new String[] { UserACL.EVERYONE });
    Page page = getSystemPage();
    Container parentContainer = (Container) page.getChildren().get(0);
    ArrayList<ModelObject> children = parentContainer.getChildren();
    if (CollectionUtils.isEmpty(children)) {
      children = new ArrayList<>();
    } else {
      children = new ArrayList<>(children);
    }
    int index = children.size();
    children.add(sectionContainer);
    parentContainer.setChildren(children);
    page.setChildren(new ArrayList<>(Collections.singletonList(parentContainer)));
    layoutService.save(page);

    parentContainer = getSystemContainer();
    sectionContainer = (Container) parentContainer.getChildren().get(index);
    saveLayoutId(Long.parseLong(sectionContainer.getStorageId()), sectionTemplate.getId());
    return sectionContainer;
  }

  private long getSettingValue(Scope scope, long id) {
    SettingValue<?> settingValue = settingService.get(CONTEXT, scope, String.valueOf(id));
    if (settingValue != null && settingValue.getValue() != null && StringUtils.isNotBlank(settingValue.getValue().toString())) {
      return Long.parseLong(settingValue.getValue().toString());
    } else {
      return 0;
    }
  }

  private void saveLayoutId(long applicationStorageId, long portletInstanceId) {
    settingService.set(CONTEXT,
                       PAGE_CONTAINER_SCOPE,
                       String.valueOf(portletInstanceId),
                       SettingValue.create(applicationStorageId));
    settingService.set(CONTEXT,
                       SECTION_TEMPLATE_SCOPE,
                       String.valueOf(applicationStorageId),
                       SettingValue.create(portletInstanceId));
  }

  private Container getSystemContainer() {
    return (Container) getSystemPage().getChildren().get(0);
  }

  private Page getSystemPage() {
    Page page = layoutService.getPage(SECTION_EDITOR_SYSTEM_PAGE_KEY);
    if (page == null) {
      page = new Page();
      page.setTitle("Section Template Working Page");
      page.setEditPermission("manager:/platform/administrators");
      page.setPageId(SECTION_EDITOR_SYSTEM_PAGE_KEY.format());

      Container container = new Container();
      container.setTemplate("nop");
      page.setChildren(new ArrayList<>(Collections.singletonList(container)));

      PageState pageState = new PageState(page.getTitle(),
                                          page.getDescription(),
                                          false,
                                          null,
                                          Arrays.asList(UserACL.EVERYONE),
                                          page.getEditPermission());
      layoutService.save(new PageContext(SECTION_EDITOR_SYSTEM_PAGE_KEY, pageState), page);
      page = layoutService.getPage(SECTION_EDITOR_SYSTEM_PAGE_KEY);
    }
    return page;
  }

}
