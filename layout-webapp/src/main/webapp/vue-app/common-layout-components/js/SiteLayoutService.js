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

export function createSite(siteName, siteTemplate, siteLabel, siteDescription, displayed, displayOrder, bannerUploadId) {
  const createModel = {
    siteTemplate,
    portalConfig: {
      name: siteName,
      label: siteLabel,
      description: siteDescription,
      displayed: displayed,
      displayOrder: displayOrder,
      bannerUploadId: bannerUploadId,
    },
  };
  return fetch('/layout/rest/sites', {
    credentials: 'include',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(createModel),
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Error when creating site');
    }
  });
}

export function updateSite(siteName, siteType, siteLabel, siteDescription, displayed, displayOrder, bannerUploadId, bannerRemoved) {
  const updateModel = {
    siteType,
    siteName,
    siteLabel,
    siteDescription,
    displayed,
    displayOrder,
    bannerRemoved: bannerRemoved || false,
  };
  if (bannerUploadId) {
    updateModel.bannerUploadId = bannerUploadId;
  }
  return fetch('/layout/rest/sites', {
    credentials: 'include',
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(updateModel),
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Error when updating site');
    }
  });
}

export function deleteSite(siteType, siteName) {
  return fetch(`/layout/rest/sites/${siteType}/${siteName}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Error when deleting site');
    }
  });
}

export function updateSitePermissions(siteType, siteName, editPermission, accessPermissions) {
  return fetch(`/layout/rest/sites/${siteType}/${siteName}/permissions`, {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      editPermission: editPermission || null,
      accessPermissions: accessPermissions || null,
    }),
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Error when updating site permissions');
    }
  });
}

export function getMembershipTypes() {
  return fetch(`/${eXo.env.portal.rest}/v1/membershipTypes`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when retrieving membership types');
    }
  });
}

export function editSiteLayout(siteName, siteType) {
  const formData = new FormData();
  formData.append('siteName', siteName);
  formData.append('siteType', siteType || 'portal');
  const params = new URLSearchParams(formData).toString();
  window.open(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/layout-editor?${params}`, '_blank');
}
