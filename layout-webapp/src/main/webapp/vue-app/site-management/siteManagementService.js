/*
 * Copyright (C) 2023 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
  
export function editSiteLayout(portalName) {
  const params = [
    {name: 'portalName', value: portalName}
  ] ;
  return fetch(`${eXo.env.server.createPortalURL('UIWorkingWorkspace', 'EditPortalLayout', true, params)}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error(resp.status);
    }
    else {
      window.location.reload();
    }
  });
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