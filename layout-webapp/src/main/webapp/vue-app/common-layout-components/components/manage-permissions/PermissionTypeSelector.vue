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
  <div class="text-center pe-1">
    <span class="text-capitalize clickable" @click="showMenu = true"> {{ getMembershipTypeLabel(membershipType) }}</span>
    <v-menu
      v-model="showMenu"
      transition="slide-x-reverse-transition"
      bottom
      offset-y
      left
      :close-on-content-click="false">
      <template #activator="{ on, attrs }">
        <v-btn
          icon
          small
          v-bind="attrs"
          v-on="on">
          <v-icon
            :size="16">
            mdi-chevron-down
          </v-icon>
        </v-btn>
      </template>
      <v-list
        class="pa-0">
        <v-list-item
          class="custom-list-item-min-height"
          v-for="(membershipTypeItem, index) in membershipTypes"
          :key="index">
          <v-list-item-title @click="membershipType = membershipTypeItem.name" class="clickable text-capitalize">
            <span class="text-font-size clickable">
              {{ getMembershipTypeLabel(membershipTypeItem.name) }}
            </span>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
export default {
  props: {
    membershipType: {
      type: String,
      default: '*',
    },
  },
  data: () => ({
    membershipTypes: [],
    showMenu: false,
  }),
  watch: {
    membershipType() {
      this.$emit('membership-type-changed', this.membershipType);
    }
  },
  created() {
    $(document).on('mousedown', () => {
      if (this.showMenu) {
        window.setTimeout(() => {
          this.showMenu = false;
        }, 200);
      }
    });
    this.getMembershipTypes();
  },
  methods: {
    getMembershipTypes() {
      return this.$siteNavigationService.getMembershipTypes()
        .then(membershipTypes => {
          this.membershipTypes = membershipTypes || [];
        });
    },
    getMembershipTypeLabel(membershipType){
      if (!this.$t(`siteNavigation.label.membershipType.${membershipType}`).includes('siteNavigation.label')) {
        return  this.$t(`siteNavigation.label.membershipType.${membershipType}`);
      } else {
        return membershipType;
      }
    }
  }
};
</script>