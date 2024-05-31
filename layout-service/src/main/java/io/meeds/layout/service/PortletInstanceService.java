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
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.model.PortletInstanceCategory;
import io.meeds.layout.plugin.attachment.PortletInstanceAttachmentPlugin;
import io.meeds.layout.plugin.translation.PortletInstanceCategoryTranslationPlugin;
import io.meeds.layout.plugin.translation.PortletInstanceTranslationPlugin;
import io.meeds.layout.storage.PortletInstanceCategoryStorage;
import io.meeds.layout.storage.PortletInstanceStorage;
import io.meeds.social.translation.model.TranslationField;
import io.meeds.social.translation.service.TranslationService;

@Service
public class PortletInstanceService {

  private static final List<String>      EVERYONE_PERMISSIONS_LIST = Collections.singletonList(UserACL.EVERYONE);

  private static final Log               LOG                       =
                                             ExoLogger.getLogger(PortletInstanceService.class);

  @Autowired
  private LayoutAclService               layoutAclService;

  @Autowired
  private TranslationService             translationService;

  @Autowired
  private AttachmentService              attachmentService;

  @Autowired
  private LocaleConfigService            localeConfigService;

  @Autowired
  private PortletInstanceCategoryStorage portletInstanceCategoryStorage;

  @Autowired
  private PortletInstanceStorage         portletInstanceStorage;

  public List<PortletInstance> getPortletInstances() {
    return portletInstanceStorage.getPortletInstances();
  }

  public List<PortletInstance> getPortletInstances(long categoryId,
                                                   String username,
                                                   Locale locale,
                                                   boolean expand) {
    List<PortletInstance> portletInstances = categoryId < 1 ? portletInstanceStorage.getPortletInstances() :
                                                            portletInstanceStorage.getPortletInstances(categoryId);
    portletInstances = portletInstances.stream().filter(p -> this.hasPermission(p, username)).toList();
    if (expand) {
      portletInstances.stream()
                      .forEach(portletInstance -> computePortletInstanceAttributes(locale, portletInstance));
    }
    return portletInstances;
  }

  public List<PortletInstanceCategory> getPortletInstanceCategories() {
    return portletInstanceCategoryStorage.getPortletInstanceCategories();
  }

  public List<PortletInstanceCategory> getPortletInstanceCategories(String username,
                                                                    Locale locale,
                                                                    boolean expand) {
    List<PortletInstanceCategory> portletInstanceCategories = portletInstanceCategoryStorage.getPortletInstanceCategories();
    portletInstanceCategories = portletInstanceCategories.stream().filter(c -> this.hasPermission(c, username)).toList();
    if (expand && locale != null) {
      portletInstanceCategories.stream()
                               .forEach(c -> computePortletInstanceCategoryAttributes(locale, c));
    }
    return portletInstanceCategories;
  }

  public PortletInstance getPortletInstance(long id,
                                            String username,
                                            Locale locale,
                                            boolean expand) throws IllegalAccessException, ObjectNotFoundException {
    PortletInstance portletInstance = portletInstanceStorage.getPortletInstance(id);
    if (portletInstance == null) {
      throw new ObjectNotFoundException("Portlet instance not found");
    }
    if (!this.hasPermission(portletInstance, username)) {
      throw new IllegalAccessException();
    }
    if (expand && locale != null) {
      computePortletInstanceAttributes(locale, portletInstance);
    }
    return portletInstance;
  }

  public PortletInstanceCategory getPortletInstanceCategory(long id) {
    return portletInstanceCategoryStorage.getPortletInstanceCategory(id);
  }

  public PortletInstanceCategory getPortletInstanceCategory(long id,
                                                            String username,
                                                            Locale locale,
                                                            boolean expand) throws ObjectNotFoundException,
                                                                            IllegalAccessException {
    PortletInstanceCategory portletInstanceCategory = portletInstanceCategoryStorage.getPortletInstanceCategory(id);
    if (portletInstanceCategory == null) {
      throw new ObjectNotFoundException("Portlet instance category not found");
    }
    if (!this.hasPermission(portletInstanceCategory, username)) {
      throw new IllegalAccessException();
    }
    if (expand) {
      computePortletInstanceCategoryAttributes(locale, portletInstanceCategory);
    }
    return portletInstanceCategory;
  }

  public PortletInstance getPortletInstance(long id) {
    return portletInstanceStorage.getPortletInstance(id);
  }

