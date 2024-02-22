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
  <div>
    <template>
      <v-data-table
        :headers="expanded && headers || []"
        :items="navigationNodes"
        :loading="loading"
        disable-pagination
        hide-default-footer>
        <template slot="item" slot-scope="props">
          <site-navigation-node-item
            :navigation-node="props.item"
            :can-move-up="canMoveUpNode(props.item)"
            :can-move-down="canMoveDownNode(props.item)"
            :hide-children="hideChildren"
            :expanded="expanded" />
        </template>
      </v-data-table>
    </template>
  </div>
</template>

<script>
export default {
  props: {
    navigationNodes: {
      type: Object,
      default: null
    },
    expanded: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    },
    hideChildren: {
      type: Boolean,
      default: false
    },
  },
  computed: {
    headers() {
      return [
        {
          text: this.$t('siteNavigation.label.node'),
          value: 'node',
          width: this.expanded && '300' || '100',
          sortable: false
        },
        {
          text: this.$t('siteNavigation.label.nodeType'),
          align: 'center',
          value: 'nodeType',
          width: '120',
          sortable: false
        },
        {
          text: this.$t('siteNavigation.label.lastUpdated'),
          align: 'center',
          value: 'lastUpdated',
          width: '120',
          sortable: false
        },
        {
          text: this.$t('siteNavigation.label.visibility'),
          align: 'center',
          value: 'visibility',
          width: '120',
          sortable: false
        },
        {
          text: this.$t('siteNavigation.label.access'),
          align: 'center',
          value: 'access',
          width: '120',
          sortable: false
        }
      ];
    },
  },
  created() {
    this.$root.$on('delete-node', this.deleteNode);
    this.$root.$on('moveup-node', this.moveUpNode);
    this.$root.$on('movedown-node', this.moveDownNode);
  },
  methods: {
    canMoveUpNode(navigationNode) {
      return this.navigationNodes.indexOf(navigationNode) > 0;
    },
    canMoveDownNode(navigationNode) {
      return this.navigationNodes.indexOf(navigationNode) < this.navigationNodes.length - 1;
    }, 
    moveUpNode(navigationNodeId) {
      if (this.navigationNodes.length) {
        const index = this.navigationNodes.findIndex(navigationNode => navigationNode.id === navigationNodeId);
        if (index !== -1) {
          const previousNodeId = index >1 ? this.navigationNodes[index - 2].id : null;
          this.$siteNavigationService.moveNode(navigationNodeId, null, previousNodeId).then(() => {
            this.$root.$emit('refresh-navigation-nodes');
          });
        }
        
      }
    },
    moveDownNode(navigationNodeId) {
      if (this.navigationNodes.length) {
        const index = this.navigationNodes.findIndex(navigationNode => navigationNode.id === navigationNodeId);
        if (index !== -1) {
          const previousNodeId = this.navigationNodes[index + 1].id;
          this.$siteNavigationService.moveNode(navigationNodeId, null, previousNodeId).then(() => {
            this.$root.$emit('refresh-navigation-nodes');
          });
        }
      }
    },
    deleteNode(navigationNodeId) {
      if (this.navigationNodes.length) {
        const index = this.navigationNodes.findIndex(navigationNode => navigationNode.id === navigationNodeId);
        if (index >= 0) {
          this.navigationNodes.splice(index, 1);
        }
      }
    }
  }
};
</script>