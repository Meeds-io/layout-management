/*
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
package io.meeds.layout.plugin;

import io.meeds.layout.model.PortletInstancePreference;
import io.meeds.layout.service.PortletInstanceService;
import io.meeds.social.databind.plugin.DatabindPreferencePlugin;
import io.meeds.social.databind.service.DatabindService;
import io.meeds.social.util.JsonUtils;
import jakarta.annotation.PostConstruct;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PortletInstanceDatabindPreferencePlugin implements DatabindPreferencePlugin {

  @Autowired
  private PortletInstanceService portletInstanceService;

  @Autowired
  private DatabindService        databindService;

  @Override
  public String getDataType() {
    return "PortletInstance";
  }

  @PostConstruct
  public void init() {
    databindService.addDataPreferencePlugin(this);
  }

  @Override
  public Map<String, String> generatePreferences(List<String> objectIds, String username) throws ObjectNotFoundException,
                                                                                          IllegalAccessException {
    Map<String, String> map = new HashMap<>();
    for (String objectId : objectIds) {
      List<PortletInstancePreference> preferences = portletInstanceService.getPortletInstancePreferences(Long.parseLong(objectId),
                                                                                                         username);
      if (map.put(objectId + ".json", JsonUtils.toJsonString(preferences)) != null) {
        throw new IllegalStateException("Duplicate key");
      }
    }
    return map;
  }
}
