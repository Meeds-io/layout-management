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

export const colValues = [1, 2, 3, 4, 6, 12];
export const breakpoints = ['xs', 'sm', 'md', 'lg', 'xl'];

export const containerModel = {
  // Used to add a specific id to the container on display
  // to be able to apply a specific CSS
  // but generally not useful
  id: null,
  // Not used for containers
  name: null,
  // Not used for containers
  icon: null,
  // Used by extensionRegistry mechanism to now which
  // Vue component to use
  template: null,
  // Used for now only for dynamic containers
  factoryId: null,
  // Not used for containers
  title: null,
  // Not used for containers
  description: null,
  // Used to specify the width of the container
  width: null,
  // Used to specify the height of the container
  height: null,
  // Used to specify some special CSS Classes to be used on container parent
  cssClass: null,
  // Used to specify whether the block should be displayed or not
  // when an addon is present
  profiles: null,
  // Generally kept to be accessible to everyone to make
  // the parent page access permissions applied globally 
  accessPermissions: ['Everyone'],
  // Deprecated, not used for containers
  moveAppsPermissions: ['Everyone'],
  // Deprecated, not used for containers
  moveContainersPermissions: ['Everyone'],
  // List of children which can be of type:
  // 1. Container
  //   or
  // 2. Application
  children: [],
};

export const currentBreakpoint = 'xl';

export const simpleContainerTemplate = 'system:/groovy/portal/webui/container/UIContainer.gtmpl';
export const rowContainerTemplate = 'system:/groovy/portal/webui/container/UIVRowContainer.gtmpl';
export const colContainerTemplate = 'system:/groovy/portal/webui/container/UIVColContainer.gtmpl';

export function addColumns(section, diffCols) {
  if (!diffCols) {
    return;
  }
  diffCols = Math.abs(diffCols);
  const gridCols = section.colsCount;
  const newGridCols = gridCols + diffCols;
  if (colValues.indexOf(newGridCols) < 0) {
    throw new Error(`${newGridCols} isn't compatible with grid system`);
  }

  const cells = JSON.parse(JSON.stringify(section.children));
  const rows = extractRows(cells);
  rows.forEach(row => {
    row.forEach(cell => breakpoints.forEach(b => {
      cell.breakpoints[b] *=  gridCols / newGridCols;
    }));
    for (let i = 0; i < diffCols; i++) {
      const cell = newCell(null, null, newGridCols);
      row.push(cell);
    }
  });

  const children = [];
  rows.forEach(row => row.forEach(c => children.push(c)));
  // Update colsCount of designated section
  applyGridStyle({children});
  Object.assign(section, {
    colsCount: newGridCols,
    children,
  });
}

export function removeColumns(section, diffCols) {
  diffCols = Math.abs(diffCols);
  if (!diffCols) {
    return;
  }
  const gridCols = section.colsCount;
  const cellSpan = 12 / gridCols;
  const newGridCols = gridCols - diffCols;
  if (colValues.indexOf(newGridCols) < 0) {
    throw new Error(`${newGridCols} isn't compatible with grid system`);
  }

  const cells = JSON.parse(JSON.stringify(section.children));
  const rows = extractRows(cells);

  const expectedSpan = 12 - diffCols * cellSpan;
  rows.forEach(row => {
    while (row.slice().reduce((sum, c) => sum += c.breakpoints[currentBreakpoint], 0) > expectedSpan) {
      const lastCell = row[row.length - 1];
      if (lastCell) {
        if (lastCell.breakpoints[currentBreakpoint] <= cellSpan) {
          // Remove last cell
          row.splice(row.length - 1, 1);
        } else {
          // Reduce Colspan of cell
          breakpoints
            .forEach(b => {
              lastCell.breakpoints[b] = lastCell.breakpoints[b] - cellSpan;
              if (lastCell.breakpoints[b] < 0) {
                lastCell.breakpoints[b] = 0;
              }
            });
        }
      }
    }
    row.forEach(cell => breakpoints.forEach(b => {
      cell.breakpoints[b] *=  gridCols / newGridCols;
    }));
  });

  const children = [];
  rows.forEach(row => row.forEach(c => children.push(c)));
  // Update colsCount of designated section
  applyGridStyle({children});
  Object.assign(section, {
    colsCount: newGridCols,
    children,
  });
}

export function addRows(section, diffRows) {
  if (!diffRows) {
    return;
  }
  const gridCols = section.colsCount;
  const cells = JSON.parse(JSON.stringify(section.children));
  const rows = extractRows(cells);
  for (let i = 0; i < diffRows; i++) {
    const row = [];
    for (let j = 0; j < gridCols; j++) {
      const cell = newCell(null, null, section.colsCount);
      row.push(cell);
    }
    rows.push(row);
  }
  const children = [];
  rows.forEach(row => row.forEach(c => children.push(c)));
  // Update colsCount of designated section
  applyGridStyle({children});
  Object.assign(section, {
    rowsCount: rows.length,
    children,
  });
}

export function removeRows(section, diffRows) {
  if (!diffRows) {
    return;
  }
  diffRows = Math.abs(diffRows);
  const cells = JSON.parse(JSON.stringify(section.children));
  const rows = extractRows(cells);
  rows.splice(rows.length - diffRows, diffRows);

  const children = [];
  rows.forEach(row => row.forEach(c => children.push(c)));
  // Update colsCount of designated section
  applyGridStyle({children});
  Object.assign(section, {
    rowsCount: rows.length,
    children,
  });
}

