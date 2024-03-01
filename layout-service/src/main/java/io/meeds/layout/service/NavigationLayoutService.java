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

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.mop.State;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.NodeData;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.DescriptionService;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.portal.mop.service.NavigationService;

import io.meeds.common.ContainerTransactional;
import io.meeds.layout.model.NavigationCreateModel;
import io.meeds.layout.model.NavigationUpdateModel;
import io.meeds.layout.model.NodeLabel;
import io.meeds.layout.rest.util.EntityBuilder;

@Service
public class NavigationLayoutService {

  private static final String            NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND = "Node with id %s doesn't exist";

  private static final Map<Long, String> QUEUE                               = new ConcurrentHashMap<>();

  @Autowired
  private NavigationService              navigationService;

  @Autowired
  private LayoutService                  layoutService;

  @Autowired
  private DescriptionService             descriptionService;

  @Autowired
  private LayoutAclService               aclService;

  public NodeData createNode(NavigationCreateModel nodeModel,
                             String username) throws ObjectNotFoundException,
                                              IllegalAccessException,
                                              IllegalArgumentException{
    NodeData parentNodeData = navigationService.getNodeById(nodeModel.getParentNodeId());
    if (parentNodeData == null) {
      throw new ObjectNotFoundException(String.format("Parent node with id %s doesn't exist", nodeModel.getParentNodeId()));
    } else if (!aclService.canEditNavigation(parentNodeData.getSiteKey(), username)) {
      throw new IllegalAccessException();
    }

    NodeState nodeState = buildNodeState(nodeModel.getNodeLabel(),
                                         nodeModel.getIcon(),
                                         getPageKey(nodeModel.getPageRef()),
                                         nodeModel.getTarget(),
                                         nodeModel.isVisible(),
                                         nodeModel.isScheduled(),
                                         nodeModel.getStartScheduleDate(),
                                         nodeModel.getEndScheduleDate(),
                                         nodeModel.isPasteMode());
    NodeData[] nodeDatas = navigationService.createNode(nodeModel.getParentNodeId(),
                                                        nodeModel.getPreviousNodeId(),
                                                        nodeModel.getNodeId(),
                                                        nodeState);
    NodeData nodeData = nodeDatas[1];
    saveNodeLabels(nodeData.getId(), nodeModel.getLabels());

    return navigationService.getNodeById(Long.parseLong(nodeData.getId()));
  }

  public void updateNode(long nodeId,
                         NavigationUpdateModel nodeModel,
                         String username) throws ObjectNotFoundException,
                                          IllegalAccessException,
                                          IllegalArgumentException {
    NodeData nodeData = navigationService.getNodeById(nodeId);
    if (nodeData == null) {
      throw new ObjectNotFoundException(String.format(NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND, nodeId));
    } else if (!aclService.canEditNavigation(nodeData.getSiteKey(), username)) {
      throw new IllegalAccessException();
    }

    NodeState nodeState = buildNodeState(nodeModel.getNodeLabel(),
                                         nodeModel.getIcon(),
                                         getPageKey(nodeModel.getPageRef()),
                                         nodeModel.getTarget(),
                                         nodeModel.isVisible(),
                                         nodeModel.isScheduled(),
                                         nodeModel.getStartScheduleDate(),
                                         nodeModel.getEndScheduleDate(),
                                         false);
    saveNodeLabels(nodeData.getId(), nodeModel.getLabels());
    navigationService.updateNode(nodeId, nodeState);
  }

  public void deleteNode(long nodeId,
                         long delay,
                         String username) throws ObjectNotFoundException,
                                          IllegalAccessException {
    NodeData nodeData = navigationService.getNodeById(nodeId);
    if (nodeData == null) {
      throw new ObjectNotFoundException(String.format(NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND, nodeId));
    } else if (!aclService.canEditNavigation(nodeData.getSiteKey(), username)) {
      throw new IllegalAccessException();
    }
    if (delay > 0) {
      QUEUE.put(nodeId, username);
      CompletableFuture.delayedExecutor(delay, TimeUnit.SECONDS).execute(() -> deleteNode(nodeId));
    } else {
      deleteNode(nodeId);
    }
  }

  public void undoDeleteNode(long nodeId, String username) throws ObjectNotFoundException {
    if (StringUtils.equals(username, QUEUE.get(nodeId))) {
      QUEUE.remove(nodeId);
    } else {
      throw new ObjectNotFoundException(String.format("Node with id %s doesn't exist in queue", nodeId));
    }
  }

