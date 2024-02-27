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
    class="transparent"
    role="main"
    flat>
    <div>
      <layout-editor-toolbar
        :page="page"
        :node="node"
        :node-labels="nodeLabels" />
      <layout-editor-content :page="page" />
    </div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    page: null,
    node: null,
    nodeLabels: null,
  }),
  computed: {
    pageKey() {
      return this.node?.state?.pageRef;
    },
    pageRef() {
      return this.getQueryParam('pageId') || this.pageKey?.ref || (this.pageKey && `${this.pageKey.site.typeName}::${this.pageKey.site.name}::${this.pageKey.name}`);
    },
    nodeId() {
      return this.getQueryParam('nodeId');
    },
  },
  watch: {
    pageRef: {
      immediate: true,
      handler() {
        if (this.pageRef) {
          this.$sitePageService.getPage(this.pageRef)
            .then(page => this.page = page);
        }
      },
    },
    nodeId: {
      immediate: true,
      handler() {
        if (this.nodeId) {
          this.$siteNavigationService.getNode(this.nodeId)
            .then(node => this.node = node);
          this.$siteNavigationService.getNodeLabels(this.nodeId)
            .then(nodeLabels => this.nodeLabels = nodeLabels);
        }
      },
    },
  },
  methods: {
    getQueryParam(paramName) {
      const uri = window.location.search.substring(1);
      const params = new URLSearchParams(uri);
      return params.get(paramName);
    },
  },
};
</script>
