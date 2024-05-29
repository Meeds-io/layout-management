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
  <tr>
    <!-- Illustration -->
    <td
      v-if="!$root.isMobile"
      align="center"
      width="70px">
      <v-img
        :src="illustrationSrc"
        max-height="30"
        max-width="60"
        contain
        @error="illustrationSrc = defaultIllustrationSrc" />
    </td>
    <!-- name -->
    <td
      v-sanitized-html="name"
      :width="$root.isMobile && '100%' || 'auto'"
      align="left">
    </td>
    <!-- description -->
    <td
      v-if="!$root.isMobile"
      align="left"
      v-sanitized-html="description"></td>
    <td
      align="center"
      width="50px">
      <v-btn
        icon
        x-small
        @click="$root.$emit('portlet-instance-drawer', portlet.contentId, portlet.name)">
        <span class="text-font-size text-color">{{ instancesCount }}</span>
      </v-btn>
    </td>
    <td
      align="center"
      width="50px">
      <portlets-menu :portlet="portlet" />
    </td>
  </tr>
</template>
<script>
export default {
  props: {
    portlet: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    hoverMenu: false,
    defaultIllustrationSrc: '/layout/images/portlets/DefaultPortlet.png',
    illustrationSrc: null,
  }),
  computed: {
    portletId() {
      return this.portlet?.id;
    },
    name() {
      return this.$te(this.portlet?.name) ? this.$t(this.portlet?.name) : this.portlet?.name;
    },
    description() {
      return this.$te(this.portlet?.description) ? this.$t(this.portlet?.description) : this.portlet?.description;
    },
    instancesCount() {
      return this.$root.portletInstances.filter(a => a.contentId === this.portlet.contentId).length || 0;
    },
  },
  watch: {
    hoverMenu() {
      if (!this.hoverMenu) {
        window.setTimeout(() => {
          if (!this.hoverMenu) {
            this.menu = false;
          }
        }, 200);
      }
    },
  },
  created() {
    this.illustrationSrc = `/${this.portlet.applicationName}/skin/DefaultSkin/portletIcons/${this.portlet.portletName}.png`;
  },
};
</script>