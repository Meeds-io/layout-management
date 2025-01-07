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
    id="sectionTemplateDrawer"
    v-model="drawer"
    :loading="saving"
    :go-back-button="goBackButton"
    allow-expand
    right>
    <template #title>
      <span class="text-wrap">{{ isNew && $t('layout.sectionTemplate.drawerTitie.add') || $t('layout.sectionTemplate.drawerTitie.edit') }}</span>
    </template>
    <template v-if="drawer" #content>
      <div class="pa-4" flat>
        <translation-text-field
          ref="title"
          id="pageTemplateTitle"
          v-model="titleTranslations"
          :field-value.sync="title"
          :placeholder="$t('layout.sectionTemplate.namePlaceholder')"
          :maxlength="maxTitleLength"
          :object-id="instanceId"
          object-type="sectionTemplate"
          field-name="title"
          drawer-title="layout.sectionTemplate.nameTranslationDrawerTitle"
          class="width-auto flex-grow-1 pb-1"
          back-icon
          required>
          <template #title>
            <div class="text-subtitle-1">
              {{ $t('layout.sectionTemplate.name') }}
            </div>
          </template>
        </translation-text-field>
        <translation-text-field
          ref="descriptionTranslation"
          v-model="descriptionTranslations"
          :field-value.sync="description"
          :maxlength="maxDescriptionLength"
          :object-id="instanceId"
          object-type="sectionTemplate"
          field-name="description"
          drawer-title="layout.sectionTemplate.descriptionTranslationDrawerTitle"
          class="ma-1px mt-4"
          back-icon
          rich-editor>
          <template #title>
            <div class="text-subtitle-1">
              {{ $t('layout.sectionTemplate.description') }}
            </div>
          </template>
          <rich-editor
            id="sectionTemplateDescription"
            ref="sectionTemplateDescriptionEditor"
            v-model="descriptionTranslations[lang]"
            :placeholder="$t('layout.sectionTemplate.descriptionTranslationDrawerTitle')"
            :max-length="maxDescriptionLength"
            :tag-enabled="false"
            ck-editor-type="sectionTemplateDescription"
            @ready="checkCKEdtiorDisplay" />
        </translation-text-field>
        <section-template-preview
          ref="sectionTemplatePreview"
          v-model="illustrationUploadId"
          :instance-id="instanceId" />
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
    instance: null,
    instanceId: null,
    title: null,
    titleTranslations: {},
    description: null,
    descriptionTranslations: {},
    maxTitleLength: 250,
    maxDescriptionLength: 1000,
    illustrationUploadId: null,
    lang: eXo.env.portal.language,
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
    this.$root.$on('section-template-edit', this.open);
  },
  beforeDestroy() {
    this.$root.$off('section-template-edit', this.open);
  },
  methods: {
    open(instance, goBackButton) {
      this.$root.$emit('close-alert-message');
      this.isNew = !instance;
      this.goBackButton = goBackButton;
      this.instance = instance || {};
      this.instanceId = instance?.id || null;
      this.title = instance?.name || null;
      this.titleTranslations = {};
      this.descriptionTranslations = {};
      this.description = instance?.description || null;
      this.$nextTick().then(() => this.$refs.drawer.open());
    },
    close() {
      this.$refs.drawer.close();
    },
    save() {
      this.saving = true;
      let message = null;
      let savedInstance = null;
      const newInstance = !this.instanceId;
      const saveInstanceRequest =
        !newInstance ?
          this.$sectionTemplateService.getSectionTemplate(this.instanceId)
            .then(instance => {
              return this.$sectionTemplateService.updateSectionTemplate(instance)
                .then(() => {
                  message = this.$t('layout.sectionTemplateUpdatedSuccessfully');
                  savedInstance = instance;
                  return instance;
                });
            })
          : this.$sectionTemplateService.createSectionTemplate({})
            .then(instance => {
              message = this.$t('layout.sectionTemplateCreatedSuccessfully');
              savedInstance = instance;
              return instance;
            });
      return saveInstanceRequest
        .then(instance => {
          if (instance) {
            this.instanceId = instance.id;
            return this.$nextTick();
          }
        })
        .then(() => this.$translationService.saveTranslations('sectionTemplate', this.instanceId, 'title', this.titleTranslations))
        .then(() => this.$translationService.saveTranslations('sectionTemplate', this.instanceId, 'description', this.descriptionTranslations))
        .then(() => this.$refs?.sectionTemplatePreview?.save())
        .then(() => {
          if (newInstance) {
            this.$root.$emit('section-template-created', savedInstance);
          } else {
            this.$root.$emit('section-template-updated', savedInstance);
          }
        })
        .then(() => {
          this.$root.$emit('section-template-saved', this.instanceId);
          this.$root.$emit('alert-message', message, 'success');
          this.close();
        })
        .finally(() => this.saving = false);
    },
    checkCKEdtiorDisplay() {
      if (this.$refs.sectionTemplateDescriptionEditor?.editor
          && this.description !== this.$refs.sectionTemplateDescriptionEditor.inputVal) {
        this.$refs.sectionTemplateDescriptionEditor.editor.setData(this.description);
      }
    },
  },
};
</script>