  public void moveNode(long nodeId,
                       Long destinationParentId,
                       Long previousNodeId,
                       String username) throws ObjectNotFoundException,
                                        IllegalAccessException {
    NodeData nodeData = navigationService.getNodeById(nodeId);
    if (nodeData == null) {
      throw new ObjectNotFoundException(String.format(NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND, nodeId));
    } else if (!aclService.canEditNavigation(nodeData.getSiteKey(), username)) {
      throw new IllegalAccessException();
    }
    long parentId = Long.parseLong(nodeData.getParentId());
    if (destinationParentId == null) {
      destinationParentId = parentId;
    }
    navigationService.moveNode(nodeId, parentId, destinationParentId, previousNodeId);
  }

  public NodeData getNode(long nodeId,
                          String username) throws ObjectNotFoundException,
                                           IllegalAccessException {
    NodeData nodeData = navigationService.getNodeById(nodeId);
    if (nodeData == null) {
      throw new ObjectNotFoundException(String.format(NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND, nodeId));
    } else if (!aclService.canViewNavigation(nodeData.getSiteKey(), nodeData.getState().getPageRef(), username)) {
      throw new IllegalAccessException();
    }
    return nodeData;
  }

  public NodeLabel getNodeLabels(long nodeId,
                                 String username) throws ObjectNotFoundException,
                                                  IllegalAccessException {
    NodeData nodeData = navigationService.getNodeById(nodeId);
    if (nodeData == null) {
      throw new ObjectNotFoundException(String.format(NODE_DATA_WITH_NODE_ID_IS_NOT_FOUND, nodeId));
    } else if (!aclService.canViewNavigation(nodeData.getSiteKey(), nodeData.getState().getPageRef(), username)) {
      throw new IllegalAccessException();
    }
    Map<Locale, State> nodeLabels = descriptionService.getDescriptions(String.valueOf(nodeId));
    return EntityBuilder.toNodeLabel(nodeLabels);
  }

  @ContainerTransactional
  public void deleteNode(long nodeId) {
    if (QUEUE.containsKey(nodeId)) {
      try {
        navigationService.deleteNode(nodeId);
      } finally {
        QUEUE.remove(nodeId);
      }
    }
  }

  private NodeState buildNodeState(String label, // NOSONAR
                                   String icon,
                                   PageKey pageKey,
                                   String target,
                                   boolean visible,
                                   boolean scheduled,
                                   Long startScheduleDate,
                                   Long endScheduleDate,
                                   boolean pasteMode) {
    if (visible
        && scheduled
        && startScheduleDate != null
        && endScheduleDate != null) {
      if (startScheduleDate > endScheduleDate) {
        throw new IllegalArgumentException("end schedule date must be after start schedule date");
      } else if (System.currentTimeMillis() > startScheduleDate && !pasteMode) {
        throw new IllegalArgumentException("start schedule date must be after current date");
      } else {
        return new NodeState(label,
                             icon,
                             startScheduleDate,
                             endScheduleDate,
                             Visibility.TEMPORAL,
                             pageKey,
                             null,
                             target,
                             System.currentTimeMillis());
      }
    } else {
      return new NodeState(label,
                           icon,
                           -1,
                           -1,
                           visible ? Visibility.DISPLAYED : Visibility.HIDDEN,
                           pageKey,
                           null,
                           target,
                           System.currentTimeMillis());
    }
  }

  private void saveNodeLabels(String nodeId, Map<String, String> labels) {
    if (labels != null) {
      Map<Locale, State> nodeLabels = new HashMap<>();
      labels.entrySet().forEach(label -> nodeLabels.put(new Locale(label.getKey()), new State(label.getValue(), null)));
      descriptionService.setDescriptions(nodeId, nodeLabels);
    } else {
      descriptionService.setDescriptions(nodeId, Collections.emptyMap());
    }
  }

  private PageKey getPageKey(String pageRef) throws ObjectNotFoundException, IllegalArgumentException {
    if (StringUtils.isNotBlank(pageRef)) {
      PageContext pageContext = layoutService.getPageContext(PageKey.parse(pageRef));
      if (pageContext == null) {
        throw new ObjectNotFoundException(String.format("Page with key %s doesn't exist", pageRef));
      } else {
        return pageContext.getKey();
      }
    } else {
      return null;
    }
  }

}
