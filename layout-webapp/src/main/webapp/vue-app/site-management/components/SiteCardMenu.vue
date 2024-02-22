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
  <v-menu
    v-model="displayActionMenu"
    transition="slide-x-reverse-transition"
    :right="!$vuetify.rtl"
    offset-x
    offset-y
    class="px-0 pt-0 mx-2 overflow-visible">
    <template #activator="{ on, attrs }">
      <v-btn
        v-bind="attrs"
        icon
        v-on="on"
        v-if="canEditSite">
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
    </template>
    <v-list class="pa-0" dense>
      <v-list-item
        v-if="isPortalSite && !isGlobalSite"
        class="subtitle-2 px-3"
        @click="openSitePropertiesDrawer">
        <v-icon
          size="16"
          class="me-2 ms-0"
          color="primary">
          fa fa-cog
        </v-icon>
        <v-list-item-title
          class="subtitle-2">
          <span class="ps-1">{{ $t('siteManagement.label.properties') }}</span>
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        class="subtitle-2 px-3"
        @click="openSiteNavigationDrawer">
        <v-icon
          size="13"
          class="me-2 ms-0"
          color="primary">
          fas fa-sitemap
        </v-icon>
        <v-list-item-title
          class="subtitle-2">
          <span class="ps-1">{{ $t('siteManagement.label.navigation') }}</span>
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-if="canEditLayout"
        class="subtitle-2 px-3"
        @click="editSiteLayout">
        <v-icon
          size="13"
          class="me-2 ms-0"
          color="primary">
          fas fa-table
        </v-icon>
        <v-list-item-title
          class="subtitle-2">
          <span class="ps-1">{{ $t('siteManagement.label.editLayout') }}</span>
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-if="!isGroupSite && !isGlobalSite"
        class="subtitle-2 px-3"
        @click="$root.$emit('open-manage-permissions-drawer', site, true)">
        <v-icon
          size="13"
          class="me-2 ms-0"
          color="primary">
          fas fa-shield-alt
        </v-icon>
        <v-list-item-title
          class="subtitle-2">
          <span class="ps-1">{{ $t('siteManagement.label.manageAccess') }}</span>
        </v-list-item-title>
      </v-list-item>
      <v-list-item
        v-if="canDelete"
        class="subtitle-2 px-3"
        @click="$root.$emit('delete-site', site)">
        <v-icon
          size="13"
          class="me-2 ms-0"
          color="primary">
          fas fa-trash
        </v-icon>
        <v-list-item-title
          class="subtitle-2">
          <span class="ps-1">{{ $t('siteManagement.label.delete') }}</span>
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
export default {
  props: {
    site: {
      type: Object,
      default: null,
    }
  },
  data: () => ({
    displayActionMenu: false,
  }),
  computed: {
    isDefaultPortalSite() {
      return this.site.name.toLowerCase() === eXo.env.portal.defaultPortalName.toLowerCase();
    },
    isGlobalSite() {
      return this.site.name.toLowerCase() === eXo.env.portal.globalPortalName.toLowerCase();
    },
    isGroupSite() {
      return this.site.siteType === 'GROUP';
    },
    canDelete() {
      return !(this.isDefaultPortalSite || this.isGlobalSite || this.isGroupSite);
    },
    isPortalSite() {
      return this.site.siteType === 'PORTAL';
    },
    canEditLayout() {
      return !(this.isGlobalSite || this.isGroupSite) && (this.site?.metaSite || !this.site.displayed);
    },
    canEditSite() {
      return this.site.canEdit;
    },
  },
  created() {
    $(document).on('mousedown', () => {
      if (this.displayActionMenu) {
        window.setTimeout(() => {
          this.displayActionMenu = false;
        }, 200);
      }
    });
  },
  methods: {
    openSiteNavigationDrawer() {
      const params = {
        siteName: this.site.name,
        siteType: this.site.siteType,
        siteId: this.site.siteId,
        includeGlobal: this.site.name.toLowerCase() === eXo.env.portal.globalPortalName.toLowerCase()
      };
      this.$root.$emit('open-site-navigation-drawer', params);
    },
    openSitePropertiesDrawer() {
      this.$root.$emit('open-site-properties-drawer', this.site);
    },
    editSiteLayout() {
      this.$siteManagementService.editSiteLayout(this.site.name);
    }
  }
};
</script>