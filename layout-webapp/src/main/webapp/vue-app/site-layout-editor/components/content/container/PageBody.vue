<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io

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
    :id="id"
    :data-storage-id="storageId"
    class="position-relative page-content d-flex flex-column flex-shrink-1 z-index-zero"
    color="transparent"
    flat>
    <v-card
      class="flex-grow-1 overflow-hidden mx-auto"
      width="100%"
      flat>
      <v-card
        :class="$vuetify.rtl && 'r-0' || 'l-0'"
        class="d-flex align-center justify-center text-title d-flex position-absolute z-index-one t-0 fa-rotate-315 ms-n12 mt-12"
        color="primary"
        min-height="30"
        min-width="220"
        dark
        flat>
        {{ $t('layout.editSite.portalPage') }}
      </v-card>
    </v-card>
  </v-card>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    parentId: {
      type: String,
      default: null,
    },
    index: {
      type: Number,
      default: () => 0,
    },
    length: {
      type: Number,
      default: () => 0,
    },
  },
  computed: {
    storageId() {
      return this.container.storageId;
    },
    id() {
      return this.container.id || this.storageId;
    },
  },
};
</script>