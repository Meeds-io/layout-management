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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.Utils;
import org.exoplatform.portal.mop.navigation.NodeContext;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.mop.service.NavigationService;

import io.meeds.layout.model.NavigationCreateModel;
import io.meeds.layout.model.SectionTemplate;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.service.ContainerLayoutService;
import io.meeds.layout.service.NavigationLayoutService;
import io.meeds.layout.service.PageLayoutService;
import io.meeds.social.util.JsonUtils;

import lombok.Synchronized;

@Component
public class SectionTemplateLayoutStorage {

  public static final String      GLOBAL_SITE_NAME     = "global";

  public static final SiteKey     GLOBAL_SITE_KEY      = SiteKey.portal(GLOBAL_SITE_NAME);

  private static final String[]   EVERYONE_PERMISSIONS = new String[] { UserACL.EVERYONE };

  @Autowired
  private LayoutService           layoutService;

  @Autowired
  private NavigationService       navigationService;

  @Autowired
  private PageLayoutService       pageLayoutService;

  @Autowired
  private ContainerLayoutService  containerLayoutService;

  @Autowired
  private NavigationLayoutService navigationLayoutService;

  @Synchronized
  public NodeData generateSectionTemplateNodeId(SectionTemplate sectionTemplate, String username) throws IllegalAccessException,
                                                                                                  ObjectNotFoundException {
    PageKey clonedPageKey = clonePage(sectionTemplate, username);
    String clonedNodeName = getSectionNodeName(sectionTemplate.getId(), username);
    NodeContext<NodeContext<Object>> parentNode = navigationService.loadNode(GLOBAL_SITE_KEY);
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
      long nodeId = Long.parseLong(clonedNode.getId());
      navigationService.updateNode(nodeId, state);
      return navigationService.getNodeById(nodeId);
    }
  }

  @Synchronized
  public String generateSectionTemplateContent(SectionTemplate sectionTemplate, String username) {
    String clonedNodeName = getSectionNodeName(sectionTemplate.getId(), username);
    NodeContext<NodeContext<Object>> parentNode = navigationService.loadNode(GLOBAL_SITE_KEY);
    NodeContext<NodeContext<Object>> nodeData = navigationLayoutService.findNode(parentNode, clonedNodeName);
    PageKey pageKey = nodeData.getState().getPageRef();
    return generateSectionTemplateContent(pageKey);
  }

  public String generateSectionTemplateContent(SectionTemplate sectionTemplate,
                                               Container section,
                                               String username) {
    section = containerLayoutService.cloneContainer(section);
    String sectionTemplateContent = JsonUtils.toJsonString(new LayoutModel(section));
    PageKey pageKey = createSectionTemplatePage(sectionTemplate.getId(), sectionTemplateContent, username);
    return generateSectionTemplateContent(pageKey);
  }

  private PageKey clonePage(SectionTemplate sectionTemplate, String username) {
    long sectionTemplateId = sectionTemplate.getId();
    String sectionTemplateContent = sectionTemplate.getContent();
    return createSectionTemplatePage(sectionTemplateId, sectionTemplateContent, username);
  }

  private PageKey createSectionTemplatePage(long sectionTemplateId,
                                            String sectionTemplateContent,
                                            String username) {
    Container parentContainer = new Container();
    parentContainer.setTemplate("system:/groovy/portal/webui/container/UIPageLayout.gtmpl");
    parentContainer.setChildren(new ArrayList<>());
    LayoutModel layoutModel = JsonUtils.fromJsonString(sectionTemplateContent, LayoutModel.class);
    layoutModel.resetStorage();
    parentContainer.getChildren().add(LayoutModel.toModelObject(layoutModel));

    Page sectionDraftPage = new Page();
    sectionDraftPage.setAccessPermissions(EVERYONE_PERMISSIONS);
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

  private String generateSectionTemplateContent(PageKey pageKey) {
    Page page = layoutService.getPage(pageKey);
    Container section = (Container) ((Container) page.getChildren().get(0)).getChildren().get(0);
    containerLayoutService.exportPortletPreferences(section);
    LayoutModel sectionLayoutModel = new LayoutModel(section);
    sectionLayoutModel.resetStorage();
    return JsonUtils.toJsonString(sectionLayoutModel);
  }

  private String getSectionNodeName(long sectionTemplateId, String username) {
    return "section_draft_" + sectionTemplateId + "_" + username;
  }

}
