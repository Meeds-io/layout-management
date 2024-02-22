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
  <exo-drawer
    ref="nodeIconPickerDrawer"
    id="nodeIconPickerDrawer"
    :right="!$vuetify.rtl"
    eager
    :allow-expand="expanded"
    @closed="close">
    <template slot="title">
      <div class="d-flex">
        <v-icon
          size="16"
          class="clickable"
          @click="close">
          fas fa-arrow-left
        </v-icon>
        <span class="ms-2"> {{ $t('siteNavigation.nodeIconPickerDrawer.title') }}</span>
      </div>
    </template>
    <template slot="content">
      <v-card-text class="d-flex pb-2">
        <v-text-field
          v-model="keyword"
          :placeholder="$t('siteNavigation.label.icon.searchPlaceholder')"
          prepend-inner-icon="fa-filter"
          class="inputPeopleFilter"
          @keyup="filterIcons($event)">
          <template #append>
            <v-btn
              icon
              size="22"
              @click="closeFilter()">
              <v-icon
                size="18"
                color="primary">
                mdi-close
              </v-icon>
            </v-btn>
          </template>
        </v-text-field>
      </v-card-text>
      <v-card-text class="d-flex pb-2">
        <v-container id="iconPicker">
          <v-row class="ma-0">
            <v-col 
              v-for="icon in icons"
              :key="icon.iconValue"
              cols="3"
              class="pa-0 pa-1">
              <v-sheet 
                class="rounded-lg clickable light-grey-background"
                :class="icon.iconColor"
                @click="selectIcon(icon)">
                <div class="d-flex flex-grow-1">
                  <v-icon
                    v-if="selectedIcon === icon"
                    color="primary"
                    size="18"
                    class="pb-8 py-1 pl-1">
                    fas fa-check-square
                  </v-icon>
                  <v-icon
                    size="32"
                    class="flex-grow-1 align-center pt-6 pb-2"
                    color="black">
                    {{ icon.iconValue }}
                  </v-icon>
                </div>
                <v-tooltip bottom>
                  <template #activator="{ on, attrs }">
                    <p                 
                      v-on="on"
                      v-bind="attrs"
                      class="align-center text-truncate text-caption text--primary font-weight-medium pb-2 mx-1">
                      {{ icon.iconName }}
                    </p>
                  </template>
                  <span>{{ icon.iconName }}</span>
                </v-tooltip>
              </v-sheet>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-text class="align-center">
        <v-btn
          v-if="!search"
          elevation="0"
          @click="showMoreIcons">
          {{ $t('siteNavigation.btn.showMore.title') }}
        </v-btn>
      </v-card-text>
    </template>
    <template slot="footer">
      <div class="d-flex justify-end">
        <v-btn
          class="btn ms-2"
          @click="close">
          {{ $t('siteNavigation.label.btn.cancel') }}
        </v-btn>
        <v-btn
          :loading="loading"
          :disabled="disabled"
          class="btn btn-primary ms-2"
          @click="saveIcon">
          {{ $t('siteNavigation.label.btn.save') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>


<script>
import fontLibrary from '../../../../js/icons.js';
export default {
  props: {
    expanded: {
      type: Boolean,
      default: null,
    },
  },
  data() {
    return {
      fontLibrary,
      allIcons: {},
      iconsNumber: 16,
      showMore: false,
      selectedIcon: null,
      filtredIcons: [],
      search: false,
      keyword: null,
      disabled: true
    };
  },
  computed: {
    icons() {
      if (this.search && this.filtredIcons) {
        return this.filtredIcons;
      } else {
        return Array.from(this.allIcons).slice(0, this.iconsNumber);
      }
    },
  },
  created() {
    this.$root.$on('open-node-icon-picker-drawer', this.open);
  },
  methods: {
    open() {
      this.allIcons = this.fontLibrary.icons.map(icon => ({
        'iconName': icon.split('fa-')[1],
        'iconValue': icon,
      })).sort((icon1, icon2) => icon1.iconName.localeCompare(icon2.iconName));
      this.$nextTick().then(() => this.$refs.nodeIconPickerDrawer.open());
    },
    close() {
      this.keyword = null;
      this.search = false;
      this.selectedIcon = null;
      this.iconsNumber = 16;
      this.$refs.nodeIconPickerDrawer.close();
    },
    showMoreIcons() {
      this.iconsNumber += 16;
    },
    selectIcon(icon) {
      if (this.selectedIcon && this.selectedIcon !== icon) {
        this.selectedIcon.iconColor = 'light-grey-background';
        icon.iconColor = 'grey-background';
        this.selectedIcon = icon;
      } else if (this.selectedIcon === icon) {
        icon.iconColor = 'light-grey-background';
        this.disabled = true;
        this.selectedIcon = null;
      } else {
        icon.iconColor = 'grey-background';
        this.disabled = false;
        this.selectedIcon = icon;
      }
    },
    saveIcon() {
      this.$root.$emit('update-node-icon', this.selectedIcon.iconValue);
      this.$nextTick().then(() => this.close());
    },
    filterIcons(event) {
      const search = event.target.value.trim();
      if (search.length > 0) {
        this.search = true;
        this.filtredIcons = this.allIcons.filter((item) => {
          return item.iconName.includes(search);
        });
      } else {
        this.filtredIcons = null;
        this.search = false;
      }
    },
    closeFilter() {
      this.keyword = '';
      this.search = false;
    }
  },
};
</script>