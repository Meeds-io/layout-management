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
package io.meeds.layout.service;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.model.SectionTemplate;
import io.meeds.layout.model.SectionTemplateDetail;
import io.meeds.layout.plugin.attachment.SectionTemplateAttachmentPlugin;
import io.meeds.layout.plugin.translation.SectionTemplateTranslationPlugin;
import io.meeds.layout.storage.SectionTemplateLayoutStorage;
import io.meeds.layout.storage.SectionTemplateStorage;
import io.meeds.social.translation.model.TranslationField;
import io.meeds.social.translation.service.TranslationService;

@Service
public class SectionTemplateService {

  public static final String           TEMPLATE_CREATED_EVENT = "layout.sectionTemplate.created";

  public static final String           TEMPLATE_UPDATED_EVENT = "layout.sectionTemplate.updated";

  public static final String           TEMPLATE_DELETED_EVENT = "layout.sectionTemplate.deleted";

  private static final Log             LOG                    = ExoLogger.getLogger(SectionTemplateService.class);

  @Autowired
  private LayoutAclService             layoutAclService;

  @Autowired
  private TranslationService           translationService;

  @Autowired
  private AttachmentService            attachmentService;

  @Autowired
  private LocaleConfigService          localeConfigService;

  @Autowired
  private SectionTemplateStorage       sectionTemplateStorage;

  @Autowired
  private SectionTemplateLayoutStorage sectionTemplateLayoutStorage;

  @Autowired
  private ListenerService              listenerService;

  public List<SectionTemplate> getSectionTemplates() {
    return sectionTemplateStorage.getSectionTemplates();
  }

  public List<SectionTemplateDetail> getSectionTemplates(Locale locale) {
    List<SectionTemplate> sectionTemplates = sectionTemplateStorage.getSectionTemplates();
    return sectionTemplates.stream()
                           .map(t -> computeSectionTemplateAttributes(t, locale))
                           .toList();
  }

  public SectionTemplate getSectionTemplate(long id) {
    return sectionTemplateStorage.getSectionTemplate(id);
  }

  public SectionTemplateDetail getSectionTemplate(long id, Locale locale) throws ObjectNotFoundException {
    SectionTemplate sectionTemplate = getSectionTemplate(id);
    if (sectionTemplate == null) {
      throw new ObjectNotFoundException("Section Template not found");
    }
    return computeSectionTemplateAttributes(sectionTemplate, locale);
  }

  public SectionTemplate createSectionTemplate(SectionTemplate sectionTemplate, String username) throws IllegalAccessException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to create a Section Template");
    }
    return createSectionTemplateWithUser(sectionTemplate, username);
  }

  public SectionTemplate createSectionTemplate(SectionTemplate sectionTemplate) {
    return createSectionTemplateWithUser(sectionTemplate, null);
  }

  public void deleteSectionTemplate(long id, String username) throws IllegalAccessException, ObjectNotFoundException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to delete a Section Template");
    }
    SectionTemplate sectionTemplate = getSectionTemplate(id);
    if (sectionTemplate == null) {
      throw new ObjectNotFoundException("Section Template doesn't exist");
    }
    if (sectionTemplate.isSystem()) {
      throw new IllegalAccessException("Can't delete a system Section Template");
    }
    deleteSectionTemplateFromStore(id);
    listenerService.broadcast(TEMPLATE_DELETED_EVENT, username, sectionTemplate);
  }

  public void deleteSectionTemplate(long id) throws ObjectNotFoundException {
    SectionTemplate sectionTemplate = getSectionTemplate(id);
    if (sectionTemplate == null) {
      throw new ObjectNotFoundException("Section Template doesn't exist");
    }
    deleteSectionTemplateFromStore(id);
    listenerService.broadcast(TEMPLATE_DELETED_EVENT, null, sectionTemplate);
  }

  public SectionTemplate updateSectionTemplate(SectionTemplate sectionTemplate, String username) throws ObjectNotFoundException,
                                                                                                 IllegalAccessException {
    if (!layoutAclService.isAdministrator(username)) {
      throw new IllegalAccessException("User isn't authorized to update a Section Template");
    }
    return updateSectionTemplateWithUser(sectionTemplate, username);
  }

  public SectionTemplate updateSectionTemplate(SectionTemplate sectionTemplate) throws ObjectNotFoundException {
    return updateSectionTemplateWithUser(sectionTemplate, null);
  }

  public long getSectionTemplateContainerId(long sectionTemplateId) {
    return sectionTemplateLayoutStorage.getContainerId(sectionTemplateId);
  }

  private void deleteSectionTemplateFromStore(long id) throws ObjectNotFoundException {
    try {
      attachmentService.deleteAttachments(SectionTemplateAttachmentPlugin.OBJECT_TYPE, String.valueOf(id));
    } catch (Exception e) {
      LOG.debug("Error while deleting attachments of deleted Section Template", e);
    }
    try {
      translationService.deleteTranslationLabels(SectionTemplateTranslationPlugin.OBJECT_TYPE, id);
    } catch (Exception e) {
      LOG.debug("Error while deleting translation labels of deleted Section Template", e);
    }
    sectionTemplateStorage.deleteSectionTemplate(id);
  }

  private SectionTemplateDetail computeSectionTemplateAttributes(SectionTemplate sectionTemplate, Locale locale) {
    if (locale == null) {
      return new SectionTemplateDetail(sectionTemplate);
    }
    SectionTemplateDetail sectionTemplateDetail = new SectionTemplateDetail(sectionTemplate);
    sectionTemplateDetail.setContainerId(getSectionTemplateContainerId(sectionTemplate.getId()));
    sectionTemplateDetail.setName(getLabel(SectionTemplateTranslationPlugin.OBJECT_TYPE,
                                           sectionTemplateDetail.getId(),
                                           SectionTemplateTranslationPlugin.TITLE_FIELD_NAME,
                                           locale));
    sectionTemplateDetail.setDescription(getLabel(SectionTemplateTranslationPlugin.OBJECT_TYPE,
                                                  sectionTemplateDetail.getId(),
                                                  SectionTemplateTranslationPlugin.DESCRIPTION_FIELD_NAME,
                                                  locale));
    List<String> attachmentFileIds = attachmentService.getAttachmentFileIds(SectionTemplateAttachmentPlugin.OBJECT_TYPE,
                                                                            String.valueOf(sectionTemplateDetail.getId()));
    if (CollectionUtils.isNotEmpty(attachmentFileIds)) {
      sectionTemplateDetail.setIllustrationId(Long.parseLong(attachmentFileIds.get(0)));
    }
    return sectionTemplateDetail;
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

  private SectionTemplate createSectionTemplateWithUser(SectionTemplate sectionTemplate, String username) {
    SectionTemplate createdSectionTemplate = sectionTemplateStorage.createSectionTemplate(sectionTemplate);
    sectionTemplateLayoutStorage.initContainer(createdSectionTemplate);
    listenerService.broadcast(TEMPLATE_CREATED_EVENT, username, createdSectionTemplate);
    return createdSectionTemplate;
  }

  private SectionTemplate updateSectionTemplateWithUser(SectionTemplate sectionTemplate,
                                                        String username) throws ObjectNotFoundException {
    SectionTemplate updatedSectionTemplate = sectionTemplateStorage.updateSectionTemplate(sectionTemplate);
    sectionTemplateLayoutStorage.initContainer(updatedSectionTemplate);
    listenerService.broadcast(TEMPLATE_UPDATED_EVENT, username, updatedSectionTemplate);
    return updatedSectionTemplate;
  }

}
