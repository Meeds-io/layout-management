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

export function installApplication(navUri, applicationStorageId, applicationElement) {
  return fetch(`/portal${navUri}?maximizedPortletId=${applicationStorageId}&showMaxWindow=true&hideSharedLayout=true`, {
    credentials: 'include',
    method: 'GET',
    redirect: 'manual'
  })
    .then(resp => {
      if (resp?.status === 200) {
        return resp.text();
      } else {
        throw new Error('The retrieved page is not a portal page');
      }
    })
    .then(applicationContent => handleApplicationContent(applicationContent, applicationElement))
    .catch(e => console.error('Error navigating to ', navUri, '.', e));
}

function handleApplicationContent(applicationContent, applicationElement) {
  const newHeadContent = applicationContent.substring(applicationContent.search('<head') + applicationContent.match(/<head.*>/g)[0].length, applicationContent.search('</head>'));
  let newBodyContent = applicationContent.substring(applicationContent.search('<body') + applicationContent.match(/<body.*>/g)[0].length, applicationContent.lastIndexOf('</body>'));
  newBodyContent = installNewCSS(newHeadContent, newBodyContent);
  installNewJS(newHeadContent);

  const newHtmlDocument = document.createElement('div');
  newHtmlDocument.innerHTML = newBodyContent;
  const portletContent = newHtmlDocument.querySelector('.UIWorkingWorkspace .PORTLET-FRAGMENT');
  applicationElement.append(portletContent);

  window.setTimeout(() => {
    // The innerHTML change doesn't interpret script element
    // thus force it by replacing nodes and creating them instead
    replaceScriptElements(applicationElement);
  }, 10);
}

function installNewCSS(newHeadContent, newBodyContent) {
  const headCSSLinks = newHeadContent.match(/<link.*id=".*".*>/g);
  if (headCSSLinks?.length) {
    headCSSLinks.forEach(link => {
      const id = link.match(/id="([^"]*)"/i)[1];
      if (!document.querySelector(`#${id}`)) {
        const skinTypeResult = link.match(/skin-type="([a-z]*-skin)"/);
        if (skinTypeResult && skinTypeResult.length === 2) {
          const skinType = skinTypeResult[1];
          const skinElements = document.querySelectorAll(`[skin-type="${skinType}"]`);
          if (skinElements.length) {
            const linkElement = document.createElement('div');
            linkElement.innerHTML = link;
            // Install new CSS in the same skin files categories (portal, portlet or custom)
            const skinElement = skinElements[skinElements.length - 1];
            skinElement.after(linkElement.childNodes[0]);
          }
        }
      }
    });
  } else {
    throw new Error('The application content does not seem to have been loaded correctly with CSS links');
  }
  const bodyCSSLinks = newBodyContent.match(/<link.*id=".*".*>/g);
  if (bodyCSSLinks && bodyCSSLinks.length) {
    bodyCSSLinks.forEach(link => {
      const id = link.match(/id="([^"]*)"/i)[1];
      if (!document.querySelector(`#${id}`)) {
        const linkElement = document.createElement('div');
        linkElement.innerHTML = link;
        // Install new CSS from newly downloaded body
        document.head.append(linkElement.childNodes[0]);
      }
      newBodyContent = newBodyContent.replace(link, '');
    });
  }
  return newBodyContent;
}

function installNewJS(newHeadContent) {
  const replacableScriptsIterator = newHeadContent.matchAll(/<script[^>]*id="[^>]*"[^>]*>/g);
  let scriptIteratorElement = replacableScriptsIterator.next().value;
  while (scriptIteratorElement) {
    const script = scriptIteratorElement[0];
    const id = script.match(/id="([^"]*)"/i)[1];
    let scriptElement = id && (document.querySelector(`#${id.trim()}`) || document.querySelector(`[data-id=${id.trim()}]`));
    if (!scriptElement) {
      const scriptContent = newHeadContent.substring(scriptIteratorElement.index, newHeadContent.indexOf('</script>', scriptIteratorElement.index));
      scriptElement = document.createElement('script');
      scriptElement.innerText = scriptContent.substring(scriptContent.indexOf('>') + 1).replace(/(\r)?(\n)?/g, '');
      document.head.append(scriptElement);
      replaceScriptElements(scriptElement);
    }
    scriptIteratorElement = replacableScriptsIterator.next().value;
  }
}

function replaceScriptElements(node) {
  if (node.tagName === 'SCRIPT') {
    node.parentNode.replaceChild(cloneScriptElement(node), node);
  } else {
    for (let i = 0; i < node.childNodes.length; i++) {
      replaceScriptElements(node.childNodes[i]);
    }
  }
  return node;
}

function cloneScriptElement(node) {
  const scriptElement  = document.createElement('script');
  scriptElement.innerText = node.innerHTML;
  const scriptAttrs = node.attributes;
  for (let i = 0; i < scriptAttrs.length; i++)  {
    scriptElement.setAttribute(scriptAttrs[i].name, scriptAttrs[i].value);
  }
  return scriptElement;
}
