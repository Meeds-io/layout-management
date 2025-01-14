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
  <v-container class="px-0">
    <v-tooltip bottom>
      <template #activator="{ on }">
        <v-card
          elevation="0"
          height="140px"
          class="px-1"
          v-on="on">
          <v-img
            :key="key"
            :src="imageSrc"
            role="presentation" />
          <div
            class="d-flex flex-grow-1 position-absolute full-height full-width justify-center"
            :style="{ 'top': '40%' }">
            <v-btn
              v-if="imageData || !isDefault"
              class="border-color mask-color me-1"
              outlined
              icon
              small
              dark
              @click="reset">
              <v-icon size="16">mdi-delete</v-icon>
            </v-btn>
            <v-btn
              class="border-color mask-color"
              outlined
              icon
              small
              dark
              @click="openCropperDrawer">
              <v-icon size="16">fas fa-file-image</v-icon>
            </v-btn>
          </div>
        </v-card>
      </template>
      <span>{{ $t(`siteManagement.label.bannerSelector.tooltip`) }}</span>
    </v-tooltip>
    <image-crop-drawer
      ref="imageCropDrawer"
      :crop-options="imageCropperOptions"
      :max-file-size="maxUploadSizeInBytes"
      :src="imageSrc"
      max-image-width="500"
      :drawer-title="imageCropperDrawerTitle"
      @input="updateUploadId"
      @data="imageData = $event" />
  </v-container>
</template>
<script>
export default {
  props: {
    isDefault: {
      type: Boolean,
      default: false,
    },
    src: {
      type: String,
      default: null,
    },
    defaultSrc: {
      type: String,
      default: null,
    },
    maxUploadSize: {
      type: Number,
      default: () => 2,
    },
    noExpand: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    imageData: null,
    imageCropperOptions: {
      aspectRatio: 278 / 107,
      viewMode: 1,
    },
    key: `siteBanner${parseInt(Math.random() * 10000).toString()}`,
  }),
  computed: {
    imageCropperDrawerTitle() {
      return this.$t('siteManagement.label.bannerSelector.addBanner');
    },
    maxUploadSizeInBytes() {
      return this.maxUploadSize * 1024 * 1024;
    },
    imageSrc() {
      return this.imageData || this.isDefault && this.defaultSrc || this.src ;
    },
  },
  methods: {
    openCropperDrawer() {
      this.$refs.imageCropDrawer.open();
    },
    updateUploadId(uploadId) {
      this.$emit('updated', uploadId);
    },
    reset() {
      this.imageData = null;
      this.$emit('reset');
    }
  },
};
</script>