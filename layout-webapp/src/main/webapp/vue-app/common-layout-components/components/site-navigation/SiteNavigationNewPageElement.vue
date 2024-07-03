<!--
 This file is part of the Meeds project (https://meeds.io/).
 
 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <div>
    <span class="font-weight-bold d-block text-start text-color body-2 mb-5">{{ $t('siteNavigation.label.pageTemplate') }}</span>
    <div class="mb-5">
      <span class="mb-2 body-2">{{ $t('siteNavigation.label.blankTemplate') }}</span>
      <div class="d-flex flex-row flex-grow-1 flex-shrink-1">
        <site-navigation-new-page-element-item
          v-for="template in blankTemplates"
          :key="template.id"
          :page-template="template"
          class="col-6 ps-0 clickable" />
      </div>
    </div>
    <div>
      <span class="pb-4 body-2">{{ $t('siteNavigation.label.defaultTemplate') }}</span>
      <div class="d-flex flex-row flex-grow-1 flex-shrink-1">
        <site-navigation-new-page-element-item
          v-for="templates in defaultTemplates"
          :key="templates.id"
          :page-template="templates"
          class="col-6 ps-0 clickable" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
    pageTemplates() {
      return this.$root.pageTemplates || [];
    },
    blankTemplates() {
      return this.pageTemplates.filter(item => item.category === 'blank');
    },
    defaultTemplates() {
      return this.pageTemplates.filter(item => item.category === 'default');
    },
  },
};
</script>
