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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelUnmarshaller;
import org.exoplatform.portal.config.model.UnmarshalledObject;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.attachment.AttachmentService;
import org.exoplatform.social.attachment.model.UploadedAttachmentDetail;
import org.exoplatform.social.rest.api.RestUtils;
import org.exoplatform.upload.UploadResource;

import io.meeds.common.ContainerTransactional;
import io.meeds.layout.model.PageTemplate;
import io.meeds.layout.model.PageTemplateDescriptor;
import io.meeds.layout.model.PageTemplateDescriptorList;
import io.meeds.layout.plugin.PageTemplateAttachmentPlugin;
import io.meeds.layout.plugin.PageTemplateTranslationPlugin;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.util.JsonUtils;
import io.meeds.social.translation.service.TranslationService;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;

@Component
public class PageTemplateImportService {

  private static final Scope            PAGE_TEMPLATE_IMPORT_SCOPE = Scope.APPLICATION.id("PAGE_TEMPLATE_IMPORT");

  private static final Context          PAGE_TEMPLATE_CONTEXT      = Context.GLOBAL.id("PAGE_TEMPLATE");

  private static final Log              LOG                        = ExoLogger.getLogger(PageTemplateImportService.class);

  private static final Random           RANDOM                     = new Random();

  @Autowired
  private LayoutAclService              layoutAclService;

  @Autowired
  private TranslationService            translationService;

  @Autowired
  private AttachmentService             attachmentService;

  @Autowired
  private LocaleConfigService           localeConfigService;

  @Autowired
  private PageTemplateService           pageTemplateService;

  @Autowired
  private SettingService                settingService;

  @Autowired
  private ConfigurationManager          configurationManager;

  @Autowired
  private PageTemplateAttachmentPlugin  pageTemplateAttachmentPlugin;

  @Autowired
  private PageTemplateTranslationPlugin pageTemplateTranslationPlugin;

  @Value("${meeds.pages.import.override:false}")
  private boolean                       forceReimportTemplates;

  @PostConstruct
  @ContainerTransactional
  public void init() {
    translationService.addPlugin(pageTemplateTranslationPlugin);
    attachmentService.addPlugin(pageTemplateAttachmentPlugin);

    CompletableFuture.runAsync(this::importPageTemplates);
  }

  @ContainerTransactional
  public void importPageTemplates() {
    LOG.info("Importing Page Templates");
    try {
      Enumeration<URL> templateFiles = PortalContainer.getInstance()
                                                      .getPortalClassLoader()
                                                      .getResources("page-templates.json");
      Collections.list(templateFiles)
                 .stream()
                 .map(this::parseDescriptors)
                 .flatMap(List::stream)
                 .sorted((d1, d2) -> {
                   if (d1.getOrder() == d2.getOrder()) {
                     return d1.getId().compareTo(d2.getId());
                   } else {
                     return d1.getOrder() - d2.getOrder();
                   }
                 })
                 .forEach(this::importDescriptor);
    } catch (Exception e) {
      LOG.warn("An error occurred while importing page templates", e);
    }
  }

  private List<PageTemplateDescriptor> parseDescriptors(URL url) {
    try (InputStream inputStream = url.openStream()) {
      String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      PageTemplateDescriptorList list = JsonUtils.fromJsonString(content, PageTemplateDescriptorList.class);
      return list.getDescriptors();
    } catch (IOException e) {
      LOG.warn("An unkown error happened while parsing page templates from url {}", url, e);
      return Collections.emptyList();
    }
  }

  private void importDescriptor(PageTemplateDescriptor descriptor) {
    String descriptorId = descriptor.getId();
    long oldTemplateId = getAlreadyImportedTemplateId(descriptorId);
    if (forceReimportTemplates || oldTemplateId == 0) {
      importPageTemplate(descriptor, oldTemplateId);
    } else {
      LOG.info("Ignore re-importing Page Template {}", descriptorId);
    }
  }

  private void importPageTemplate(PageTemplateDescriptor d, long oldTemplateId) {
    ConversationState currentConversationState = ConversationState.getCurrent();
    ConversationState.setCurrent(layoutAclService.getSuperUserConversationState());
    try {
      LOG.info("Importing Page Template {}", d.getId());
      PageTemplate pageTemplate = createPageTemplate(d, oldTemplateId);
      if (oldTemplateId == 0 || pageTemplate.getId() != oldTemplateId) {
        LOG.info("Importing Page Template {} title translations", d.getId());
        saveTemplateNames(d, pageTemplate);
        LOG.info("Importing Page Template {} description translations", d.getId());
        saveTemplateDescriptions(d, pageTemplate);
        LOG.info("Importing Page Template {} illustration", d.getId());
        saveTemplateIllustration(pageTemplate.getId(), d.getIllustrationPath());
        // Mark as imported
        settingService.set(PAGE_TEMPLATE_CONTEXT,
                           PAGE_TEMPLATE_IMPORT_SCOPE,
                           d.getId(),
                           SettingValue.create(String.valueOf(pageTemplate.getId())));
      }
      LOG.info("Importing Page Template {} finished successfully", d.getId());
    } catch (Exception e) {
      LOG.warn("An error occurred while importing page template {}", d.getId(), e);
    } finally {
      ConversationState.setCurrent(currentConversationState);
    }
  }

