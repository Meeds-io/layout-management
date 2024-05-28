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

import static io.meeds.layout.util.EntityMapper.fromEntity;
import static io.meeds.layout.util.EntityMapper.toEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.dao.PortletInstanceDAO;
import io.meeds.layout.entity.PortletInstanceEntity;
import io.meeds.layout.model.PortletInstance;

@Component
public class PortletInstanceStorage {

  @Autowired
  private PortletStorage     portletStorage;

  @Autowired
  private PortletInstanceDAO portletInstanceDAO;

  public List<PortletInstance> getPortletInstances() {
    List<PortletInstanceEntity> entities = portletInstanceDAO.findAll();
    return entities.stream()
                   .map(entity -> fromEntity(entity,
                                             portletStorage.getPortletDescriptor(entity.getContentId())))
                   .toList();
  }

  public List<PortletInstance> getPortletInstances(long categoryId) {
    List<PortletInstanceEntity> entities = portletInstanceDAO.findByCategoryId(categoryId);
    return entities.stream()
                   .map(entity -> fromEntity(entity,
                                             portletStorage.getPortletDescriptor(entity.getContentId())))
                   .toList();
  }

  public PortletInstance getPortletInstance(long id) {
    return portletInstanceDAO.findById(id)
                             .map(entity -> fromEntity(entity,
                                                       portletStorage.getPortletDescriptor(entity.getContentId())))
                             .orElse(null);
  }

  public PortletInstance createPortletInstance(PortletInstance portletInstance) {
    return save(portletInstance);
  }

  public PortletInstance updatePortletInstance(PortletInstance portletInstance) throws ObjectNotFoundException {
    if (!portletInstanceDAO.existsById(portletInstance.getId())) {
      throw new ObjectNotFoundException("Entity doesn't exist");
    }
    return save(portletInstance);
  }

  public void deletePortletInstance(long id) throws ObjectNotFoundException {
    if (!portletInstanceDAO.existsById(id)) {
      throw new ObjectNotFoundException(String.format("Entity with id %s doesn't exist", id));
    }
    portletInstanceDAO.deleteById(id);
  }

  private PortletInstance save(PortletInstance portletInstance) {
    PortletInstanceEntity entity = toEntity(portletInstance);
    entity = portletInstanceDAO.save(entity);
    return fromEntity(entity,
                      portletStorage.getPortletDescriptor(entity.getContentId()));
  }

}
