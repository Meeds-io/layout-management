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
        <layout-editor-background-input
          v-if="parentContainer"
          ref="backgroundInput"
          v-model="parentContainer"
          class="mt-4" />
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
    drawer: false,
    saving: false,
  }),
  computed: {
    cssStyle() {
      const style = {};
      if (this.parentContainer.backgroundColor) {
        style['background-color'] = this.parentContainer.backgroundColor;
      }
      if (this.parentContainer.backgroundImage) {
        if (this.parentContainer.backgroundEffect) {
          style['background-image'] = `${this.parentContainer.backgroundEffect},url(${this.parentContainer.backgroundImage})`;
        } else {
          style['background-image'] = `url(${this.parentContainer.backgroundImage})`;
        }
        if (this.parentContainer.backgroundRepeat) {
          style['background-repeat'] = this.parentContainer.backgroundRepeat;
        }
        if (this.parentContainer.backgroundSize) {
          style['background-size'] = this.parentContainer.backgroundSize;
        }
      }
      return style;
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
  },
  beforeDestroy() {
    this.$root.$off('layout-page-properties-open', this.open);
  },
  methods: {
    open() {
      let parentContainer = this.$layoutUtils.getParentContainer(this.$root.layout);
      parentContainer = JSON.parse(JSON.stringify(parentContainer));
      this.parentContainer = Object.assign({...this.$layoutUtils.containerModel}, parentContainer);
      this.fullWindow = this.parentContainer.width === 'fullWindow';
      this.$refs.drawer.open();
    },
    async apply() {
      this.saving = true;
      try {
        this.parentContainer.width = this.fullWindow && 'fullWindow' || null;
        await this.$refs.backgroundInput.apply();
        const parentContainer = this.$layoutUtils.getParentContainer(this.$root.layout);
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