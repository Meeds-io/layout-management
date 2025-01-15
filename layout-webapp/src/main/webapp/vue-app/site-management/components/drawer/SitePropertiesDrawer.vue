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
  <exo-drawer
    id="sitePropertiesDrawer"
    ref="drawer"
    v-model="drawer"
    :loading="loading"
    :right="!$vuetify.rtl"
    :allow-expand="!$root.isMobile && editMode"
    eager
    @expand-updated="expanded = $event"
    @closed="close">
    <template #title>
      <span>{{ $t('siteManagement.drawer.properties.title') }}</span>
    </template>
    <template v-if="drawer" #content>
      <v-form>
        <v-card-text class="d-flex pb-2">
          <translation-text-field
            ref="siteTitleTranslation"
            v-model="siteTitleTranslations"
            v-bind="{
              autofocus: true,
            }"
            :field-value.sync="siteLabel"
            :maxlength="maxTitleLength"
            :no-expand-icon="!expanded"
            :supported-languages="supportedLanguages"
            :default-language="defaultLanguage"
            :object-id="siteId"
            drawer-title="siteManagement.label.translateTitle"
            object-type="site"
            field-name="label"
            name="siteTitle"
            class="width-auto flex-grow-1 pt-4"
            back-icon
            required
            @blur="convertSiteName">
            <template #title>
              <span>
                {{ $t('siteManagement.label.siteLabel.title') }} *              
              </span>
            </template>
          </translation-text-field>
        </v-card-text>
        <v-card-text class="d-flex pb-2">
          <span>
            {{ $t('siteManagement.label.siteName.title') }} *              
          </span>
        </v-card-text>
        <v-card-text class="d-flex py-0">
          <v-text-field
            v-model="siteName"
            class="pt-0"
            type="text"
            required="required"
            :disabled="editMode"
            outlined
            dense />
        </v-card-text>
        <v-card-text class="d-flex py-0">
          <translation-text-field
            ref="siteDescriptionTranslation"
            v-model="siteDescriptionTranslations"
            :field-value.sync="siteDescription"
            :object-id="siteId"
            :max-length="maxDescriptionLength"
            :no-expand-icon="!expanded"
            :supported-languages="supportedLanguages"
            :default-language="defaultLanguage"
            drawer-title="siteManagement.label.translateDescription"
            object-type="site"
            field-name="description"
            class="width-auto flex-grow-1 pt-4"
            back-icon
            rich-editor>
            <template #title>
              <span>
                {{ $t('siteManagement.label.siteDescription.title') }}           
              </span>
            </template>
            <rich-editor
              id="siteDescription"
              ref="siteDescriptionEditor"
              v-model="siteDescription"
              :max-length="maxDescriptionLength"
              :tag-enabled="false"
              ck-editor-type="site" />
          </translation-text-field>
        </v-card-text>
        <v-card-text class="mt-3 py-4 px-3">
          <font-icon-input
            v-model="siteIcon"
            class="mb-4" />
          <p class="mb-2 px-1"> {{ $t('siteManagement.label.banner') }}</p>
          <p class="text-subtitle px-1"> {{ $t('siteManagement.label.caption') }}</p>
          <site-management-banner-selector
            ref="siteBannerSelector"
            :src="siteBannerUrl"
            :default-src="defaultSiteBannerUrl"
            :is-default="isDefaultBanner"
            :no-expand="!expanded"
            @updated="updateBannerUploadId"
            @reset="resetBannerUploadId" />
        </v-card-text>
      </v-form>
    </template>
    <template slot="footer">
      <div class="d-flex justify-end">
        <v-btn
          class="btn ms-2"
          @click="close">
          {{ $t('siteManagement.label.btn.cancel') }}
        </v-btn>
        <v-btn
          v-if="editMode"
          :loading="loading"
          @click="updateSite"
          :disabled="saveDisabled"
          class="btn btn-primary ms-2">
          {{ $t('siteManagement.label.btn.save') }}
        </v-btn>
        <v-btn
          v-else
          :disabled="saveDisabled"
          :loading="loading"
          @click="$root.$emit('open-site-template-drawer')"
          class="btn btn-primary ms-2">
          {{ $t('siteManagement.label.btn.next') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data() {
    return {
      drawer: false,
      site: null,
      siteName: '',
      siteLabel: '',
      siteDescription: '',
      siteIcon: '',
      maxDescriptionLength: 1300,
      siteTitleTranslations: {},
      siteDescriptionTranslations: {},
      supportedLanguages: {},
      defaultLanguage: 'en',
      siteId: null,
      expanded: false,
      siteBannerUrl: '',
      bannerUploadId: null,
      hasDefaultBanner: true,
      isDefaultBanner: true,
      defaultSiteBannerUrl: '',
      rules: {
        value: (v) => (v > 0 && v<= 9999) || this.$t('siteManagement.displayOrder.error')
      },
      editMode: false,
      loading: false,
    };
  },
  created() {
    this.$root.$on('open-site-properties-drawer', this.open);
    this.$root.$on('create-site', this.createSite);
  },
  watch: {
    siteDescription() {
      if (this.$refs.siteDescriptionTranslation && this.drawer) {
        this.$refs.siteDescriptionTranslation.setValue(this.siteDescription);
      }
    },
  },
  computed: {
    saveDisabled(){
      return !this.siteLabel;
    },
  },
  methods: {
    async open(site) {
      this.loading = true;
      if (site) {
        this.editMode = true;
        this.site = site;
        this.siteName = site.name;
        this.siteId = site.siteId;
        this.siteLabel = site.displayName || site.name ;
        this.siteDescription = site.description;
        this.siteBannerUrl = site.bannerUrl;
        this.defaultSiteBannerUrl = `/portal/rest/v1/social/sites/${site.name}/banner?bannerId=0`;
        this.isDefaultBanner = site.bannerFileId === 0 ;
        this.hasDefaultBanner = this.isDefaultBanner;
        this.bannerUploadId = null;
        await this.getSiteLabels();
        await this.getSiteDescriptions();
      } else {
        this.defaultSiteBannerUrl = '/portal/rest/v1/social/sites/default/banner?bannerId=0';
        this.isDefaultBanner = true ;
        this.hasDefaultBanner = true;
        this.bannerUploadId = null;
        this.siteTitleTranslations = {};
        this.siteDescriptionTranslations = {};
      }
      this.siteIcon = site?.icon || 'fa-globe';
      await this.$nextTick().then(() => this.$refs.drawer.open());
      this.loading = false;
    },
    getSiteLabels() {
      return this.$siteLayoutService.getSiteLabels(this.site.siteId)
        .then(data => {
          if (this.editMode && data.labels) {
            this.siteTitleTranslations = data.labels;
          } else {
            this.siteTitleTranslations = {
              'en': null,
            };
          }
          this.defaultLanguage = data.defaultLanguage;
          this.supportedLanguages = data.supportedLanguages;
        });
    },
    getSiteDescriptions() {
      return this.$siteLayoutService.getSiteDescriptions(this.site.siteId)
        .then(data => {
          if (this.editMode && data.labels != null) {
            this.siteDescriptionTranslations = data.labels;
          } else {
            this.siteDescriptionTranslations = {
              'en': null,
            };
          }
          this.defaultLanguage = data.defaultLanguage;
          this.supportedLanguages = data.supportedLanguages;
        });
    },
    close() {
      this.site = null;
      this.reset();
      this.$refs.drawer.close();
    },
    updateBannerUploadId(bannerUploadId) {
      this.bannerUploadId = bannerUploadId;
      this.isDefaultBanner = false;
    },
    resetBannerUploadId() {
      this.bannerUploadId = '0';
      this.isDefaultBanner = true;
    },
    reset() {
      this.siteId = null;
      this.siteName = '';
      this.siteLabel = '';
      this.siteDescription = '';
      this.editMode = false;
      this.siteTitleTranslations = {};
      this.siteDescriptionTranslations = {};
      this.drawer = false;
      this.isDefaultBanner = true;
      this.siteBannerUrl = null;
      this.defaultSiteBannerUrl = null;
      this.$refs.siteBannerSelector?.reset?.();
    },
    updateSite() {
      this.loading = true;
      return this.$siteLayoutService.updateSite(this.site.name, this.site.siteType, this.siteLabel, this.siteDescription, this.site.metaSite || this.site.displayed, this.site.displayed && this.site.displayOrder || 0, this.bannerUploadId !== '0' && this.bannerUploadId || null, !this.hasDefaultBanner && this.bannerUploadId === '0', this.siteIcon)
        .then(() => this.$translationService.saveTranslations('site', this.siteId, 'label', this.siteTitleTranslations))
        .then(() => this.$translationService.saveTranslations('site', this.siteId, 'description', this.siteDescriptionTranslations))
        .then(() => {
          this.$root.$emit('alert-message', this.$t('siteManagement.label.updateSite.success'), 'success');
          this.$root.$emit('refresh-sites');
          this.close();
        }).catch((e) => {
          console.error(e);
          const message = e.message ==='401' &&  this.$t('siteManagement.label.updateSite.unauthorized') || this.$t('siteManagement.label.updateSite.error');
          this.$root.$emit('alert-message', message, 'error');
        })
        .finally(() => this.loading = false);
    },
    createSite(template) {
      if (!template) {
        const message = this.$t('siteManagement.label.template.mandatory');
        this.$root.$emit('alert-message', message, 'error');
        return ;
      }
      this.siteName = this.normalizeText(this.siteName);
      this.loading = true;
      return this.$siteLayoutService.createSite(this.siteName, template, this.siteLabel, this.siteDescription, false, 0, this.bannerUploadId !== '0' && this.bannerUploadId || null, this.siteIcon)
        .then((site) =>{
          this.siteId = site.siteId;
          if (this.siteTitleTranslations.length) {
            this.$translationService.saveTranslations('site',  site.siteId, 'label', this.siteTitleTranslations);
          }
        })
        .then(() => this.siteDescriptionTranslations?.length && this.$translationService.saveTranslations('site', this.siteId, 'description', this.siteDescriptionTranslations))
        .then(() => {
          this.$root.$emit('alert-message', this.$t('siteManagement.label.createSite.success'), 'success');
          this.$root.$emit('refresh-sites');
          this.$root.$emit('close-site-template-drawer', this.close);
          this.close();
        }).catch(e => {
          console.error(e);
          const message = this.$t('siteManagement.label.createSite.error');
          this.$root.$emit('alert-message', message, 'error');
        })
        .finally(() => this.loading = false);
    },
    convertSiteName() {
      if (!this.editMode && !this.siteName) {
        this.siteName = this.normalizeText(this.siteLabel);
      }
    },
    normalizeText(text) {
      return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-zA-Z0-9_-]/g, '').replace(/\s+/g, '').toLowerCase();
    },
  }
};
</script>
