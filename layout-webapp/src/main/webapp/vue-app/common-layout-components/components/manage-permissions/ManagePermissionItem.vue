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
  <div class="d-flex" v-if="permissionGroup?.id">
    <v-icon
      size="19"
      class="fas fa-users me-3" />
    <span
      class="d-flex text-truncate font-weight-bold text-start text-body mt-2">
      {{ displayName }}
    </span>
    <v-spacer />
    <permission-type-selector
      :membership-type="membershipType"
      class="mt-1"
      @membership-type-changed="$emit('membership-type-changed', $event)" />
    <v-icon
      size="16"
      color="grey lighten-1"
      class="me-2 iconStyle"
      @click="$emit('remove-permission', permissionGroup)">
      fas fa-trash
    </v-icon>
  </div>
</template>

<script>
export default {
  props: {
    permissionGroup: {
      type: Object ,
      default: null
    },
    membershipType: {
      type: String ,
      default: '*'
    }
  },
  computed: {
    displayName(){
      return this.permissionGroup?.label || this.permissionGroup?.displayName;
    }
  },
};
</script>
