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
    <p class="font-weight-bold text-start text-color body-2">{{ whoCanView }}</p>
    <v-select
      v-model="type"
      :items="typeLabel"
      item-text="text"
      item-value="value"
      dense
      class="caption"
      outlined />
    <exo-identity-suggester
      v-if="showAccessPermissionsGroupSuggester"
      ref="navigationNodeAccessPermissions"
      v-model="accessPermission"
      :group-type="groupType"
      :all-groups-for-admin="allGroupsForAdmin"
      :group-member="userGroup"
      :search-options="searchOptions"
      :labels="suggesterLabels"
      name="accessPermissions"
      height="40"
      include-groups
      include-spaces
      required />
    <span v-if="!accessPermissions.length && showAccessPermissionsGroupSuggester" class="caption mx-2 mt-n3 position-absolute error-color">
      {{ $t('siteNavigation.required.error.message') }}
    </span>
    <template v-if="showAccessPermissionsList">
      <manage-permission-item
        v-for="(permission, index) in accessPermissions"
        :key="index"
        :permission-group="permission.group"
        :membership-type="permission.membershipType"
        @membership-type-changed="updatePermissionMembershipType(index, $event)"
        @remove-permission="$root.$emit('remove-access-permission', index)" />
    </template>
  </div>
</template>

<script>
export default {
  props: {
    accessPermissions: {
      type: Array,
      default: () => {
        return [];
      }
    },
    type: {
      type: String,
      default: 'Everyone'
    },
    isSite: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      allGroupsForAdmin: true,
      groupType: 'GROUP',
      userGroup: '/platform/users',
      searchOptions: {filterType: eXo.env.portal.isAdministrator && 'all' || 'member'},
      loading: false,
      navigationNode: null,
      accessPermission: null
    };
  },
  computed: {
    suggesterLabels() {
      return {
        placeholder: this.$t('siteNavigation.label.groupSuggester.placeholder'),
        noDataLabel: this.$t('siteNavigation.label.groupSuggester.noData')
      };
    },
    typeLabel() {
      return [
        {
          text: this.$t('siteNavigation.label.view.everyone'),
          value: 'Everyone',
        },
        {
          text: this.$t('siteNavigation.label.view.designedGroup'),
          value: 'GROUP',
        },
      ];
    },
    showAccessPermissionsGroupSuggester() {
      return this.type === 'GROUP';
    },
    showAccessPermissionsList() {
      return this.showAccessPermissionsGroupSuggester;
    },
    whoCanView() {
      return this.isSite && this.$t('siteManagement.label.whoCanView') || this.$t('siteNavigation.label.whoCanView');
    }
  },
  watch: {
    accessPermission() {
      if (this.accessPermission?.id) {
        const permission = {
          group: this.accessPermission,
          membershipType: '*'};
        this.$root.$emit('add-access-permission', permission);
        this.$refs.navigationNodeAccessPermissions.emitSelectedValue({});
      }
    },
    type(newVal, oldVal) {
      if ( newVal !== oldVal && oldVal !== ''){
        this.$root.$emit('change-access-permission-type', this.type);
      }
    }
  },
  methods: {
    updatePermissionMembershipType(index, membershipType) {
      const permission = {
        index: index,
        membershipType: membershipType};
      this.$root.$emit('update-access-permission-membership-type', permission);
    }
  }
};
</script>