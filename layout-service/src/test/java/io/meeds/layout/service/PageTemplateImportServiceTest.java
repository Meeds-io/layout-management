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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.plugin.PageTemplateAttachmentPlugin;
import io.meeds.layout.plugin.PageTemplateTranslationPlugin;
import io.meeds.social.translation.service.TranslationService;

@SpringBootTest(classes = { PageTemplateImportService.class })
@ExtendWith(MockitoExtension.class)
public class PageTemplateImportServiceTest {

  @MockBean
  private LayoutAclService              layoutAclService;

  @MockBean
  private TranslationService            translationService;

  @MockBean
  private AttachmentService             attachmentService;

  @MockBean
  private LocaleConfigService           localeConfigService;

  @MockBean
  private PageTemplateService           pageTemplateService;

  @MockBean
  private SettingService                settingService;

  @MockBean
  private ConfigurationManager          configurationManager;

  @MockBean
  private PageTemplateAttachmentPlugin  pageTemplateAttachmentPlugin;

  @MockBean
  private PageTemplateTranslationPlugin pageTemplateTranslationPlugin;

  @Autowired
  private PageTemplateImportService     pageTemplateImportService;

  @Test
  public void init() {
    assertDoesNotThrow(() -> pageTemplateImportService.init(), "Shouldn't stop the container initialization if page templates fails");
    assertDoesNotThrow(() -> pageTemplateImportService.importPageTemplates(), "Shouldn't stop the container initialization if page templates fails");
  }
}
