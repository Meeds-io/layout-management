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
    <v-tooltip bottom>
      <template #activator="{on, attrs}">
        <div v-bind="attrs" v-on="on">
          <v-btn
            :href="draftPageUrl"
            :loading="saving"
            :aria-label="$t('layout.previewDraftPage')"
            target="_blank"
            icon
            @click.stop.prevent="previewPage">
            <v-icon size="20" class="icon-default-color">fa-eye</v-icon>
          </v-btn>
        </div>
      </template>
      <span>{{ $t('layout.previewDraftPage') }}</span>
    </v-tooltip>
  </div>
</template>
<script>
export default {
  data: () => ({
    saving: false,
  }),
  computed: {
    draftPageUrl() {
      return `/portal${this.$root.draftNodeUri}`;
    },
  },
  methods: {
    previewPage() {
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
  },
};
</script>