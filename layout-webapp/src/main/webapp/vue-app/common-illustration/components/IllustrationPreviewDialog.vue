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
  <v-dialog
    v-model="dialog"
    :persistent="false"
    height="100%"
    max-height="100%"
    width="100%"
    max-width="100%"
    overlay-opacity="0.9"
    content-class="overflow-y-initial full-height full-width pa-5">
    <template v-if="dialog">
      <div class="d-flex justify-end">
        <v-btn
          :aria-label="$t('pageTemplate.label.close')"
          class="ma-4"
          icon
          @click="dialog = false">
          <v-icon color="white">fa-times</v-icon>
        </v-btn>
      </div>
      <v-card
        max-height="80vh"
        class="transparent"
        flat
        @click="dialog = false">
        <v-img
          :src="illustrationSrc"
          :aspect-ratio="2"
          height="80vh"
          contain />
      </v-card>
    </template>
  </v-dialog>
</template>
<script>
export default {
  data: () => ({
    dialog: false,
    illustrationSrc: null, 
  }),
  watch: {
    dialog() {
      if (this.dialog) {
        document.dispatchEvent(new CustomEvent('modalOpened'));
      } else {
        document.dispatchEvent(new CustomEvent('modalClosed'));
      }
    }
  },
  created() {
    this.$root.$on('layout-illustration-preview', this.open);
  },
  methods: {
    open(illustrationSrc) {
      this.illustrationSrc = illustrationSrc;
      this.dialog = true;
    },
  }
};
</script>