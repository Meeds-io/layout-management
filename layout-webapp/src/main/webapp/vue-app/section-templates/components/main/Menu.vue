<!--

  This file is part of the Meeds project (https://meeds.io/).

  Copyright (C) 2020 - 2025 Meeds Association contact@meeds.io

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
  <v-menu
    v-model="menu"
    :left="!$vuetify.rtl"
    :right="$vuetify.rtl"
    :content-class="menuId"
    offset-y>
    <template #activator="{ on, attrs }">
      <v-btn
        :aria-label="$t('sectionTemplates.menu.open')"
        icon
        small
        class="mx-auto"
        v-bind="attrs"
        v-on="on">
        <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
      </v-btn>
    </template>
    <v-hover v-if="menu" @input="hoverMenu = $event">
      <v-list
        class="pa-0"
        dense
        @mouseout="menu = false"
        @focusout="menu = false">
        <v-list-item-group v-model="listItem">
          <v-tooltip :disabled="!sectionTemplate.system" bottom>
            <template #activator="{ on, attrs }">
              <div
                v-on="on"
                v-bind="attrs">
                <v-list-item
                  :href="!sectionTemplate.system && editLayoutLink"
                  :disabled="sectionTemplate.system"
                  target="_blank"
                  rel="opener"
                  dense>
                  <v-card
                    color="transparent"
                    min-width="15"
                    flat>
                    <v-icon
                      :class="sectionTemplate.system && 'disabled--text'"
                      size="13">
                      fa-edit
                    </v-icon>
                  </v-card>
                  <v-list-item-title class="ps-2">
                    {{ $t('sectionTemplates.label.editInstance') }}
                  </v-list-item-title>
                </v-list-item>
              </div>
            </template>
            <span>{{ $t('sectionTemplates.label.system.noEdit') }}</span>
          </v-tooltip>
          <v-list-item
            dense
            @click="$root.$emit('section-template-edit', sectionTemplate)">
            <v-card
              color="transparent"
              min-width="15"
              flat>
              <v-icon size="13">
                fa-edit
              </v-icon>
            </v-card>
            <v-list-item-title class="ps-2">
              {{ $t('sectionTemplates.label.editProperties') }}
            </v-list-item-title>
          </v-list-item>
          <v-tooltip :disabled="!sectionTemplate.system" bottom>
            <template #activator="{ on, attrs }">
              <div
                v-on="on"
                v-bind="attrs">
                <v-list-item
                  :disabled="sectionTemplate.system"
                  dense
                  @click="$root.$emit('section-template-delete', sectionTemplate)">
                  <v-card
                    color="transparent"
                    min-width="15"
                    flat>
                    <v-icon
                      :class="!sectionTemplate.system && 'error--text' || 'disabled--text'"
                      size="13">
                      fa-trash
                    </v-icon>
                  </v-card>
                  <v-list-item-title class="ps-2">
                    <span :class="!sectionTemplate.system && 'error--text' || 'disabled--text'">{{ $t('sectionTemplates.label.delete') }}</span>
                  </v-list-item-title>
                </v-list-item>
              </div>
            </template>
            <span>{{ $t('sectionTemplates.label.system.noDelete') }}</span>
          </v-tooltip>
        </v-list-item-group>
      </v-list>
    </v-hover>
  </v-menu>
</template>
<script>
export default {
  props: {
    sectionTemplate: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    hoverMenu: false,
    listItem: null,
    menuId: `sectionTemplateMenu${parseInt(Math.random() * 10000)}`,
  }),
  computed: {
    sectionTemplateId() {
      return this.sectionTemplate?.id;
    },
    name() {
      return this.$te(this.sectionTemplate?.name) ? this.$t(this.sectionTemplate?.name) : this.sectionTemplate?.name;
    },
    hasEditMode() {
      return this.sectionTemplate?.supportedModes?.find?.(mode => mode === 'edit');
    },
    editLayoutLink() {
      return `/portal/${eXo.env.portal.portalName}/section-editor?id=${this.sectionTemplateId}`;
    },
  },
  watch: {
    listItem() {
      if (this.menu) {
        this.menu = false;
        this.listItem = null;
      }
    },
    menu() {
      if (this.menu) {
        this.$root.$emit('section-template-menu-opened', this.sectionTemplateId);
      } else {
        this.$root.$emit('section-template-menu-closed', this.sectionTemplateId);
      }
    },
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
    this.$root.$on('section-template-menu-opened', this.checkMenuStatus);
    document.addEventListener('click', this.closeMenuOnClick);
  },
  beforeDestroy() {
    this.$root.$off('section-template-menu-opened', this.checkMenuStatus);
    document.removeEventListener('click', this.closeMenuOnClick);
  },
  methods: {
    closeMenuOnClick(e) {
      if (e.target && !e.target.closest(`.${this.menuId}`)) {
        this.menu = false;
      }
    },
    checkMenuStatus(instanceId) {
      if (this.menu && instanceId !== this.sectionTemplate.id) {
        this.menu = false;
      }
    },
  },
};
</script>