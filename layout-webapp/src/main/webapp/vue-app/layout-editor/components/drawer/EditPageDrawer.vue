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
          <div class="text-title me-auto mb-2">
            {{ $t('layout.globalPageDesign') }}
          </div>
        </div>
        <div class="d-flex align-center mt-4">
          <div class="text-header me-auto mb-2">
            {{ $t('layout.fullWindow') }}
          </div>
          <v-switch
            v-model="fullWindow"
            class="ms-auto my-auto me-n2" />
        </div>
        <layout-editor-background-input
          v-if="parentContainer"
          ref="backgroundInput"
          v-model="parentContainer"
          :default-background-color="defaultBackgroundColor"
          class="mt-2"
          page-style>
          <template #title>
            <div class="text-header me-auto mb-2">
              {{ $t('layout.globalPageBackground') }}
            </div>
          </template>
        </layout-editor-background-input>

        <div class="d-flex align-center mt-4">
          <div class="text-title me-auto mb-2">
            {{ $t('layout.applicationStyling') }}
          </div>
        </div>
        <layout-editor-border-input
          ref="appBorderInput"
          v-model="parentContainer"
          class="mt-4"
          page-style />
        <layout-editor-border-radius-input
          ref="appBorderRadiusInput"
          v-model="parentContainer"
          class="mt-4"
          page-style />
        <layout-editor-background-input
          ref="appBackgroundInput"
          v-if="appBackgroundProperties"
          v-model="appBackgroundProperties"
          class="mt-4"
          page-style />
        <layout-editor-text-input
          ref="appTextInput"
          v-model="parentContainer"
          class="mt-4"
          page-style />
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
    defaultBackgroundColor: '#F2F2F2FF',
    layout: null,
    parentContainer: null,
    appBackgroundProperties: null,
    fullWindow: false,
    drawer: false,
    saving: false,
  }),
  computed: {
    cssStyle() {
      return this.$applicationUtils.getStyle(this.parentContainer, {
        onlyBackgroundStyle: true,
      });
    },
  },
  watch: {
    parentContainer() {
      if (this.drawer) {
        this.optionsModified = true;
      }
    },
  },
  created() {
    this.$root.$on('layout-page-properties-open', this.open);
    if (document.body.computedStyleMap().get('--allPagesLightGrey')) {
      this.defaultBackgroundColor = document.body.computedStyleMap().get('--allPagesLightGrey')[0] || this.defaultBackgroundColor;
    }
  },
  beforeDestroy() {
    this.$root.$off('layout-page-properties-open', this.open);
  },
  methods: {
    open() {
      const parentContainer = this.$layoutUtils.getParentContainer(this.$root.layout);
      this.parentContainer = Object.assign({...this.$layoutUtils.containerModel}, JSON.parse(JSON.stringify(parentContainer)));
      this.fullWindow = this.parentContainer.width === 'fullWindow' || !!document.body.style.getPropertyValue('--allPagesWidth');
      this.appBackgroundProperties = {
        storageId: 0,
        backgroundColor: this.parentContainer.appBackgroundColor || null,
        backgroundImage: this.parentContainer.appBackgroundImage || null,
        backgroundEffect: this.parentContainer.appBackgroundEffect || null,
        backgroundRepeat: this.parentContainer.appBackgroundRepeat || null,
        backgroundSize: this.parentContainer.appBackgroundSize || null,
      };
      this.$refs.drawer.open();
    },
    async apply() {
      this.saving = true;
      try {
        if (this.fullWindow) {
          this.parentContainer.width = 'fullWindow';
        } else if (document.body.style.getPropertyValue('--allPagesWidth')) {
          this.parentContainer.width = 'singlePageApplication';
        }
        await this.$refs.backgroundInput.apply();
        await this.$refs.appBackgroundInput.apply();
        this.parentContainer.appBackgroundColor = this.appBackgroundProperties.backgroundColor;
        this.parentContainer.appBackgroundImage = this.appBackgroundProperties.backgroundImage;
        this.parentContainer.appBackgroundEffect = this.appBackgroundProperties.backgroundEffect;
        this.parentContainer.appBackgroundRepeat = this.appBackgroundProperties.backgroundRepeat;
        this.parentContainer.appBackgroundSize = this.appBackgroundProperties.backgroundSize;
        const parentContainer = this.$layoutUtils.getParentContainer(this.$root.layout);
        this.parentContainer.children = parentContainer.children;
        Object.assign(parentContainer, this.parentContainer);
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