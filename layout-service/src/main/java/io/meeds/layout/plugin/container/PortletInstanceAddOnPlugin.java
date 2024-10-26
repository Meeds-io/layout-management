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
package io.meeds.layout.plugin.container;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.addons.AddOnPlugin;
import org.exoplatform.commons.addons.AddOnService;
import org.exoplatform.portal.config.model.Application;

import jakarta.annotation.PostConstruct;

@Component
public class PortletInstanceAddOnPlugin extends AddOnPlugin {

  private static final String               PORTLET_EDITOR_DYNAMIC_CONTAINER = "portlet-viewer";

  @Autowired
  private PortletInstanceApplicationAdapter portletInstanceApplicationAdapter;

  @Autowired
  private AddOnService                      addonService;

  @PostConstruct
  public void init() {
    addonService.addPlugin(this);
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public String getContainerName() {
    return PORTLET_EDITOR_DYNAMIC_CONTAINER;
  }

  @Override
  public List<Application> getApplications() {
    return Collections.singletonList(portletInstanceApplicationAdapter);
  }

}
