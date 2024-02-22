<!--
Copyright (C) 2023 eXo Platform SAS.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
-->
<template>
  <v-app class="siteManagementApplication">
    <v-main class="white pa-5 card-border-radius">
      <site-management-toolbar />
      <site-management-sites-list :sites="sites" />
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
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      sites: [],
      siteToDelete: null,
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
      this.loading = true;
      return this.$siteService.getSites(null, 'USER', null, true, true, false, false, false, null, true)
        .then(sites => {
          this.sites = sites || [];
        })
        .finally(() => this.loading = false);
    },
    confirmDelete(siteToDelete) {
      this.siteToDelete = siteToDelete;
      this.deleteConfirmMessage = this.$t('siteManagement.label.confirmDelete.message', {0: this.siteToDelete.name});
      this.$refs.deleteSiteConfirmDialog.open();
    },
    deleteSite() {
      return this.$siteManagementService.deleteSite(this.siteToDelete.siteType, this.siteToDelete.name)
        .then(() => {
          this.$root.$emit('refresh-sites');
        });}
  }
};
</script>
