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
  <v-menu
    v-model="menu"
    :close-on-content-click="false"
    :close-on-click="false"
    content-class="pagePreviewMenu"
    transition="slide-x-reverse-transition"
    bottom
    offset-y
    left>
    <template #activator="{on, attrs}">
      <v-btn
        v-bind="attrs"
        v-on="on"
        :title="$t('layout.previewDraftPage')"
        :loading="saving"
        :aria-label="$t('layout.previewDraftPage')"
        class="me-3"
        icon
        @click.stop.prevent="menu = true">
        <v-icon size="20" class="icon-default-color">fa-eye</v-icon>
      </v-btn>
    </template>
    <v-card
      class="d-flex flex-column white pa-5"
      width="450"
      max-width="450">
      <span>{{ $t('layout.previewAsASpace') }}</span>
      <identity-suggester
        v-model="space"
        :search-options="searchOptions"
        :labels="suggesterLabels"
        include-spaces />
      <div class="d-flex justify-end align-center">
        <v-btn
          class="btn"
          @click="menu = false">
          {{ $t('layout.cancel') }}
        </v-btn>
        <v-btn
          class="btn-primary ms-4"
          elevation="0"
          @click.stop.prevent="previewPage">
          {{ $t('layout.preview') }}
        </v-btn>
      </div>
    </v-card>
  </v-menu>
</template>
<script>
export default {
  data: () => ({
    menu: false,
    saving: false,
    space: null,
    searchOptions: {
      filterType: 'member_or_managing'
    },
  }),
  computed: {
    draftPageUrl() {
      return `/portal${this.$root.draftNodeUri}?previewSpaceId=${this.space?.spaceId || ''}&mask=true`;
    },
    suggesterLabels() {
      return {
        placeholder: this.$t('layout.searchForASpace'),
        noDataLabel: this.$t('layout.noSpaceFound'),
      };
    },
  },
  watch: {
    menu() {
      if (this.menu) {
        document.addEventListener('mousedown', this.closeMenu);
      } else {
        document.removeEventListener('mousedown', this.closeMenu);
      }
    },
  },
  beforeDestroy() {
    document.removeEventListener('mousedown', this.closeMenu);
  },
  methods: {
    previewPage() {
      this.menu = false;
      this.saving = true;
      this.$root.$on('layout-draft-saved', this.openPreviewPage);
      this.$root.$on('layout-draft-save-error', this.stopLoading);
      this.$root.$emit('layout-save-draft');
    },
    stopLoading() {
      this.saving = false;
    },
    openPreviewPage() {
      this.$root.$off('layout-draft-saved', this.openPreviewPage);
      this.saving = false;
      window.open(this.draftPageUrl, '_blank');
    },
    closeMenu(event) {
      if (this.menu && !event?.target?.closest?.('.menuable__content__active')) {
        window.setTimeout(() => {
          this.menu = false;
        },200);
      }
    },
  },
};
</script>