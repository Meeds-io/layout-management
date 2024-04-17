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
    v-model="drawer"
    go-back-button
    allow-expand
    right
    @expand-updated="expand = $event">
    <template #title>
      <span :title="drawerTitle" class="text-truncate">
        {{ drawerTitle }}
      </span>
    </template>
    <template v-if="drawer" #content>
      <v-card
        :id="applicationId"
        data-mode="EDIT"
        max-width="100%"
        class="ma-4"
        flat>
        <div
          ref="content"
          :id="id"
          class="layout-application"></div>
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    expand: false,
    applicationId: null,
    applicationCategoryTitle: null,
    applicationTitle: null,
  }),
  computed: {
    drawerTitle() {
      return this.$t('layout.editPortletTitle', {
        0: this.applicationTitle,
        1: this.applicationCategoryTitle,
      });
    },
    nodeUri() {
      return this.$root.draftNodeUri;
    },
  },
  watch: {
    drawer() {
      if (this.drawer) {
        window.eXo.env.portal.maximizedPortletMode = 'EDIT';
        this.$refs.drawer.toogleExpand();
        window.setTimeout(() => this.installApplication(), 200);
      } else {
        window.eXo.env.portal.maximizedPortletMode = null;
        this.$root.$emit('layout-editor-portlet-properties-updated', this.applicationId);
      }
    },
  },
  created() {
    this.$root.$on('layout-editor-portlet-edit', this.open);
  },
  beforeDestroy() {
    this.$root.$off('layout-editor-portlet-edit', this.open);
  },
  methods: {
    open(applicationId, applicationCategoryTitle, applicationTitle) {
      this.applicationId = applicationId;
      this.applicationCategoryTitle = applicationCategoryTitle;
      this.applicationTitle = applicationTitle;
      this.$nextTick(() => this.$refs.drawer.open());
    },
    close() {
      this.$refs.drawer.close();
    },
    installApplication() {
      if (this.$refs.content) {
        this.$applicationUtils.installApplication(this.nodeUri, this.applicationId, this.$refs.content, 'EDIT');
      }
    },
  },
};
</script>