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
  <v-card
    class="d-flex align-center no-border-radius border-box-sizing absolute-vertical-center t-0 px-4"
    height="57"
    min-width="100vw"
    width="100vw"
    flat>
    <v-icon class="icon-default-color">fa-pager</v-icon>
    <span class="px-2">{{ $t('layout.editPageName', {0: pageName}) }}</span>
    <v-spacer />
    <layout-editor-toolbar-history-buttons class="me-4" />
    <layout-editor-toolbar-save-button :disabled="disableSave" />
  </v-card>
</template>
<script>
export default {
  props: {
    page: {
      type: Object,
      default: null,
    },
    node: {
      type: Object,
      default: null,
    },
    nodeLabels: {
      type: Object,
      default: null,
    },
    disableSave: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    pageName() {
      return this.nodeLabels?.labels && (this.nodeLabels.labels[eXo.env.portal.language] || this.nodeLabels.labels[this.defaultLanguage]) || this.page?.title;
    },
  },
  mounted() {
    document.querySelector('#layoutTopBarContentChildren').append(this.$el);
  },
};
</script>
