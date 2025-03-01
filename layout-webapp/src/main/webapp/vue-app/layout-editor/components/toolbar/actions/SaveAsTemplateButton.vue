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
  <v-tooltip bottom>
    <template #activator="{on, attrs}">
      <div
        v-bind="attrs"
        v-on="on"
        class="me-3">
        <v-btn
          :loading="loading"
          :disabled="disabled"
          :aria-label="$t('layout.saveAsTemplate')"
          icon
          @click="savePageTemplate">
          <v-icon>fa-columns</v-icon>
        </v-btn>
      </div>
    </template>
    <span>{{ $t('layout.saveAsTemplate') }}</span>
  </v-tooltip>
</template>
<script>
export default {
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    loading: false,
  }),
  methods: {
    savePageTemplate() {
      this.loading = true;
      window.setTimeout(() => this.openPageTemplateDrawer(), 10);
    },
    openPageTemplateDrawer() {
      this.$root.$on('layout-draft-saved', this.handleDraftSavedSuccess);
      this.$root.$on('layout-draft-save-error', this.handleDraftSavedError);
      this.$root.$emit('layout-save-draft');
    },
    handleDraftSavedSuccess() {
      this.handleDraftSaved(true);
    },
    handleDraftSavedError() {
      this.handleDraftSaved();
    },
    async handleDraftSaved(success) {
      this.$root.$off('layout-draft-saved', this.handleDraftSavedSuccess);
      this.$root.$off('layout-draft-save-error', this.handleDraftSavedError);
      if (success) {
        try {
          document.addEventListener('drawerOpened', this.endLoading);
          const draftPageLayout = await this.$pageLayoutService.getPageLayout({
            pageRef: this.$root.draftPageRef,
            impersonate: true,
          });
          this.$layoutUtils.parseSections(draftPageLayout);
          const pageLayout = this.$layoutUtils.cleanAttributes(draftPageLayout, true, true);
          this.$root.$emit('layout-page-template-drawer-open', {
            content: JSON.stringify(pageLayout),
          }, false, true);
        } finally {
          window.setTimeout(() => this.loading = false, 2000);
        }
      } else {
        this.loading = false;
      }
    },
    endLoading() {
      window.setTimeout(() => this.loading = false, 200);
      document.removeEventListener('drawerOpened', this.endLoading);
    },
  },
};
</script>