export function newSection(parentContainer, index, rows, cols) {
  rows = rows || 1;
  cols = cols || 4;
  const section = newContainer('system:/groovy/portal/webui/container/UIVRowContainer.gtmpl', null, parentContainer, index);
  for (let i = 0; i < rows; i++) {
    for (let j = 0; j < cols; j++) {
      newCell(section, index, cols);
    }
  }
  section.rowsCount = rows;
  section.colsCount = cols;
  return section;
}

export function newCell(section, index, cols) {
  const span = 12 / cols;
  const container = newContainer('system:/groovy/portal/webui/container/UIVColContainer.gtmpl',
    `col-${span} py-0 aspect-ratio-1`,
    section,
    index);
  parseCell(container);
  return container;
}

export function newContainer(template, cssClass, parentContainer, index) {
  const container = JSON.parse(JSON.stringify(containerModel));
  container.template = template;
  container.cssClass = cssClass;
  container.storageId = parseInt(Math.random() * 65536);
  container.color = `#${parseInt(256 - Math.random() * 128).toString(16)}${parseInt(256 - Math.random() * 128).toString(16)}${parseInt(256 - Math.random() * 128).toString(16)}`;
  container.id = container.storageId;
  container.name = container.storageId;
  if (parentContainer) {
    container.parentId = parentContainer.storageId;
    parentContainer.children.splice(index || 0, 0, container);
  }
  return container;
}

export function getParentContainer(layout) {
  const parentContainer = layout?.children?.[0]?.children?.[0];
  if (!parentContainer?.cssClass?.includes?.('v-application')) {
    return;
  } else {
    return parentContainer;
  }
}

export function parseLayout(layout) {
  const parentContainer = getParentContainer(layout);
  if (parentContainer) {
    const compatible = parentContainer.children.every(c => c.template === rowContainerTemplate);
    if (compatible) {
      try {
        parentContainer.children.forEach(parseRow);
      } catch (e) {
        console.debug(e); // eslint-disable-line no-console
        return false;
      }
    }
    return compatible;
  } else {
    return false;
  }
}

function parseRow(rowContainer) {
  if (!rowContainer.children.length) {
    return;
  }
  rowContainer.children.forEach(parseCell);
  const cellsCount = rowContainer.children.reduce((sum, c) => sum += c.breakpoints[currentBreakpoint], 0);
  rowContainer.rowsCount = parseInt(cellsCount / 12);
  rowContainer.colsCount = 12 / Math.min(...rowContainer.children.map(c => c.breakpoints[currentBreakpoint]));
}

function applyGridStyle(container) {
  container.children.forEach(applyGridStyle);
  if (!container.cssClass?.length) {
    return;
  } else {
    let cssClasses = container.cssClass || '';
    const colClasses = cssClasses.match(/(^| )col-((sm|md|lg|xl)-)?[0-9]{1,2}/g);
    if (colClasses?.length) {
      colClasses.forEach(c => cssClasses = cssClasses.replace(c, ''));
    }
    if (container.breakpoints) {
      cssClasses = cssClasses.replace(/  +/g, ' ');
      breakpoints.forEach(b => cssClasses += ` col${b !== 'xs' && `-${  b}` || ''}-${container.breakpoints[b]}`);
      container.cssClass = cssClasses;
    }
  }
}

function parseCell(colContainer) {
  if (colContainer.template !== colContainerTemplate) {
    throw Error(`Container template '${colContainer.template}' not compatible. Fallback to old editor.`);
  }
  const colClasses = colContainer.cssClass.match(/(^| )col-((sm|md|lg|xl)-)?[0-9]{1,2}/g);
  if (!colClasses.length) {
    throw Error(`Container CSS classes '${colContainer.cssClass}' not compatible. Fallback to old editor.`);
  }
  const bp = {
    xs: 0,
    sm: 0,
    md: 0,
    lg: 0,
    xl: 0,
  };
  breakpoints.forEach(b => {
    const prefix = b === 'xs' ?  'col-' : `col-${b}-`;
    const colClass = colClasses.find(c => c.match(`${prefix}[0-9]{1,2}`)?.length);
    if (colClass) {
      const colCount = Number(colClass.replace(prefix, ''));
      if (!Number.isNaN(colCount)) {
        bp[b] = colCount;
      }
    }
  });
  bp.xl = bp.xl || bp.lg || bp.md || bp.sm || bp.xs;
  bp.lg = bp.lg || bp.md || bp.sm || bp.xs;
  bp.md = bp.md || bp.sm || bp.xs;
  bp.sm = bp.sm || bp.xs;
  if (bp.xl) {
    colContainer.breakpoints = bp;
  } else {
    throw Error(`Container CSS classes '${colContainer.cssClass}' not compatible. Fallback to old editor.`);
  }
}

function extractRows(cells) {
  const rows = [];
  let rowCols = 0;
  if (cells.length) {
    let row = [];
    cells.forEach(cell => {
      if (!row.length) {
        rows.push(row);
      }
      row.push(cell);
      rowCols += cell.breakpoints[currentBreakpoint];
      if (rowCols % 12 === 0) {
        row = [];
      }
    });
  }
  return rows;
}
