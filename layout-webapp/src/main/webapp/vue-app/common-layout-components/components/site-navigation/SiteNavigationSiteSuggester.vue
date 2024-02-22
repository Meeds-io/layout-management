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
  <v-flex id="siteNavigationsSiteSuggesterAutoComplete">
    <v-autocomplete
      ref="selectSiteNavigation"
      v-model="selectedSiteNavigation"
      :placeholder="suggesterLabels.placeholder"
      :items="sites"
      :loading="loadingSuggestions"
      append-icon=""
      menu-props="closeOnClick, closeOnContentClick, maxHeight = 100"
      class="identitySuggester identitySuggesterInputStyle"
      content-class="identitySuggesterContent"
      width="100%"
      max-width="100%"
      item-text="displayName"
      item-value="name"
      return-object
      persistent-hint
      hide-selected
      chips
      dense
      flat
      required
      @update:search-input="searchTerm = $event"
      attach>
      <template slot="no-data">
        <v-list-item class="pa-0">
          <v-list-item-title
            class="px-2">
            {{ suggesterLabels.noData }}
          </v-list-item-title>
        </v-list-item>
      </template>
      <template slot="selection" slot-scope="{item, selected}">
        <v-chip
          :input-value="selected"
          :close="true"
          class="identitySuggesterItem"
          @click:close="remove()">
          <span class="text-truncate">
            {{ item.displayName }}
          </span>
        </v-chip>
      </template>
      <template slot="item" slot-scope="data">
        <v-list-item-title
          class="text-truncate identitySuggestionMenuItemText"
          v-text="data.item.displayName" />
      </template>
    </v-autocomplete>
    <span v-if="!allSites && !selectedSiteNavigation" class="caption mt-n3 mx-2 position-absolute error-color">
      {{ $t('siteNavigation.required.error.message') }}
    </span>
  </v-flex>
</template>

<script>
export default {
  model: {
    prop: 'selectedSiteNavigation',
    event: 'change'
  },
  props: {
    selectedSiteNavigation: {
      type: Object,
      default: null,
    },
    allSites: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      sites: [],
      searchTerm: null,
      loadingSuggestions: false
    };
  },
  computed: {
    suggesterLabels() {
      return {
        placeholder: this.$t('siteNavigation.label.sitesSuggester.searchPlaceholder'),
        noData: this.$t('siteNavigation.label.sitesSuggester.noData')
      };
    }
  },
  watch: {
    selectedSiteNavigation() {
      this.$emit('change', this.selectedSiteNavigation);
    },
  },
  mounted() {
    $('#siteNavigationsSiteSuggesterAutoComplete input').on('blur', () => {
      this.$refs.selectSiteNavigation.isFocused = false;
    });
  },
  created(){
    this.getSites();
  },
  methods: {
    remove() {
      this.selectedSiteNavigation = null;
      this.$emit('change', this.selectedSiteNavigation);
    },
    getSites() {
      this.loadingSuggestions = true;
      return this.$siteService.getSites(null, 'USER', null, true, true, false, false, false, null, true)
        .then(sites => {
          this.sites = sites || [];
        })
        .finally(() => this.loadingSuggestions = false);
    },
  }
};
</script>
