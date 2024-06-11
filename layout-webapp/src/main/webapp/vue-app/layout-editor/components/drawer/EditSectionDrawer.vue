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
    id="editSectionDrawer"
    v-model="drawer"
    right
    disable-pull-to-refresh>
    <template #title>
      {{ $t('layout.editSectionTitle', {0: index + 1}) }}
    </template>
    <template v-if="drawer && section" #content>
      <v-card class="pa-4" flat>
        <layout-editor-section-grid-editor
          v-if="sectionType === $layoutUtils.gridTemplate"
          :rows-count="section.rowsCount"
          :cols-count="section.colsCount"
          :background-image="backgroundImage"
          :background-size="backgroundSize"
          :background-repeat="backgroundRepeat"
          :background-color="backgroundColor"
          @rows-updated="rows = $event"
          @cols-updated="cols = $event" />
        <layout-editor-section-flex-editor
          v-else-if="sectionType === $layoutUtils.flexTemplate"
          :cols-count="cols"
          :background-image="backgroundImage"
          :background-size="backgroundSize"
          :background-repeat="backgroundRepeat"
          :background-effect="backgroundEffect"
          :background-color="backgroundColor"
          @cols-updated="cols = $event" />
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.backgroundColor') }}
          </div>
          <v-switch
            v-model="enableBackgroundColor"
            class="ms-auto my-auto me-n2" />
        </div>
        <v-list-item
          v-if="enableBackgroundColor"
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
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.backgroundImage') }}
          </div>
          <v-switch
            v-model="enableBackgroundImage"
            class="ms-auto my-auto me-n2" />
        </div>
        <v-list-item
          v-if="enableBackgroundImage"
          class="pa-0"
          dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.image') }}
          </v-list-item-content>
          <v-list-item-action class="my-auto me-0 ms-auto">
            <layout-editor-background-image-attachment
              v-if="section"
              v-model="backgroundImage"
              ref="backgroundImage"
              :storage-id="`${$root.pageId}_${section.storageId}`"
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
        <template v-if="isDynamicSection">
          <div class="d-flex align-center mt-4">
            <div class="subtitle-1 font-weight-bold me-auto mb-2">
              {{ $t('layout.advancedOptions') }}
            </div>
          </div>
          <div class="d-flex align-center mt-2">
            {{ $t('layout.mobileView') }}
          </div>
          <div class="d-flex flex-column justify-center">
            <v-radio-group
              v-model="mobileInColumns"
              class="my-auto text-no-wrap ms-n1 mt-2"
              mandatory>
              <v-radio
                :value="false"
                class="mx-0">
                <template #label>
                  <span class="text-font-size text-color">{{ $t('layout.listAppsInRows') }}</span>
                </template>
              </v-radio>
              <v-radio
                :value="true"
                class="mx-0">
                <template #label>
                  <span class="text-font-size text-color">{{ $t('layout.listAppsInColumns') }}</span>
                </template>
              </v-radio>
            </v-radio-group>
          </div>
          <div class="d-flex align-center mt-2">
            {{ $t('layout.scrollStickyBehavior') }}
          </div>
          <div class="d-flex flex-column justify-center">
            <v-radio-group
              v-model="stickyApplication"
              class="my-auto text-no-wrap ms-n1 mt-2"
              mandatory>
              <v-radio
                :value="false"
                class="mx-0">
                <template #label>
                  <span class="text-font-size text-color">{{ $t('layout.scrollNoSticky') }}</span>
                </template>
              </v-radio>
              <v-radio
                :value="true"
                class="mx-0">
                <template #label>
                  <span class="text-font-size text-color">{{ $t('layout.scrollStickyLastApp') }}</span>
                </template>
              </v-radio>
            </v-radio-group>
          </div>
        </template>
      </v-card>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-btn
          v-if="canRemove"
          color="error"
          outlined
          elevation="0"
          class="ignore-vuetify-classes"
          @click="removeSection">
          <span class="text-none">{{ $t('layout.delete') }}</span>
        </v-btn>
        <v-spacer />
        <v-btn
          class="btn"
          @click="close">
          <span class="text-none">{{ $t('layout.cancel') }}</span>
        </v-btn>
        <v-btn
          :disabled="!modified"
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
    originalSection: null,
    section: null,
    drawer: false,
    index: null,
    rows: 0,
    cols: 0,
    canRemove: false,
    mobileInColumns: false,
    enableBackgroundColor: false,
    enableBackgroundImage: false,
    backgroundColor: '#FFFFFFFF',
    backgroundImageStyle: null,
    backgroundImage: null,
    backgroundEffect: null,
    backgroundRepeat: null,
    backgroundSize: null,
    stickyApplication: false,
    optionsModified: false,
  }),
  computed: {
    colsCount() {
      return this.section.template === this.$layoutUtils.flexTemplate ? this.section.children.length : this.section?.colsCount;
    },
    modified() {
      return this.optionsModified || this.section?.rowsCount !== this.rows || this.colsCount !== this.cols;
    },
    sectionType() {
      return this.section?.template;
    },
    isDynamicSection() {
      return this.sectionType === this.$layoutUtils.flexTemplate;
    },
  },
  watch: {
    stickyApplication() {
      if (this.drawer) {
        if (!this.section.cssClass) {
          this.section.cssClass = '';
        }
        if (this.stickyApplication) {
          this.section.cssClass += ' layout-sticky-application';
        } else {
          this.section.cssClass = this.section.cssClass.replace(/layout-sticky-application/g, '');
        }
        this.optionsModified = true;
      }
    },
    mobileInColumns() {
      if (this.drawer) {
        if (!this.section.cssClass) {
          this.section.cssClass = '';
        }
        if (this.mobileInColumns) {
          this.section.cssClass += ' layout-mobile-columns';
        } else {
          this.section.cssClass = this.section.cssClass.replace(/layout-mobile-columns/g, '');
        }
        this.optionsModified = true;
      }
    },
    enableBackgroundColor(val) {
      if (val) {
        if (!this.backgroundColor) {
          this.backgroundColor = '#FFFFFFFF';
        }
      } else {
        this.backgroundColor = null;
      }
    },
    enableBackgroundImage() {
      if (this.drawer) {
        this.backgroundImage = null;
        this.backgroundImageStyle = 'cover';
      }
    },
    backgroundColor() {
      if (this.drawer) {
        this.optionsModified = true;
        this.section.backgroundColor = this.backgroundColor;
      }
    },
    backgroundImage() {
      if (this.drawer) {
        this.optionsModified = true;
        this.section.backgroundImage = this.backgroundImage;
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
        this.section.backgroundEffect = this.backgroundEffect;
      }
    },
    backgroundRepeat() {
      if (this.drawer) {
        this.optionsModified = true;
        this.section.backgroundRepeat = this.backgroundRepeat;
      }
    },
    backgroundSize() {
      if (this.drawer) {
        this.optionsModified = true;
        this.section.backgroundSize = this.backgroundSize;
      }
    },
  },
  methods: {
    open(section, index, length) {
      this.section = JSON.parse(JSON.stringify(section));
      this.stickyApplication = this.section.cssClass?.includes?.('layout-sticky-application');
      this.mobileInColumns = this.section.cssClass?.includes?.('layout-mobile-columns');
      this.enableBackgroundColor = !!this.section.backgroundColor;
      this.enableBackgroundImage = !!this.section.backgroundImage;
      this.backgroundColor = this.section.backgroundColor || '#FFFFFFFF';
      this.backgroundImage = this.section.backgroundImage;
      this.backgroundEffect = this.section.backgroundEffect;
      if (this.section.backgroundSize || this.section.backgroundRepeat) {
        if (this.section.backgroundSize === 'cover'
            || this.section.backgroundSize === 'contain') {
          this.backgroundImageStyle = this.section.backgroundSize;
          this.backgroundSize = this.section.backgroundSize;
          this.backgroundRepeat = null;
        } else {
          this.backgroundImageStyle = this.section.backgroundRepeat;
          this.backgroundRepeat = this.section.backgroundRepeat;
          this.backgroundSize = null;
        }
      }
      this.optionsModified = false;
      this.originalSection = JSON.parse(JSON.stringify(section));
      this.index = index;
      if (this.section.template === this.$layoutUtils.flexTemplate) {
        this.rows = 1;
        this.cols = this.section.children.length;
      } else {
        this.rows = this.section?.rowsCount;
        this.cols = this.section?.colsCount;
      }
      this.canRemove = length > 1;
      this.$nextTick().then(() => this.$refs.drawer.open());
    },
    removeSection() {
      this.close();
      this.$root.$emit('layout-remove-section', this.index);
    },
    async apply() {
      if (this.$refs.backgroundImage) {
        const backgroundImage = await this.$refs.backgroundImage.save();
        if (backgroundImage) {
          this.section.backgroundImage = backgroundImage;
        }
      } else {
        this.section.backgroundImage = null;
      }
      const section = JSON.parse(JSON.stringify(this.section));
      if (section.template === this.$layoutUtils.flexTemplate && this.section.children.length !== this.cols) {
        this.$layoutUtils.editDynamicSection(section, this.cols);
      } else if (section.template === this.$layoutUtils.gridTemplate) {
        this.$layoutUtils.editGridSection(section, this.rows, this.cols);
      }
      this.$root.$emit('layout-replace-section', this.index, section);
      this.close();
    },
    close() {
      this.$refs.drawer.close();
      this.section = null;
    },
  },
};
</script>