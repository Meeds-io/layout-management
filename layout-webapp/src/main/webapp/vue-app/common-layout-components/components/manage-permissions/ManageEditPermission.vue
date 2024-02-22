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
  <div>
    <p class="font-weight-bold text-start text-color body-2">{{ whoCanEdit }}</p>
    <exo-identity-suggester
      ref="navigationNodeEditPermissions"
      :labels="suggesterLabels"
      v-model="permission.group"
      :group-type="groupType"
      :all-groups-for-admin="allGroupsForAdmin"
      :group-member="userGroup"
      :search-options="searchOptions"
      name="editPermissions"
      height="40"
      include-groups
      include-spaces
      required />
    <span v-if="!permission.group" class="caption mt-n3 mx-2 position-absolute error-color">
      {{ $t('siteNavigation.required.error.message') }}
    </span>
    <manage-permission-item
      v-if="permission.group && permission.group"
      :permission-group="permission.group"
      :membership-type="permission.membershipType"
      @membership-type-changed="$root.$emit('edit-permission-membership-type-changed', $event)"
      @remove-permission="$root.$emit('reset-edit-permission')" />
  </div>
</template>

<script>
export default {
  props: {
    permission: {
      type: Object,
      default: () => ({
        group: {},
        membershipType: '*'}),
    },
    isSite: {
      type: Boolean,
      default: false
    },
  },
  data: () => ({
    loading: false,
    allGroupsForAdmin: true,
    groupType: 'GROUP',
    userGroup: '/platform/users',
    searchOptions: {filterType: eXo.env.portal.isAdministrator && 'all' || 'member'},
  }),
  computed: {
    suggesterLabels() {
      return {
        placeholder: this.$t('siteNavigation.label.groupSuggester.placeholder'),
        noDataLabel: this.$t('siteNavigation.label.groupSuggester.noData')
      };
    },
    whoCanEdit() {
      return this.isSite && this.$t('siteManagement.label.whoCanEdit') || this.$t('siteNavigation.label.whoCanEdit');
    }
  },
  watch: {
    'permission.group'(newVal) {
      if (newVal?.spaceId){
        this.$set(this.permission,'membershipType', '*');
        this.$root.$emit('edit-permission-changed');
      }
    }
  }
};
</script>