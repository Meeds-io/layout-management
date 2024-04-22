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
      v-model="pageTemplateId"
      :items="items"
      item-text="name"
      item-value="value"
      class="caption pt-1 mb-5"
      mandatory
      outlined
      dense />
    <v-img
      :src="illustrationSrc"
      class="align-center mb-5 mx-auto"
      max-height="350"
      max-width="500" />
    <div
      v-if="description"
      v-sanitized-html="description"
      class="mb-5"></div>
  </div>
</template>

<script>
export default {
  data: () => ({
    pageTemplateId: null,
  }),
  computed: {
    pageTemplates() {
      return this.$root.pageTemplates;
    },
    items() {
      return this.pageTemplates?.map?.(t => ({
        name: this.$te(t.name) ? this.$t(t.name) : t.name,
        value: t.id,
      }));
    },
    pageTemplate() {
      return this.pageTemplates?.find?.(t => t.id === Number(this.pageTemplateId));
    },
    description() {
      return this.$te(this.pageTemplate?.description) ? this.$t(this.pageTemplate?.description) : this.pageTemplate?.description;
    },
    illustrationId() {
      return this.pageTemplate?.illustrationId;
    },
    illustrationSrc() {
      return this.illustrationId && `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/attachments/pageTemplate/${this.pageTemplateId}/${this.illustrationId}` ||  '/layout/images/page-templates/DefaultPreview.webp';
    },
  },
  watch: {
    pageTemplate() {
      if (this.pageTemplate) {
        this.$root.$emit('page-template-changed', this.pageTemplate);
      }
    },
    items: {
      immediate: true,
      handler() {
        if (this.items?.length) {
          this.pageTemplateId = this.items[0].value;
        }
      },
    },
  },
  created() {
    this.$root.$on('reset-element-drawer', this.reset);
  },
  methods: {
    reset() {
      this.pageTemplate = null;
    },
  }
};
</script>
