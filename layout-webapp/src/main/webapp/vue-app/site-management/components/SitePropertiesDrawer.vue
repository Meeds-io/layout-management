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
    id="sitePropertiesDrawer"
    ref="sitePropertiesDrawer"
    v-model="sitePropertiesDrawer"
    :right="!$vuetify.rtl"
    :allow-expand="!$root.isMobile && editMode"
    eager
    @expand-updated="expanded = $event"
    @closed="close">
    <template slot="title">
      <span>{{ $t('siteManagement.drawer.properties.title') }}</span>
    </template>
    <template v-if="initialized" slot="content">
      <v-form>
        <v-card-text class="d-flex pb-2">
          <translation-text-field
            ref="siteTitleTranslation"
            v-model="siteTitleTranslations"
            :field-value.sync="siteLabel"
            :maxlength="maxTitleLength"
            :no-expand-icon="!expanded"
            drawer-title="siteManagement.label.translateTitle"
            :object-id="siteId"
            object-type="site"
            field-name="label"
            name="siteTitle"
            class="width-auto flex-grow-1 pt-4"
            back-icon
            autofocus
            required
            @blur="convertSiteName">
            <template #title>
              <v-label>
                <span class="text-color font-weight-bold">
                  {{ $t('siteManagement.label.siteLabel.title') }} *              
                </span>
              </v-label>
            </template>
          </translation-text-field>
        </v-card-text>
        <v-card-text class="d-flex pb-2">
          <v-label>
            <span class="text-color font-weight-bold">
              {{ $t('siteManagement.label.siteName.title') }} *              
            </span>
          </v-label>
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
            drawer-title="siteManagement.label.translateDescription"
            object-type="site"
            field-name="description"
            class="width-auto flex-grow-1 pt-4"
            back-icon
            rich-editor>
            <template #title>
              <v-label>
                <span class="text-color font-weight-bold">
                  {{ $t('siteManagement.label.siteDescription.title') }}           
                </span>
              </v-label>
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
          <v-label>
            <span class="text-color font-weight-bold mb-2 px-1 text-capitalize"> {{ $t('siteManagement.label.banner') }}</span>
          </v-label>
          <v-label>
            <span class="text-color caption px-1 text-capitalize"> {{ $t('siteManagement.label.caption') }}</span>
          </v-label>
          <site-management-banner-selector
            ref="siteBannerSelector"
            :src="siteBannerUrl"
            :default-src="defaultSiteBannerUrl"
            :is-default="isDefaultBanner"
            :no-expand="!expanded"
            @updated="updateBannerUploadId"
            @reset="resetBannerUploadId" />
        </v-card-text>
        <v-card-text class="mt-4">
          <v-label>
            <span class="text-color font-weight-bold mb-2"> {{ $t('siteManagement.label.displayOrder') }}</span>
          </v-label>
          <v-row class="mx-0 mt-2">
            <v-col class="d-flex flex-row px-0 py-0 col-10">
              <v-switch
                v-model="displayed"
                :disabled="displayedDisabled"
                class="mt-2" />
              <label v-if="displayed" class="subtitle-1 mx-1"> {{ $t('siteManagement.label.displayed') }} </label>
              <label v-else class="subtitle-1 mx-1"> {{ $t('siteManagement.label.notDisplayed') }} </label>
            </v-col>
            <v-col class="col-2 px-0 py-0 mt-n1 orderDisplay">
              <v-text-field
                v-if="displayed"
                v-model="displayOrder"
                :rules="[rules.value]"
                type="number"
                class="pt-2"
                outlined
                required />
            </v-col>
          </v-row>
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
      sitePropertiesDrawer: false,
      site: null,
      siteName: '',
      siteLabel: '',
      siteDescription: '',
      maxDescriptionLength: 1300,
      displayOrder: 0,
      displayed: true,
      siteTitleTranslations: {},
      siteDescriptionTranslations: {},
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
      initialized: false,
    };
  },
  created() {
    this.$root.$on('open-site-properties-drawer', this.open);
    this.$root.$on('create-site', this.createSite);
  },
  watch: {
    siteDescription() {
      if (this.$refs.siteDescriptionTranslation) {
        this.$refs.siteDescriptionTranslation.setValue(this.siteDescription);
      }
    },
  },
  computed: {
    saveDisabled(){
      return !this.siteLabel || (this.displayed && this.displayOrder < 1);
    },
    displayedDisabled(){
      return this.site?.metaSite;
    },
  },
  methods: {
    open(site, freshInstance) {
      if (site) {
        this.editMode = true;
        if (!freshInstance) {
          this.$refs.sitePropertiesDrawer.open();
          return this.$siteService.getSiteById(parseInt(site.siteId), {
            expandNavigations: false,
            excludeEmptyNavigationSites: false,
            lang: 'en' })
            .then(freshSite => this.open(freshSite, true));
        }
        this.site = site;
        this.siteName = site.name;
        this.siteId = site.siteId;
        this.siteLabel = site.displayName || site.name ;
        this.siteDescription = site.description;
        this.displayed = site.displayed;
        this.displayOrder = site.displayOrder;
        this.siteBannerUrl = site.bannerUrl;
        this.defaultSiteBannerUrl = `/portal/rest/v1/social/sites/${site.name}/banner?bannerId=0`;
        this.isDefaultBanner = site.bannerFileId === 0 ;
        this.hasDefaultBanner = this.isDefaultBanner;
        this.bannerUploadId = null;
      } else {
        this.displayed = false;
        this.displayOrder = 0;
        this.defaultSiteBannerUrl = '/portal/rest/v1/social/sites/default/banner?bannerId=0';
        this.isDefaultBanner = true ;
        this.hasDefaultBanner = true;
        this.bannerUploadId = null;
      }
      this.siteTitleTranslations = {};
      this.siteDescriptionTranslations = {};
      this.initialized = true;
      this.$nextTick().then(() => {
        this.$refs.sitePropertiesDrawer.open();
      });
    },
    close() {
      this.site = null;
      this.reset();
      this.$refs.sitePropertiesDrawer.close();
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
      this.displayed = false;
      this.displayOrder = 0;
      this.editMode = false;
      this.siteTitleTranslations = {};
      this.siteDescriptionTranslations = {};
      this.initialized = false;
      this.sitePropertiesDrawer = false;
      this.isDefaultBanner = true;
      this.siteBannerUrl = null;
      this.defaultSiteBannerUrl = null;
      this.$refs.siteBannerSelector.reset();
    },
    updateSite() {
      this.loading = true;
      this.$refs.sitePropertiesDrawer.startLoading();
      return this.$siteManagementService.updateSite(this.site.name, this.site.siteType, this.siteLabel, this.siteDescription, this.site.metaSite || this.displayed, this.displayed && this.displayOrder || 0, this.bannerUploadId !== '0' && this.bannerUploadId || null, !this.hasDefaultBanner && this.bannerUploadId === '0')
        .then(() => this.$translationService.saveTranslations('site', this.siteId, 'label', this.siteTitleTranslations))
        .then(() => this.$translationService.saveTranslations('site', this.siteId, 'description', this.siteDescriptionTranslations))
        .then(() => {
          this.$root.$emit('alert-message', this.$t('siteManagement.label.updateSite.success'), 'success');
          this.$root.$emit('refresh-sites');
          this.close();
        }).catch((e) => {
          const message = e.message ==='401' &&  this.$t('siteManagement.label.updateSite.unauthorized') || this.$t('siteManagement.label.updateSite.error');
          this.$root.$emit('alert-message', message, 'error');
        })
        .finally(() => {
          this.loading = false;
          this.$refs.sitePropertiesDrawer.endLoading();
        });
    },
    createSite(template) {
      if (!template) {
        const message = this.$t('siteManagement.label.template.mandatory');
        this.$root.$emit('alert-message', message, 'error');
        return ;
      }
      this.siteName = this.normalizeText(this.siteName);
      this.loading = true;
      this.$refs.sitePropertiesDrawer.startLoading();
      return this.$siteManagementService.createSite(this.siteName, template, this.siteLabel, this.siteDescription, this.displayed, this.displayOrder || 0, this.bannerUploadId !== '0' && this.bannerUploadId || null,)
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
        }).catch(() => {
          const message = this.$t('siteManagement.label.createSite.error');
          this.$root.$emit('alert-message', message, 'error');
        })
        .finally(() => {
          this.loading = false;
          this.$refs.sitePropertiesDrawer.endLoading();
        });
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
