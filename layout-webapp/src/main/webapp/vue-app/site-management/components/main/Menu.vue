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
        :aria-label="$t('siteManagement.label.properties')"
        role="button"
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
        :aria-label="$t('siteManagement.label.navigation')"
        role="button"
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
        v-if="!isGroupSite && !isGlobalSite"
        :aria-label="$t('siteManagement.label.manageAccess')"
        role="button"
        class="subtitle-2 px-3"
        @click="$root.$emit('open-manage-permissions-drawer', site, true, true)">
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
        :aria-label="$t('siteManagement.label.delete')"
        role="button"
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
    isMetaSite() {
      return this.site.name === eXo.env.portal.defaultPortal;
    },
    isGlobalSite() {
      return this.site.name === 'global';
    },
    isGroupSite() {
      return this.site.siteType === 'GROUP';
    },
    isPortalSite() {
      return this.site.siteType === 'PORTAL';
    },
    canDelete() {
      return !this.isMetaSite && !this.isGlobalSite && !this.isGroupSite;
    },
    canEditSite() {
      return this.site.canEdit;
    },
  },
  watch: {
    displayActionMenu() {
      if (this.displayActionMenu) {
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
    closeMenu() {
      window.setTimeout(() => {
        this.displayActionMenu = false;
      },200);
    },
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
  }
};
</script>