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
    id="editSiteSectionsDrawer"
    v-model="drawer"
    right>
    <template #title>
      {{ title }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4 d-flex flex-column"
        flat>
        <div class="text-header mb-2">
          {{ $t('layout.editSiteSidebarSection.label.setDisplay') }}
        </div>
        <div class="d-flex align-center mb-2">
          <div class="me-auto">
            {{ $t('layout.editSiteSidebarSection.label.sectionHeight') }}
          </div>
          <number-input
            v-model="height"
            :min="0"
            :max="1000"
            :step="10"
            class="ms-auto my-n2"
            editable />
        </div>
        <div class="d-flex align-center ms-n1 mb-4">
          <v-checkbox
            v-model="hiddenOnMobile"
            :label="$t('layout.sectionHiddenOnMobile')"
            on-icon="fa-check-square"
            off-icon="far fa-square"
            class="my-0 ml-n2px" />
        </div>
        <div class="d-flex align-center">
          <div class="flex-grow-0 flex-shrink-0 align-start pb-3">
            <span class="subtitle-1 text-color">{{ $t('layout.columnsCount') }}</span>
          </div>
          <v-card
            class="flex-grow-1 flex-shrink-1 align-end ms-auto"
            max-width="80%"
            flat>
            <v-slider
              v-model="cols"
              :thumb-size="24"
              :min="1"
              :max="64"
              thumb-label="always">
              <template #prepend>
                <v-btn
                  :disabled="cols <= 1"
                  class="me-n2 mt-n1"
                  icon
                  fab
                  x-small
                  @click="cols--">
                  <v-icon class="icon-default-color pt-2px">fa-minus</v-icon>
                </v-btn>
              </template>
              <template #append>
                <v-btn
                  :disabled="cols >= 60"
                  class="ms-n2 mt-n1"
                  fab
                  icon
                  x-small
                  @click="cols++">
                  <v-icon class="icon-default-color pt-2px">fa-plus</v-icon>
                </v-btn>
              </template>
            </v-slider>
          </v-card>
        </div>
        <v-card
          class="d-flex border-color elevation-2 mx-auto mb-4"
          max-width="335px"
          width="100%"
          flat>
          <div class="d-flex full-width ps-1 py-1">
            <v-card
              v-for="i in cols"
              :key="i"
              :height="height"
              min-height="50"
              max-height="150"
              class="flex-grow-1 flex-shrink-1 grey-background no-border-radius me-1"
              flat />
          </div>
        </v-card>
        <div class="text-header mb-4">
          {{ $t('layout.editSiteSidebarSection.label.updateStyle') }}
        </div>
        <div v-if="isTopContainer" class="d-flex align-center mb-2">
          <div class="me-auto mb-2">
            {{ $t('layout.fixPositionWhenScrolling') }}
          </div>
          <v-switch
            v-model="stickySection"
            class="ms-auto my-auto me-n2" />
        </div>
        <layout-editor-background-input
          ref="backgroundInput"
          v-model="container"
          :scroll-color="stickySection"
          class="mb-2"
          text-bold />
        <layout-editor-text-input
          ref="textInput"
          v-model="container"
          class="mb-2"
          text-bold />
        <layout-editor-section-margin-input
          ref="marginInput"
          v-model="container"
          :max="60"
          :min="-60"
          class="mb-2"
          text-bold
          right
          left />
      </v-card>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-btn
          :disabled="saving"
          :title="$t('layout.deleteSection')"
          color="error"
          outlined
          @click="removeSection">
          {{ $t('layout.delete') }}
        </v-btn>
        <v-spacer />
        <v-btn
          :disabled="saving"
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
    drawer: false,
    stickySection: false,
    container: null,
    index: null,
    height: null,
    cols: null,
    hiddenOnMobile: false,
    defaultHeight: 57,
    saving: false,
  }),
  computed: {
    isTopContainer() {
      return this.container?.cssClass?.includes?.('layout-banner-top-section');
    },
    title() {
      return this.isTopContainer ? this.$t('layout.editSiteBannerSection.label.editSiteTopbarSection') : this.$t('layout.editSiteBannerSection.label.editSiteBottomSection');
    },
  },
  created() {
    this.$root.$on('layout-site-banner-section-open', this.open);
  },
  beforeDestroy() {
    this.$root.$off('layout-site-banner-section-open', this.open);
  },
  methods: {
    open(container) {
      this.index = this.$root.middleContainer.children.findIndex(c => c.storageId === container.storageId);
      this.container = JSON.parse(JSON.stringify(container));
      this.height = this.container.height || this.defaultHeight;
      this.cols = this.container.children.length;
      this.stickySection = this.container.cssClass?.includes?.('layout-sticky-section');
      this.hiddenOnMobile = this.container.cssClass?.includes?.('hidden-sm-and-down');
      this.$refs.drawer.open();
    },
    removeSection() {
      this.$root.$emit('layout-section-history-add');
      this.$root.middleContainer.children.splice(this.index, 1);
      this.close();
    },
    async apply() {
      this.saving = true;
      try {
        this.$root.$emit('layout-section-history-add');
        this.container.height = this.height;
        await this.$refs.backgroundInput.apply();
        if (this.stickySection && !this.container.cssClass?.includes?.('layout-sticky-section')) {
          this.container.cssClass = this.container.cssClass ? `${this.container.cssClass} layout-sticky-section` : 'layout-sticky-section';
        } else if (!this.stickySection && this.container.cssClass?.includes?.('layout-sticky-section')) {
          this.container.cssClass = this.container.cssClass.replace('layout-sticky-section', '');
        }
        if (this.container.children.length < this.cols) {
          for (let i = this.container.children.length; i < this.cols; i++) {
            this.container.children.push(this.$layoutUtils.newContainer(this.$layoutUtils.bannerCellTemplate));
          }
        } else if (this.container.children.length > this.cols) {
          this.container.children.splice(this.cols - 1, this.container.children.length - this.cols);
        }
        const container = this.$layoutUtils.getContainerById(this.$root.layout, this.container.storageId);
        Object.assign(container, this.container);
        this.container.hiddenOnMobile = this.hiddenOnMobile;
        this.$layoutUtils.applyContainerStyle(container, this.container);
        if (this.hiddenOnMobile && !container.cssClass?.includes?.('hidden-sm-and-down')) {
          this.$set(container, 'cssClass', container.cssClass ? `${container.cssClass} hidden-sm-and-down` : 'hidden-sm-and-down');
        } else if (!this.hiddenOnMobile && container.cssClass?.includes?.('hidden-sm-and-down')) {
          this.$set(container, 'cssClass', container.cssClass.replace('hidden-sm-and-down', ''));
        }
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