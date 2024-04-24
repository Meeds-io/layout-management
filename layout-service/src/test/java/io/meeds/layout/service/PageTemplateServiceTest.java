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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.model.PageTemplate;
import io.meeds.layout.plugin.PageTemplateAttachmentPlugin;
import io.meeds.layout.plugin.PageTemplateTranslationPlugin;
import io.meeds.layout.storage.PageTemplateStorage;
import io.meeds.social.translation.model.TranslationField;
import io.meeds.social.translation.service.TranslationService;

@SpringBootTest(classes = {
  PageTemplateService.class,
})
@ExtendWith(MockitoExtension.class)
public class PageTemplateServiceTest {

  private static final String LAYOUT_CONTENT = "...layout...";

  @MockBean
  private LayoutAclService    layoutAclService;

  @MockBean
  private TranslationService  translationService;

  @MockBean
  private AttachmentService   attachmentService;

  @MockBean
  private LocaleConfigService localeConfigService;

  @MockBean
  private PageTemplateStorage pageTemplateStorage;

  @Mock
  private PageTemplate        pageTemplate;

  @Mock
  private LocaleConfig        defaultLocaleConfig;

  @Mock
  private LocaleConfig        localeConfig;

  @Autowired
  private PageTemplateService pageTemplateService;

  private String              testuser       = "testuser";

