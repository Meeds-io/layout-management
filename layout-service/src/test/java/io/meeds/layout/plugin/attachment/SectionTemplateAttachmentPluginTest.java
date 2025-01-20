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
package io.meeds.layout.plugin.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.exoplatform.services.security.Identity;
import org.exoplatform.social.attachment.AttachmentService;

import io.meeds.layout.service.LayoutAclService;
import io.meeds.layout.service.SectionTemplateService;

import lombok.SneakyThrows;

@SpringBootTest(classes = {
  SectionTemplateAttachmentPlugin.class,
})
@ExtendWith(MockitoExtension.class)
public class SectionTemplateAttachmentPluginTest {

  @Mock
  private Identity                        userIdentity;

  @MockBean
  private AttachmentService               attachmentService;

  @MockBean
  private LayoutAclService                layoutAclService;

  @MockBean
  private SectionTemplateService          sectionTemplateService;

  @Autowired
  private SectionTemplateAttachmentPlugin attachmentPlugin;

  @Test
  void getObjectType() {
    assertEquals("sectionTemplate", attachmentPlugin.getObjectType());
    assertEquals(SectionTemplateAttachmentPlugin.OBJECT_TYPE, attachmentPlugin.getObjectType());
  }

  @Test
  @SneakyThrows
  void hasEditPermission() {
    assertFalse(attachmentPlugin.hasEditPermission(null, null));
    assertFalse(attachmentPlugin.hasEditPermission(userIdentity, null));
    when(userIdentity.getUserId()).thenReturn("test");
    when(layoutAclService.isAdministrator(userIdentity.getUserId())).thenReturn(true);
    assertTrue(attachmentPlugin.hasEditPermission(userIdentity, null));
  }

  @Test
  @SneakyThrows
  void hasAccessPermission() {
    assertTrue(attachmentPlugin.hasAccessPermission(userIdentity, "1"));
  }

  @Test
  @SneakyThrows
  void getAudienceId() {
    assertEquals(0l, attachmentPlugin.getAudienceId(null));
  }

  @Test
  @SneakyThrows
  void getSpaceId() {
    assertEquals(0l, attachmentPlugin.getSpaceId(""));
  }

}
