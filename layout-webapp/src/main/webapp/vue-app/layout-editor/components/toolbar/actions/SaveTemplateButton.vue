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
  <v-btn
    :loading="loading"
    :aria-label="$t('layout.save')"
    class="btn btn-primary d-flex align-center"
    elevation="0"
    @click="savePageTemplate">
    <span class="text-none">{{ $t('layout.save') }}</span>
  </v-btn>
</template>
<script>
export default {
  data: () => ({
    loading: false,
  }),
  computed: {
    newTemplate() {
      return !this.$root.pageTemplate?.name;
    },
  },
  methods: {
    savePageTemplate() {
      this.loading = true;
      window.setTimeout(() => this.openPageTemplateDrawer(), 10);
    },
    async openPageTemplateDrawer() {
      document.addEventListener('drawerOpened', this.endLoading);
      let pageLayout = JSON.parse(JSON.stringify(this.$root.layout));
      await this.attachPortletsPreferences(pageLayout);
      pageLayout = this.$layoutUtils.cleanAttributes(pageLayout, true, true);
      this.$root.$emit('layout-page-template-drawer-open', {
        content: JSON.stringify(pageLayout),
      }, false, true);
    },
    async attachPortletsPreferences(layoutModel) {
      if (layoutModel.children?.length) {
        await Promise.all(layoutModel.children.map(c => this.attachPortletsPreferences(c)));
      } else if (layoutModel.contentId) {
        const application = await this.$pageLayoutService.getPageApplicationLayout(this.$root.draftPageRef, layoutModel.storageId);
        layoutModel.preferences = application?.preferences;
      }
    },
    endLoading() {
      window.setTimeout(() => this.loading = false, 200);
      document.removeEventListener('drawerOpened', this.endLoading);
    },
  },
};
</script>