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
  <v-app>
    <v-card class="card-border-radius overflow-hidden position-static pb-5" flat>
      <h4 class="title font-weight-bold px-5 pt-5 ma-0">
        {{ $t('pageTemplates.title') }}
      </h4>
      <page-templates-management-toolbar
        ref="toolbar"
        @page-templates-filter="keyword = $event" />
      <page-templates-management-list
        ref="list"
        :keyword="keyword" />
    </v-card>
    <layout-editor-page-template-drawer />
    <page-templates-management-illustration-preview />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    keyword: null,
  }),
};
</script>
