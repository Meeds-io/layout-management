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
  <div>
    <div class="d-flex pb-2">
      <v-list-item class="pa-0" dense>
        <v-list-item-content class="pa-0">
          <v-list-item-title />
          {{ $t('siteManagement.label.banner') }}
          <v-list-item-subtitle>
            {{ $t('siteManagement.label.caption') }}
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
      <v-spacer />
      <v-tooltip bottom>
        <template #activator="{on, attrs}">
          <div
            v-on="on"
            v-bind="attrs">
            <v-file-input
              id="imageFileInput"
              ref="uploadInput"
              accept="image/*"
              prepend-icon="fas fa-camera z-index-two rounded-circle primary-border-color white py-1 ms-3"
              class="file-selector pa-0 ma-0"
              rounded
              clearable
              dense
              @change="uploadFile" />
          </div>
        </template>
        <span>{{ $t('sites.uploadPreviewTitle') }}</span>
      </v-tooltip>
    </div>
    <v-img
      :lazy-src="illustrationSrc"
      :src="illustrationSrc"
      class="border-radius"
      transition="none"
      eager />
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    bannerData: {
      type: String,
      default: null,
    },
    bannerUrl: {
      type: String,
      default: null,
    },
  },
  data: () => ({
    sendingImage: false,
    avatarData: null,
  }),
  computed: {
    illustrationSrc() {
      return this.avatarData || this.bannerUrl || `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/sites/global/banner?isDefault=true`;
    },
  },
  watch: {
    sendingImage() {
      this.$emit('sending', this.sendingImage);
    },
    bannerData() {
      this.avatarData = this.bannerData;
    },
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      if (this.bannerData) {
        this.avatarData = this.bannerData;
      } else {
        this.avatarData = null;
      }
    },
    uploadFile(file) {
      if (file && file.size) {
        if (file.size > this.maxUploadSize) {
          this.$root.$emit('alert-message', this.$t(this.$uploadService.avatarExcceedsLimitError), 'error');
          return;
        }
        this.sendingImage = true;
        const thiss = this;
        return this.$uploadService.upload(file)
          .then(uploadId => {
            const reader = new FileReader();
            reader.onload = (e) => {
              thiss.avatarData = e.target.result;
              thiss.$forceUpdate();
            };
            reader.readAsDataURL(file);
            this.$emit('input', uploadId);
          })
          .then(() => this.$emit('refresh'))
          .catch(() => this.$root.$emit('alert-message', this.$t('layout.errorUploadingPreview'), 'error'))
          .finally(() => this.sendingImage = false);
      }
    },
  },
};
</script>