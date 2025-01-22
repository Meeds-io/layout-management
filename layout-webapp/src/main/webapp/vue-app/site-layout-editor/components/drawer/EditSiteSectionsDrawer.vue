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
      {{ $t('layout.manageSiteSections') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4 d-flex flex-column"
        flat>
        <div class="mb-2">
          {{ $t('layout.manageSiteSections.description1') }}
        </div>
        <div class="mb-4">
          {{ $t('layout.manageSiteSections.description2') }}
        </div>
        <div class="text-header mb-2">
          {{ $t('layout.manageSiteSections.label.preview') }}
        </div>
        <v-card
          class="d-flex border-color elevation-2 mx-auto mb-4"
          max-width="335px"
          width="100%"
          flat>
          <div class="d-flex full-width pa-1">
            <v-card
              :width="leftSidebar && 75"
              class="flex-grow-0 flex-shrink-0 grey-background no-border-radius"
              flat />
            <v-card
              class="d-flex flex-column flex-grow-1 flex-shrink-1 no-border-radius"
              flat>
              <v-card
                :class="topBanner && 'grey-background'"
                class="no-border-radius"
                height="20px"
                flat />
              <v-card
                height="100px"
                flat />
              <v-card
                :class="bottomBanner && 'grey-background'"
                class="no-border-radius"
                height="20px"
                flat />
            </v-card>
            <v-card
              :width="rightSidebar && 75"
              class="flex-grow-0 flex-shrink-0 grey-background no-border-radius"
              flat />
          </div>
        </v-card>
        <div class="text-header mb-4">
          {{ $t('layout.manageSiteSections.label.sectionsChoice') }}
        </div>
        <div class="d-flex align-center mb-2">
          <div class="font-weight-bold me-auto mb-2">
            {{ $t('layout.manageSiteSections.label.leftSidebar') }}
          </div>
          <v-switch
            v-model="leftSidebar"
            class="ms-auto my-auto me-n2" />
        </div>
        <div class="d-flex align-center mb-2">
          <div class="font-weight-bold me-auto mb-2">
            {{ $t('layout.manageSiteSections.label.topBanner') }}
          </div>
          <v-switch
            v-model="topBanner"
            class="ms-auto my-auto me-n2" />
        </div>
        <div class="d-flex align-center mb-2">
          <div class="font-weight-bold me-auto mb-2">
            {{ $t('layout.manageSiteSections.label.rightSidebar') }}
          </div>
          <v-switch
            v-model="rightSidebar"
            class="ms-auto my-auto me-n2" />
        </div>
        <div class="d-flex align-center mb-2">
          <div class="font-weight-bold me-auto mb-2">
            {{ $t('layout.manageSiteSections.label.bottomBanner') }}
          </div>
          <v-switch
            v-model="bottomBanner"
            class="ms-auto my-auto me-n2" />
        </div>
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
    leftSidebar: false,
    topBanner: false,
    bottomBanner: false,
    rightSidebar: false,
  }),
  created() {
    this.$root.$on('layout-site-sections-open', this.open);
  },
  beforeDestroy() {
    this.$root.$off('layout-site-sections-open', this.open);
  },
  methods: {
    open() {
      this.leftSidebar = !!this.$root.draftLayout?.children?.[0]?.children?.length;
      this.topBanner = !!this.$root.draftLayout?.children?.[1]?.children?.[0]?.children?.length;
      this.bottomBanner = !!this.$root.draftLayout?.children?.[1]?.children?.[2]?.children?.length;
      this.rightSidebar = !!this.$root.draftLayout?.children?.[2]?.children?.length;
      this.$refs.drawer.open();
    },
    apply() {
      this.$root.$emit('layout-section-history-add');
      const leftSidebar = !!this.$root.draftLayout?.children?.[0]?.children?.length;
      if (leftSidebar !== this.leftSidebar) {
        if (this.leftSidebar) {
          this.$root.draftLayout.children[0] = {
            ...this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate),
            children: [
              this.$layoutUtils.newContainer(this.$layoutUtils.sidebarCellTemplate),
            ]
          };
        } else {
          this.$root.draftLayout.children[0] = this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate);
        }
      }
      const rightSidebar = !!this.$root.draftLayout?.children?.[2]?.children?.length;
      if (rightSidebar !== this.rightSidebar) {
        if (this.rightSidebar) {
          this.$root.draftLayout.children[2] = {
            ...this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate),
            children: [
              this.$layoutUtils.newContainer(this.$layoutUtils.sidebarCellTemplate),
            ]
          };
        } else {
          this.$root.draftLayout.children[2] = this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate);
        }
      }
      const topBanner = !!this.$root.draftLayout?.children?.[1]?.children?.[0]?.children?.length;
      if (topBanner !== this.topBanner) {
        if (this.topBanner) {
          this.$root.draftLayout.children[1].children[0] = {
            ...this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate),
            children: [
              this.$layoutUtils.newContainer(this.$layoutUtils.sidebarCellTemplate),
            ]
          };
        } else {
          this.$root.draftLayout.children[1].children[0] = this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate);
        }
      }
      const bottomBanner = !!this.$root.draftLayout?.children?.[1]?.children?.[2]?.children?.length;
      if (bottomBanner !== this.bottomBanner) {
        if (this.bottomBanner) {
          this.$root.draftLayout.children[1].children[2] = {
            ...this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate),
            children: [
              this.$layoutUtils.newContainer(this.$layoutUtils.sidebarCellTemplate),
            ]
          };
        } else {
          this.$root.draftLayout.children[1].children[2] = this.$layoutUtils.newContainer(this.$layoutUtils.sidebarTemplate);
        }
      }
      this.$root.draftLayout.children = this.$root.draftLayout.children.slice();
      this.$root.draftLayout.children[1].children = this.$root.draftLayout.children[1].children.slice();
      this.close();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>