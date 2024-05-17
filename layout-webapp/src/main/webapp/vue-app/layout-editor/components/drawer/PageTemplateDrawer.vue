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
    ref="drawer"
    id="defaultSpacesRegistrationDrawer"
    v-model="drawer"
    :loading="saving"
    right
    disable-pull-to-refresh>
    <template #title>
      {{ $t('layout.saveAsTemplateTitle') }}
    </template>
    <template v-if="drawer" #content>
      <div class="pa-4" flat>
        <div class="d-flex">
          <translation-text-field
            ref="title"
            id="pageTemplateTitle"
            v-model="titleTranslations"
            :field-value.sync="title"
            :placeholder="$t('layout.templateTitlePlaceholder')"
            :maxlength="maxTitleLength"
            :object-id="templateId"
            object-type="pageTemplate"
            field-name="title"
            drawer-title="layout.templateTitleDrawerTitle"
            class="width-auto flex-grow-1 pb-1"
            back-icon
            required>
            <template #title>
              <div class="text-subtitle-1">
                {{ $t('layout.templateTitle') }}
              </div>
            </template>
          </translation-text-field>
        </div>
        <div class="pa-0">
          <translation-text-field
            ref="descriptionTranslation"
            v-model="descriptionTranslations"
            :field-value.sync="description"
            :maxlength="maxDescriptionLength"
            :object-id="templateId"
            object-type="pageTemplate"
            field-name="description"
            drawer-title="layout.templateDescriptionDrawerTitle"
            class="ma-1px mt-4"
            back-icon
            rich-editor>
            <template #title>
              <div class="text-subtitle-1">
                {{ $t('layout.templateDescription') }}
              </div>
            </template>
            <rich-editor
              id="pageTemplateDescription"
              ref="pageTemplateDescriptionEditor"
              v-model="description"
              :placeholder="$t('layout.templateDescriptionPlaceholder')"
              :max-length="maxDescriptionLength"
              :tag-enabled="false"
              ck-editor-type="pageTemplateDescription" />
          </translation-text-field>
        </div>
        <layout-editor-page-template-preview
          ref="pagePreview"
          v-model="illustrationUploadId"
          :template-id="templateId" />
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
    saving: false,
    template: null,
    title: null,
    description: null,
    titleTranslations: {},
    descriptionTranslations: {},
    illustration: null,
    maxTitleLength: 250,
    maxDescriptionLength: 1000,
    illustrationUploadId: null,
    templateId: null,
  }),
  computed: {
    pageRef() {
      return this.$root.layout.pageRef;
    },
  },
  watch: {
    description() {
      if (this.$refs.descriptionTranslation) {
        this.$refs.descriptionTranslation.setValue(this.description);
      }
      if (this.$refs.pageTemplateDescriptionEditor?.editor && this.description !== this.$refs.pageTemplateDescriptionEditor.inputVal) {
        this.$refs.pageTemplateDescriptionEditor.editor.setData(this.description);
      }
    },
  },
  created() {
    this.$root.$on('layout-page-template-drawer-open', this.open);
  },
  beforeDestroy() {
    this.$root.$off('layout-page-template-drawer-open', this.open);
  },
  methods: {
    open() {
      this.$nextTick().then(() => this.$refs.drawer.open());
    },
    close() {
      this.$refs.drawer.close();
    },
    save() {
      const pageLayout = this.$layoutUtils.cleanAttributes(this.$root.layout, true, true);
      const savePageRequest =
        this.templateId ?
          this.$pageTemplateService.getPageTemplate(this.templateId)
            .then(pageTemplate => this.$pageTemplateService.updatePageTemplate({
              ...pageTemplate,
              content: JSON.stringify(pageLayout),
            }))
          : this.$pageTemplateService.createPageTemplate(pageLayout);
      return savePageRequest
        .then(pageTemplate => {
          if (pageTemplate) {
            this.templateId = pageTemplate.id;
            return this.$nextTick();
          }
        })
        .then(() => this.$translationService.saveTranslations('pageTemplate', this.templateId, 'title', this.titleTranslations))
        .then(() => this.$translationService.saveTranslations('pageTemplate', this.templateId, 'description', this.descriptionTranslations))
        .then(() => this.$refs?.pagePreview?.save())
        .then(() => {
          this.close();
          this.$root.$emit('alert-message', this.$t('layout.pageTemplateCreatedSuccessfully'), 'success');
        });
    },
  },
};
</script>