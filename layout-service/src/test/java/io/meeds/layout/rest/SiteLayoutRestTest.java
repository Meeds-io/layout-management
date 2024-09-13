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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.model.NodeLabel;
import io.meeds.layout.service.SiteLayoutService;
import io.meeds.spring.web.security.PortalAuthenticationManager;
import io.meeds.spring.web.security.WebSecurityConfiguration;

import jakarta.servlet.Filter;

@SpringBootTest(classes = { SiteLayoutRest.class, PortalAuthenticationManager.class, })
@ContextConfiguration(classes = { WebSecurityConfiguration.class })
@AutoConfigureWebMvc
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class SiteLayoutRestTest {

  private static final String   REST_PATH = "/sites"; // NOSONAR

  @MockBean
  private SiteLayoutService     siteLayoutService;

  @Autowired
  private SecurityFilterChain   filterChain;

  @Autowired
  private WebApplicationContext context;

  @Mock
  private NodeLabel             nodeLabel;

  private MockMvc               mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
                             .addFilters(filterChain.getFilters().toArray(new Filter[0]))
                             .build();
  }

  @Test
  void getSiteLabels() throws Exception {
    doThrow(ObjectNotFoundException.class).when(siteLayoutService)
                                          .getSiteLabels(2l, null);

    ResultActions response = mockMvc.perform(get(REST_PATH + "/2/labels"));
    response.andExpect(status().isNotFound());

    doThrow(IllegalAccessException.class).when(siteLayoutService)
                                         .getSiteLabels(3l, null);
    response = mockMvc.perform(get(REST_PATH + "/3/labels"));
    response.andExpect(status().isForbidden());

    doReturn(nodeLabel).when(siteLayoutService)
                       .getSiteLabels(4l, null);
    when(nodeLabel.getDefaultLanguage()).thenReturn("en");

    response = mockMvc.perform(get(REST_PATH + "/4/labels"));
    response.andExpect(status().isOk())
            .andExpect(jsonPath("$.defaultLanguage").value("en"));
  }

  @Test
  void getSiteDescriptions() throws Exception {
    doThrow(ObjectNotFoundException.class).when(siteLayoutService)
                                          .getSiteDescriptions(2l, null);

    ResultActions response = mockMvc.perform(get(REST_PATH + "/2/descriptions"));
    response.andExpect(status().isNotFound());

    doThrow(IllegalAccessException.class).when(siteLayoutService)
                                         .getSiteDescriptions(3l, null);
    response = mockMvc.perform(get(REST_PATH + "/3/descriptions"));
    response.andExpect(status().isForbidden());

    doReturn(nodeLabel).when(siteLayoutService)
                       .getSiteDescriptions(4l, null);
    when(nodeLabel.getDefaultLanguage()).thenReturn("en");

    response = mockMvc.perform(get(REST_PATH + "/4/descriptions"));
    response.andExpect(status().isOk())
            .andExpect(jsonPath("$.defaultLanguage").value("en"));
  }

}
