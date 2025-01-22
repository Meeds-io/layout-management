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
    class="position-relative overflow-hidden d-flex flex-column flex-grow-1 my-5 mx-auto"
    color="transparent"
    max-width="1280px"
    min-width="calc(100% - 40px)"
    min-height="225px"
    flat>
    <v-card
      class="d-flex align-center justify-center text-title d-flex position-absolute z-index-one t-0 fa-rotate-315 ms-n12 mt-12"
      color="primary"
      min-height="30"
      min-width="220"
      dark
      flat>
      {{ $t('layout.editSite.portalPage') }}
    </v-card>
    <v-card
      class="full-width full-height flex-grow-1"
      flat />
  </v-card>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
  },
  computed: {
    id() {
      return this.container.id || this.storageId;
    },
    storageId() {
      return this.container?.storageId;
    },
  },
};
</script>