  public PortletInstance createPortletInstance(PortletInstance portletInstance, String username) throws IllegalAccessException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to create a Portlet instance");
    }
    return createPortletInstance(portletInstance);
  }

  public PortletInstance createPortletInstance(PortletInstance portletInstance) {
    return portletInstanceStorage.createPortletInstance(portletInstance);
  }

  public PortletInstanceCategory createPortletInstanceCategory(PortletInstanceCategory portletInstanceCategory,
                                                               String username) throws IllegalAccessException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to create a Portlet instance Category");
    }
    return createPortletInstanceCategory(portletInstanceCategory);
  }

  public PortletInstanceCategory createPortletInstanceCategory(PortletInstanceCategory category) {
    return portletInstanceCategoryStorage.createPortletInstanceCategory(category);
  }

  public void deletePortletInstance(long id, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to delete a Portlet instance");
    }
    PortletInstance portletInstance = getPortletInstance(id);
    if (portletInstance == null) {
      throw new ObjectNotFoundException("Portlet instance doesn't exist");
    }
    if (portletInstance.isSystem()) {
      throw new IllegalAccessException("Can't delete a system Portlet instance");
    }
    deletePortletInstance(id);
  }

  public void deletePortletInstance(long id) throws ObjectNotFoundException {
    try {
      attachmentService.deleteAttachments(PortletInstanceAttachmentPlugin.OBJECT_TYPE, String.valueOf(id));
    } catch (Exception e) {
      LOG.debug("Error while deleting attachments of deleted Portlet instance", e);
    }
    try {
      translationService.deleteTranslationLabels(PortletInstanceTranslationPlugin.OBJECT_TYPE, id);
    } catch (ObjectNotFoundException e) {
      LOG.debug("Error while deleting translation labels of deleted Portlet instance", e);
    }
    portletInstanceStorage.deletePortletInstance(id);
  }

  public void deletePortletInstanceCategory(long id, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to create a Portlet instance Category");
    }
    PortletInstanceCategory portletInstanceCategory = getPortletInstanceCategory(id);
    if (portletInstanceCategory == null) {
      throw new ObjectNotFoundException("Portlet instance Category doesn't exist");
    }
    if (portletInstanceCategory.isSystem()) {
      throw new IllegalAccessException("Can't delete a system Portlet instance Category");
    }
    deletePortletInstanceCategory(id);
  }

  public void deletePortletInstanceCategory(long id) throws ObjectNotFoundException {
    try {
      translationService.deleteTranslationLabels(PortletInstanceCategoryTranslationPlugin.OBJECT_TYPE, id);
    } catch (ObjectNotFoundException e) {
      LOG.debug("Error while deleting translation labels of deleted Portlet instance Category", e);
    }
    portletInstanceCategoryStorage.deletePortletInstanceCategory(id);
  }

  public PortletInstance updatePortletInstance(PortletInstance portletInstance, String username) throws ObjectNotFoundException,
                                                                                                 IllegalAccessException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to update a Portlet instance");
    }
    return updatePortletInstance(portletInstance);
  }

  public PortletInstance updatePortletInstance(PortletInstance portletInstance) throws ObjectNotFoundException {
    return portletInstanceStorage.updatePortletInstance(portletInstance);
  }

  public PortletInstanceCategory updatePortletInstanceCategory(PortletInstanceCategory portletInstanceCategory,
                                                               String username) throws ObjectNotFoundException,
                                                                                IllegalAccessException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to update a Portlet instance category");
    }
    return updatePortletInstanceCategory(portletInstanceCategory);
  }

  public PortletInstanceCategory updatePortletInstanceCategory(PortletInstanceCategory category) throws ObjectNotFoundException {
    return portletInstanceCategoryStorage.updatePortletInstanceCategory(category);
  }

  private void computePortletInstanceAttributes(Locale locale, PortletInstance portletInstance) {
    portletInstance.setName(getLabel(PortletInstanceTranslationPlugin.OBJECT_TYPE,
                                     portletInstance.getId(),
                                     PortletInstanceTranslationPlugin.TITLE_FIELD_NAME,
                                     locale));
    portletInstance.setDescription(getLabel(PortletInstanceTranslationPlugin.OBJECT_TYPE,
                                            portletInstance.getId(),
                                            PortletInstanceTranslationPlugin.DESCRIPTION_FIELD_NAME,
                                            locale));
    List<String> attachmentFileIds = attachmentService.getAttachmentFileIds(PortletInstanceAttachmentPlugin.OBJECT_TYPE,
                                                                            String.valueOf(portletInstance.getId()));
    if (CollectionUtils.isNotEmpty(attachmentFileIds)) {
      portletInstance.setIllustrationId(Long.parseLong(attachmentFileIds.get(0)));
    }
  }

  private void computePortletInstanceCategoryAttributes(Locale locale, PortletInstanceCategory portletInstanceCategory) {
    portletInstanceCategory.setName(getLabel(PortletInstanceCategoryTranslationPlugin.OBJECT_TYPE,
                                             portletInstanceCategory.getId(),
                                             PortletInstanceCategoryTranslationPlugin.TITLE_FIELD_NAME,
                                             locale));
  }

  private String getLabel(String objectType, long objectId, String fieldName, Locale locale) {
    if (locale == null) {
      locale = localeConfigService.getDefaultLocaleConfig().getLocale();
    }
    try {
      TranslationField translationField = translationService.getTranslationField(objectType,
                                                                                 objectId,
                                                                                 fieldName);
      if (translationField != null && MapUtils.isNotEmpty(translationField.getLabels())) {
        String label = translationField.getLabels().get(locale);
        if (label == null) {
          Locale defaultLocale = localeConfigService.getDefaultLocaleConfig().getLocale();
          label = translationField.getLabels().get(defaultLocale);
        }
        if (label == null) {
          label = translationField.getLabels().values().iterator().next();
        }
        return label;
      } else {
        return null;
      }
    } catch (ObjectNotFoundException e) {
      return null;
    }
  }

  private boolean hasPermission(PortletInstance portletInstance, String username) {
    List<String> permissions = portletInstance.getPermissions();
    return CollectionUtils.isEmpty(permissions)
           || permissions.equals(EVERYONE_PERMISSIONS_LIST)
           || (StringUtils.isNotBlank(username) && permissions.stream().anyMatch(p -> layoutAclService.isMemberOf(username, p)));
  }

  private boolean hasPermission(PortletInstanceCategory category, String username) {
    List<String> permissions = category.getPermissions();
    return CollectionUtils.isEmpty(permissions)
           || permissions.equals(EVERYONE_PERMISSIONS_LIST)
           || (StringUtils.isNotBlank(username) && permissions.stream().anyMatch(p -> layoutAclService.isMemberOf(username, p)));
  }

}
