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
  <v-layout class="pa-3">
    <v-row no-gutters>
      <v-col
        v-for="site in sites"
        :key="site.id"
        class="mb-4"
        cols="12"
        sm="6"
        lg="4">
        <site-management-site-card
          :site="site"
          class="mx-2" />
      </v-col>
    </v-row>
  </v-layout>
</template>

<script>
export default {
  props: {
    sites: {
      type: Array,
      default: () => [],
    },
  }
};
</script>
