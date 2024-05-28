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
  <v-expansion-panel
    v-if="hasApplications"
    class="border-color border-radius mt-4">
    <v-expansion-panel-header v-sanitized-html="categoryLabel" />
    <v-divider v-if="expanded" />
    <v-expansion-panel-content>
      <div
        v-for="application in categoryApplications"
        :key="application.id"
        class="d-flex flex-no-wrap justify-space-between border-radius border-color ApplicationCard ApplicationCardEmbedded">
        <layout-editor-application-card
          :application="application"
          class="d-flex flex-grow-1"
          @add="$emit('addApplication', application)" />
      </div>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>

<script>
export default {
  props: {
    expanded: {
      type: Boolean,
      default: false,
    },
    category: {
      type: Object,
      default: null,
    },
    applications: {
      type: Array,
      default: null,
    },
  },
  computed: {
    categoryName() {
      return this.category?.name;
    },
    categoryLabel() {
      return this.category?.label || this.category?.name;
    },
    categoryId() {
      return this.category?.id;
    },
    categoryApplications() {
      return this.applications?.filter?.(a => (a.categoryId && a.categoryId === this.categoryId) || (!a.categoryId && a.applicationName === this.categoryName)) || [];
    },
    hasApplications() {
      return this.categoryApplications.length > 0;
    },
  },
};
</script>

