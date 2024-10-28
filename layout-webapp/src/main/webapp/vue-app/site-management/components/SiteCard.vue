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
  <v-card
    height="240"
    max-height="240"
    outlined
    hover
    @click="openSiteNavigationDrawer">
    <v-toolbar
      flat
      dense
      class="mt-2">
      <span class="font-weight-bold">  {{ site.displayName || site.name }} </span>
      <v-spacer />
      <site-management-site-card-menu
        :site="site" />
    </v-toolbar>
    <div class="d-flex flex-grow-0 flex-shrink-1 rich-editor-content">
      <span class="align-self-center text-wrap text-left text-break mx-4 text-truncate-4" v-sanitized-html="site.description"></span>
    </div>
    <v-footer absolute class="px-0 py-0">
      <v-toolbar
        flat
        dense>
        <v-icon
          v-if="isGroupSite"
          size="19"
          color="primary"
          class="fas fa-users me-3" />
        <v-icon
          v-else
          size="19"
          color="primary"
          class="fas fa-compass me-3" />
        <span> {{ siteTypeLabel }} </span>
      </v-toolbar>
    </v-footer>
  </v-card>
</template>
<script>
export default {
  props: {
    site: {
      type: Object,
      default: null,
    },
  },
  computed: {
    isGroupSite() {
      return this.site.siteType === 'GROUP';
    },
    siteTypeLabel() {
      return this.$t(`siteManagement.label.${this.site.siteType.toLowerCase()}`) ;
    },
  },
  methods: {
    openSiteNavigationDrawer() {
      if (this.site?.canEdit) {
        const params = {
          siteName: this.site.name,
          siteType: this.site.siteType,
          siteId: this.site.siteId,
          includeGlobal: this.site.name.toLowerCase() === eXo.env.portal.globalPortalName.toLowerCase()
        };
        this.$root.$emit('open-site-navigation-drawer', params);
      }
    },
  }
};
</script>