  private void saveTemplateNames(PageTemplateDescriptor d, PageTemplate pageTemplate) {
    d.getNames()
     .forEach((k, v) -> saveTranslationLabel(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                             pageTemplate.getId(),
                                             PageTemplateTranslationPlugin.TITLE_FIELD_NAME,
                                             Locale.forLanguageTag(k),
                                             v));
    String defaultName = d.getNames().get("en");
    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(config -> !StringUtils.equals(config.getLocale().toLanguageTag(), "en"))
                       .forEach(config -> saveTranslationLabel(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                                               pageTemplate.getId(),
                                                               PageTemplateTranslationPlugin.TITLE_FIELD_NAME,
                                                               config.getLocale(),
                                                               defaultName));
  }

  private void saveTemplateDescriptions(PageTemplateDescriptor d, PageTemplate pageTemplate) {
    d.getDescriptions()
     .forEach((k, v) -> saveTranslationLabel(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                             pageTemplate.getId(),
                                             PageTemplateTranslationPlugin.DESCRIPTION_FIELD_NAME,
                                             Locale.forLanguageTag(k),
                                             v));
    String defaultDescription = d.getDescriptions().get("en");
    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(config -> !StringUtils.equals(config.getLocale().toLanguageTag(), "en"))
                       .forEach(config -> saveTranslationLabel(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                                               pageTemplate.getId(),
                                                               PageTemplateTranslationPlugin.DESCRIPTION_FIELD_NAME,
                                                               config.getLocale(),
                                                               defaultDescription));
  }

  @SneakyThrows
  private PageTemplate createPageTemplate(PageTemplateDescriptor d, long oldTemplateId) {
    PageTemplate pageTemplate = null;
    if (oldTemplateId > 0) {
      pageTemplate = pageTemplateService.getPageTemplate(oldTemplateId);
    }
    boolean isNew = false;
    if (pageTemplate == null) {
      pageTemplate = new PageTemplate();
      isNew = true;
    }
    try (InputStream is = configurationManager.getInputStream(d.getLayoutPath())) {
      String xml = IOUtil.getStreamContentAsString(is);
      Container layout = fromXML(xml);
      pageTemplate.setContent(JsonUtils.toJsonString(new LayoutModel(layout)));
    }
    if (isNew) {
      return pageTemplateService.createPageTemplate(pageTemplate);
    } else {
      return pageTemplateService.updatePageTemplate(pageTemplate);
    }
  }

  @SneakyThrows
  private void saveTranslationLabel(String objectType, long id, String fieldName, Locale locale, String label) {
    translationService.saveTranslationLabel(objectType,
                                            id,
                                            fieldName,
                                            locale,
                                            label);
  }

  private void saveTemplateIllustration(long pageTemplateId, String imagePath) {
    try {
      URL resource = configurationManager.getResource(imagePath);
      String uploadId = "PageTemplateIllustration" + RANDOM.nextLong();
      UploadResource uploadResource = new UploadResource(uploadId);
      uploadResource.setFileName(new File(resource.getPath()).getName());
      uploadResource.setMimeType("image/png");
      uploadResource.setStatus(UploadResource.UPLOADED_STATUS);
      uploadResource.setStoreLocation(resource.getPath());
      UploadedAttachmentDetail uploadedAttachmentDetail = new UploadedAttachmentDetail(uploadResource);
      attachmentService.saveAttachment(uploadedAttachmentDetail,
                                       PageTemplateAttachmentPlugin.OBJECT_TYPE,
                                       String.valueOf(pageTemplateId),
                                       null,
                                       RestUtils.getCurrentUserIdentityId());
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Error while saving Image '%s' as attachment for template '%s'",
                                                    imagePath,
                                                    pageTemplateId),
                                      e);
    }
  }

  @SneakyThrows
  private Container fromXML(String xml) {
    UnmarshalledObject<Container> obj = ModelUnmarshaller.unmarshall(Container.class, xml.getBytes(StandardCharsets.UTF_8));
    return obj.getObject();
  }

  private long getAlreadyImportedTemplateId(String descriptorId) {
    try {
      SettingValue<?> settingValue = settingService.get(PAGE_TEMPLATE_CONTEXT, PAGE_TEMPLATE_IMPORT_SCOPE, descriptorId);
      return settingValue == null || settingValue.getValue() == null ? 0l : Long.parseLong(settingValue.getValue().toString());
    } catch (NumberFormatException e) {
      return 0l;
    }
  }

}
