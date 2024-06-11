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
  <exo-drawer
    ref="drawer"
    id="editPagePropertiesDrawer"
    v-model="drawer"
    right
    disable-pull-to-refresh>
    <template #title>
      {{ $t('layout.editPageProperties') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        class="pa-4"
        flat>
        <v-card
          :class="fullWindow && 'px-1' || 'px-6'"
          :style="cssStyle"
          class="py-1"
          width="380px"
          flat>
          <v-img
            :src="pagePreview"
            width="100%"
            height="200"
            class="border-radius mx-auto"
            transition="none"
            eager
            cover />
        </v-card>

        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.fullWindow') }}
          </div>
          <v-switch
            v-model="fullWindow"
            class="ms-auto my-auto me-n2" />
        </div>
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
              v-model="backgroundColor"
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
              v-model="backgroundImage"
              ref="backgroundImage"
              :storage-id="`${$root.pageId}_${parentContainer.storageId}`"
              class="my-auto"
              @image-data="backgroundImage = $event" />
          </v-list-item-action>
        </v-list-item>
        <v-radio-group
          v-if="backgroundImage"
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
      </v-card>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-spacer />
        <v-btn
          class="btn"
          @click="close">
          <span class="text-none">{{ $t('layout.cancel') }}</span>
        </v-btn>
        <v-btn
          :loading="saving"
          class="btn btn-primary ms-4"
          @click="apply">
          <span class="text-none">{{ $t('layout.apply') }}</span>
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    pagePreview: '/layout/images/page-templates/DefaultPreview.webp',
    parentContainer: null,
    fullWindow: false,
    enableBackground: false,
    backgroundColor: '#FFFFFFFF',
    backgroundImageStyle: null,
    backgroundImage: null,
    backgroundEffect: null,
    backgroundRepeat: null,
    backgroundSize: null,
    drawer: false,
    saving: false,
  }),
  computed: {

    cssStyle() {
      const style = {};
      if (this.backgroundColor) {
        style['background-color'] = this.backgroundColor;
      }
      if (this.backgroundImage) {
        if (this.backgroundEffect) {
          style['background-image'] = `${this.backgroundEffect},url(${this.backgroundImage})`;
        } else {
          style['background-image'] = `url(${this.backgroundImage})`;
        }
        if (this.backgroundRepeat) {
          style['background-repeat'] = this.backgroundRepeat;
        }
        if (this.backgroundSize) {
          style['background-size'] = this.backgroundSize;
        }
      }
      return style;
    },
  },
  watch: {
    enableBackground() {
      if (this.drawer) {
        this.optionsModified = true;
        this.backgroundColor = this.enableBackground && '#FFFFFFFF' || null;
        this.backgroundImageStyle = null;
        this.backgroundImage = null;
      }
    },
    backgroundColor() {
      if (this.drawer) {
        this.optionsModified = true;
      }
    },
    backgroundImage() {
      if (this.drawer) {
        this.optionsModified = true;
      }
    },
    backgroundImageStyle() {
      if (this.drawer) {
        this.optionsModified = true;
        if (this.backgroundImageStyle === 'cover' || this.backgroundImageStyle === 'contain') {
          this.backgroundSize = this.backgroundImageStyle;
          this.backgroundRepeat = null;
        } else {
          this.backgroundSize = null;
          this.backgroundRepeat = this.backgroundImageStyle;
        }
      }
    },
    backgroundEffect() {
      if (this.drawer) {
        this.optionsModified = true;
      }
    },
  },
  created() {
    this.$root.$on('layout-page-properties-open', this.open);
  },
  beforeDestroy() {
    this.$root.$off('layout-page-properties-open', this.open);
  },
  methods: {
    open() {
      this.parentContainer = this.$layoutUtils.getParentContainer(this.$root.layout);
      this.fullWindow = this.parentContainer.width === 'fullWindow';
      this.backgroundColor = this.parentContainer.backgroundColor;
      this.backgroundImage = this.parentContainer.backgroundImage;
      this.backgroundEffect = this.parentContainer.backgroundEffect;
      this.backgroundSize = null;
      this.backgroundRepeat = null;
      if (this.parentContainer.backgroundSize || this.parentContainer.backgroundRepeat) {
        if (this.parentContainer.backgroundSize === 'cover'
            || this.parentContainer.backgroundSize === 'contain') {
          this.backgroundImageStyle = this.parentContainer.backgroundSize;
          this.backgroundSize = this.parentContainer.backgroundSize;
          this.backgroundRepeat = null;
        } else {
          this.backgroundImageStyle = this.parentContainer.backgroundRepeat;
          this.backgroundRepeat = this.parentContainer.backgroundRepeat;
          this.backgroundSize = null;
        }
      }
      this.enableBackground = !!this.backgroundColor || !!this.backgroundImage;
      if (this.enableBackground && !this.backgroundColor) {
        this.backgroundColor = '#FFFFFFFF';
      }
      this.$nextTick().then(() => this.$refs.drawer.open());
    },
    async apply() {
      this.saving = true;
      try {
        if (this.enableBackground && this.$refs.backgroundImage) {
          const backgroundImage = await this.$refs.backgroundImage.save();
          if (backgroundImage) {
            this.parentContainer.backgroundImage = backgroundImage;
          }
        } else {
          this.parentContainer.backgroundImage = null;
        }
        this.parentContainer.width = this.fullWindow && 'fullWindow' || null;
        this.parentContainer.backgroundColor = this.enableBackground && this.backgroundColor || null;
        this.parentContainer.backgroundEffect = this.enableBackground && this.backgroundEffect || null;
        this.parentContainer.backgroundSize = this.enableBackground && this.backgroundSize || null;
        this.parentContainer.backgroundRepeat = this.enableBackground && this.backgroundRepeat || null;
        this.close();
      } finally {
        this.saving = false;
      }
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>