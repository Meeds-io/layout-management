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
  <v-hover v-model="hover">
    <v-card
      :id="`PortletInstance_${application.id}`"
      class="border-color card-border-radius overflow-hidden position-relative"
      flat>
      <v-expand-transition>
        <v-card
          v-if="hover"
          class="d-flex absolute-full-size z-index-one align-center justify-center transition-fast-in-fast-out mask-color"
          flat>
          <div class="ApplicationCardAction">
            <v-btn
              :aria-label="$t('layout.add')"
              elevation="0"
              class="primary mx-2"
              @click="$emit('add')">
              <v-icon size="13" class="me-2">fa-plus</v-icon>
              {{ $t('layout.add') }}
            </v-btn>
            <v-btn
              v-if="illustrationId"
              :aria-label="$t('layout.preview')"
              elevation="0"
              class="primary mx-2 primary-border-color"
              outlined
              @click="preview">
              <v-icon size="13" class="me-2">fa-search</v-icon>
              {{ $t('layout.preview') }}
            </v-btn>
          </div>
        </v-card>
      </v-expand-transition>
      <div class="d-flex flex-column full-width pa-5">
        <div class="subtitle-1 text-color ApplicationCardTitle">
          {{ name }}
        </div>
        <div
          v-if="description"
          v-sanitized-html="description"
          class="subtitle-2 pt-0 pb-2 text-sub-title ApplicationCardDescription"></div>
        <layout-image-illustration
          ref="illustration"
          :value="application"
          object-type="portletInstance"
          max-height="110"
          max-width="100%"
          no-hover />
      </div>
    </v-card>
  </v-hover>
</template>
<script>
export default {
  props: {
    application: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    defaultImageSrc: '/sites/images/application/DefaultPortlet.png',
    hover: false,
  }),
  computed: {
    name() {
      return this.application?.name;
    },
    description() {
      return this.application?.description;
    },
    instanceId() {
      return this.application?.id;
    },
    illustrationId() {
      return this.application?.illustrationId;
    },
    illustrationSrc() {
      return this.illustrationId && `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/attachments/portletInstance/${this.instanceId}/${this.illustrationId}` || this.defaultImageSrc;
    },
  },
  methods: {
    preview() {
      this.$refs.illustration.openIllustration();
    },
  },
};
</script>

