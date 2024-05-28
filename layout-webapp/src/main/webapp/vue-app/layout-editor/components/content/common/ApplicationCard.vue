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
  <v-card :id="portletName" flat>
    <div class="d-flex flex-no-wrap full-width">
      <v-avatar
        class="ApplicationCardImage mx-1 my-auto"
        size="45"
        tile>
        <v-img
          :src="imgSrc"
          :alt="portletName"
          max-height="45"
          max-width="45"
          @error="displayDefault = true" />
      </v-avatar>
      <div class="d-flex flex-column flex-grow-1 ApplicationCardBody text-truncate ms-1 my-auto">
        <div
          :title="name"
          class="text-truncate subtitle-1 px-1 pt-2 text-color ApplicationCardTitle">
          {{ name }}
        </div>
        <v-card-subtitle
          :title="description"
          class="text-truncate subtitle-2 px-1 pt-0 pb-2 text-sub-title ApplicationCardDescription">
          {{ description || name }}
        </v-card-subtitle>
      </div>
      <div class="ApplicationCardAction">
        <v-btn
          text
          height="100%"
          width="100%px"
          class="primary--text"
          @click="$emit('add')">
          <v-icon size="36">fa-plus</v-icon>
        </v-btn>
      </div>
    </div>
  </v-card>
</template>
<script>
export default {
  props: {
    application: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    displayActionMenu: false,
    displayDefault: false,
    defaultImageSrc: '/sites/images/application/DefaultPortlet.png',
  }),
  computed: {
    imgSrc() {
      return this.displayDefault && this.defaultImageSrc || `/${this.applicationName}/skin/DefaultSkin/portletIcons/${this.portletName}.png`;
    },
    applicationName() {
      return this.application?.applicationName || this.application?.contentId?.split?.('/')?.[0];
    },
    portletName() {
      return this.application?.portletName || this.application?.contentId?.split?.('/')?.[1];
    },
    name() {
      return this.application?.name;
    },
    description() {
      return this.application?.description;
    },
  },
};
</script>

