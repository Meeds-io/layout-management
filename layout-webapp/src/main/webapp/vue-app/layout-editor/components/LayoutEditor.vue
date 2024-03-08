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
      <layout-editor-content
        :page="page"
        :node="draftNode"
        :layout="draftLayout" />
    </div>
  </v-app>
</template>
<script>
export default {
  data: () => ({
    node: null,
    page: null,
    draftNode: null,
    draftLayout: null,
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
    draftNodeId() {
      return this.draftNode?.id;
    },
    draftPageKey() {
      return this.draftNode?.state?.pageRef;
    },
    draftPageRef() {
      return this.draftPageKey?.ref || (this.draftPageKey && `${this.draftPageKey.site.typeName}::${this.draftPageKey.site.name}::${this.draftPageKey.name}`);
    },
  },
  watch: {
    pageRef: {
      immediate: true,
      handler() {
        if (this.pageRef) {
          this.$root.pageRef = this.pageRef;
          this.$pageLayoutService.getPage(this.pageRef)
            .then(page => this.page = page);
        }
      },
    },
    draftPageRef: {
      immediate: true,
      handler() {
        if (this.draftPageRef) {
          this.$root.draftPageRef = this.draftPageRef;
          this.$pageLayoutService.getPageLayout(this.draftPageRef)
            .then(draftLayout => this.draftLayout = draftLayout);
        }
      },
    },
    nodeId: {
      immediate: true,
      handler() {
        if (this.nodeId) {
          this.$root.nodeId = this.nodeId;
          if (this.nodeId && !this.nodeLabels) {
            this.$navigationLayoutService.getNodeLabels(this.nodeId)
              .then(nodeLabels => this.nodeLabels = nodeLabels);
          }
          if (this.nodeId && !this.$root.nodeUri) {
            this.$navigationLayoutService.getNodeUri(this.nodeId)
              .then(uri => this.$root.nodeUri = uri);
          }
          this.$navigationLayoutService.getNode(this.nodeId)
            .then(node => this.node = node)
            .then(() => this.$navigationLayoutService.createDraftNode(this.node.id))
            .then(draftNode => this.draftNode = draftNode);
        }
      },
    },
    draftNodeId: {
      immediate: true,
      handler() {
        if (this.draftNodeId) {
          this.$root.draftNode = this.draftNode;
          this.$root.draftNodeId = this.draftNodeId;
          if (this.draftNodeId && !this.$root.draftNodeUri) {
            this.$navigationLayoutService.getNodeUri(this.draftNodeId)
              .then(uri => this.$root.draftNodeUri = uri);
          }
        }
      },
    },
    layout(newVal, oldVal) {
      if (!oldVal && newVal) {
        this.$root.$applicationLoaded();
      }
    },
  },
  created() {
    this.$root.$on('layout-draft-refresh', this.setDraftLayout);
  },
  beforeDestroy() {
    this.$root.$off('layout-draft-refresh', this.setDraftLayout);
  },
  methods: {
    setDraftLayout(draftLayout) {
      this.draftLayout = draftLayout;
    },
    getQueryParam(paramName) {
      const uri = window.location.search.substring(1);
      const params = new URLSearchParams(uri);
      return params.get(paramName);
    },
  },
};
</script>
