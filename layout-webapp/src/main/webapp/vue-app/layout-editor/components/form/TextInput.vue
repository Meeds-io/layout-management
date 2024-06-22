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
        {{ $t('layout.textStyle') }}
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
        <span class="text-font-size text-color">{{ $t('layout.textColorTitle') }}</span>
      </v-list-item-content>
      <v-list-item-action
        class="me-0 my-auto ms-auto">
        <v-btn-toggle v-model="textTitleStyle" multiple>
          <v-btn value="bold" small>
            <v-icon size="18">fa-bold</v-icon>
          </v-btn>
          <v-btn value="italic" small>
            <v-icon size="18">fa-italic</v-icon>
          </v-btn>
        </v-btn-toggle>
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
        <layout-editor-number-input
          v-if="textTitleFontSize"
          v-model="textTitleFontSize"
          :min="1"
          :max="60"
          :step="1" />
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
        <layout-editor-color-picker
          v-model="textTitleColor"
          min-text-width="64"
          class="my-auto" />
      </v-list-item-action>
    </v-list-item>
    <v-list-item
      v-if="enabledTextColor"
      class="pa-0"
      dense>
      <v-list-item-content class="my-auto">
        <span class="text-font-size text-color">{{ $t('layout.textColorHeader') }}</span>
      </v-list-item-content>
      <v-list-item-action
        class="me-0 my-auto ms-auto">
        <v-btn-toggle v-model="textHeaderStyle" multiple>
          <v-btn value="bold" small>
            <v-icon size="18">fa-bold</v-icon>
          </v-btn>
          <v-btn value="italic" small>
            <v-icon size="18">fa-italic</v-icon>
          </v-btn>
        </v-btn-toggle>
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
        <layout-editor-number-input
          v-if="textHeaderFontSize"
          v-model="textHeaderFontSize"
          :min="1"
          :max="60"
          :step="1" />
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
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
        <v-btn-toggle v-model="textStyle" multiple>
          <v-btn value="bold" small>
            <v-icon size="18">fa-bold</v-icon>
          </v-btn>
          <v-btn value="italic" small>
            <v-icon size="18">fa-italic</v-icon>
          </v-btn>
        </v-btn-toggle>
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
        <layout-editor-number-input
          v-if="textFontSize"
          v-model="textFontSize"
          :min="1"
          :max="60"
          :step="1" />
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
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
        <v-btn-toggle v-model="textSubtitleStyle" multiple>
          <v-btn value="bold" small>
            <v-icon size="18">fa-bold</v-icon>
          </v-btn>
          <v-btn value="italic" small>
            <v-icon size="18">fa-italic</v-icon>
          </v-btn>
        </v-btn-toggle>
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
        <layout-editor-number-input
          v-if="textSubtitleFontSize"
          v-model="textSubtitleFontSize"
          :min="1"
          :max="60"
          :step="1" />
      </v-list-item-action>
      <v-list-item-action
        class="me-0 my-auto">
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
    textTitleColor: null,
    textTitleFontSize: 0,
    textTitleStyle: [],
    textHeaderColor: null,
    textHeaderFontSize: 0,
    textHeaderStyle: [],
    textColor: null,
    textFontSize: 0,
    textStyle: [],
    textSubtitleColor: null,
    textSubtitleFontSize: 0,
    textSubtitleStyle: [],
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
    textTitleColor() {
      if (this.initialized) {
        this.$set(this.container, 'textTitleColor', this.textTitleColor);
      }
    },
    textTitleFontSize() {
      if (this.initialized) {
        this.$set(this.container, 'textTitleFontSize', this.textTitleFontSize && `${this.textTitleFontSize}px`);
      }
    },
    textTitleStyle: {
      deep: true,
      handler() {
        if (this.initialized) {
          this.$set(this.container, 'textTitleFontWeight', this.textTitleStyle?.find?.(s => s === 'bold') && 'bold' || 'normal');
          this.$set(this.container, 'textTitleFontStyle', this.textTitleStyle?.find?.(s => s === 'italic') && 'italic' || 'normal');
        }
      },
    },
    textHeaderColor() {
      if (this.initialized) {
        this.$set(this.container, 'textHeaderColor', this.textHeaderColor);
      }
    },
    textHeaderFontSize() {
      if (this.initialized) {
        this.$set(this.container, 'textHeaderFontSize', this.textHeaderFontSize && `${this.textHeaderFontSize}px`);
      }
    },
    textHeaderStyle: {
      deep: true,
      handler() {
        if (this.initialized) {
          this.$set(this.container, 'textHeaderFontWeight', this.textHeaderStyle?.find?.(s => s === 'bold') && 'bold' || 'normal');
          this.$set(this.container, 'textHeaderFontStyle', this.textHeaderStyle?.find?.(s => s === 'italic') && 'italic' || 'normal');
        }
      }
    },
    textColor() {
      if (this.initialized) {
        this.$set(this.container, 'textColor', this.textColor);
      }
    },
    textFontSize() {
      if (this.initialized) {
        this.$set(this.container, 'textFontSize', this.textFontSize && `${this.textFontSize}px`);
      }
    },
    textStyle: {
      deep: true,
      handler() {
        if (this.initialized) {
          this.$set(this.container, 'textFontWeight', this.textStyle?.find?.(s => s === 'bold') && 'bold' || 'normal');
          this.$set(this.container, 'textFontStyle', this.textStyle?.find?.(s => s === 'italic') && 'italic' || 'normal');
        }
      }
    },
    textSubtitleColor() {
      if (this.initialized) {
        this.$set(this.container, 'textSubtitleColor', this.textSubtitleColor);
      }
    },
    textSubtitleFontSize() {
      if (this.initialized) {
        this.$set(this.container, 'textSubtitleFontSize', this.textSubtitleFontSize && `${this.textSubtitleFontSize}px`);
      }
    },
    textSubtitleStyle: {
      deep: true,
      handler() {
        if (this.initialized) {
          this.$set(this.container, 'textSubtitleFontWeight', this.textSubtitleStyle?.find?.(s => s === 'bold') && 'bold' || 'normal');
          this.$set(this.container, 'textSubtitleFontStyle', this.textSubtitleStyle?.find?.(s => s === 'italic') && 'italic' || 'normal');
        }
      }
    },
    enabledTextColor() {
      if (this.enabledTextColor) {
        this.textTitleColor = this.textTitleColor || '#20282C';
        this.textTitleFontSize = this.textTitleFontSize || 18;
        this.textTitleStyle = this.textTitleStyle || ['bold'];
        this.textHeaderColor = this.textHeaderColor || '#707070';
        this.textHeaderStyle = this.textHeaderStyle || [];
        this.textHeaderFontSize = this.textHeaderFontSize || 18;
        this.textColor = this.textColor || '#20282C';
        this.textStyle = this.textStyle || [];
        this.textFontSize = this.textFontSize || 14;
        this.textSubtitleColor = this.textSubtitleColor || '#707070';
        this.textSubtitleStyle = this.textSubtitleStyle || [];
        this.textSubtitleFontSize = this.textSubtitleFontSize || 12;
      } else {
        this.textTitleColor = null;
        this.textTitleFontSize = null;
        this.textTitleStyle = null;
        this.textHeaderColor = null;
        this.textHeaderStyle = null;
        this.textHeaderFontSize = null;
        this.textColor = null;
        this.textStyle = null;
        this.textFontSize = null;
        this.textSubtitleColor = null;
        this.textSubtitleStyle = null;
        this.textSubtitleFontSize = null;
      }
    },
  },
  created() {
    this.container = this.value;

    this.textTitleColor = this.container.textTitleColor;
    this.textTitleFontSize = this.container.textTitleFontSize?.replace?.('px', '');
    this.textTitleStyle = [];
    if (this.container.textTitleFontWeight === 'bold') {
      this.textTitleStyle.push('bold');
    }
    if (this.container.textTitleFontStyle === 'italic') {
      this.textTitleStyle.push('italic');
    }

    this.textHeaderColor = this.container.textHeaderColor;
    this.textHeaderFontSize = this.container.textHeaderFontSize?.replace?.('px', '');
    this.textHeaderStyle = [];
    if (this.container.textHeaderFontWeight === 'bold') {
      this.textHeaderStyle.push('bold');
    }
    if (this.container.textHeaderFontStyle === 'italic') {
      this.textHeaderStyle.push('italic');
    }

    this.textColor = this.container.textColor;
    this.textFontSize = this.container.textFontSize?.replace?.('px', '');
    this.textStyle = [];
    if (this.container.textFontWeight === 'bold') {
      this.textStyle.push('bold');
    }
    if (this.container.textFontStyle === 'italic') {
      this.textStyle.push('italic');
    }

    this.textSubtitleColor = this.container.textSubtitleColor;
    this.textSubtitleFontSize = this.container.textSubtitleFontSize?.replace?.('px', '');
    this.textSubtitleStyle = [];
    if (this.container.textSubtitleFontWeight === 'bold') {
      this.textSubtitleStyle.push('bold');
    }
    if (this.container.textSubtitleFontStyle === 'italic') {
      this.textSubtitleStyle.push('italic');
    }

    this.enabledTextColor = !!this.textTitleColor || !!this.textHeaderColor || !!this.textColor || !!this.textSubtitleColor;
    this.$nextTick().then(() => this.initialized = true);
  },
};
</script>