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
  <exo-drawer
    ref="drawer"
    id="siteTemplateDrawer"
    v-model="drawer"
    :loading="saving"
    :go-back-button="goBackButton"
    allow-expand
    right>
    <template #title>
      <span class="text-wrap">{{ isNew && $t('layout.siteTemplate.drawerTitie.add') || $t('layout.siteTemplate.drawerTitie.edit') }}</span>
    </template>
    <template v-if="drawer" #content>
      <div class="pa-4" flat>
        <translation-text-field
          ref="title"
          id="pageTemplateTitle"
          v-model="titleTranslations"
          :field-value.sync="title"
          :placeholder="$t('layout.siteTemplate.namePlaceholder')"
          :maxlength="maxTitleLength"
          :object-id="siteTemplateId"
          object-type="siteTemplate"
          field-name="title"
          drawer-title="layout.siteTemplate.nameTranslationDrawerTitle"
          class="width-auto flex-grow-1 pb-1"
          back-icon
          required>
          <template #title>
            <div class="text-subtitle-1">
              {{ $t('layout.siteTemplate.name') }}
            </div>
          </template>
        </translation-text-field>
        <translation-text-field
          ref="descriptionTranslation"
          v-model="descriptionTranslations"
          :field-value.sync="description"
          :maxlength="maxDescriptionLength"
          :object-id="siteTemplateId"
          object-type="siteTemplate"
          field-name="description"
          drawer-title="layout.siteTemplate.descriptionTranslationDrawerTitle"
          class="ma-1px mt-4"
          back-icon
          rich-editor>
          <template #title>
            <div class="text-subtitle-1">
              {{ $t('layout.siteTemplate.description') }}
            </div>
          </template>
          <rich-editor
            id="siteTemplateDescription"
            ref="siteTemplateDescriptionEditor"
            v-model="descriptionTranslations[lang]"
            :placeholder="$t('layout.siteTemplate.descriptionTranslationDrawerTitle')"
            :max-length="maxDescriptionLength"
            :tag-enabled="false"
            ck-editor-type="siteTemplateDescription"
            @ready="checkCKEdtiorDisplay" />
        </translation-text-field>
        <site-template-preview
          ref="siteTemplatePreview"
          v-model="illustrationUploadId"
          :preview-image="previewImage"
          :site-template-id="siteTemplateId" />
      </div>
    </template>
    <template #footer>
      <div class="d-flex me-2">
        <v-spacer />
        <v-btn
          class="btn mx-1"
          @click="close">
          {{ $t('layout.cancel') }}
        </v-btn>
        <v-btn
          :disabled="disabled"
          :loading="saving"
          class="btn btn-primary"
          @click="save">
          {{ $t('layout.save') }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    siteTemplate: null,
    siteTemplateId: null,
    title: null,
    titleTranslations: {},
    description: null,
    descriptionTranslations: {},
    maxTitleLength: 250,
    maxDescriptionLength: 1000,
    illustrationUploadId: null,
    lang: eXo.env.portal.language,
    previewImage: null,
    goBackButton: false,
    saving: false,
    isNew: false,
  }),
  computed: {
    disabled() {
      return !this.title?.length;
    },
  },
  watch: {
    description() {
      if (this.$refs.descriptionTranslation) {
        this.$refs.descriptionTranslation.setValue(this.description);
      }
      this.checkCKEdtiorDisplay();
    },
  },
  created() {
    this.$root.$on('site-template-add', this.open);
    this.$root.$on('site-template-edit', this.open);
  },
  beforeDestroy() {
    this.$root.$off('site-template-add', this.open);
    this.$root.$off('site-template-edit', this.open);
  },
  methods: {
    async open(siteTemplate, goBackButton, previewElement) {
      this.$root.$emit('close-alert-message');
      this.isNew = !siteTemplate?.name;
      this.goBackButton = goBackButton;
      this.siteTemplate = siteTemplate || {};
      this.siteTemplateId = siteTemplate?.id || null;
      this.closeOnSave = !!previewElement;
      this.title = siteTemplate?.name || null;
      this.titleTranslations = {};
      this.descriptionTranslations = {};
      this.description = siteTemplate?.description || null;
      try {
        if (previewElement) {
          this.illustrationUploadId = await this.generatePreview(previewElement);
        } else {
          this.previewImage = null;
        }
      } finally {
        this.$nextTick().then(() => this.$refs.drawer.open());
      }
    },
    async generatePreview(previewElement) {
      const previewCanvas = await window.html2canvas(previewElement);
      this.previewImage = previewCanvas.toDataURL('image/png');
      const previewBlob = this.convertPreviewToFile(this.previewImage);
      const uploadId =  await this.$uploadService.upload(previewBlob);
      return await new Promise((resolve, reject) => {
        const interval = window.setInterval(() => {
          this.$uploadService.getUploadProgress(uploadId)
            .then(percent => {
              if (Number(percent) === 100) {
                window.clearInterval(interval);
                resolve(uploadId);
              }
            })
            .catch(e => reject(e));
        }, 200);
      });
    },
    convertPreviewToFile(previewImage) {
      const imgString = window.atob(previewImage.replace(/^data:image\/\w+;base64,/, ''));
      const bytes = new Uint8Array(imgString.length);
      for (let i = 0; i < imgString.length; i++) {
        bytes[i] = imgString.charCodeAt(i);
      }
      return new Blob([bytes], {type: 'image/png'});
    },
    close() {
      this.$refs.drawer.close();
    },
    save() {
      this.saving = true;
      return this.$siteTemplateService.updateSiteTemplate(this.siteTemplate)
        .then(() => this.$translationService.saveTranslations('siteTemplate', this.siteTemplateId, 'title', this.titleTranslations))
        .then(() => this.$translationService.saveTranslations('siteTemplate', this.siteTemplateId, 'description', this.descriptionTranslations))
        .then(() => this.$refs?.siteTemplatePreview?.save())
        .then(() => {
          this.$root.$emit('site-template-saved', this.siteTemplateId);
          if (window?.opener && this.closeOnSave) {
            window?.opener?.dispatchEvent?.(new CustomEvent('site-template-layout-updated', {
              detail: this.siteTemplate,
            }));
            window.close();
          } else {
            if (this.isNew) {
              this.$root.$emit('alert-message', this.$t('layout.siteTemplateCreatedSuccessfully'), 'success');
            } else {
              this.$root.$emit('alert-message', this.$t('layout.siteTemplateUpdatedSuccessfully'), 'success');
            }
            this.close();
          }
        })
        .finally(() => this.saving = false);
    },
    checkCKEdtiorDisplay() {
      if (this.$refs.siteTemplateDescriptionEditor?.editor
          && this.description !== this.$refs.siteTemplateDescriptionEditor.inputVal) {
        this.$refs.siteTemplateDescriptionEditor.editor.setData(this.description);
      }
    },
  },
};
</script>