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

export function createSite(siteName, siteTemplate, siteLabel, siteDescription, displayed, displayOrder, bannerUploadId, icon) {
  const createModel = {
    siteTemplate,
    portalConfig: {
      name: siteName,
      label: siteLabel,
      description: siteDescription,
      displayed: displayed,
      displayOrder: displayOrder,
      bannerUploadId: bannerUploadId,
      properties: {
        icon: icon || 'fa-globe',
      },
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
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when creating site');
    }
  });
}

export function getSiteById(siteId, lang) {
  const formData = new FormData();
  if (lang) {
    formData.append('lang', lang);
  }
  const params = new URLSearchParams(formData).toString();
  return fetch(`/layout/rest/sites/${siteId}${params.length && '?' || ''}${params.length && params || ''}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when Getting site');
    }
  });
}

export function getSite(siteType, siteName, lang) {
  const formData = new FormData();
  formData.append('siteName', siteName);
  formData.append('siteType', siteType || 'portal');
  if (lang) {
    formData.append('lang', lang);
  }
  const params = new URLSearchParams(formData).toString();
  return fetch(`/layout/rest/sites?${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when Getting site');
    }
  });
}

export function getSiteLabels(siteId) {
  return fetch(`/layout/rest/sites/${siteId}/labels`, {
    credentials: 'include',
    method: 'GET'
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when retrieving site labels');
    }
  });
}

export function getSiteDescriptions(siteId) {
  return fetch(`/layout/rest/sites/${siteId}/descriptions`, {
    credentials: 'include',
    method: 'GET'
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when retrieving site descriptions');
    }
  });
}

export function updateSite(siteName, siteType, siteLabel, siteDescription, displayed, displayOrder, bannerUploadId, bannerRemoved, siteIcon) {
  const updateModel = {
    siteType,
    siteName,
    siteLabel,
    siteDescription,
    siteIcon: siteIcon || 'fa-globe',
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
  const formData = new FormData();
  formData.append('siteName', siteName);
  formData.append('siteType', siteType || 'portal');
  const params = new URLSearchParams(formData).toString();
  return fetch(`/layout/rest/sites?${params}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Error when deleting site');
    }
  });
}

export function updateSitePermissions(siteType, siteName, editPermission, accessPermissions) {
  return fetch('/layout/rest/sites/permissions', {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      siteType,
      siteName,
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
