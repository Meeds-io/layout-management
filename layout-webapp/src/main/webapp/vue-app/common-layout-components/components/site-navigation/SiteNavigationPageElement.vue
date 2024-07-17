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
  <div v-if="hasMoreThanOneTemplate">
    <site-navigation-new-page-element 
      @input="$emit('input', $event)" />
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: null
    }
  },
  computed: {
    templatesCount() {
      return this.$root.pageTemplates?.length || 0;
    },
    hasMoreThanOneTemplate() {
      return this.templatesCount > 1;
    },
  },
  created() {
    this.getPageTemplates();
  },
  methods: {
    getPageTemplates() {
      if (!this.$root.pageTemplates?.length) {
        return this.$pageTemplateService.getPageTemplates()
          .then(pageTemplates => this.$root.pageTemplates = pageTemplates && pageTemplates.filter(t => !t.disabled && t.name) || []);
      }
    },
  },
};
</script>
