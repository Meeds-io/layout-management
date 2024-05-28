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
package io.meeds.layout.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.model.PageTemplate;
import io.meeds.layout.service.PageTemplateService;
import io.meeds.spring.web.security.PortalAuthenticationManager;
import io.meeds.spring.web.security.WebSecurityConfiguration;

import jakarta.servlet.Filter;
import lombok.SneakyThrows;

@SpringBootTest(classes = { PageTemplateRest.class, PortalAuthenticationManager.class, })
@ContextConfiguration(classes = { WebSecurityConfiguration.class })
@AutoConfigureWebMvc
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PageTemplateRestTest {

  private static final String REST_PATH     = "/page/templates"; // NOSONAR

  private static final String SIMPLE_USER   = "simple";

  private static final String TEST_PASSWORD = "testPassword";

  static final ObjectMapper   OBJECT_MAPPER;

  static {
    // Workaround when Jackson is defined in shared library with different
    // version and without artifact jackson-datatype-jsr310
    OBJECT_MAPPER = JsonMapper.builder()
                              .configure(JsonReadFeature.ALLOW_MISSING_VALUES, true)
                              .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                              .build();
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
  }

  @MockBean
  private PageTemplateService   pageTemplateService;

  @Autowired
  private SecurityFilterChain   filterChain;

  @Autowired
  private WebApplicationContext context;

  private MockMvc               mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .addFilters(filterChain.getFilters().toArray(new Filter[0]))
                             .build();
  }

  @Test
  void getPageTemplatesAnonymously() throws Exception {
    ResultActions response = mockMvc.perform(get(REST_PATH));
    response.andExpect(status().isForbidden());
    verifyNoInteractions(pageTemplateService);
  }

  @Test
  void getPageTemplatesWithUser() throws Exception {
    ResultActions response = mockMvc.perform(get(REST_PATH).with(testSimpleUser()));
    response.andExpect(status().isOk());
    verify(pageTemplateService).getPageTemplates(any(), anyBoolean());
  }

  @Test
  void getPageTemplateAnonymously() throws Exception {
    ResultActions response = mockMvc.perform(get(REST_PATH + "/1"));
    response.andExpect(status().isForbidden());
    verifyNoInteractions(pageTemplateService);
  }

  @Test
  void getPageTemplateWithUser() throws Exception {
    ResultActions response = mockMvc.perform(get(REST_PATH + "/1").with(testSimpleUser()));
    response.andExpect(status().isOk());
    verify(pageTemplateService).getPageTemplate(eq(1l), any(), eq(true));
  }

  @Test
  void createPageTemplateAnonymously() throws Exception {
    ResultActions response = mockMvc.perform(post(REST_PATH).content(asJsonString(new PageTemplate()))
                                                            .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isForbidden());
    verifyNoInteractions(pageTemplateService);
  }

  @Test
  void createPageTemplateWithUser() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    ResultActions response = mockMvc.perform(post(REST_PATH).with(testSimpleUser())
                                                            .content(asJsonString(pageTemplate))
                                                            .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isOk());
    verify(pageTemplateService).createPageTemplate(pageTemplate, SIMPLE_USER);
  }

  @Test
  void createPageTemplateWithUserForbidden() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    when(pageTemplateService.createPageTemplate(pageTemplate, SIMPLE_USER)).thenThrow(IllegalAccessException.class);

    ResultActions response = mockMvc.perform(post(REST_PATH).with(testSimpleUser())
                                                            .content(asJsonString(pageTemplate))
                                                            .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isForbidden());
  }

  @Test
  void updatePageTemplateAnonymously() throws Exception {
    ResultActions response = mockMvc.perform(put(REST_PATH + "/1").content(asJsonString(new PageTemplate()))
                                                                  .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isForbidden());
    verifyNoInteractions(pageTemplateService);
  }

  @Test
  void updatePageTemplateWithUser() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    ResultActions response = mockMvc.perform(put(REST_PATH + "/1").with(testSimpleUser())
                                                                  .content(asJsonString(pageTemplate))
                                                                  .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isOk());

    pageTemplate.setId(1l);
    verify(pageTemplateService).updatePageTemplate(pageTemplate, SIMPLE_USER);
  }

  @Test
  void updatePageTemplateWithUserForbidden() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    pageTemplate.setId(1l);
    when(pageTemplateService.updatePageTemplate(pageTemplate, SIMPLE_USER)).thenThrow(IllegalAccessException.class);

    ResultActions response = mockMvc.perform(put(REST_PATH + "/1").with(testSimpleUser())
                                                                  .content(asJsonString(pageTemplate))
                                                                  .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isForbidden());
  }

  @Test
  void updatePageTemplateWithUserNotFound() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    pageTemplate.setId(1l);
    when(pageTemplateService.updatePageTemplate(pageTemplate, SIMPLE_USER)).thenThrow(ObjectNotFoundException.class);

    ResultActions response = mockMvc.perform(put(REST_PATH + "/1").with(testSimpleUser())
                                                                  .content(asJsonString(pageTemplate))
                                                                  .contentType(MediaType.APPLICATION_JSON));
    response.andExpect(status().isNotFound());
  }

  @Test
  void deletePageTemplateAnonymously() throws Exception {
    ResultActions response = mockMvc.perform(delete(REST_PATH + "/1"));
    response.andExpect(status().isForbidden());
    verifyNoInteractions(pageTemplateService);
  }

  @Test
  void deletePageTemplateWithUser() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    ResultActions response = mockMvc.perform(delete(REST_PATH + "/1").with(testSimpleUser()));
    response.andExpect(status().isOk());

    pageTemplate.setId(1l);
    verify(pageTemplateService).deletePageTemplate(1l, SIMPLE_USER);
  }

  @Test
  void deletePageTemplateWithUserForbidden() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    pageTemplate.setId(1l);
    doThrow(IllegalAccessException.class).when(pageTemplateService).deletePageTemplate(1l, SIMPLE_USER);

    ResultActions response = mockMvc.perform(delete(REST_PATH + "/1").with(testSimpleUser()));
    response.andExpect(status().isForbidden());
  }

  @Test
  void deletePageTemplateWithUserNotFound() throws Exception {
    PageTemplate pageTemplate = new PageTemplate();
    pageTemplate.setId(1l);
    doThrow(ObjectNotFoundException.class).when(pageTemplateService).deletePageTemplate(1l, SIMPLE_USER);

    ResultActions response = mockMvc.perform(delete(REST_PATH + "/1").with(testSimpleUser()));
    response.andExpect(status().isNotFound());
  }

  private RequestPostProcessor testSimpleUser() {
    return user(SIMPLE_USER).password(TEST_PASSWORD)
                            .authorities(new SimpleGrantedAuthority("users"));
  }

  @SneakyThrows
  public static String asJsonString(final Object obj) {
    return OBJECT_MAPPER.writeValueAsString(obj);
  }

}
