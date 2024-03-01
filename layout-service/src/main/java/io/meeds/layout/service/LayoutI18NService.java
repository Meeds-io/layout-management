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

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.ResourceBundleService;

@Service
public class LayoutI18NService {

  @Autowired
  private ResourceBundleService resourceBundleService;

  @Autowired
  private LocaleConfigService   localeConfigService;

  public String getLabel(String label, Locale locale) {
    if (StringUtils.isBlank(label)) {
      return label;
    } else if (locale == null) {
      locale = localeConfigService.getDefaultLocaleConfig().getLocale();
    }
    try {
      ResourceBundle resourceBundle = resourceBundleService.getResourceBundle(resourceBundleService.getSharedResourceBundleNames(), locale);
      String key = "UIWizardPageSelectLayoutForm.label." + label;
      if (resourceBundle != null && resourceBundle.containsKey(key)) {
        return resourceBundle.getString(key);
      } else {
        return label;
      }
    } catch (Exception e) {
      return label;
    }
  }

}
