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
  <div
    v-show="displayBorder"
    :class="moving && 'layout-section-moving' || 'layout-section-hover'"
    class="absolute-full-size layout-no-multi-select border-radius">
    <v-slide-y-transition>
      <!-- eslint-disable-next-line vuejs-accessibility/no-static-element-interactions -->
      <div
        v-if="open"
        class="position-relative full-width d-flex flex-column"
        @focusin="hoverArea = true"
        @mouseover="hoverArea = true"
        @focusout="hoverArea = false"
        @mouseout="hoverArea = false">
        <v-hover v-model="hoverButton">
          <div class="position-absolute t-10 r-0 z-index-one full-height">
            <div class="position-sticky t-10 z-index-one me-3">
              <v-tooltip bottom>
                <template #activator="{on, attrs}">
                  <div
                    v-on="on"
                    v-bind="attrs">
                    <v-btn
                      class="white text-color border-color elevation-2"
                      height="32"
                      width="32"
                      icon
                      @click="$root.$emit('layout-site-sidebar-section-open', cellContainer)">
                      <v-icon class="icon-default-color" size="20">fa-edit</v-icon>
                    </v-btn>
                  </div>
                </template>
                {{ $t('layout.editSection') }}
              </v-tooltip>
            </div>
          </div>
        </v-hover>
      </div>
    </v-slide-y-transition>
  </div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
    hover: {
      type: Boolean,
      default: false,
    },
    moving: {
      type: Boolean,
      default: false,
    },
    index: {
      type: Number,
      default: null,
    },
    length: {
      type: Number,
      default: null,
    },
  },
  data: () => ({
    open: false,
    savingAsTemplate: false,
    hoverButton: false,
    hoverArea: false,
  }),
  computed: {
    displayMoveButton() {
      return this.length > 1;
    },
    displayBorder() {
      return this.open || this.hover;
    },
    hoveredSectionMenu() {
      return this.hoverButton || (!this.hoverArea && !this.hoveredApplication);
    },
    hoveredApplication() {
      return this.$root.hoveredApplication;
    },
    cellContainer() {
      return this.container?.children?.[0];
    },
  },
  watch: {
    hover() {
      window.setTimeout(() => {
        if (!this.moving) {
          this.open = this.hover;
        }
      }, 200);
    },
    hoveredSectionMenu() {
      this.$emit('hover-button', this.hoveredSectionMenu);
    },
    moving() {
      window.setTimeout(() => {
        if (!this.hover) {
          this.open = false;
        }
      }, 200);
    },
    open(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.$root.hoveredSectionId = this.container.storageId;
      } else if (!newVal && this.$root.hoveredSectionId === this.container.storageId) {
        this.$root.hoveredSectionId = null;
      }
    },
  },
};
</script>
