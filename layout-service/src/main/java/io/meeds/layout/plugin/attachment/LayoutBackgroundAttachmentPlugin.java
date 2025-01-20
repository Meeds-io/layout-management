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
package io.meeds.layout.plugin.attachment;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.attachment.AttachmentPlugin;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.service.LayoutAclService;

import jakarta.annotation.PostConstruct;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LayoutBackgroundAttachmentPlugin extends AttachmentPlugin {

  public static final String OBJECT_TYPE = "containerBackground";

  @Autowired
  private LayoutAclService   layoutAclService;

  @Autowired
  private LayoutService      layoutService;

  @Autowired
  private AttachmentService  attachmentService;

  @PostConstruct
  public void init() {
    attachmentService.addPlugin(this);
  }

  @Override
  public String getObjectType() {
    return OBJECT_TYPE;
  }

  @Override
  public boolean hasEditPermission(Identity userIdentity, String entityId) throws ObjectNotFoundException {
    return layoutAclService.canEditPage(getPageKey(entityId), getUsername(userIdentity));
  }

  @Override
  public boolean hasAccessPermission(Identity userIdentity, String entityId) throws ObjectNotFoundException {
    PageKey pageKey = getPageKey(entityId);
    return pageKey.getSite().getType() == SiteType.GROUP_TEMPLATE
           || pageKey.getSite().getType() == SiteType.PORTAL_TEMPLATE
           || layoutAclService.canViewPage(pageKey, getUsername(userIdentity));
  }

  @Override
  public long getAudienceId(String objectId) throws ObjectNotFoundException {
    return 0;
  }

  @Override
  public long getSpaceId(String objectId) throws ObjectNotFoundException {
    return 0;
  }

  private PageKey getPageKey(String entityId) {
    String pageUuid = entityId.split("_")[0].replace("page_", "");
    long pageId = StringUtils.isNumeric(pageUuid) ? Long.parseLong(pageUuid) : 0;
    Page page = layoutService.getPage(pageId);
    return page.getPageKey();
  }

  private String getUsername(Identity userIdentity) {
    return userIdentity == null ? null : userIdentity.getUserId();
  }

}
