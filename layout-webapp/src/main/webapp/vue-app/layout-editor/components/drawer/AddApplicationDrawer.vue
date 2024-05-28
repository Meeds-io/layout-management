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
    id="addApplicationDrawer"
    v-model="drawer"
    allow-expand
    right
    @closed="$root.$emit('layout-application-drawer-closed')">
    <template slot="title">
      {{ $t('layout.addApplicationTitle') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4 overflow-hidden"
        flat>
        <v-expansion-panels
          v-model="expanded"
          class="mt-n4"
          accordion
          focusable
          flat>
          <layout-editor-application-category-card
            v-for="(category, index) in applicationCategories"
            :key="category.id"
            :category="category"
            :applications="applications"
            :expanded="expanded === index"
            @addApplication="addApplication" />
          <layout-editor-application-category-card
            v-for="(category, index) in otherCategories"
            :key="category.name"
            :category="category"
            :applications="otherApplications"
            :expanded="expanded === index"
            @addApplication="addApplication" />
        </v-expansion-panels>
      </v-card>
      <layout-editor-application-add-all-drawer
        v-if="layoutAllAppsDrawer"
        ref="allAppsDrawer" />
    </template>
    <template v-if="canLoadMore && layoutAllAppsDrawer" #footer>
      <div class="d-flex align-center justify-center">
        <v-btn
          class="btn"
          @click="loadMore">
          {{ $t('layout.loadMore') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    expanded: 0,
    canLoadMore: true,
    layoutAllAppsDrawer: eXo.env.portal.layoutAllAppsDrawer,
    allApplications: [],
  }),
  computed: {
    applicationCategories() {
      const applicationCategories = this.$root.applicationCategories.slice();
      applicationCategories.forEach(c => c.label = this.$te(`layout.${c.name}`) ? this.$t(`layout.${c.name}`) : c.name);
      return applicationCategories;
    },
    applications() {
      return this.$root.allApplications;
    },
    otherApplications() {
      return this.allApplications.filter(a => !this.applications.find(app => app.contentId === a.contentId));
    },
    otherCategories() {
      return this.otherApplications.reduce((otherCategories, application) => {
        const category = otherCategories.find(c => c.name === application.applicationName);
        if (category) {
          category.applications.push(application);
        } else {
          otherCategories.push({
            name: application.applicationName,
            label: `<strong>${this.$t('layout.otherApplications')}:</strong> ${this.$te(`layout.${application.applicationName}`) ? this.$t(`layout.${application.applicationName}`) : application.applicationName}`,
            applications: [application],
          });
        }
        return otherCategories;
      }, []);
    },
  },
  methods: {
    open() {
      this.$refs.drawer.endLoading();
      this.$refs.drawer.open();
    },
    addApplication(application) {
      this.$refs.drawer.startLoading();
      this.$root.$emit('layout-add-application', application);
      window.setTimeout(() => {
        this.close();
      }, 200);
    },
    loadMore() {
      this.$refs.drawer.startLoading();
      this.$portletService.getPortlets()
        .then(applications => this.allApplications = applications)
        .finally(() => {
          this.canLoadMore = false;
          this.$refs.drawer.endLoading();
        });
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>