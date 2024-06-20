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
    id="editApplicationDrawer"
    v-model="drawer"
    allow-expand
    right>
    <template #title>
      <span :title="drawerTitle" class="text-truncate">
        {{ drawerTitle }}
      </span>
    </template>
    <template v-if="canEditPortletProperties" #titleIcons>
      <v-btn
        icon
        @click="$root.$emit('layout-editor-portlet-edit', applicationId, applicationCategoryTitle, applicationTitle)">
        <v-icon size="20">fa-edit</v-icon>
      </v-btn>
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4"
        flat>
        <div class="subtitle-1 font-weight-bold mb-2">
          {{ $t('layout.margins') }}
        </div>
        <div
          :class="marginChoice === 'same' && 'flex-row' || 'flex-column'"
          class="d-flex">
          <v-radio-group v-model="marginChoice" class="my-auto text-no-wrap ms-n1">
            <v-radio
              :label="$t('layout.sameForAllSides')"
              value="same"
              class="mx-0" />
            <v-radio
              :label="$t('layout.differentForEachSide')"
              value="different"
              class="mx-0" />
          </v-radio-group>
          <v-list-item class="pe-0 ps-7 py-0" dense>
            <v-list-item-content v-if="marginChoice === 'different'" class="my-auto">
              {{ $t('layout.top') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="marginTop"
              :diff="-20"
              :class="marginChoice === 'different' && 'my-auto' || 'mb-auto ms-auto'"
              class="me-n3" />
          </v-list-item>
          <v-list-item
            v-if="marginChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.right') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="marginRight"
              :diff="-20"
              class="my-auto me-n3" />
          </v-list-item>
          <v-list-item
            v-if="marginChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.bottom') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="marginBottom"
              :diff="-20"
              class="my-auto me-n3" />
          </v-list-item>
          <v-list-item
            v-if="marginChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.left') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="marginLeft"
              :diff="-20"
              class="my-auto me-n3" />
          </v-list-item>
        </div>
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.borderColor') }}
          </div>
          <v-switch
            v-model="enableBorderColor"
            class="ms-auto my-auto me-n2" />
        </div>
        <v-list-item
          v-if="enableBorderColor"
          class="pa-0"
          dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.color') }}
          </v-list-item-content>
          <v-list-item-action class="my-auto me-0 ms-auto">
            <layout-editor-color-picker
              v-model="borderColor"
              class="my-auto" />
          </v-list-item-action>
        </v-list-item>
        <v-list-item
          v-if="enableBorderColor"
          class="pa-0"
          dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.borderSize') }}
          </v-list-item-content>
          <v-list-item-action class="my-auto me-0 ms-auto">
            <layout-editor-number-input
              v-model="borderSize"
              :step="1"
              :min="0"
              :max="8"
              class="me-n3" />
          </v-list-item-action>
        </v-list-item>
        <v-list-item
          v-if="enableBorderColor"
          class="pa-0"
          dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.boxShadow') }}
          </v-list-item-content>
          <v-list-item-action class="my-auto me-0 ms-auto">
            <v-checkbox v-model="boxShadow" />
          </v-list-item-action>
        </v-list-item>
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.borderRadius') }}
          </div>
          <v-switch
            v-model="enableBorderRadius"
            class="ms-auto my-auto me-n2" />
        </div>
        <div
          v-if="enableBorderRadius"
          :class="radiusChoice === 'same' && 'flex-row' || 'flex-column'"
          class="d-flex">
          <v-radio-group v-model="radiusChoice" class="my-auto text-no-wrap ms-n1">
            <v-radio
              :label="$t('layout.sameForAllCorners')"
              value="same"
              class="mx-0" />
            <v-radio
              :label="$t('layout.differentForEachCorner')"
              value="different"
              class="mx-0" />
          </v-radio-group>
          <v-list-item class="pe-0 ps-7 py-0" dense>
            <v-list-item-content v-if="radiusChoice === 'different'" class="my-auto">
              {{ $t('layout.topRight') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="radiusTopRight"
              :class="radiusChoice === 'different' && 'my-auto' || 'mb-auto ms-auto'"
              class="me-n3" />
          </v-list-item>
          <v-list-item
            v-if="radiusChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.topLeft') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="radiusTopLeft"
              class="my-auto me-n3" />
          </v-list-item>
          <v-list-item
            v-if="radiusChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.bottomRight') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="radiusBottomRight"
              class="my-auto me-n3" />
          </v-list-item>
          <v-list-item
            v-if="radiusChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.bottomLeft') }}
            </v-list-item-content>
            <layout-editor-number-input
              v-model="radiusBottomLeft"
              class="my-auto me-n3" />
          </v-list-item>
        </div>
        <layout-editor-background-input
          v-if="backgroundProperties"
          ref="backgroundInput"
          v-model="backgroundProperties"
          immediate-save
          class="mt-4" />
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto mb-2">
            {{ $t('layout.advancedOptions') }}
          </div>
        </div>
        <template v-if="isDynamicSection">
          <div class="d-flex align-center ms-n1">
            <v-checkbox
              v-model="fixedHeight"
              :label="$t('layout.fixedHeight')"
              on-icon="fa-check-square"
              off-icon="far fa-square"
              class="my-0 ml-n2px" />
          </div>
          <div v-if="fixedHeight" class="d-flex flex-column justify-center">
            <v-radio-group
              v-model="height"
              class="my-auto text-no-wrap ms-7"
              mandatory>
              <v-radio
                value="150px"
                class="mx-0">
                <template #label>
                  <span class="text-font-size">{{ $t('layout.fixedHeight150') }}</span>
                </template>
              </v-radio>
              <v-radio
                value="300px"
                class="mx-0">
                <template #label>
                  <span class="text-font-size">{{ $t('layout.fixedHeight300') }}</span>
                </template>
              </v-radio>
              <v-radio
                value="500px"
                class="mx-0">
                <template #label>
                  <span class="text-font-size">{{ $t('layout.fixedHeight500') }}</span>
                </template>
              </v-radio>
              <v-radio
                :value="customHeight"
                class="mx-0">
                <template #label>
                  <div class="d-flex full-width align-center">
                    <span class="text-font-size">{{ $t('layout.fixedHeightCustom') }}</span>
                    <layout-editor-number-input
                      v-model="height"
                      v-if="height === customHeight"
                      :label="$t('layout.fixedHeight')"
                      :min="minHeight"
                      :max="maxHeight"
                      :step="10"
                      class="ms-auto my-n2"
                      editable
                      @valid="invalidCustomHeight = !$event" />
                  </div>
                </template>
              </v-radio>
            </v-radio-group>
            <span v-if="invalidCustomHeight" class="error-color mt-n1 ms-8">{{ $t('layout.invalidFixedHeight', {
              0: minHeightFormatted,
              1: maxHeightFormatted,
            }) }}</span>
          </div>
        </template>
        <div class="d-flex align-center ms-n1">
          <v-checkbox
            v-model="hiddenOnMobile"
            :label="$t('layout.hiddenOnMobile')"
            on-icon="fa-check-square"
            off-icon="far fa-square"
            class="my-0 ml-n2px" />
        </div>
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    initialized: false,
    fixedHeight: false,
    customHeightValue: false,
    height: null,
    minHeight: 100,
    maxHeight: 1000,
    invalidCustomHeight: false,
    hiddenOnMobile: false,
    section: null,
    container: null,
    backgroundProperties: null,
    marginChoice: 'same',
    marginTop: 20,
    marginRight: 20,
    marginBottom: 20,
    marginLeft: 20,
    enableBorderRadius: true,
    radiusChoice: 'same',
    radiusTopRight: null,
    radiusTopLeft: null,
    radiusBottomRight: null,
    radiusBottomLeft: null,
    enableBorderColor: true,
    borderColor: '#FFFFFF',
    borderSize: 1,
    boxShadow: false,
    applicationCategoryTitle: null,
    applicationTitle: null,
  }),
  computed: {
    applicationId() {
      return this.container?.children?.[0]?.storageId || this.container?.storageId;
    },
    applicationContentId() {
      return this.container?.children?.[0]?.contentId || this.container?.contentId;
    },
    application() {
      return this.$root.portletInstances?.find?.(a => a?.contentId === this.applicationContentId);
    },
    applicationCategory() {
      return this.applicationTitle && this.$root.portletInstanceCategories?.find?.(c => c?.applications?.find?.(a => a?.name === this.applicationTitle));
    },
    supportedModes() {
      return this.application?.supportedModes || [];
    },
    canEditPortletProperties() {
      return this.supportedModes.find(m => m === 'edit');
    },
    sectionId() {
      return this.section?.storageId;
    },
    isDynamicSection() {
      return this.section?.template === this.$layoutUtils.flexTemplate;
    },
    customHeight() {
      return (!this.height || this.height === '150px' || this.height === '300px' || this.height === '500px') ? 400 : this.height;
    },
    drawerTitle() {
      return this.applicationCategoryTitle?.length && this.$t('layout.editApplicationTitle', {
        0: this.applicationTitle,
        1: this.applicationCategoryTitle,
      }) || this.$t('layout.editApplicationTitleNoCategory', {
        0: this.applicationTitle,
      });
    },
    minHeightFormatted() {
      if (this.minHeight === 0 || this.minHeight) {
        return new Intl.NumberFormat(eXo.env.portal.language, {
          style: 'decimal',
          minimumFractionDigits: 0,
          maximumFractionDigits: 0,
        }).format(this.minHeight);
      }
      return null;
    },
    maxHeightFormatted() {
      return new Intl.NumberFormat(eXo.env.portal.language, {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(this.maxHeight);
    },
    styleClasses() {
      return this.drawer && this.sectionId && {
        marginTop: this.marginTop,
        marginRight: this.marginRight,
        marginBottom: this.marginBottom,
        marginLeft: this.marginLeft,
        radiusTopRight: this.radiusTopRight,
        radiusTopLeft: this.radiusTopLeft,
        radiusBottomRight: this.radiusBottomRight,
        radiusBottomLeft: this.radiusBottomLeft,
        borderColor: this.borderColor,
        borderSize: this.borderSize || 0,
        boxShadow: this.boxShadow && 'true' || null,
        ...this.backgroundProperties,
        hiddenOnMobile: this.hiddenOnMobile,
      } || null;
    },
  },
  watch: {
    marginTop() {
      if (this.marginChoice === 'same') {
        this.marginRight = this.marginTop;
        this.marginBottom = this.marginTop;
        this.marginLeft = this.marginTop;
      }
    },
    radiusTopRight() {
      if (this.radiusChoice === 'same') {
        this.radiusTopLeft = this.radiusTopRight;
        this.radiusBottomRight = this.radiusTopRight;
        this.radiusBottomLeft = this.radiusTopRight;
      }
    },
    enableBorderColor(val) {
      if (val) {
        if (!this.borderColor) {
          this.borderColor = '#FFFFFF';
          this.borderSize = 1;
        }
      } else {
        this.boxShadow = null;
        this.borderColor = null;
        this.borderSize = 0;
      }
    },
    enableBorderRadius(val) {
      if (val) {
        if (this.radiusTopRight !== 0 && !this.radiusTopRight) {
          const defaultBorderRadius = parseInt(this.$root.branding?.themeStyle?.borderRadius?.replace?.('px', '') || '4');
          this.radiusTopRight = defaultBorderRadius;
          this.radiusTopLeft = defaultBorderRadius;
          this.radiusBottomRight = defaultBorderRadius;
          this.radiusBottomLeft = defaultBorderRadius;
        }
      } else {
        this.radiusTopRight = null;
        this.radiusTopLeft = null;
        this.radiusBottomRight = null;
        this.radiusBottomLeft = null;
      }
    },
    radiusChoice() {
      if (this.radiusChoice === 'same') {
        this.radiusTopLeft = this.radiusTopRight;
        this.radiusBottomRight = this.radiusTopRight;
        this.radiusBottomLeft = this.radiusTopRight;
      }
    },
    marginChoice() {
      if (this.marginChoice === 'same') {
        this.marginRight = this.marginTop;
        this.marginBottom = this.marginTop;
        this.marginLeft = this.marginTop;
      }
    },
    styleClasses(value, oldVal) {
      if (value && oldVal) {
        this.$root.$emit('layout-section-history-add', this.sectionId);
        this.$layoutUtils.applyContainerStyle(this.container, value);
        this.$root.$emit('layout-section-application-update-style', this.container);
      } else if (value && !oldVal) {
        this.initialized = true;
      }
    },
    fixedHeight(value) {
      if (value) {
        this.height = this.height || '150px';
      } else {
        this.height = null;
      }
    },
    height(value) {
      if (this.initialized) {
        this.$root.$emit('layout-section-history-add', this.sectionId);
        this.container.height = value;
        this.$root.$emit('layout-section-application-update-style', this.container);
      }
    },
  },
  methods: {
    open(section, container, applicationCategoryTitle, applicationTitle) {
      this.initialized = false;

      this.section = section;
      this.container = container;
      this.section = section;
      this.height = container.height;
      this.hiddenOnMobile = container.cssClass?.includes?.('hidden-sm-and-down') || false;
      this.fixedHeight = !!this.height;
      this.applicationCategoryTitle = applicationCategoryTitle;
      this.applicationTitle = applicationTitle;
      this.$layoutUtils.parseContainerStyle(this.container, this.styleClasses);

      this.marginTop = this.container.marginTop || 0;
      this.marginRight = this.container.marginRight || 0;
      this.marginBottom = this.container.marginBottom || 0;
      this.marginLeft = this.container.marginLeft || 0;

      this.radiusTopRight = this.container.radiusTopRight;
      this.radiusTopLeft = this.container.radiusTopLeft;
      this.radiusBottomRight = this.container.radiusBottomRight;
      this.radiusBottomLeft = this.container.radiusBottomLeft;
      this.enableBorderRadius = this.radiusBottomLeft === 0 || !!this.radiusBottomLeft;

      this.borderColor = this.container.borderColor;
      this.borderSize = this.container.borderSize || 0;
      this.boxShadow = this.container.boxShadow === 'true';
      this.enableBorderColor = !!this.borderColor;

      this.backgroundProperties = {
        storageId: this.container.storageId,
        backgroundColor: this.container.backgroundColor || null,
        backgroundImage: this.container.backgroundImage || null,
        backgroundEffect: this.container.backgroundEffect || null,
        backgroundRepeat: this.container.backgroundRepeat || null,
        backgroundSize: this.container.backgroundSize || null,
      };

      this.marginChoice = this.marginTop === this.marginRight
        && this.marginRight === this.marginLeft
        && this.marginLeft === this.marginBottom ? 'same' : 'different';
      this.radiusChoice = this.radiusTopRight === this.radiusTopLeft
        && this.radiusBottomRight === this.radiusTopLeft
        && this.radiusTopLeft === this.radiusBottomLeft
        && this.radiusBottomLeft === this.radiusTopRight ? 'same' : 'different';

      this.$nextTick(() => this.$refs.drawer.open());
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>