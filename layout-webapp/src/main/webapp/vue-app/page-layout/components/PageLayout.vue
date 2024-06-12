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
  <v-app
    class="layout-sections-parent singlePageApplication transparent"
    role="main"
    flat>
    <page-layout-page-container
      v-if="pageLayout"
      :container="pageLayout" />
  </v-app>
</template>
<script>
export default {
  data: () => ({
    page: null,
  }),
  computed: {
    pageLayout() {
      if (this.page?.children?.[0]?.children?.[0]?.template === 'system:/groovy/portal/webui/container/UIPageLayout.gtmpl') {
        return this.page?.children?.[0]?.children?.[0];
      } else {
        return this.page?.children?.[0];
      }
    },
  },
  created() {
    this.$pageLayoutService.getPageLayout(this.$root.pageRef)
      .then(page => this.page = page);
  },
};
</script>
