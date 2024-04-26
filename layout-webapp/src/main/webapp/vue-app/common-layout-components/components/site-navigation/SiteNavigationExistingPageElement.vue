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
    <span class="font-weight-bold text-start text-truncate-2 text-color body-2 mt-8">{{ $t('siteNavigation.label.selectExistingPage') }}</span>
    <v-flex class="mt-4 text-left">
      <v-chip
        :class="allSitesChipClass"
        @click="allSites = true">
        <span class="text-truncate">
          {{ $t('siteNavigation.label.allSites') }}
        </span>
      </v-chip>
      <v-chip
        :class="selectSiteChipClass"
        @click="allSites = false">
        <span class="text-truncate">
          {{ $t('siteNavigation.label.selectSite') }}
        </span>
      </v-chip>
    </v-flex>
    <site-navigation-site-suggester
      v-if="!allSites"
      v-model="selectedSiteNavigation"
      :all-sites="allSites"
      class="mb-6" />
    <site-navigation-page-suggester
      :page="selectedPage"
      :all-sites="allSites"
      :site-type="selectedSiteNavigation?.siteType"
      :site-name="selectedSiteNavigation?.name" />
  </div>
</template>

<script>
export default {
  props: {
    selectedPage: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      allSites: true,
      selectedSiteNavigation: null,
    };
  },
  computed: {
    allSitesChipClass() {
      return this.allSites && 'primary' || '';
    },
    selectSiteChipClass() {
      return !this.allSites &&'primary' || '';
    },
  },
  watch: {
    allSites(){
      this.selectedSiteNavigation = null;
    },
  },
};
</script>
