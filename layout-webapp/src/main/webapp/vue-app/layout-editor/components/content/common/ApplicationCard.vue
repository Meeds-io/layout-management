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
  <v-card :id="applicationId" flat>
    <div class="d-flex flex-no-wrap">
      <v-avatar
        class="ApplicationCardImage mx-1 my-auto"
        size="45"
        tile>
        <i :class="applicationIcon"></i>
      </v-avatar>
      <div class="flex-grow-1 ApplicationCardBody text-truncate">
        <div
          :title="applicationName"
          class="text-truncate subtitle-1 px-1 pt-4 text-color ApplicationCardTitle">
          {{ applicationName }}
        </div>
        <v-card-subtitle
          :title="applicationDescription"
          class="text-truncate subtitle-2 px-1 pt-0 text-sub-title ApplicationCardDescription">
          {{ applicationDescription || applicationName }}
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
  }),
  computed: {
    applicationId() {
      return this.application?.contentId?.split?.('/')?.[1];
    },
    applicationName() {
      return this.application?.displayName || this.application?.applicationName;
    },
    applicationDescription() {
      return this.application?.description;
    },
    applicationIcon() {
      if (!this.applicationId) {
        return '';
      }
      const iconSuffix = `${this.applicationId.charAt(0).toUpperCase()}${this.applicationId.substring(1)}`;
      return `uiIconApp${iconSuffix} uiIconDefaultApp`;
    },
  },
};
</script>

