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
  <exo-drawer
    ref="drawer"
    id="editPagePropertiesDrawer"
    v-model="drawer"
    right
    disable-pull-to-refresh>
    <template #title>
      {{ $root.isSiteLayout && $t('layout.editSitePagesProperties') || $t('layout.editPageProperties') }}
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
        <div class="d-flex flex-column mt-4">
          <div class="text-header mb-2">
            {{ $t('layout.updateSitePagesWidth') }}
          </div>
          <v-radio-group
            v-model="width"
            class="ms-0 mt-0 me-auto full-width text-no-wrap"
            mandatory>
            <v-radio
              :value="customWidth"
              class="mx-0">
              <template #label>
                <div class="d-flex full-width align-center">
                  <span class="text-font-size">{{ $t('layout.fixedWidthCustom') }}</span>
                  <number-input
                    v-model="width"
                    v-if="width === customWidth"
                    :label="$t('layout.fixedWidth')"
                    :min="minWidth"
                    :max="maxWidth"
                    :step="10"
                    class="ms-auto my-n2"
                    editable />
                </div>
              </template>
            </v-radio>
            <v-radio
              value="100%"
              class="mx-0">
              <template #label>
                <span class="text-font-size">{{ $t('layout.fullWindow') }}</span>
              </template>
            </v-radio>
          </v-radio-group>
        </div>
        <layout-editor-section-margin-input
          v-model="parentContainer"
          :max="80"
          :min="0"
          left
          right
          top
          bottom>
          <template #title>
            <div class="text-header mt-4">
              {{ $t('layout.pageMargins') }}
            </div>
          </template>
        </layout-editor-section-margin-input>
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
    originalParentContainer: null,
    parentContainer: null,
    appBackgroundProperties: null,
    fullWindow: false,
    width: 1320,
    minWidth: 300,
    maxWidth: 5000,
    drawer: false,
    saving: false,
    defaultMarginTop: 20,
    defaultMarginRight: 20,
    defaultMarginBottom: 20,
    defaultMarginLeft: 20,
  }),
  computed: {
    cssStyle() {
      return this.$applicationUtils.getStyle(this.parentContainer, {
        onlyBackgroundStyle: true,
      });
    },
    customWidth() {
      return this.width === '100%' ? 0 : this.width;
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
    open(parentContainer) {
      this.originalParentContainer = parentContainer;
      this.parentContainer = Object.assign({...this.$layoutUtils.containerModel}, JSON.parse(JSON.stringify(parentContainer)));
      if (this.parentContainer.marginTop !== 0 && !this.parentContainer.marginTop) {
        this.parentContainer.marginTop = this.defaultMarginTop;
      }
      if (this.parentContainer.marginRight !== 0 && !this.parentContainer.marginRight) {
        this.parentContainer.marginRight = this.defaultMarginRight;
      }
      if (this.parentContainer.marginBottom !== 0 && !this.parentContainer.marginBottom) {
        this.parentContainer.marginBottom = this.defaultMarginBottom;
      }
      if (this.parentContainer.marginLeft !== 0 && !this.parentContainer.marginLeft) {
        this.parentContainer.marginLeft = this.defaultMarginLeft;
      }
      this.width = (this.parentContainer.width === 'fullWindow' ? '100%' : this.parentContainer.width)
        || (this.parentContainer.width === 'singlePageApplication' ? 1320 : this.parentContainer.width)
        || (!!document.body.style.getPropertyValue('--allPagesWidth') && '100%')
        || 1320;
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
        await this.$refs.backgroundInput.apply();
        await this.$refs.appBackgroundInput.apply();
        Object.assign(this.originalParentContainer, this.parentContainer);
        this.$set(this.originalParentContainer, 'appBackgroundColor', this.appBackgroundProperties.backgroundColor);
        this.$set(this.originalParentContainer, 'appBackgroundImage', this.appBackgroundProperties.backgroundImage);
        this.$set(this.originalParentContainer, 'appBackgroundEffect', this.appBackgroundProperties.backgroundEffect);
        this.$set(this.originalParentContainer, 'appBackgroundRepeat', this.appBackgroundProperties.backgroundRepeat);
        this.$set(this.originalParentContainer, 'appBackgroundSize', this.appBackgroundProperties.backgroundSize);
        this.$set(this.originalParentContainer, 'width', this.width);

        if (this.parentContainer.marginTop === this.defaultMarginTop) {
          this.parentContainer.marginTop = null;
        }
        if (this.parentContainer.marginRight === this.defaultMarginRight) {
          this.parentContainer.marginRight = null;
        }
        if (this.parentContainer.marginBottom === this.defaultMarginBottom) {
          this.parentContainer.marginBottom = null;
        }
        if (this.parentContainer.marginLeft === this.defaultMarginLeft) {
          this.parentContainer.marginLeft = null;
        }

        this.$layoutUtils.applyContainerStyle(this.originalParentContainer, this.originalParentContainer);
        this.$root.pageFullWindow = this.fullWindow;
        this.$root.$emit('layout-editor-page-design-updated');
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