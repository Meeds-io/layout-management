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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.exoplatform.commons.exception.ObjectNotFoundException;

import io.meeds.layout.dao.PageTemplateDAO;
import io.meeds.layout.entity.PageTemplateEntity;
import io.meeds.layout.model.PageTemplate;

@Component
public class PageTemplateStorage {

  @Autowired
  private PageTemplateDAO pageTemplateDAO;

  public List<PageTemplate> getPageTemplates() {
    List<PageTemplateEntity> entities = pageTemplateDAO.findAll();
    return entities.stream()
                   .map(e -> new PageTemplate(e.getId(),
                                              e.isDisabled(),
                                              e.isSystem(),
                                              e.getCategory(),
                                              e.getContent()))
                   .toList();
  }

  public PageTemplate getPageTemplate(long id) {
    return pageTemplateDAO.findById(id)
                          .map(e -> new PageTemplate(e.getId(),
                                                     e.isDisabled(),
                                                     e.isSystem(),
                                                     e.getCategory(),
                                                     e.getContent()))
                          .orElse(null);
  }

  public PageTemplate createPageTemplate(PageTemplate pageTemplate) {
    PageTemplateEntity entity = new PageTemplateEntity(null,
                                                       pageTemplate.isDisabled(),
                                                       pageTemplate.isSystem(),
                                                       pageTemplate.getCategory(),
                                                       pageTemplate.getContent());
    entity = pageTemplateDAO.save(entity);
    return new PageTemplate(entity.getId(),
                            entity.isDisabled(),
                            entity.isSystem(),
                            entity.getCategory(),
                            entity.getContent());
  }

  public PageTemplate updatePageTemplate(PageTemplate pageTemplate) throws ObjectNotFoundException {
    if (!pageTemplateDAO.existsById(pageTemplate.getId())) {
      throw new ObjectNotFoundException("Page template doesn't exist");
    }
    PageTemplateEntity entity = new PageTemplateEntity(pageTemplate.getId(),
                                                       pageTemplate.isDisabled(),
                                                       pageTemplate.isSystem(),
                                                       pageTemplate.getCategory(),
                                                       pageTemplate.getContent());
    entity = pageTemplateDAO.save(entity);
    return new PageTemplate(entity.getId(),
                            entity.isDisabled(),
                            entity.isSystem(),
                            entity.getCategory(),
                            entity.getContent());
  }

  public void deletePageTemplate(long templateId) throws ObjectNotFoundException {
    if (!pageTemplateDAO.existsById(templateId)) {
      throw new ObjectNotFoundException(String.format("Page template with id %s doesn't exist", templateId));
    }
    pageTemplateDAO.deleteById(templateId);
  }

}
