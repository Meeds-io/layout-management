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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.Utils;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.mop.service.NavigationService;
import org.exoplatform.portal.pom.spi.portlet.Portlet;

import io.meeds.layout.model.NavigationCreateModel;
import io.meeds.layout.model.SectionTemplate;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.service.NavigationLayoutService;
import io.meeds.layout.service.PageLayoutService;
import io.meeds.layout.service.PortletInstanceService;
import io.meeds.social.util.JsonUtils;

import lombok.Synchronized;

@Component
public class SectionTemplateLayoutStorage {

  private static final String     GLOBAL_SITE_NAME = "global";

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private NavigationService       navigationService;

  @Autowired
  private PageLayoutService       pageLayoutService;

  @Autowired
  private PortletInstanceService  portletInstanceService;

  @Autowired
  private NavigationLayoutService navigationLayoutService;

  @Synchronized
  public NodeData generateSectionTemplateNodeId(SectionTemplate sectionTemplate, String username) throws IllegalAccessException,
                                                                                                  ObjectNotFoundException {
    PageKey clonedPageKey = clonePage(sectionTemplate, username);
    String clonedNodeName = getSectionNodeName(sectionTemplate.getId(), username);
    NodeContext<NodeContext<Object>> parentNode = navigationService.loadNode(SiteKey.portal(GLOBAL_SITE_NAME));
    NodeContext<NodeContext<Object>> clonedNode = navigationLayoutService.findNode(parentNode, clonedNodeName);
    if (clonedNode == null) {
      return navigationLayoutService.createNode(new NavigationCreateModel(Long.parseLong(parentNode.getId()),
                                                                          null,
                                                                          clonedNodeName,
                                                                          clonedNodeName,
                                                                          false,
                                                                          false,
                                                                          true,
                                                                          null,
                                                                          null,
                                                                          clonedPageKey.format(),
                                                                          null,
                                                                          false,
                                                                          null,
                                                                          null),
                                                username);
    } else {
      NodeState state = clonedNode.getState().builder().pageRef(clonedPageKey).build();
      navigationService.updateNode(Long.parseLong(clonedNode.getId()), state);
      return navigationService.getNodeById(Long.parseLong(clonedNode.getId()));
    }
  }

  @Synchronized
  public String generateSectionTemplateContent(SectionTemplate sectionTemplate, String username) {
    String clonedNodeName = getSectionNodeName(sectionTemplate.getId(), username);
    NodeContext<NodeContext<Object>> parentNode = navigationService.loadNode(SiteKey.portal(GLOBAL_SITE_NAME));
    NodeContext<NodeContext<Object>> nodeData = navigationLayoutService.findNode(parentNode, clonedNodeName);
    PageKey pageKey = nodeData.getState().getPageRef();
    Page page = layoutService.getPage(pageKey);
    Container section = (Container) ((Container) page.getChildren().get(0)).getChildren().get(0);
    exportPortletPreferences(section);
    LayoutModel sectionLayoutModel = new LayoutModel(section);
    sectionLayoutModel.resetStorage();
    return JsonUtils.toJsonString(sectionLayoutModel);
  }

  private PageKey clonePage(SectionTemplate sectionTemplate, String username) throws ObjectNotFoundException {
    Container parentContainer = new Container();
    parentContainer.setTemplate("system:/groovy/portal/webui/container/UIPageLayout.gtmpl");
    parentContainer.setChildren(new ArrayList<>());
    parentContainer.getChildren()
                   .add(LayoutModel.toModelObject(JsonUtils.fromJsonString(sectionTemplate.getContent(), LayoutModel.class)));
    parentContainer.resetStorage();

    long sectionTemplateId = sectionTemplate.getId();
    Page sectionDraftPage = new Page();
    sectionDraftPage.setOwnerType(PortalConfig.PORTAL_TYPE);
    sectionDraftPage.setOwnerId(GLOBAL_SITE_NAME);
    sectionDraftPage.setName(getSectionNodeName(sectionTemplateId, username));
    sectionDraftPage.setTitle("Section Template Draft " + username);
    sectionDraftPage.setChildren(new ArrayList<>());
    sectionDraftPage.getChildren().add(parentContainer);

    PageKey clonedPageKey = sectionDraftPage.getPageKey();
    layoutService.save(new PageContext(clonedPageKey, Utils.toPageState(sectionDraftPage)), sectionDraftPage);
    pageLayoutService.impersonatePage(clonedPageKey);
    return clonedPageKey;
  }

  private void exportPortletPreferences(ModelObject object) {
    if (object instanceof Container container && CollectionUtils.isNotEmpty(container.getChildren())) {
      container.getChildren().forEach(this::exportPortletPreferences);
    } else if (object instanceof Application application) {
      Portlet preferences = portletInstanceService.exportApplicationPreferences(application);
      String portletContentId = layoutService.getId(application.getState());
      application.setState(new TransientApplicationState(portletContentId, preferences));
    }
  }

  private String getSectionNodeName(long sectionTemplateId, String username) {
    return "section_draft_" + sectionTemplateId + "_" + username;
  }

}
