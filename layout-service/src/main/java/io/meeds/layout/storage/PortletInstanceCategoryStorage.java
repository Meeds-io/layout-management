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
package io.meeds.layout.storage;

import static io.meeds.layout.util.EntityMapper.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.dao.PortletInstanceCategoryDAO;
import io.meeds.layout.entity.PortletInstanceCategoryEntity;
import io.meeds.layout.model.PortletInstanceCategory;
import io.meeds.layout.util.EntityMapper;

@Component
public class PortletInstanceCategoryStorage {

  @Autowired
  private PortletInstanceCategoryDAO portletInstanceCategoryDAO;

  public List<PortletInstanceCategory> getPortletInstanceCategories() {
    List<PortletInstanceCategoryEntity> entities = portletInstanceCategoryDAO.findAll();
    return entities.stream()
                   .map(EntityMapper::fromEntity)
                   .toList();
  }

  public PortletInstanceCategory getPortletInstanceCategory(long id) {
    return portletInstanceCategoryDAO.findById(id)
                                     .map(EntityMapper::fromEntity)
                                     .orElse(null);
  }

  public PortletInstanceCategory createPortletInstanceCategory(PortletInstanceCategory portletInstanceCategory) {
    return save(portletInstanceCategory);
  }

  public PortletInstanceCategory updatePortletInstanceCategory(PortletInstanceCategory portletInstanceCategory) throws ObjectNotFoundException {
    if (!portletInstanceCategoryDAO.existsById(portletInstanceCategory.getId())) {
      throw new ObjectNotFoundException("Entity doesn't exist");
    }
    return save(portletInstanceCategory);
  }

  public void deletePortletInstanceCategory(long id) throws ObjectNotFoundException {
    if (!portletInstanceCategoryDAO.existsById(id)) {
      throw new ObjectNotFoundException(String.format("Entity with id %s doesn't exist", id));
    }
    portletInstanceCategoryDAO.deleteById(id);
  }

  private PortletInstanceCategory save(PortletInstanceCategory portletInstanceCategory) {
    PortletInstanceCategoryEntity entity = toEntity(portletInstanceCategory);
    entity = portletInstanceCategoryDAO.save(entity);
    return fromEntity(entity);
  }

}
