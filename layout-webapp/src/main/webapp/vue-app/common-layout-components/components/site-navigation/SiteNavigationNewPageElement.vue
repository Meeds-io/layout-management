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
    <span class="d-block text-start text-sub-title text-header mb-5">{{ $t('siteNavigation.label.pageTemplate') }}</span>
    <div class="mb-5">
      <span class="mb-2 font-weight-bold">{{ $t('siteNavigation.label.blankTemplate') }}</span>
      <v-row class="mx-0 d-flex flex-row flex-grow-1 flex-shrink-1">
        <site-navigation-new-page-element-item
          v-for="template in blankTemplates"
          :key="template.id"
          :page-template="template"
          class="col-6 ps-0 clickable" />
      </v-row>
    </div>
    <div class="mb-5">
      <span class="pb-4 font-weight-bold">{{ $t('siteNavigation.label.defaultTemplate') }}</span>
      <v-row class="mx-0 d-flex flex-row flex-grow-1 flex-shrink-1">
        <site-navigation-new-page-element-item
          v-for="templates in defaultTemplates"
          :key="templates.id"
          :page-template="templates"
          class="col-6 ps-0 clickable" />
      </v-row>
    </div>
    <div>
      <span class="pb-4 font-weight-bold">{{ $t('siteNavigation.label.customizedTemplate') }}</span>
      <v-row class="mx-0 d-flex flex-row flex-grow-1 flex-shrink-1">
        <site-navigation-new-page-element-item
          v-for="templates in customizedTemplates"
          :key="templates.id"
          :page-template="templates"
          class="col-6 ps-0 clickable" />
      </v-row>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      collator: new Intl.Collator(eXo.env.portal.language, {numeric: true, sensitivity: 'base'}),
    };
  },
  computed: {
    pageTemplates() {
      return this.$root.pageTemplates || [];
    },
    blankTemplates() {
      const items = this.pageTemplates.filter(item => item.category === 'blank');
      items.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return items;
    },
    defaultTemplates() {
      const items = this.pageTemplates.filter(item => item.category === 'default');
      items.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return items;
    },
    customizedTemplates() {
      const items = this.pageTemplates.filter(item => item.category !== 'blank' && item.category !== 'default');
      items.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return items;
    },
  },
};
</script>