  @Test
  public void getPageTemplates() {
    when(pageTemplate.getId()).thenReturn(2l);
    when(pageTemplate.getContent()).thenReturn(LAYOUT_CONTENT);
    
    when(pageTemplateStorage.getPageTemplates()).thenReturn(Collections.singletonList(pageTemplate));
    List<PageTemplate> pageTemplates = pageTemplateService.getPageTemplates();
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());
    assertEquals(pageTemplate.getId(), pageTemplates.get(0).getId());
    assertEquals(pageTemplate.getContent(), pageTemplates.get(0).getContent());
    assertNull(pageTemplates.get(0).getName());
    assertNull(pageTemplates.get(0).getDescription());
    assertEquals(0l, pageTemplates.get(0).getIllustrationId());
  }

  @Test
  public void getPageTemplatesWithExpand() throws ObjectNotFoundException {
    PageTemplate template = new PageTemplate(2l, LAYOUT_CONTENT);
    when(localeConfigService.getDefaultLocaleConfig()).thenReturn(defaultLocaleConfig);
    when(defaultLocaleConfig.getLocale()).thenReturn(Locale.ENGLISH);

    when(pageTemplateStorage.getPageTemplates()).thenReturn(Collections.singletonList(template));

    List<PageTemplate> pageTemplates = pageTemplateService.getPageTemplates(true);
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());
    assertEquals(template.getId(), pageTemplates.get(0).getId());
    assertEquals(template.getContent(), pageTemplates.get(0).getContent());
    assertNull(pageTemplates.get(0).getName());
    assertNull(pageTemplates.get(0).getDescription());
    assertEquals(0l, pageTemplates.get(0).getIllustrationId());

    when(translationService.getTranslationField(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                                template.getId(),
                                                PageTemplateTranslationPlugin.TITLE_FIELD_NAME)).thenThrow(ObjectNotFoundException.class);
    pageTemplates = pageTemplateService.getPageTemplates(true);
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());

    reset(translationService);

    TranslationField titleTranslationField = mock(TranslationField.class);
    when(translationService.getTranslationField(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                                template.getId(),
                                                PageTemplateTranslationPlugin.TITLE_FIELD_NAME)).thenReturn(titleTranslationField);
    pageTemplates = pageTemplateService.getPageTemplates(true);
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());
    assertEquals(template.getId(), pageTemplates.get(0).getId());
    assertEquals(template.getContent(), pageTemplates.get(0).getContent());
    assertNull(pageTemplates.get(0).getName());
    assertNull(pageTemplates.get(0).getDescription());
    assertEquals(0l, pageTemplates.get(0).getIllustrationId());

    String frTitle = "testTitle";
    when(titleTranslationField.getLabels()).thenReturn(Collections.singletonMap(Locale.FRENCH, frTitle));

    pageTemplates = pageTemplateService.getPageTemplates(true);
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());
    assertEquals(frTitle, pageTemplates.get(0).getName());

    TranslationField descriptionTranslationField = mock(TranslationField.class);
    when(translationService.getTranslationField(PageTemplateTranslationPlugin.OBJECT_TYPE,
                                                template.getId(),
                                                PageTemplateTranslationPlugin.DESCRIPTION_FIELD_NAME)).thenReturn(descriptionTranslationField);
    String enDesc = "testDescription";
    when(descriptionTranslationField.getLabels()).thenReturn(Collections.singletonMap(Locale.ENGLISH, enDesc));

    pageTemplates = pageTemplateService.getPageTemplates(true);
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());
    assertEquals(enDesc, pageTemplates.get(0).getDescription());

    when(attachmentService.getAttachmentFileIds(PageTemplateAttachmentPlugin.OBJECT_TYPE, "2")).thenReturn(Collections.singletonList("32"));
    pageTemplates = pageTemplateService.getPageTemplates(Locale.GERMAN, true);
    assertNotNull(pageTemplates);
    assertEquals(1, pageTemplates.size());
    assertEquals(32l, pageTemplates.get(0).getIllustrationId());
  }

  @Test
  public void getPageTemplate() {
    when(pageTemplateStorage.getPageTemplate(2l)).thenReturn(pageTemplate);
    PageTemplate retrievedPageTemplate = pageTemplateService.getPageTemplate(3l);
    assertNull(retrievedPageTemplate);

    retrievedPageTemplate = pageTemplateService.getPageTemplate(2l);
    assertNotNull(retrievedPageTemplate);
    assertEquals(pageTemplate, retrievedPageTemplate);
  }

  @Test
  public void createPageTemplate() throws IllegalAccessException {
    assertThrows(IllegalAccessException.class, () -> pageTemplateService.createPageTemplate(pageTemplate, testuser));

    when(layoutAclService.isAdministrator(testuser)).thenReturn(true);
    pageTemplateService.createPageTemplate(pageTemplate, testuser);
    verify(pageTemplateStorage, times(1)).createPageTemplate(pageTemplate);
  }

  @Test
  public void deletePageTemplate() throws ObjectNotFoundException, IllegalAccessException {
    assertThrows(IllegalAccessException.class, () -> pageTemplateService.deletePageTemplate(2l, testuser));
    
    when(layoutAclService.isAdministrator(testuser)).thenReturn(true);
    pageTemplateService.deletePageTemplate(2l, testuser);
    verify(attachmentService, times(1)).deleteAttachments(PageTemplateAttachmentPlugin.OBJECT_TYPE, "2");
    verify(translationService, times(1)).deleteTranslationLabels(PageTemplateTranslationPlugin.OBJECT_TYPE, 2l);
    verify(pageTemplateStorage, times(1)).deletePageTemplate(2l);
  }

  @Test
  public void deletePageTemplateWhenException() throws ObjectNotFoundException, IllegalAccessException {
    assertThrows(IllegalAccessException.class, () -> pageTemplateService.deletePageTemplate(2l, testuser));

    when(layoutAclService.isAdministrator(testuser)).thenReturn(true);
    doThrow(RuntimeException.class).when(attachmentService).deleteAttachments(anyString(), any());
    doThrow(ObjectNotFoundException.class).when(translationService).deleteTranslationLabels(anyString(), anyLong());
    pageTemplateService.deletePageTemplate(2l, testuser);
    verify(pageTemplateStorage, times(1)).deletePageTemplate(2l);
  }

  @Test
  public void updatePageTemplate() throws ObjectNotFoundException, IllegalAccessException {
    assertThrows(IllegalAccessException.class, () -> pageTemplateService.updatePageTemplate(pageTemplate, testuser));

    when(layoutAclService.isAdministrator(testuser)).thenReturn(true);
    pageTemplateService.updatePageTemplate(pageTemplate, testuser);
    verify(pageTemplateStorage, times(1)).updatePageTemplate(pageTemplate);
  }

}
