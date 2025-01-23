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
  <v-app
    class="transparent"
    flat>
    <main class="content-box-sizing">
      <site-layout-editor-toolbar />
      <coediting
        v-if="$root.site"
        v-model="$root.draftSiteId"
        :object-id="siteId"
        :messages="{
          lockConfirmTitle: 'layout.lockConfirmTitle',
          lockConfirmMessage: 'layout.lockConfirmMessage',
          lockConfirmQuestion: 'layout.lockConfirmQuestion',
          lockConfirmOkLabel: 'layout.lockConfirmOkLabel',
          lockConfirmCancelLabel: 'layout.lockConfirmCancelLabel',
          draftConfirmTitle: 'layout.draftConfirmTitle',
          draftConfirmMessage: 'layout.draftConfirmMessage',
          draftConfirmQuestion: 'layout.draftConfirmQuestion',
          draftConfirmOkLabel: 'layout.draftConfirmOkLabel',
          draftConfirmCancelLabel: 'layout.draftConfirmCancelLabel',
        }"
        object-type="site"
        @initialized="initDraftSite"
        @locked="stopLoading"
        @draft-detected="stopLoading"
        @canceled="cancelEditSite">
        <site-layout-editor-content />
      </coediting>
      <layout-editor-cells-selection-box />
    </main>
    <site-layout-editor-edit-sections-drawer />
    <site-layout-editor-sidebar-section-drawer />
    <layout-analytics application-name="siteLayoutEditor" />
  </v-app>
</template>
<script>
export default {
  computed: {
    siteId() {
      return this.$root.siteId;
    },
  },
  created() {
    this.$root.$on('layout-draft-refresh', this.setDraftLayout);
    this.$root.$on('layout-site-saved', this.deleteDraft);
  },
  beforeDestroy() {
    this.$root.$off('layout-draft-refresh', this.setDraftLayout);
    this.$root.$off('layout-site-saved', this.deleteDraft);
  },
  methods: {
    async initDraftSite() {
      if (this.$root.draftSiteId) {
        this.$root.draftSite = await this.$siteLayoutService.getSiteById(this.$root.draftSiteId);
      } else {
        this.$root.draftSite = await this.$siteLayoutService.createDraftSite(this.$root.siteType, this.$root.siteName);
      }
      this.$root.draftSiteId = this.$root.draftSite.siteId;
      const draftLayout = await this.$siteLayoutService.getSiteLayout(this.$root.draftSiteType, this.$root.draftSiteName, 'contentId');
      this.setDraftLayout(draftLayout);
    },
    cancelEditSite() {
      this.stopLoading();
      window.close();
    },
    stopLoading() {
      document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
    },
    setDraftLayout(draftLayout) {
      this.$root.layout = draftLayout;
    },
    deleteDraft() {
      this.$root.$emit('coediting-remove-revision');
    },
  },
};
</script>
