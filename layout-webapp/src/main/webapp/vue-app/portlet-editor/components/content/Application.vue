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
  <div
    ref="content"
    :id="id"
    class="layout-application no-applications-spacing full-width"></div>
</template>
<script>
export default {
  data: () => ({
    applicationContent: null,
  }),
  computed: {
    portletInstanceId() {
      return this.$root.portletInstanceId;
    },
    id() {
      return `UIPortlet-${this.portletInstanceId}`;
    },
  },
  watch: {
    applicationContent() {
      if (this.applicationContent) {
        this.installApplication();
      }
    },
  },
  created() {
    this.retrieveData();
  },
  mounted() {
    this.installApplication();
  },
  methods: {
    installApplication() {
      if (this.$refs.content && this.applicationContent) {
        this.$applicationUtils.handleApplicationContent(this.applicationContent, this.$refs.content);
        // Cleanup JS memory
        this.applicationContent = null;
      }
    },
    retrieveData() {
      fetch(`/portal/${eXo.env.portal.portalName}/portlet-viewer?portletInstanceId=${this.portletInstanceId}&noCache=true`, {
        credentials: 'include',
        method: 'GET',
        redirect: 'manual'
      })
        .then(resp => {
          if (resp?.status === 200) {
            return resp.text();
          } else {
            throw new Error('The retrieved page is not a portal page');
          }
        })
        .then(applicationContent => this.applicationContent = applicationContent);
    },
  }
};
</script>