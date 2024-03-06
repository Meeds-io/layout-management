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
package io.meeds.layout.model;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gatein.pc.api.invocation.resolver.AttributeResolver;
import org.gatein.pc.api.invocation.resolver.MapAttributeResolver;
import org.gatein.pc.api.spi.UserContext;

public class PortletInvocationUserContext implements UserContext {

  @SuppressWarnings("unchecked")
  private AttributeResolver<String, Object> attributeResolver = new MapAttributeResolver();

  private Locale                            locale;

  private String                            username;

  public PortletInvocationUserContext(String username, Locale locale) {
    this.username = username;
    this.locale = locale;
  }

  @Override
  public String getId() {
    return username;
  }

  @Override
  public void setAttribute(String attrKey, Object attrValue) {
    attributeResolver.setAttribute(attrKey, attrValue);
  }

  @Override
  public Object getAttribute(String attrKey) {
    return attributeResolver.getAttribute(attrKey);
  }

  @Override
  public List<Locale> getLocales() {
    return Collections.singletonList(locale);
  }

  @Override
  public Locale getLocale() {
    return locale;
  }

  @Override
  public Map<String, String> getInformations() {
    return Collections.emptyMap();
  }

}
