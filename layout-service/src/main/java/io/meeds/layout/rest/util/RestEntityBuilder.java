/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.layout.rest.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.I18N;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.State;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.rest.model.UserNodeRestEntity;
import org.exoplatform.portal.mop.service.LayoutService;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.entity.SiteEntity;

import io.meeds.layout.model.NodeLabel;
import io.meeds.layout.rest.model.LayoutModel;
import io.meeds.layout.rest.model.SiteRestEntity;
import io.meeds.layout.service.PageLayoutService;

import jakarta.servlet.http.HttpServletRequest;

public class RestEntityBuilder {

  private RestEntityBuilder() {
  }

  public static NodeLabel toNodeLabel(Map<Locale, State> nodeLabels) {
    LocaleConfigService localeConfigService = CommonsUtils.getService(LocaleConfigService.class);
    Locale defaultLocale = localeConfigService.getDefaultLocaleConfig() == null ? Locale.ENGLISH :
                                                                                localeConfigService.getDefaultLocaleConfig()
                                                                                                   .getLocale();
    String defaultLanguage = defaultLocale.getLanguage();
    Map<String, String> supportedLanguages =
                                           localeConfigService.getLocalConfigs()
                                               == null ?
                                                       Collections.singletonMap(defaultLocale.getLanguage(),
                                                                                defaultLocale.getDisplayName()) :
                                                       localeConfigService.getLocalConfigs()
                                                                          .stream()
                                                                          .filter(localeConfig -> !StringUtils.equals(localeConfig.getLocaleName(),
                                                                                                                      "ma"))
                                                                          .collect(Collectors.toMap(LocaleConfig::getLocaleName,
                                                                                                    localeConfig -> localeConfig.getLocale()
                                                                                                                                .getDisplayName()));
    Map<String, String> localized = new HashMap<>();
    NodeLabel nodeLabelRestEntity = new NodeLabel();
    if (nodeLabels != null && nodeLabels.size() != 0) {
      for (Map.Entry<Locale, State> entry : nodeLabels.entrySet()) {
        Locale locale = entry.getKey();
        State state = entry.getValue();
        localized.put(I18N.toTagIdentifier(locale), state.getName());
      }
      if (!nodeLabels.containsKey(defaultLocale)) {
        localized.put(I18N.toTagIdentifier(defaultLocale), null);
      }
      nodeLabelRestEntity.setLabels(localized);
    }
    nodeLabelRestEntity.setDefaultLanguage(defaultLanguage);
    nodeLabelRestEntity.setSupportedLanguages(supportedLanguages);
    return nodeLabelRestEntity;
  }

  public static SiteRestEntity toSiteEntity(PageLayoutService pageLayoutService,
                                            PortalConfig site,
                                            HttpServletRequest request,
                                            Locale locale) throws Exception {
    SiteEntity siteEntity = EntityBuilder.buildSiteEntity(site,
                                                          request,
                                                          true,
                                                          null,
                                                          true,
                                                          false,
                                                          false,
                                                          locale);
    SiteRestEntity siteRestEntity = new SiteRestEntity(siteEntity);
    List<UserNodeRestEntity> siteNavigations = siteEntity.getSiteNavigations();
    computeCompatibilityWithEditor(pageLayoutService, siteRestEntity, siteNavigations);
    return siteRestEntity;
  }

  private static void computeCompatibilityWithEditor(PageLayoutService pageLayoutService,
                                                     SiteRestEntity siteRestEntity,
                                                     List<UserNodeRestEntity> siteNavigations) {
    if (CollectionUtils.isNotEmpty(siteNavigations)) {
      siteNavigations.forEach(n -> {
        PageKey pageKey = n.getPageKey();
        if (n.isCanEditPage()) {
          Page page = pageLayoutService.getPageLayout(pageKey);
          siteRestEntity.getPagesCompatibility().put(pageKey.format(), page != null && isCompatibleWithEditor(page));
        } else if (pageKey != null) {
          siteRestEntity.getPagesCompatibility().put(pageKey.format(), false);
        }
        computeCompatibilityWithEditor(pageLayoutService, siteRestEntity, n.getChildren());
      });
    }
  }

  public static LayoutModel toLayoutModel(Page page, LayoutService layoutService, String expand) {
    LayoutModel layoutModel = new LayoutModel(page);
    if (StringUtils.contains(expand, "contentId")) {
      Map<String, String> contentIds = new HashMap<>();
      computeApplicationContentId(layoutService, page.getChildren(), contentIds);
      applyApplicationContentId(layoutModel.getChildren(), contentIds);
    }
    return layoutModel;
  }

  @SuppressWarnings("unchecked")
  private static void computeApplicationContentId(LayoutService layoutService,
                                                  ArrayList<ModelObject> children,
                                                  Map<String, String> contentIds) {
    if (CollectionUtils.isEmpty(children)) {
      return;
    }
    for (ModelObject layoutModel : children) {
      if (layoutModel instanceof Container container) {
        computeApplicationContentId(layoutService, container.getChildren(), contentIds);
      } else if (layoutModel instanceof Application application) { // NOSONAR
        String storageId = application.getStorageId();
        if (StringUtils.isNotBlank(storageId)) {
          String contentId = layoutService.getId(application.getState());
          contentIds.put(storageId, contentId);
        }
      }
    }
  }

  private static void applyApplicationContentId(List<LayoutModel> children,
                                                Map<String, String> contentIds) {
    if (CollectionUtils.isEmpty(children)) {
      return;
    }
    for (LayoutModel layoutModel : children) {
      if (contentIds.containsKey(layoutModel.getStorageId())) {
        layoutModel.setContentId(contentIds.get(layoutModel.getStorageId()));
      } else if (CollectionUtils.isNotEmpty(layoutModel.getChildren())) { // NOSONAR
        applyApplicationContentId(layoutModel.getChildren(), contentIds);
      }
    }
  }

  public static Page fromLayoutModel(LayoutModel layoutModel) {
    return layoutModel.toPage();
  }

  private static boolean isCompatibleWithEditor(Page page) { // NOSONAR
    ArrayList<ModelObject> children = page.getChildren();
    if (CollectionUtils.isEmpty(children)) {
      return true;
    } else {
      if (children.size() != 1) {
        return false;
      }
      ModelObject parentContainer = children.get(0);
      if (parentContainer != null
          && parentContainer instanceof Container appContainer
          && StringUtils.contains(appContainer.getTemplate(), "UIPageLayout.gtmpl")) {
        return isChildrenOfTypeSection(appContainer);
      } else if (parentContainer == null
                 || !(parentContainer instanceof Container vAppContainer)
                 || !StringUtils.contains(vAppContainer.getCssClass(), "VuetifyApp")) {
        return false;
      } else if (CollectionUtils.isEmpty(vAppContainer.getChildren())) {
        return true;
      } else {
        parentContainer = vAppContainer.getChildren().get(0);
        if (parentContainer == null
            || !(parentContainer instanceof Container appContainer)
            || !StringUtils.contains(appContainer.getCssClass(), "v-application")) {
          return false;
        }
        return isChildrenOfTypeSection(appContainer);
      }
    }
  }

  private static boolean isChildrenOfTypeSection(Container appContainer) {
    return appContainer.getChildren()
                       .stream()
                       .allMatch(c -> c instanceof Container container
                                      && (StringUtils.equals("GridContainer", container.getTemplate())
                                          || StringUtils.equals("FlexContainer", container.getTemplate())));
  }

}
