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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.service.LayoutAclService;
import io.meeds.layout.service.PageTemplateService;

@SpringBootTest(classes = { PageTemplateImportService.class })
@ExtendWith(MockitoExtension.class)
public class PageTemplateImportServiceTest {

  @MockBean
  private LayoutAclService               layoutAclService;

  @MockBean
  private LayoutTranslationImportService layoutTranslationImportService;

  @MockBean
  private AttachmentService              attachmentService;

  @MockBean
  private PageTemplateService            pageTemplateService;

  @MockBean
  private SettingService                 settingService;

  @MockBean
  private ResourceBundleService          resourceBundleService;

  @MockBean
  private ConfigurationManager           configurationManager;

  @Autowired
  private PageTemplateImportService      pageTemplateImportService;

  @Test
  public void init() {
    assertDoesNotThrow(() -> pageTemplateImportService.init(),
                       "Shouldn't stop the container initialization if page templates fails");
    assertDoesNotThrow(() -> pageTemplateImportService.importPageTemplates(),
                       "Shouldn't stop the container initialization if page templates fails");
  }

}
