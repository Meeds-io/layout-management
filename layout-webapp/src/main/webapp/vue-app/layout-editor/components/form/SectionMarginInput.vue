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
  <div>
    <div class="d-flex align-center mb-2">
      <div
        class="text-header me-auto">
        {{ $t('layout.margins') }}
      </div>
      <v-switch
        v-model="enableMargin"
        class="ms-auto my-auto me-n2" />
    </div>
    <div
      v-if="enableMargin"
      class="d-flex flex-column">
      <v-list-item class="pa-0" dense>
        <v-list-item-content class="my-auto">
          {{ $t('layout.top') }}
        </v-list-item-content>
        <layout-editor-number-input
          v-model="marginTop"
          :max="48"
          class="my-auto me-n3" />
      </v-list-item>
      <v-list-item class="pa-0" dense>
        <v-list-item-content class="my-auto">
          {{ $t('layout.bottom') }}
        </v-list-item-content>
        <layout-editor-number-input
          v-model="marginBottom"
          :max="48"
          class="my-auto me-n3" />
      </v-list-item>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    container: null,
    initialized: false,
    enableMargin: true,
    marginTop: 0,
    marginBottom: 0,
  }),
  watch: {
    marginTop() {
      if (this.initialized) {
        this.$set(this.container, 'marginTop', this.marginTop || null);
        this.$emit('refresh');
      }
    },
    marginBottom() {
      if (this.initialized) {
        this.$set(this.container, 'marginBottom', this.marginBottom || null);
        this.$emit('refresh');
      }
    },
    enableMargin() {
      if (this.initialized) {
        this.marginChoice = 'same';
        this.marginTop = 0;
        this.marginRight = 0;
        this.marginBottom = 0;
        this.marginLeft = 0;
      }
    },
  },
  created() {
    this.container = this.value;
    this.marginTop = this.container.marginTop || 0;
    this.marginBottom = this.container.marginBottom || 0;
    this.enableMargin = !!(this.marginRight || this.marginBottom);
    this.$nextTick().then(() => this.initialized = true);
  },
};
</script>