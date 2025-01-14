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
  <v-app class="siteManagementApplication">
    <v-main class="application-body">
      <site-management-toolbar
        ref="toolbar"
        @site-filter="keyword = $event" />
      <site-management-list
        :sites="sites"
        :loading="loading > 0"
        :keyword="keyword" />
    </v-main>
    <exo-confirm-dialog
      ref="deleteSiteConfirmDialog"
      :message="deleteConfirmMessage"
      :title="$t('siteManagement.label.confirmDelete')"
      :ok-label="$t('siteManagement.label.confirm')"
      :cancel-label="$t('siteManagement.label.cancel')"
      @ok="deleteSite" />
    <site-properties-drawer />
    <site-template-drawer />
    <site-navigation-drawer />
    <site-navigation-node-drawer />
    <site-navigation-element-drawer />
    <manage-permissions-drawer />
    <layout-analytics application-name="siteManagement" />
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      sites: [],
      loading: 0,
      siteToDelete: null,
      keyword: null,
      deleteConfirmMessage: '',
    };
  },
  created() {
    this.$root.$on('delete-site', this.confirmDelete);
    this.$root.$on('refresh-sites', this.getSites);
    this.getSites();
  },
  methods: {
    getSites() {
      this.loading++;
      return this.$siteService.getSites('PORTAL', null, 'public', true, true, false, false, false, null, true)
        .then(sites => this.sites = sites?.filter(s => !s?.properties?.IS_SPACE_PUBLIC_SITE) || [])
        .finally(() => this.loading--);
    },
    confirmDelete(siteToDelete) {
      this.siteToDelete = siteToDelete;
      this.deleteConfirmMessage = this.$t('siteManagement.label.confirmDelete.message', {0: this.siteToDelete.name});
      this.$refs.deleteSiteConfirmDialog.open();
    },
    deleteSite() {
      this.loading++;
      return this.$siteLayoutService.deleteSite(this.siteToDelete.siteType, this.siteToDelete.name)
        .then(() => this.$root.$emit('refresh-sites'))
        .finally(() => this.loading--);
    },
  }
};
</script>
