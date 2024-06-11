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
  <div>
    <div class="d-flex align-center mt-4">
      <div class="subtitle-1 font-weight-bold me-auto">
        {{ $t('layout.background') }}
      </div>
      <v-switch
        v-model="enableBackground"
        class="ms-auto my-auto me-n2" />
    </div>
    <v-list-item
      v-if="enableBackground"
      class="pa-0"
      dense>
      <v-list-item-content class="my-auto">
        {{ $t('layout.color') }}
      </v-list-item-content>
      <v-list-item-action class="my-auto me-0 ms-auto">
        <layout-editor-color-picker
          v-model="container.backgroundColor"
          class="my-auto" />
      </v-list-item-action>
    </v-list-item>

    <v-list-item
      v-if="enableBackground"
      class="pa-0"
      dense>
      <v-list-item-content class="my-auto">
        {{ $t('layout.image') }}
      </v-list-item-content>
      <v-list-item-action class="my-auto me-0 ms-auto">
        <layout-editor-background-image-attachment
          v-model="container.backgroundImage"
          ref="backgroundImage"
          :storage-id="`${$root.pageId}_${container.storageId}`"
          :immediate-save="immediateSave"
          class="my-auto" />
      </v-list-item-action>
    </v-list-item>
    <v-radio-group
      v-if="container.backgroundImage"
      v-model="backgroundImageStyle"
      class="my-auto text-no-wrap"
      mandatory>
      <v-radio
        value="cover"
        class="mx-0">
        <template #label>
          <span class="text-font-size text-color">{{ $t('layout.imageSizeCover') }}</span>
        </template>
      </v-radio>
      <v-radio
        value="contain"
        class="mx-0">
        <template #label>
          <span class="text-font-size text-color">{{ $t('layout.imageSizeContain') }}</span>
        </template>
      </v-radio>
      <v-radio
        value="repeat"
        class="mx-0">
        <template #label>
          <span class="text-font-size text-color">{{ $t('layout.imageRepeat') }}</span>
        </template>
      </v-radio>
      <v-radio
        value="no-repeat"
        class="mx-0">
        <template #label>
          <span class="text-font-size text-color">{{ $t('layout.imageNoRepeat') }}</span>
        </template>
      </v-radio>
    </v-radio-group>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: Object,
      default: null,
    },
    immediateSave: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    container: null,
    enableBackground: false,
    backgroundImageStyle: null,
    initialized: false,
  }),
  computed: {
    cssStyle() {
      const style = {};
      if (this.container.backgroundColor) {
        style['background-color'] = this.container.backgroundColor;
      }
      if (this.container.backgroundImage) {
        if (this.container.backgroundEffect) {
          style['background-image'] = `${this.container.backgroundEffect},url(${this.container.backgroundImage})`;
        } else {
          style['background-image'] = `url(${this.container.backgroundImage})`;
        }
        if (this.container.backgroundRepeat) {
          style['background-repeat'] = this.container.backgroundRepeat;
        }
        if (this.container.backgroundSize) {
          style['background-size'] = this.container.backgroundSize;
        }
      }
      return style;
    },
  },
  watch: {
    container: {
      deep: true,
      handler() {
        if (this.container) {
          this.$emit('input', this.container);
        }
      },
    },
    enableBackground() {
      if (this.initialized) {
        this.container.backgroundColor = this.enableBackground && '#FFFFFFFF' || null;
        this.container.backgroundImage = null;
        this.backgroundImageStyle = null;
      }
    },
    backgroundImageStyle() {
      if (this.initialized) {
        if (this.backgroundImageStyle === 'cover' || this.backgroundImageStyle === 'contain') {
          this.container.backgroundSize = this.backgroundImageStyle;
          this.container.backgroundRepeat = null;
        } else {
          this.container.backgroundSize = null;
          this.container.backgroundRepeat = this.backgroundImageStyle;
        }
      }
    },
  },
  created() {
    this.container = this.value;
    if (this.container.backgroundSize || this.container.backgroundRepeat) {
      if (this.container.backgroundSize === 'cover'
          || this.container.backgroundSize === 'contain') {
        this.backgroundImageStyle = this.container.backgroundSize;
      } else {
        this.backgroundImageStyle = this.container.backgroundRepeat;
      }
    }
    this.enableBackground = !!this.container.backgroundColor || !!this.container.backgroundImage;
    if (this.enableBackground && !this.container.backgroundColor) {
      this.container.backgroundColor = '#FFFFFFFF';
    }
    this.$nextTick().then(() => this.initialized = true);
  },
  methods: {
    async apply() {
      if (this.enableBackground && this.$refs.backgroundImage) {
        const backgroundImage = await this.$refs.backgroundImage.save();
        if (backgroundImage) {
          this.container.backgroundImage = backgroundImage;
        }
      } else {
        this.container.backgroundImage = null;
      }
      return this.container;
    },
  },
};
</script>