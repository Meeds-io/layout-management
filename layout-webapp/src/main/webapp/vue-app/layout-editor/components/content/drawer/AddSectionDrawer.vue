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
    <template v-if="drawer && rows && cols" #content>
      <v-card class="pa-4" flat>
        <layout-editor-section-grid-editor
          :rows-count="rows"
          :cols-count="cols"
          @rows-updated="rows = $event"
          @cols-updated="cols = $event" />
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
    parentContainer: null,
    section: null,
    drawer: false,
    index: null,
    rows: 0,
    cols: 0,
  }),
  methods: {
    open(parentContainer, index) {
      this.parentContainer = parentContainer;
      this.index = index;
      this.rows = 1;
      this.cols = 4;
      this.$nextTick().then(() => this.$refs.drawer.open());
    },
    apply() {
      this.section = this.$layoutUtils.newSection(null, null, this.rows, this.cols);
      this.$root.$emit('layout-add-section', this.section, this.index);
      this.close();
    },
    close() {
      this.$refs.drawer.close();
      this.section = null;
    },
  },
};
</script>