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
      {{ $t('layout.editSiteSidebarSection') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4 d-flex flex-column"
        flat>
        <div class="d-flex mb-4">
          <div class="me-auto">
            <div class="font-weight-bold mb-2">
              {{ $t('layout.editSiteSidebarSection.label.showSection') }}
            </div>
            <div class="text-subtitle">
              {{ $t('layout.editSiteSidebarSection.subtitle.showSection') }}
            </div>
          </div>
          <v-switch
            v-model="show"
            class="ms-auto my-auto me-n2" />
        </div>
        <template v-if="show">
          <div class="text-header mb-2">
            {{ $t('layout.editSiteSidebarSection.label.setDisplay') }}
          </div>
          <div class="d-flex align-center mb-4">
            <div class="me-auto mb-2">
              {{ $t('layout.editSiteSidebarSection.label.sectionMaxWidth') }}
            </div>
            <number-input
              v-model="width"
              :min="50"
              :max="1000"
              :step="10"
              class="ms-auto my-n2"
              editable />
          </div>
          <div class="text-header mb-4">
            {{ $t('layout.editSiteSidebarSection.label.updateStyle') }}
          </div>
          <layout-editor-background-input
            ref="backgroundInput"
            v-model="container"
            class="mb-4"
            text-bold />
          <layout-editor-text-input
            ref="textInput"
            v-model="container"
            class="mb-4"
            text-bold />
        </template>
      </v-card>
    </template>
    <template #footer>
      <div class="d-flex">
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
    leftSidebar: false,
    rightSidebar: false,
    show: true,
    saving: false,
    width: null,
    container: null,
  }),
  computed: {
    isLeftBar() {
      return this.$root.layout?.children?.[0]?.children?.[0]?.storageId === this.container?.storageId;
    },
    isRightBar() {
      return this.$root.layout?.children?.[2]?.children?.[0]?.storageId === this.container?.storageId;
    },
    sidebarContainer() {
      if (this.isLeftBar) {
        return this.$root.layout?.children?.[0];
      } else if (this.isRightBar) {
        return this.$root.layout?.children?.[2];
      } else {
        return null;
      }
    },
  },
  created() {
    this.$root.$on('layout-site-sidebar-section-open', this.open);
  },
  beforeDestroy() {
    this.$root.$off('layout-site-sidebar-section-open', this.open);
  },
  methods: {
    open(container) {
      this.container = JSON.parse(JSON.stringify(container));
      this.width = this.container.width || 310;
      this.show = true;
      this.leftSidebar = this.$root.layout?.children?.[0]?.children?.[0]?.storageId === this.container?.storageId;
      this.rightSidebar = this.$root.layout?.children?.[2]?.children?.[0]?.storageId === this.container?.storageId;
      this.$refs.drawer.open();
    },
    async apply() {
      this.saving = true;
      try {
        this.$root.$emit('layout-section-history-add');
        if (!this.show) {
          this.sidebarContainer.children = [];
        } else {
          this.container.width = this.width;
          await this.$refs.backgroundInput.apply();
          const container = this.$layoutUtils.getContainerById(this.$root.layout, this.container.storageId);
          Object.assign(container, this.container);
        }
      } finally {
        this.saving = false;
      }
      this.close();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>