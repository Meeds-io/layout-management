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
  <div>
    <span class="font-weight-bold text-start text-color body-2 mt-8">{{ $t('siteNavigation.label.pageTemplate') }}</span>
    <v-select
      v-model="pageTemplate"
      :items="pageTemplates"
      item-text="label"
      item-value="value"
      dense
      class="caption pt-1 mb-5"
      outlined />
    <v-img
      :src="pageTemplateSkeleton"
      class="align-center mx-auto"
      max-height="350"
      max-width="500" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      pageTemplate: 'empty',
      pageTemplates: [],
    };
  },
  computed: {
    pageTemplateSkeleton() {
      return `/layout/images/page-templates/${this.pageTemplate}.png`;
    }
  },
  watch: {
    pageTemplate() {
      if (this.pageTemplate) {
        this.$root.$emit('page-template-changed', this.pageTemplate);
      }
    }
  },
  created() {
    this.$root.$on('reset-element-drawer', this.reset);
    this.getPageTemplates();
  },
  methods: {
    getPageTemplates() {
      return this.$siteNavigationService.getPageTemplates()
        .then(pageTemplates => {
          this.pageTemplates = pageTemplates || [];
        });
    },
    reset() {
      this.pageTemplate = 'empty';
    }
  }
};
</script>
