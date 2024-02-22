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
  <v-tooltip bottom>
    <template #activator="{ on, attrs }">
      <v-btn
        size="16"
        outlined
        icon
        class="mx-2"
        :disabled="disabled">
        <v-icon
          v-on="on"
          v-bind="attrs"
          class="mb-2"
          :color="iconColor"
          :class="iconClass"
          size="16"
          @click="openSiteNavigationDrawer">
          fas fa-sitemap
        </v-icon>
      </v-btn>
    </template>
    <span>{{ $t('siteNavigation.button.tooltip.label') }}</span>
  </v-tooltip>
</template>

<script>
export default {
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    iconClass: {
      type: String,
      default: null,
    },
    iconColor: {
      type: String,
      default: null,
    },
    siteName: {
      type: String,
      default: null,
    },
    siteType: {
      type: String,
      default: null,
    },
    siteId: {
      type: String,
      default: null,
    },
  },
  methods: {
    openSiteNavigationDrawer() {
      let params;
      if (this.siteName && this.siteType) {
        params = {
          siteName: this.siteName,
          siteType: this.siteType,
          siteId: this.siteId,
        };
      }
      document.dispatchEvent(new CustomEvent('open-site-navigation-drawer',{detail: params}));
    }
  }
};
</script>
