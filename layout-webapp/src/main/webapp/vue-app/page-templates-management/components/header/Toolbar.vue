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
  <application-toolbar
    id="pageTemplatesApplication"
    :right-text-filter="{
      hide: false,
      minCharacters: 1,
      text: $t('pageTemplates.add'),
      placeholder: $t('pageTemplates.filter.placeholder'),
      tooltip: $t('pageTemplates.filter.placeholder'),
    }"
    @filter-text-input="$emit('page-templates-filter', $event)">
    <template #left>
      <v-btn
        id="applicationToolbarLeftButton"
        :aria-label="$t('pageTemplates.add')"
        class="btn btn-primary text-truncate"
        disabled
        @click="$emit('add-page-template')">
        <v-icon
          class="me-2"
          size="18">
          fa-plus
        </v-icon>
        <span class="text-truncate text-none">
          {{ $t('pageTemplates.add') }}
        </span>
      </v-btn>
    </template>
  </application-toolbar>
</template>
<script>
export default {
  data: () => ({
    pageTemplates: null,
  }),
};
</script>
