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
  <exo-drawer
    ref="drawer"
    id="selectApplicationCategoryDrawer"
    v-model="drawer"
    :loading="$root.loadingPortletInstances"
    allow-expand
    right>
    <template slot="title">
      {{ $t('layout.selectApplicationCategoryTitle') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="d-flex flex-wrap my-4 ms-4 me-2 justify-space-between overflow-hidden"
        flat>
        <layout-editor-application-category-card
          v-for="category in portletInstanceCategories"
          :key="category.id"
          :category="category"
          :applications="applications"
          class="me-2" />
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    portletInstances: [],
  }),
  computed: {
    portletInstanceCategories() {
      const portletInstanceCategories = this.$root.portletInstanceCategories.slice();
      portletInstanceCategories.forEach(c => c.label = this.$te(`layout.${c.name}`) ? this.$t(`layout.${c.name}`) : c.name);
      return portletInstanceCategories;
    },
    applications() {
      return this.$root.portletInstances;
    },
  },
  created() {
    this.$root.$on('layout-add-application', this.close);
  },
  beforeDestroy() {
    this.$root.$off('layout-add-application', this.close);
  },
  methods: {
    open() {
      this.$root.$emit('layout-editor-portlet-instances-refresh');
      this.$refs.drawer.open();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>