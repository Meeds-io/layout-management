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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.ResourceBundleService;

import io.meeds.social.translation.service.TranslationService;

import lombok.SneakyThrows;

@Component
public class LayoutTranslationImportService {

  private static final Log            LOG                  = ExoLogger.getLogger(LayoutTranslationImportService.class);

  @Autowired
  private TranslationService          translationService;

  @Autowired
  private LocaleConfigService         localeConfigService;

  @Autowired
  private ResourceBundleService       resourceBundleService;

  private Map<String, List<Runnable>> postImportProcessors = new ConcurrentHashMap<>();

  private Map<Locale, ResourceBundle> bundles              = new ConcurrentHashMap<>();

  public void saveTranslationLabels(String objectType,
                                    long objectId,
                                    String fieldName,
                                    Map<String, String> labels) {
    try {
      translationService.deleteTranslationLabels(objectType,
                                                 objectId,
                                                 fieldName);
    } catch (Exception e) { // NOSONAR
      // Normal, when not exists
    }
    String defaultLabel = saveDefaultTranslationLabel(objectType,
                                                      objectId,
                                                      fieldName,
                                                      labels.get("en"));
    // Make Heavy processing made at the end or import process
    postImportProcessors.computeIfAbsent(objectType, k -> new ArrayList<>())
                        .add(() -> saveTranslationLabelsForAllLanguages(objectType,
                                                                        objectId,
                                                                        fieldName,
                                                                        labels,
                                                                        defaultLabel));
  }

  public void postImport(String objectType) {
    postImportProcessors.computeIfAbsent(objectType, k -> new ArrayList<>()).forEach(Runnable::run);
    postImportProcessors.remove(objectType);
    bundles.clear();
  }

  @SneakyThrows
  private void saveTranslationLabelsForAllLanguages(String objectType,
                                                    long objectId,
                                                    String fieldName,
                                                    Map<String, String> labels,
                                                    String defaultLabel) {
    String i18nKey = labels.get("en");
    Map<Locale, String> translations = new HashMap<>();
    localeConfigService.getLocalConfigs()
                       .stream()
                       .filter(config -> !StringUtils.equals(config.getLocale().toLanguageTag(), "ma"))
                       .forEach(config -> translations.put(config.getLocale(),
                                                           getI18NLabel(i18nKey,
                                                                        config.getLocale(),
                                                                        defaultLabel)));
    translationService.saveTranslationLabels(objectType,
                                             objectId,
                                             fieldName,
                                             translations);
  }

  @SneakyThrows
  protected String saveDefaultTranslationLabel(String objectType,
                                               long objectId,
                                               String fieldName,
                                               String i18nKey) {
    String label = getI18NLabel(i18nKey, Locale.ENGLISH);
    translationService.saveTranslationLabel(objectType, objectId, fieldName, Locale.ENGLISH, label);
    return label;
  }

  protected String getI18NLabel(String label, Locale locale) {
    return getI18NLabel(label, locale, null);
  }

  protected String getI18NLabel(String label, Locale locale, String defaultLabel) {
    try {
      ResourceBundle resourceBundle = getResourceBundle(locale);
      if (resourceBundle != null
          && resourceBundle.containsKey(label)) {
        return resourceBundle.getString(label);
      }
    } catch (Exception e) {
      LOG.debug("Resource Bundle not found with locale {}", locale, e);
    }
    return defaultLabel == null ? label : defaultLabel;
  }

  private ResourceBundle getResourceBundle(Locale locale) {
    return bundles.computeIfAbsent(locale,
                                   l -> resourceBundleService.getResourceBundle(new String[] { "locale.portlet.Portlets",
                                                                                               "locale.portlet.LayoutEditor" },
                                                                                l,
                                                                                PortalContainer.getInstance()
                                                                                               .getPortalClassLoader()));
  }

}
