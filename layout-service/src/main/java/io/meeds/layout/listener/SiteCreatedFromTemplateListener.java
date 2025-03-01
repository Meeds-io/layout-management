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
package io.meeds.layout.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.ListenerBase;
import org.exoplatform.services.listener.ListenerService;

import io.meeds.layout.service.PageLayoutService;

import jakarta.annotation.PostConstruct;

@Component
public class SiteCreatedFromTemplateListener implements ListenerBase<SiteKey, SiteKey> {

  @Autowired
  private ListenerService   listenerService;

  @Autowired
  private PageLayoutService pageLayoutService;

  @PostConstruct
  public void init() {
    listenerService.addListener(UserPortalConfigService.SITE_TEMPLATE_INSTANTIATED, this);
  }

  @Override
  public void onEvent(Event<SiteKey, SiteKey> event) throws Exception {
    pageLayoutService.impersonateSite(event.getData());
  }

}
