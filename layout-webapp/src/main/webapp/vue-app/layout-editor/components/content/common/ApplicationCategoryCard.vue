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
  <v-card
    v-if="hasApplications"
    class="d-flex flex-column border-color border-radius mt-4 align-center justify-center aspect-ratio-1"
    min-width="110px"
    width="calc(33% - 8px)"
    max-width="120px"
    flat
    @click="$root.$emit('layout-add-application-drawer', categoryApplications)">
    <v-icon class="mt-auto mb-2">{{ categoryIcon }}</v-icon>
    <div
      v-sanitized-html="categoryLabel"
      :title="$utils.htmlToText(categoryLabel)"
      class="text-truncate-2 mb-auto subtitle-1 mt-2"></div>
  </v-card>
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
    parentId: {
      type: Object,
      default: null,
    },
    container: {
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
    categoryIcon() {
      return this.category?.icon;
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

