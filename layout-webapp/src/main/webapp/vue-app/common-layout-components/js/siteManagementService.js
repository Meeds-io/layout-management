/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

export function deleteSite(siteType, siteName) {
  const formData = new FormData();
  formData.append('siteName', siteName);
  formData.append('siteType', siteType);
  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/sites?${params}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Error when deleting site');

    }
  });
}

export function editSiteLayout(siteName, siteType) {
  const formData = new FormData();
  formData.append('siteName', siteName);
  formData.append('siteType', siteType || 'portal');
  const params = new URLSearchParams(formData).toString();
  window.open(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/layout-editor?${params}&showMaxWindow=true&hideSharedLayout=true`, '_blank');
}

export function updateSite(siteName, siteType, siteLabel, siteDescription, displayed, displayOrder, bannerUploadId, bannerRemoved) {
  const formData = new FormData();
  formData.append('siteName', siteName);
  formData.append('siteType', siteType);
  formData.append('siteLabel', siteLabel);
  formData.append('siteDescription', siteDescription);
  formData.append('displayed', displayed);
  formData.append('displayOrder', displayOrder);
  formData.append('lang', eXo.env.portal.language);
  formData.append('bannerRemoved', bannerRemoved);
  if (bannerUploadId) {
    formData.append('bannerUploadId', bannerUploadId);
  }

  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/sites?${params}`, {
    credentials: 'include',
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
  }).then((resp) => {
    if (resp?.ok) {
      return resp.ok;
    } else {
      throw new Error('Error when updating site');
    }
  });
}

export function createSite(siteName, siteTemplate, siteLabel, siteDescription, displayed, displayOrder, bannerUploadId) {
  const formData = new FormData();
  formData.append('siteTemplate', siteTemplate);
  const portalConfig = {
    name: siteName,
    label: siteLabel,
    description: siteDescription,
    displayed: displayed,
    displayOrder: displayOrder,
    bannerUploadId: bannerUploadId,
  };
  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/sites?${params}`, {
    credentials: 'include',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(portalConfig),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when creating site');
    }
  });
}