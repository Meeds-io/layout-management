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
            :context="context"
            style="zoom: 0.3" />
        </div>
        <v-list-item class="px-0" dense>
          <v-list-item-content>
            <v-list-item-title>
              <span class="subtitle-1 text-color">{{ $t('layout.row') }}</span>
            </v-list-item-title>
          </v-list-item-content>
          <v-list-item-action class="my-0">
            <v-btn
              :title="$t('layout.addRow')"
              icon
              @click="addRow">
              <v-icon size="20" class="icon-default-color">fa-plus</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
        <v-list-item class="px-0" dense>
          <v-list-item-content>
            <v-list-item-title>
              <h4 class="my-0 text-font-size text-color">{{ $t('layout.column') }}</h4>
            </v-list-item-title>
          </v-list-item-content>
          <v-list-item-action class="my-0">
            <v-btn
              :title="$t('layout.addColumn')"
              icon
              @click="addColumn">
              <v-icon size="20" class="icon-default-color">fa-plus</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-card>
    </template>
    <template #footer>
      <div class="d-flex">
        <v-btn
          v-if="length > 1"
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
    context: 'edit-section',
    originalSection: null,
    section: null,
    drawer: false,
    rows: 0,
    cols: 0,
    index: null,
    length: 0,
  }),
  computed: {
    sectionPreviewContainer() {
      const section = JSON.parse(JSON.stringify(this.section));
      section.children.forEach(c => c.children = []);
      return section;
    },
    modified() {
      return JSON.stringify(this.section) !== JSON.stringify(this.originalSection);
    },
  },
  created() {
    this.$root.$on('layout-delete-container', this.deleteContainer);
  },
  methods: {
    open(section, index, length) {
      this.section = JSON.parse(JSON.stringify(section));
      this.originalSection = JSON.parse(JSON.stringify(section));
      this.index = index;
      this.length = length;
      this.$refs.drawer.open();
    },
    addRow() {
      this.$layoutUtils.newRow(this.section, this.section.children.length, 4);
    },
    addColumn() {
      this.$layoutUtils.newColumn(this.section, this.section.children.length);
    },
    removeSection() {
      this.close();
      this.$root.$emit('layout-remove-section', this.index);
    },
    deleteContainer(container, index, context) {
      if (this.context === context) {
        this.section.children.splice(index, 1);
      }
    },
    apply() {
      this.$root.$emit('layout-replace-section', this.index, this.section);
      this.close();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>