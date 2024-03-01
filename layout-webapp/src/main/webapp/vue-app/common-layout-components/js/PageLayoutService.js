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

export function getPages(siteType, siteName, pageDisplayName) {
  const formData = new FormData();
  if (siteName) {
    formData.append('siteName', siteName);
  }
  if (siteType) {
    formData.append('siteType', siteType);
  }
  if (pageDisplayName) {
    formData.append('pageDisplayName', pageDisplayName);
  }
  const params = new URLSearchParams(formData).toString();

  return fetch(`/layout/rest/pages?${params}`, {
    method: 'GET',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when retrieving pages');
    }
  });
}

export function createPage(pageName, pageTitle, pageSiteName, pageSiteType, pageType, link, pageTemplate) {
  return fetch('/layout/rest/pages', {
    credentials: 'include',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      pageName,
      pageTemplate,
      pageType,
      pageTitle,
      pageSiteType,
      pageSiteName,
      link,
    }),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when creating page');
    }
  });
}

export function updatePageLink(pageRef, link) {
  const formData = new FormData();
  formData.append('link', link);
  const params = new URLSearchParams(formData).toString();
  return fetch(`/layout/rest/pages/${pageRef}/link`, {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: params
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Error when updating page link');
    }
  });
}

export function updatePagePermissions(pageRef, editPermission, accessPermissions) {
  return fetch(`/layout/rest/pages/${pageRef}/permissions`, {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      editPermission,
      accessPermissions,
    }),
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error(resp.status);
    }
  });
}

export function getPage(pageRef) {
  return fetch(`/layout/rest/pages/${pageRef}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when retrieving page');
    }
  });
}

export function getPageLayout(pageRef) {
  return fetch(`/layout/rest/pages/${pageRef}/layout`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when retrieving page layout');
    }
  });
}

export function getPageTemplates() {
  return fetch('/layout/rest/pages/templates', {
    method: 'GET',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Error when retrieving page templates');
    } else {
      return resp.json();
    }
  });
}

export function editPageLayout(nodeId, pageId) {
  const formData = new FormData();
  if (nodeId) {
    formData.append('nodeId', nodeId);
  } else if (pageId) {
    formData.append('pageId', pageId);
  }
  const params = new URLSearchParams(formData).toString();
  window.open(`${eXo.env.portal.context}/${eXo.env.portal.portalName}/layout-editor?${params}`, '_blank');
}
