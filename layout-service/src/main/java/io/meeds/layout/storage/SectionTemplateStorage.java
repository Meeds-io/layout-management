/**
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
package io.meeds.layout.storage;

import static io.meeds.layout.util.EntityMapper.fromEntity;
import static io.meeds.layout.util.EntityMapper.toEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.dao.SectionTemplateDAO;
import io.meeds.layout.entity.SectionTemplateEntity;
import io.meeds.layout.model.SectionTemplate;
import io.meeds.layout.util.EntityMapper;

@Component
public class SectionTemplateStorage {

  @Autowired
  private SectionTemplateDAO sectionTemplateDAO;

  public List<SectionTemplate> getSectionTemplates() {
    List<SectionTemplateEntity> entities = sectionTemplateDAO.findAll();
    return entities.stream()
                   .map(EntityMapper::fromEntity)
                   .toList();
  }

  public SectionTemplate getSectionTemplate(long id) {
    return sectionTemplateDAO.findById(id)
                             .map(EntityMapper::fromEntity)
                             .orElse(null);
  }

  public SectionTemplate createSectionTemplate(SectionTemplate sectionTemplate) {
    return save(sectionTemplate);
  }

  public SectionTemplate updateSectionTemplate(SectionTemplate sectionTemplate) throws ObjectNotFoundException {
    if (!sectionTemplateDAO.existsById(sectionTemplate.getId())) {
      throw new ObjectNotFoundException("Entity doesn't exist");
    }
    return save(sectionTemplate);
  }

  public void deleteSectionTemplate(long id) throws ObjectNotFoundException {
    if (!sectionTemplateDAO.existsById(id)) {
      throw new ObjectNotFoundException(String.format("Entity with id %s doesn't exist", id));
    }
    sectionTemplateDAO.deleteById(id);
  }

  private SectionTemplate save(SectionTemplate sectionTemplate) {
    SectionTemplateEntity entity = toEntity(sectionTemplate);
    entity = sectionTemplateDAO.save(entity);
    return fromEntity(entity);
  }

}
