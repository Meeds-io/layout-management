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
    <div class="d-flex align-center mt-4">
      <div class="subtitle-1 font-weight-bold me-auto">
        {{ $t('layout.textColor') }}
      </div>
      <v-switch
        v-model="enabledTextColor"
        class="ms-auto my-auto me-n2" />
    </div>
    <v-list-item
      v-if="enabledTextColor"
      class="pa-0"
      dense>
      <v-list-item-content class="my-auto">
        <span class="text-font-size text-color">{{ $t('layout.textColorHeader') }}</span>
      </v-list-item-content>
      <v-list-item-action
        class="me-0 my-auto ms-auto">
        <layout-editor-color-picker
          v-model="textHeaderColor"
          min-text-width="64"
          class="my-auto" />
      </v-list-item-action>
    </v-list-item>
    <v-list-item
      v-if="enabledTextColor"
      class="pa-0"
      dense>
      <v-list-item-content class="my-auto">
        <span class="text-font-size text-color">{{ $t('layout.textColorBody') }}</span>
      </v-list-item-content>
      <v-list-item-action
        class="me-0 my-auto ms-auto">
        <layout-editor-color-picker
          v-model="textColor"
          min-text-width="64"
          class="my-auto" />
      </v-list-item-action>
    </v-list-item>
    <v-list-item
      v-if="enabledTextColor"
      class="pa-0"
      dense>
      <v-list-item-content class="my-auto">
        <span class="text-font-size text-color">{{ $t('layout.textColorSubtitle') }}</span>
      </v-list-item-content>
      <v-list-item-action
        class="me-0 my-auto ms-auto">
        <layout-editor-color-picker
          v-model="textSubtitleColor"
          min-text-width="64"
          class="my-auto" />
      </v-list-item-action>
    </v-list-item>
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
    enabledTextColor: false,
    textColor: null,
    textHeaderColor: null,
    textSubtitleColor: null,
    initialized: false,
  }),
  watch: {
    container: {
      deep: true,
      handler() {
        if (this.container) {
          this.$emit('input', this.container);
        }
      },
    },
    textHeaderColor() {
      if (this.initialized) {
        this.container.textHeaderColor = this.textHeaderColor;
      }
    },
    textColor() {
      if (this.initialized) {
        this.container.textColor = this.textColor;
      }
    },
    textSubtitleColor() {
      if (this.initialized) {
        this.container.textSubtitleColor = this.textSubtitleColor;
      }
    },
    enabledTextColor() {
      if (this.initialized) {
        if (this.enabledTextColor) {
          this.textHeaderColor = '#707070';
          this.textColor = '#20282C';
          this.textSubtitleColor = '#707070';
        } else {
          this.textHeaderColor = null;
          this.textColor = null;
          this.textSubtitleColor = null;
        }
      }
    },
  },
  created() {
    this.container = this.value;
    this.textHeaderColor = this.container.textHeaderColor;
    this.textColor = this.container.textColor;
    this.textSubtitleColor = this.container.textSubtitleColor;
    this.enabledTextColor = !!this.textHeaderColor || !!this.textColor || !!this.textSubtitleColor;
    if (!this.enabledTextColor) {
      this.$set(this.container, 'textHeaderColor', this.container.textHeaderColor || null);
      this.$set(this.container, 'textColor', this.container.textColor || null);
      this.$set(this.container, 'textSubtitleColor', this.container.textSubtitleColor || null);
      this.$emit('input', this.container);
    }
    this.$nextTick().then(() => this.initialized = true);
  },
};
</script>