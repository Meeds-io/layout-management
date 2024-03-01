<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io

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
      {{ $t('layout.editSectionTitle', {0: index + 1}) }}
    </template>
    <template #content>
      <v-card class="pa-4" flat>
        <span class="my-0 subtitle-1 text-color font-weight-bold">{{ $t('layout.chooseSectionDisplay') }}</span>
        <div class="border-color-thin-grey-opacity2 border-radius mt-2 mb-4 pt-2 px-2">
          <layout-editor-container-container-extension
            :container="sectionPreviewContainer"
            preview
            style="zoom: 0.3" />
        </div>
        <v-list-item class="px-0" dense>
          <v-list-item-content>
            <v-list-item-title>
              <span class="subtitle-1 text-color">{{ $t('layout.row') }}</span>
            </v-list-item-title>
          </v-list-item-content>
          <v-list-item-action>
            <v-text-field
              id="layoutSectionRows"
              v-model="rows"
              :placeholder="$t('layout.row')"
              class="border-box-sizing py-0"
              name="layoutSectionRows"
              type="number"
              aria-required="true"
              required="required"
              outlined
              dense />
          </v-list-item-action>
        </v-list-item>
        <v-list-item class="px-0" dense>
          <v-list-item-content>
            <v-list-item-title>
              <h4 class="my-0 text-font-size text-color">{{ $t('layout.column') }}</h4>
            </v-list-item-title>
          </v-list-item-content>
          <v-list-item-action>
            <v-text-field
              id="layoutSectionColumns"
              v-model="columns"
              :placeholder="$t('layout.column')"
              class="border-box-sizing py-0"
              name="layoutSectionColumns"
              type="number"
              aria-required="true"
              required="required"
              outlined
              dense />
          </v-list-item-action>
        </v-list-item>
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    section: null,
    children: null,
    rows: 0,
    cols: 0,
    index: null,
  }),
  computed: {
    sectionPreviewContainer() {
      const section = JSON.parse(JSON.stringify(this.section));
      section.children.forEach(c => c.children = []);
      return section;
    },
  },
  methods: {
    open(section, index) {
      this.section = JSON.parse(JSON.stringify(section));
      this.children = this.section.children;
      this.index = index;
      this.computeGrid();
      this.$refs.drawer.open();
    },
    computeGrid() {
      // TODO
      this.rows = 0;
      this.cols = 0;
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>