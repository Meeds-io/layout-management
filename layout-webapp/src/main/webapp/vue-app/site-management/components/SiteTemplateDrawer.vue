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
    ref="siteTemplateDrawer"
    id="siteManagementSiteTemplateDrawer"
    :right="!$vuetify.rtl"
    eager
    @closed="close">
    <template slot="title">
      <div class="d-flex">
        <v-icon
          size="16"
          class="clickable"
          @click="close">
          fas fa-arrow-left
        </v-icon>
        <span class="ms-2"> {{ $t('siteManagement.label.siteTemplate.drawer.title') }} </span>
      </div>
    </template>
    <template slot="content">
      <v-card class="mx-4 my-4 px-2 py-2 elevation-0">
        <span class="font-weight-bold text-start text-color body-2">{{ $t('siteManagement.label.siteTemplate') }}</span>
        <v-select
          v-model="template"
          :items="templates"
          item-text="text"
          item-value="value"
          dense
          class="caption pt-1 mb-5"
          outlined />

        <v-img
          :src="siteTemplateSkeleton"
          class="align-center mx-auto"
          max-height="350"
          max-width="500" />
      </v-card>
    </template>
    <template slot="footer">
      <div class="d-flex justify-end">
        <v-btn
          class="btn ms-2"
          @click="close">
          {{ $t('siteManagement.label.btn.cancel') }}
        </v-btn>
        <v-btn
          :loading="loading"
          class="btn btn-primary ms-2"
          @click="saveSite">
          {{ $t('siteManagement.label.btn.save') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {

  data() {
    return {
      template: 'navigationWebsite',
      loading: false,
      isValidForm: true,
    };
  },
  computed: {
    templates() {
      return [
        {
          text: this.$t('siteManagement.label.navigationWebsite'),
          value: 'navigationWebsite',
        }
      ];
    },
    siteTemplateSkeleton() {
      return `/layout/images/site-templates/${this.template}.png`;
    }
  },
  created() {
    this.$root.$on('open-site-template-drawer', this.open);
    this.$root.$on('close-site-template-drawer', this.close);

  },
  methods: {
    open() {
      this.$refs.siteTemplateDrawer.open();
    },
    close() {
      this.loading = false;
      this.$refs.siteTemplateDrawer.close();
      this.$refs.siteTemplateDrawer.endLoading();
    },
    saveSite() {
      this.loading = true;
      this.$refs.siteTemplateDrawer.startLoading();
      this.$root.$emit('create-site', this.template);
    }
  }
};
</script>