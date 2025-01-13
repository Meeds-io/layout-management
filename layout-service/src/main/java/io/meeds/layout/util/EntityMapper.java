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
package io.meeds.layout.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.portal.config.model.PortalConfig;

import io.meeds.layout.entity.PortletInstanceCategoryEntity;
import io.meeds.layout.entity.PortletInstanceEntity;
import io.meeds.layout.entity.SectionTemplateEntity;
import io.meeds.layout.model.PortletDescriptor;
import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.model.PortletInstanceCategory;
import io.meeds.layout.model.PortletInstancePreference;
import io.meeds.layout.model.SectionTemplate;
import io.meeds.layout.model.SiteTemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class EntityMapper {

  public static final String SITE_ENABLED_PROP         = "SITE_ENABLED";

  public static final String SITE_TEMPLATE_SYSTEM_PROP = "SITE_TEMPLATE_SYSTEM";

  public static final String SITE_TEMPLATE_ICON_PROP   = "SITE_TEMPLATE_ICON";

  private EntityMapper() {
    // Utils Class
  }

  public static PortletInstanceCategory fromEntity(PortletInstanceCategoryEntity entity) {
    return new PortletInstanceCategory(entity.getId(),
                                       null,
                                       entity.getIcon(),
                                       entity.isSystem(),
                                       entity.getPermissions()
                                           == null ? Collections.emptyList() :
                                                   entity.getPermissions().stream().filter(StringUtils::isNotBlank).toList());
  }

  public static PortletInstance fromEntity(PortletInstanceEntity entity, PortletDescriptor portlet) {
    return new PortletInstance(entity.getId(),
                               null,
                               null,
                               entity.getCategoryId(),
                               entity.getContentId(),
                               0,
                               getPreferences(entity),
                               0l,
                               entity.getPermissions()
                                   == null ? Collections.emptyList() :
                                           entity.getPermissions().stream().filter(StringUtils::isNotBlank).toList(),
                               portlet == null ? null : portlet.getSupportedModes(),
                               entity.isSystem(),
                               entity.isDisabled(),
                               false);
  }

  public static PortletInstanceCategoryEntity toEntity(PortletInstanceCategory instance) {
    return new PortletInstanceCategoryEntity(instance.getId() == 0 ? null : instance.getId(),
                                             instance.getPermissions(),
                                             instance.getIcon(),
                                             instance.isSystem());
  }

  public static PortletInstanceEntity toEntity(PortletInstance instance) {
    return new PortletInstanceEntity(instance.getId() == 0 ? null : instance.getId(),
                                     instance.getCategoryId(),
                                     instance.getContentId(),
                                     instance.getPermissions(),
                                     getPreferencesString(instance),
                                     instance.isSystem(),
                                     instance.isDisabled(),
                                     Objects.hash(instance.getCategoryId(),
                                                  getPreferencesString(instance),
                                                  instance.getContentId()));
  }

  public static SectionTemplateEntity toEntity(SectionTemplate sectionTemplate) {
    return new SectionTemplateEntity(sectionTemplate.getId(),
                                     sectionTemplate.getCategory(),
                                     sectionTemplate.getContent(),
                                     sectionTemplate.isSystem(),
                                     sectionTemplate.isDisabled());
  }

  public static SectionTemplate fromEntity(SectionTemplateEntity entity) {
    return new SectionTemplate(entity.getId(),
                               entity.getCategory(),
                               entity.getContent(),
                               entity.isSystem(),
                               entity.isDisabled());
  }

  public static SiteTemplate toSiteTemplate(PortalConfig portalConfig) {
    SiteTemplate siteTemplate = new SiteTemplate();
    siteTemplate.setId(portalConfig.getId());
    siteTemplate.setLayout(portalConfig.getName());
    siteTemplate.setIcon(portalConfig.getProperty(SITE_TEMPLATE_ICON_PROP));
    siteTemplate.setSystem(StringUtils.equals(portalConfig.getProperty(SITE_TEMPLATE_SYSTEM_PROP), "true"));
    siteTemplate.setDisabled(StringUtils.equals(portalConfig.getProperty(SITE_ENABLED_PROP), "false"));
    return siteTemplate;
  }

  private static String getPreferencesString(PortletInstance instance) {
    if (CollectionUtils.isNotEmpty(instance.getPreferences())) {
      return JsonUtils.toJsonString(new PortletInstancePreferences(instance.getPreferences()));
    }
    return null;
  }

  private static List<PortletInstancePreference> getPreferences(PortletInstanceEntity entity) {
    if (StringUtils.isNotBlank(entity.getPreferences())) {
      return JsonUtils.fromJsonString(entity.getPreferences(), PortletInstancePreferences.class).getPreferences();
    }
    return Collections.emptyList();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PortletInstancePreferences {
    private List<PortletInstancePreference> preferences;
  }

}
