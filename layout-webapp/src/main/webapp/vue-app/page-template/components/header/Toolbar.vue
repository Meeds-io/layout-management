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
      minCharacters: 1,
      placeholder: $t('pageTemplates.filter.placeholder'),
      tooltip: $t('pageTemplates.filter.placeholder'),
    }"
    class="px-1"
    @filter-text-input="$emit('page-templates-filter', $event)">
    <template v-if="!$root.isMobile" #left>
      <v-btn
        id="applicationToolbarLeftButton"
        :aria-label="$t('pageTemplates.add')"
        :class="$root.isMobile && 'px-0'"
        :loading="creating"
        class="btn btn-primary text-truncate"
        @click="$root.$emit('page-templates-create')">
        <v-icon
          size="18">
          fa-plus
        </v-icon>
        <span
          v-if="!$root.isMobile"
          class="text-truncate text-none ms-2">
          {{ $t('pageTemplates.add') }}
        </span>
      </v-btn>
    </template>
  </application-toolbar>
</template>
<script>
export default {
  props: {
    creating: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    pageTemplates: null,
  }),
};
</script>
