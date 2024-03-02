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
    id="defaultSpacesRegistrationDrawer"
    v-model="drawer"
    right
    disable-pull-to-refresh>
    <template #title>
      {{ $t('layout.addSectionTitle') }}
    </template>
    <template v-if="drawer" #content>
      <v-card class="pa-4" flat>
        <div v-show="rows && cols">
          <span class="my-0 subtitle-1 text-color font-weight-bold">{{ $t('layout.chooseSectionDisplay') }}</span>
          <div class="d-flex my-2">
            <div class="flex-grow-0 flex-shrink-0 align-start">
              <span class="subtitle-1 text-color">{{ $t('layout.row') }}</span>
            </div>
            <v-card
              class="flex-grow-1 flex-shrink-1 align-end ms-auto mt-4"
              max-width="80%"
              flat>
              <v-slider
                v-model="rows"
                :thumb-size="24"
                :min="1"
                :max="12"
                thumb-label="always">
                <template #prepend>
                  <v-btn
                    :disabled="rows === 1"
                    class="me-n2 mt-n1"
                    icon
                    fab
                    x-small
                    @click="rows--">
                    <v-icon class="pt-2px">fa-minus</v-icon>
                  </v-btn>
                </template>
                <template #append>
                  <v-btn
                    :disabled="rows === 12"
                    class="ms-n2 mt-n1"
                    icon
                    fab
                    x-small
                    @click="rows++">
                    <v-icon class="pt-2px">fa-plus</v-icon>
                  </v-btn>
                </template>
              </v-slider>
            </v-card>
          </div>
          <div class="d-flex my-2">
            <div class="flex-grow-0 flex-shrink-0 align-start">
              <span class="subtitle-1 text-color">{{ $t('layout.column') }}</span>
            </div>
            <v-card
              class="flex-grow-1 flex-shrink-1 align-end ms-auto"
              max-width="80%"
              flat>
              <v-slider
                v-model="cols"
                :thumb-size="24"
                :min="1"
                :max="12"
                thumb-label="always">
                <template #prepend>
                  <v-btn
                    :disabled="cols === 1"
                    class="me-n2 mt-n1"
                    icon
                    fab
                    x-small
                    @click="decrementCols">
                    <v-icon class="pt-2px">fa-minus</v-icon>
                  </v-btn>
                </template>
                <template #append>
                  <v-btn
                    :disabled="cols === 12"
                    class="ms-n2 mt-n1"
                    fab
                    icon
                    x-small
                    @click="incrementCols">
                    <v-icon class="pt-2px">fa-plus</v-icon>
                  </v-btn>
                </template>
              </v-slider>
            </v-card>
          </div>
          <div class="border-color-thin-grey-opacity2 border-radius mt-2 mb-4 pt-2 px-2">
            <layout-editor-container-container-extension
              :container="section"
              :context="context"
              style="zoom: 0.3" />
          </div>
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
    context: 'add-section',
    parentContainer: null,
    section: null,
    drawer: false,
    rows: 0,
    cols: 0,
    index: null,
  }),
  watch: {
    rows(newVal, oldVal) {
      if (!this.drawer) {
        return;
      }
      const diff = newVal - oldVal;
      if (!diff) {
        return;
      } else if (diff > 0) {
        this.$layoutUtils.addRows(this.section, diff);
      } else {
        this.$layoutUtils.removeRows(this.section, diff);
      }
    },
    cols() {
      if (!this.drawer) {
        return;
      }
      const diff = this.cols - this.section.colsCount;
      if (!diff) {
        return;
      }
      if (this.$layoutUtils.colValues.indexOf(this.cols) < 0) {
        if (diff > 0) {
          const index = this.$layoutUtils.colValues.indexOf(this.section.colsCount);
          this.cols = this.$layoutUtils.colValues[index + 1];
        } else {
          const index = this.$layoutUtils.colValues.indexOf(this.section.colsCount);
          this.cols = this.$layoutUtils.colValues[index - 1];
        }
      } else if (diff > 0) {
        this.$layoutUtils.addColumns(this.section, diff);
      } else {
        this.$layoutUtils.removeColumns(this.section, diff);
      }
    },
  },
  methods: {
    open(parentContainer, index) {
      this.parentContainer = parentContainer;
      this.index = index;
      this.rows = 1;
      this.cols = 4;
      this.section = this.$layoutUtils.newSection(null, null, this.rows, this.cols);
      this.$nextTick().then(() => this.$refs.drawer.open());
    },
    removeSection() {
      this.close();
      this.$root.$emit('layout-remove-section', this.index);
    },
    incrementCols() {
      const index = this.$layoutUtils.colValues.indexOf(this.cols);
      this.cols = this.$layoutUtils.colValues[index + 1];
    },
    decrementCols() {
      const index = this.$layoutUtils.colValues.indexOf(this.cols);
      this.cols = this.$layoutUtils.colValues[index - 1];
    },
    apply() {
      this.parentContainer.children.splice(this.index || 0, 0, this.section);
      this.close();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>