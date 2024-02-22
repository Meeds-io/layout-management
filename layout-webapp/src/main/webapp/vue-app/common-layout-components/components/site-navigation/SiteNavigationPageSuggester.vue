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
  <v-flex id="siteNavigationsPagesSuggesterAutoComplete">
    <v-autocomplete
      ref="selectPage"
      v-model="page"
      :placeholder="suggesterLabels.placeholder"
      :items="items"
      :loading="loadingSuggestions"
      hide-no-data
      append-icon=""
      menu-props="closeOnClick, closeOnContentClick, maxHeight = 100"
      class="identitySuggester identitySuggesterInputStyle"
      content-class="identitySuggesterContent"
      width="100%"
      max-width="100%"
      item-text="displayName"
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
    <span v-if="!page" class="caption mt-n3 mx-2 position-absolute error-color">
      {{ $t('siteNavigation.required.error.message') }}
    </span>
  </v-flex>
</template>

<script>
export default {
  model: {
    prop: 'page',
    event: 'change'
  },
  props: {
    page: {
      type: Object,
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
    allSites: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      pages: [],
      searchTerm: null,
      loadingSuggestions: false,
      startSearchAfterInMilliseconds: 300,
      endTypingKeywordTimeout: 50,
      startTypingKeywordTimeout: 0,
      typing: false,
    };
  },
  computed: {
    suggesterLabels() {
      return {
        placeholder: this.$t('siteNavigation.label.pagesSuggester.searchPlaceholder'),
        noData: this.$t('siteNavigation.label.pagesSuggester.noData'),
      };
    },
    items() {
      this.pages.forEach(page => {
        page.displayName = page.displayName || page.name;
      });
      return this.pages.slice();
    },
  },
  watch: {
    searchTerm() {
      if (this.searchTerm) {
        this.startTypingKeywordTimeout = Date.now() + this.startSearchAfterInMilliseconds;
        if (!this.typing) {
          this.typing = true;
          this.waitForEndTyping();
        }
      }
    },
    page() {
      this.$emit('change', this.page);
    },
    siteType() {
      this.page = null;
    },
    allSites() {
      this.page = null;
      this.searchTerm = ' ';
    },
    siteName() {
      this.page = null;
      this.pages = [];
      this.searchTerm = ' ';
    },
  },
  mounted() {
    $('#siteNavigationsPagesSuggesterAutoComplete input').on('blur', () => {
      this.$refs.selectPage.isFocused = false;
    });
  },
  created() {
    this.$root.$on('set-selected-page', this.emitSelectedValue);
  },
  methods: {
    remove() {
      this.page = null;
      this.$emit('change', this.page);
    },
    searchPages() {
      if (this.allSites || (!this.allSites && this.siteType && this.siteName)) {
        this.loadingSuggestions = true;
        this.pages = [];
        this.$siteNavigationService.getPages(this.siteType, this.siteName, this.searchTerm)
          .then(pages => this.pages = pages)
          .finally(() => this.loadingSuggestions = false);
      }
    },
    waitForEndTyping() {
      window.setTimeout(() => {
        if (Date.now() - this.startTypingKeywordTimeout > this.startSearchAfterInMilliseconds) {
          this.typing = false;
          this.searchPages();
        } else {
          this.waitForEndTyping();
        }
      }, this.endTypingKeywordTimeout);
    },
    emitSelectedValue(value) {
      this.page = value;
      this.pages.push(this.page);
    },
  }
};
</script>
