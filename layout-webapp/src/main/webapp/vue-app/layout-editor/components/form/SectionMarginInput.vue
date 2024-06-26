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
        v-model="enabled"
        class="ms-auto my-auto me-n2" />
    </div>
    <div
      v-if="enabled"
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
    enabled: true,
    marginTop: null,
    marginBottom: null,
  }),
  watch: {
    marginTop() {
      if (this.initialized) {
        this.$set(this.container, 'marginTop', this.enabled ? this.marginTop || 0 : null);
        this.$emit('refresh');
      }
    },
    marginBottom() {
      if (this.initialized) {
        this.$set(this.container, 'marginBottom', this.enabled ? this.marginBottom || 0 : null);
        this.$emit('refresh');
      }
    },
    enabled() {
      if (this.initialized) {
        this.marginTop = this.enabled ? 0 : null;
        this.marginBottom = this.enabled ? 0 : null;
      }
    },
  },
  created() {
    this.container = this.value;
    this.marginTop = this.container.marginTop;
    this.marginBottom = this.container.marginBottom;
    this.enabled = (this.marginTop || this.marginTop === 0) && (this.marginBottom || this.marginBottom === 0) ? true : false;
    this.$nextTick().then(() => this.initialized = true);
  },
};
</script>