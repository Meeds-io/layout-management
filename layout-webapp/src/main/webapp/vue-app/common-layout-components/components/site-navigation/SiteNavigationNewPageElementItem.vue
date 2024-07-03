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
    <div
      :style="imageContainerStyle"
      class="overflow-hidden border-color rounded pa-1 hover-elevation"
      @click="selectTemplate">
      <img
        :src="illustrationSrc"
        :style="imageStyle"
        class="align-center mb-5 mx-auto object-fit-cover">
    </div>
    <div class="text-center mt-2">
      <p
        v-if="title"
        v-sanitized-html="title"
        class="mb-0 font-weight-regular text-truncate subtitle-2"></p>
      <p
        v-if="description"
        v-sanitized-html="description"
        class="mb-0 caption text-truncate-2"></p>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    pageTemplate: {
      type: Object,
      default: null
    },
  },
  computed: {
    title() {
      return this.pageTemplate?.name;
    },
    description() {
      return this.pageTemplate?.description;
    },
    illustrationId() {
      return this.pageTemplate?.illustrationId;
    },
    pageTemplateId() {
      return this.pageTemplate?.id;
    },
    illustrationSrc() {
      return this.illustrationId && `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/attachments/pageTemplate/${this.pageTemplateId}/${this.illustrationId}` ||  '/layout/images/page-templates/DefaultPreview.webp';
    },
    imageStyle() {
      return {
        'max-width': '100%',
        'min-width': '100%',
        'min-height': '100%'
      };
    },
    imageContainerStyle() {
      return {
        'max-width': '100%',
        'min-width': '100%',
        'min-height': '100px',
        'max-height': '100px',
        'height': '100px'
      };
    },
  },
  methods: {
    selectTemplate() {
      this.$root.$emit('page-template-changed', this.pageTemplate);
    }
  }
};


</script>
