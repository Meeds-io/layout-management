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
package io.meeds.layout.service.injection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.attachment.AttachmentService;
import org.exoplatform.social.attachment.model.UploadedAttachmentDetail;
import org.exoplatform.upload.UploadResource;

import io.meeds.common.ContainerTransactional;
import io.meeds.layout.model.PortletDescriptor;
import io.meeds.layout.model.PortletInstance;
import io.meeds.layout.model.PortletInstanceCategory;
import io.meeds.layout.model.PortletInstanceCategoryDescriptor;
import io.meeds.layout.model.PortletInstanceCategoryDescriptorList;
import io.meeds.layout.model.PortletInstanceDescriptor;
import io.meeds.layout.model.PortletInstanceDescriptorList;
import io.meeds.layout.plugin.attachment.PortletInstanceAttachmentPlugin;
import io.meeds.layout.plugin.translation.PortletInstanceCategoryTranslationPlugin;
import io.meeds.layout.plugin.translation.PortletInstanceTranslationPlugin;
import io.meeds.layout.service.LayoutAclService;
import io.meeds.layout.service.PortletInstanceService;
import io.meeds.layout.service.PortletService;
import io.meeds.layout.util.JsonUtils;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class PortletInstanceImportService {

  private static final String            PORTLET_INSTANCE_IMPORT                = "PORTLET_INSTANCE_IMPORT";

  private static final Scope             PORTLET_INSTANCE_IMPORT_SCOPE          =
                                                                       Scope.APPLICATION.id(PORTLET_INSTANCE_IMPORT);

  private static final Scope             PORTLET_INSTANCE_CATEGORY_IMPORT_SCOPE =
                                                                                Scope.APPLICATION.id("PORTLET_INSTANCE_CATEGORY_IMPORT");

  private static final Context           PORTLET_INSTANCE_CONTEXT               = Context.GLOBAL.id("PORTLET_INSTANCE");

  private static final String            PORTLET_INSTANCE_VERSION               = "version";

  private static final long              PORTLET_INSTANCE_IMPORT_VERSION        = 1;

  private static final Log               LOG                                    =
                                             ExoLogger.getLogger(PortletInstanceImportService.class);

  private static final Random            RANDOM                                 = new Random();

  @Autowired
  private LayoutAclService               layoutAclService;

  @Autowired
  private LayoutTranslationImportService layoutTranslationService;

  @Autowired
  private AttachmentService              attachmentService;

  @Autowired
  private PortletInstanceService         portletInstanceService;

  @Autowired
  private PortletService                 portletService;

  @Autowired
  private SettingService                 settingService;

  @Autowired
  private ConfigurationManager           configurationManager;

  @Value("${meeds.portlets.import.override:false}")
  private boolean                        forceReimport;

  @PostConstruct
  public void init() {
    CompletableFuture.runAsync(this::importPortletInstances);
  }

  @ContainerTransactional
  public void importPortletInstances() {
    LOG.info("Importing Portlet instances");
    if (!forceReimport
        && getSettingValue(PORTLET_INSTANCE_VERSION) != PORTLET_INSTANCE_IMPORT_VERSION) {
      forceReimport = true;
    }

    ConversationState.setCurrent(layoutAclService.getSuperUserConversationState());
    try {
      Collections.list(getClassLoader().getResources("portlet-instance-categories.json"))
                 .stream()
                 .map(this::parseCategoryDescriptors)
                 .flatMap(List::stream)
                 .forEach(this::importCategoryDescriptor);
      Collections.list(getClass().getClassLoader().getResources("portlet-instances.json"))
                 .stream()
                 .map(this::parseDescriptors)
                 .flatMap(List::stream)
                 .forEach(this::importDescriptor);
      LOG.info("Importing Portlet instances finished successfully.");

      LOG.info("Processing Post Portlet instances import");
      layoutTranslationService.postImport(PortletInstanceCategoryTranslationPlugin.OBJECT_TYPE);
      layoutTranslationService.postImport(PortletInstanceTranslationPlugin.OBJECT_TYPE);
      LOG.info("Processing Post Portlet instances import finished");

      setSettingValue(PORTLET_INSTANCE_VERSION, PORTLET_INSTANCE_IMPORT_VERSION);
    } catch (Exception e) {
      LOG.warn("An error occurred while importing portlet instances", e);
    } finally {
      ConversationState.setCurrent(null);
    }
  }

  protected List<PortletInstanceDescriptor> parseDescriptors(URL url) {
    try (InputStream inputStream = url.openStream()) {
      String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      PortletInstanceDescriptorList list = JsonUtils.fromJsonString(content, PortletInstanceDescriptorList.class);
      return list.getDescriptors();
    } catch (IOException e) {
      LOG.warn("An unkown error happened while parsing portlet instances from url {}", url, e);
      return Collections.emptyList();
    }
  }

  protected List<PortletInstanceCategoryDescriptor> parseCategoryDescriptors(URL url) {
    try (InputStream inputStream = url.openStream()) {
      String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      PortletInstanceCategoryDescriptorList list = JsonUtils.fromJsonString(content, PortletInstanceCategoryDescriptorList.class);
      return list.getDescriptors();
    } catch (IOException e) {
      LOG.warn("An unkown error happened while parsing portlet instances from url {}", url, e);
      return Collections.emptyList();
    }
  }

  protected void importCategoryDescriptor(PortletInstanceCategoryDescriptor descriptor) {
    String descriptorId = descriptor.getNameId();
    long existingId = getCategorySettingValue(descriptorId);
    if (forceReimport || existingId == 0) {
      importPortletInstanceCategory(descriptor, existingId);
    } else {
      LOG.debug("Ignore re-importing Portlet instance category {}", descriptorId);
    }
  }

  protected void importDescriptor(PortletInstanceDescriptor descriptor) {
    String descriptorId = descriptor.getNameId();
    long existingId = getSettingValue(descriptorId);
    if (forceReimport || existingId == 0) {
      importPortletInstance(descriptor, existingId);
    } else {
      LOG.debug("Ignore re-importing Portlet instance {}", descriptorId);
    }
  }

  protected void importPortletInstanceCategory(PortletInstanceCategoryDescriptor d, long oldId) {
    String descriptorId = d.getNameId();
    LOG.debug("Importing Portlet category instance {}", descriptorId);
    try {
      PortletInstanceCategory category = savePortletInstanceCategory(d, oldId);
      if (category != null && (forceReimport || oldId == 0 || category.getId() != oldId)) {
        LOG.debug("Importing Portlet instance category {} title translations", descriptorId);
        saveCategoryNames(d, category);
        // Mark as imported
        setCategorySettingValue(descriptorId, category.getId());
      }
      LOG.debug("Importing Portlet instance category {} finished successfully", descriptorId);
    } catch (Exception e) {
      LOG.warn("An error occurred while importing portlet instance category {}", descriptorId, e);
    }
  }

  protected void importPortletInstance(PortletInstanceDescriptor d, long oldId) {
    String descriptorId = d.getNameId();
    LOG.debug("Importing Portlet instance {}", descriptorId);
    try {
      PortletInstance portletInstance = savePortletInstance(d, oldId);
      if (portletInstance == null) {
        return;
      }
      if (forceReimport || oldId == 0 || portletInstance.getId() != oldId) {
        LOG.debug("Importing Portlet instance {} title translations", descriptorId);
        saveNames(d, portletInstance);
        LOG.debug("Importing Portlet instance {} description translations", descriptorId);
        saveDescriptions(d, portletInstance);
        if (StringUtils.isNotBlank(d.getIllustrationPath())) {
          LOG.debug("Importing Portlet instance {} illustration", descriptorId);
          saveIllustration(portletInstance.getId(), d.getIllustrationPath());
        }
        // Mark as imported
        setSettingValue(descriptorId, portletInstance.getId());
      }
      LOG.debug("Importing Portlet instance {} finished successfully", descriptorId);
    } catch (Exception e) {
      LOG.warn("An error occurred while importing portlet instance {}", descriptorId, e);
    }
  }

  protected void saveNames(PortletInstanceDescriptor d, PortletInstance portletInstance) {
    layoutTranslationService.saveTranslationLabels(PortletInstanceTranslationPlugin.OBJECT_TYPE,
                                                   portletInstance.getId(),
                                                   PortletInstanceTranslationPlugin.TITLE_FIELD_NAME,
                                                   d.getNames());
  }

  protected void saveDescriptions(PortletInstanceDescriptor d, PortletInstance portletInstance) {
    layoutTranslationService.saveTranslationLabels(PortletInstanceTranslationPlugin.OBJECT_TYPE,
                                                   portletInstance.getId(),
                                                   PortletInstanceTranslationPlugin.DESCRIPTION_FIELD_NAME,
                                                   d.getDescriptions());
  }

  protected void saveCategoryNames(PortletInstanceCategoryDescriptor d, PortletInstanceCategory category) {
    layoutTranslationService.saveTranslationLabels(PortletInstanceCategoryTranslationPlugin.OBJECT_TYPE,
                                                   category.getId(),
                                                   PortletInstanceCategoryTranslationPlugin.TITLE_FIELD_NAME,
                                                   d.getNames());
  }

  @SneakyThrows
  protected PortletInstanceCategory savePortletInstanceCategory(PortletInstanceCategoryDescriptor d, long oldId) {
    PortletInstanceCategory category = null;
    if (oldId > 0) {
      category = portletInstanceService.getPortletInstanceCategory(oldId);
    }
    boolean isNew = category == null;
    if (isNew) {
      category = new PortletInstanceCategory();
    }
    category.setIcon(d.getIcon());
    category.setPermissions(d.getPermissions());
    category.setSystem(d.isSystem());
    if (isNew) {
      return portletInstanceService.createPortletInstanceCategory(category);
    } else {
      return portletInstanceService.updatePortletInstanceCategory(category);
    }
  }

  @SneakyThrows
  protected PortletInstance savePortletInstance(PortletInstanceDescriptor d, long oldId) {
    PortletDescriptor portlet = portletService.getPortlet(d.getPortletName());
    if (portlet == null) {
      LOG.debug("Saving Portlet instance descriptor {} aborted since portlet {} doesn't exist.",
                d.getNameId(),
                d.getPortletName());
      return null;
    }
    PortletInstance portletInstance = null;
    if (oldId > 0) {
      portletInstance = portletInstanceService.getPortletInstance(oldId);
    }
    boolean isNew = portletInstance == null;
    if (isNew) {
      portletInstance = new PortletInstance();
    }
    portletInstance.setContentId(portlet.getContentId());
    portletInstance.setCategoryId(getCategorySettingValue(d.getCategoryNameId()));
    portletInstance.setPermissions(d.getPermissions());
    portletInstance.setPreferences(d.getPreferences());
    portletInstance.setSystem(d.isSystem());
    if (isNew) {
      return portletInstanceService.createPortletInstance(portletInstance);
    } else {
      return portletInstanceService.updatePortletInstance(portletInstance);
    }
  }

  protected void saveIllustration(long portletInstanceId, String imagePath) {
    try {
      URL resource = configurationManager.getResource(imagePath);
      String uploadId = "PortletInstanceIllustration" + RANDOM.nextLong();
      UploadResource uploadResource = new UploadResource(uploadId);
      uploadResource.setFileName(new File(resource.getPath()).getName());
      uploadResource.setMimeType("image/png");
      uploadResource.setStatus(UploadResource.UPLOADED_STATUS);
      uploadResource.setStoreLocation(resource.getPath());
      UploadedAttachmentDetail uploadedAttachmentDetail = new UploadedAttachmentDetail(uploadResource);
      attachmentService.saveAttachment(uploadedAttachmentDetail,
                                       PortletInstanceAttachmentPlugin.OBJECT_TYPE,
                                       String.valueOf(portletInstanceId),
                                       null,
                                       layoutAclService.getSuperUserIdentityId());
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Error while saving Image '%s' as attachment for portlet instance '%s'",
                                                    imagePath,
                                                    portletInstanceId),
                                      e);
    }
  }

  protected void setCategorySettingValue(String name, long value) {
    settingService.set(PORTLET_INSTANCE_CONTEXT,
                       PORTLET_INSTANCE_CATEGORY_IMPORT_SCOPE,
                       name,
                       SettingValue.create(String.valueOf(value)));
  }

  protected long getCategorySettingValue(String name) {
    try {
      SettingValue<?> settingValue = settingService.get(PORTLET_INSTANCE_CONTEXT, PORTLET_INSTANCE_CATEGORY_IMPORT_SCOPE, name);
      return settingValue == null || settingValue.getValue() == null ? 0l : Long.parseLong(settingValue.getValue().toString());
    } catch (NumberFormatException e) {
      return 0l;
    }
  }

  protected void setSettingValue(String name, long value) {
    settingService.set(PORTLET_INSTANCE_CONTEXT,
                       PORTLET_INSTANCE_IMPORT_SCOPE,
                       name,
                       SettingValue.create(String.valueOf(value)));
  }

  protected long getSettingValue(String name) {
    try {
      SettingValue<?> settingValue = settingService.get(PORTLET_INSTANCE_CONTEXT, PORTLET_INSTANCE_IMPORT_SCOPE, name);
      return settingValue == null || settingValue.getValue() == null ? 0l : Long.parseLong(settingValue.getValue().toString());
    } catch (NumberFormatException e) {
      return 0l;
    }
  }

  private ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

}
