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
  <exo-drawer
    id="siteNavigationDrawer"
    ref="siteNavigationDrawer"
    :right="!$vuetify.rtl"
    eager
    allow-expand
    @closed="close">
    <template slot="title">
      <span>{{ $t('siteNavigation.drawer.title') }}<v-chip
        v-if="isMetaSite"
        class="ms-4 primary">
        {{ siteName }}
      </v-chip></span> 
    </template>
    <template slot="content">
      <div :class="$refs.siteNavigationDrawer?.expand ? 'singlePageApplication' : ' ' ">
        <v-toolbar
          color="white"
          flat
          dense>
          <v-btn
            v-if="isMetaSite"
            @click="createNode"
            class="btn btn-primary ms-2">
            {{ $t('siteNavigation.label.btn.createNode') }}
          </v-btn>
          <v-chip
            v-else
            class="ms-4 mt-3 mb-4 primary">
            {{ siteName }}
          </v-chip>
          <v-spacer />
          <select
            id="siteNavigationDrawerFilterSelect"
            v-model="filter"
            v-if="$refs.siteNavigationDrawer?.expand"
            class="ignore-vuetify-classes width-auto pa-0 me-5 mb-0">
            <option
              v-for="item in navigationsFilter"
              :key="item.value"
              :value="item.value">
              {{ item.text }}
            </option>
          </select>
        </v-toolbar>
        <site-navigation-nodes-list
          :navigation-nodes="navigationNodesToDisplay"
          :expanded="$refs.siteNavigationDrawer && $refs.siteNavigationDrawer?.expand"
          :loading="loading"
          :hide-children="hideChildren" />
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data() {
    return {
      navigationNodes: [],
      navigationNodesToDisplay: [],
      siteName: null,
      siteId: null,
      includeGlobal: false,
      loading: false,
      filter: 'ALL',
      site: null,
    };
  },
  computed: {
    navigationsFilter() {
      return [{
        text: this.$t('siteNavigation.label.all'),
        value: 'ALL',
      },{
        text: this.$t('siteNavigation.label.group'),
        value: 'GROUP',
      },{
        text: this.$t('siteNavigation.label.page'),
        value: 'PAGE',
      },{
        text: this.$t('siteNavigation.label.link'),
        value: 'LINK',
      }];
    },
    hideChildren(){
      return this.filter !== 'ALL';
    },
    isMetaSite() {
      return this.site?.metaSite;
    }
  },
  watch: {
    filter() {
      this.$root.$emit('site-navigation-hide-nodes-tree');
      this.$nextTick().then (() => this.getNavigationNodes());
    },
  },
  created() {
    this.$root.$on('navigation-node-deleted', this.getNavigationNodes);
    this.$root.$on('refresh-navigation-nodes', this.getNavigationNodes);
    this.$root.$on('open-site-navigation-drawer', this.open);
  },
  methods: {
    open(event) {
      this.siteName = event?.siteName || eXo.env.portal.siteKeyName;
      this.siteId = event?.siteId || eXo.env.portal.siteId;
      this.includeGlobal = event?.includeGlobal || false;
      this.getNavigationNodes();
      this.$refs.siteNavigationDrawer.open();
      this.$nextTick().then(() =>  this.$root.$emit('site-navigation-drawer-opened'));
    },
    close() {
      this.siteName = null;
      this.siteId = null;
      this.site = null;
      this.includeGlobal = false;
      this.navigationNodes = [];
      this.navigationNodesToDisplay = [];
      this.$root.$emit('site-navigation-hide-nodes-tree');
      this.$refs.siteNavigationDrawer.close();
    },
    getNavigationNodes() {
      this.loading = true;
      return this.$siteService.getSiteById(this.siteId, {
        expandNavigations: true,
        excludeEmptyNavigationSites: true,
        lang: eXo.env.portal.language,
      })
        .then(site => {
          this.site = site;
          this.navigationNodes = site.siteNavigations || [];
          this.filterNavigationNodes();
        })
        .finally(() => this.loading = false);
    },
    createNode() {
      this.$root.$emit('open-site-navigation-add-node-drawer', this.site.rootNode);
    },
    filterNavigationNodes(){
      this.navigationNodesToDisplay = [];
      if (this.filter === 'PAGE') {
        this.filterPageNavigationNodes(this.navigationNodes);
      } else if ( this.filter === 'LINK') {
        this.filterLinkNavigationNodes(this.navigationNodes);
      } else if ( this.filter === 'GROUP') {
        this.filterGroupNavigationNodes(this.navigationNodes);
      } else {
        this.navigationNodesToDisplay = this.navigationNodes;
      }
    },
    filterPageNavigationNodes(navigationNodes){
      navigationNodes.forEach(child => {
        if (child.children.length > 0) {
          this.filterPageNavigationNodes(child.children);
        }
        if (child.pageKey && !child.pageLink) {
          this.navigationNodesToDisplay.push(child);
        }
      });
    },
    filterLinkNavigationNodes(navigationNodes){
      navigationNodes.forEach(child => {
        if (child.children.length > 0) {
          this.filterLinkNavigationNodes(child.children);
        }
        if (child.pageKey && child.pageLink) {
          this.navigationNodesToDisplay.push(child);
        }
      });
    },
    filterGroupNavigationNodes(navigationNodes){
      navigationNodes.forEach(child => {
        if (child.children.length > 0) {
          this.filterGroupNavigationNodes(child.children);
        }
        if (!child.pageKey && !child.pageLink) {
          this.navigationNodesToDisplay.push(child);
        }
      });
    },
  }
};
</script>
