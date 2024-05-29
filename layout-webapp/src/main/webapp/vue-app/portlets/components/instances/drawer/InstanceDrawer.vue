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
    id="portletInstanceDrawer"
    v-model="drawer"
    :loading="saving"
    allow-expand
    right>
    <template #title>
      <span class="text-wrap">{{ isNew && $t('layout.portletInstance.drawerTitie.add') || $t('layout.portletInstance.drawerTitie.edit') }}</span>
    </template>
    <template v-if="drawer" #content>
      <div class="pa-4" flat>
        <translation-text-field
          ref="title"
          id="pageTemplateTitle"
          v-model="titleTranslations"
          :field-value.sync="title"
          :placeholder="$t('layout.portletInstance.namePlaceholder')"
          :maxlength="maxTitleLength"
          :object-id="instanceId"
          object-type="portletInstance"
          field-name="title"
          drawer-title="layout.portletInstance.nameTranslationDrawerTitle"
          class="width-auto flex-grow-1 pb-1"
          back-icon
          required>
          <template #title>
            <div class="text-subtitle-1">
              {{ $t('layout.portletInstance.name') }}
            </div>
          </template>
        </translation-text-field>
        <translation-text-field
          ref="descriptionTranslation"
          v-model="descriptionTranslations"
          :field-value.sync="description"
          :maxlength="maxDescriptionLength"
          :object-id="instanceId"
          object-type="portletInstance"
          field-name="description"
          drawer-title="layout.portletInstance.descriptionTranslationDrawerTitle"
          class="ma-1px mt-4"
          back-icon
          rich-editor>
          <template #title>
            <div class="text-subtitle-1">
              {{ $t('layout.portletInstance.description') }}
            </div>
          </template>
          <rich-editor
            id="portletInstanceDescription"
            ref="portletInstanceDescriptionEditor"
            v-model="descriptionTranslations[lang]"
            :placeholder="$t('layout.portletInstance.descriptionTranslationDrawerTitle')"
            :max-length="maxDescriptionLength"
            :tag-enabled="false"
            ck-editor-type="portletInstanceDescription"
            @ready="checkCKEdtiorDisplay" />
        </translation-text-field>
        <portlets-instance-category-input
          v-model="categoryId"
          class="mt-4" />
        <portlets-instance-portlet-input
          v-model="contentId"
          :disabled="!isNew"
          class="mt-4" />
        <div class="d-flex align-center mt-4">
          <v-switch
            v-model="spaceApplication"
            :aria-label="$t('layout.portlet.allowUsingInSpaceContext')"
            class="mt-1 me-2 width-fit-content" />
          <div>{{ $t('layout.portlet.allowUsingInSpaceContext') }}</div>
        </div>
        <portlets-instance-preview
          ref="portletInstancePreview"
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
    categoryId: null,
    contentId: null,
    title: null,
    titleTranslations: {},
    description: null,
    descriptionTranslations: {},
    maxTitleLength: 250,
    maxDescriptionLength: 1000,
    illustrationUploadId: null,
    lang: eXo.env.portal.language,
    spaceApplication: false,
    saving: false,
    isNew: false,
  }),
  computed: {
    disabled() {
      return !this.categoryId || !this.contentId || !this.title;
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
    this.$root.$on('portlet-instance-add', this.open);
    this.$root.$on('portlet-instance-edit', this.open);
  },
  beforeDestroy() {
    this.$root.$off('portlet-instance-add', this.open);
    this.$root.$off('portlet-instance-edit', this.open);
  },
  methods: {
    open(instance) {
      this.$root.$emit('close-alert-message');
      this.isNew = !instance;
      this.instance = instance || {};
      this.instanceId = instance?.id || null;
      this.categoryId = instance?.categoryId || null;
      this.contentId = instance?.contentId || null;
      this.spaceApplication = instance?.spaceApplication || false;
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
      const saveInstanceRequest =
        this.instanceId ?
          this.$portletInstanceService.getPortletInstance(this.instanceId)
            .then(instance => {
              instance.categoryId = this.categoryId;
              instance.spaceApplication = this.spaceApplication;
              return this.$portletInstanceService.updatePortletInstance(instance)
                .then(() => {
                  this.$root.$emit('portlet-instance-updated', instance);
                  message = this.$t('layout.portletInstanceUpdatedSuccessfully');
                  return instance;
                });
            })
          : this.$portletInstanceService.createPortletInstance({
            categoryId: this.categoryId,
            contentId: this.contentId,
            spaceApplication: this.spaceApplication,
          })
            .then(instance => {
              this.$root.$emit('portlet-instance-created', instance);
              message = this.$t('layout.portletInstanceCreatedSuccessfully');
              return instance;
            });
      return saveInstanceRequest
        .then(instance => {
          if (instance) {
            this.instanceId = instance.id;
            return this.$nextTick();
          }
        })
        .then(() => this.$translationService.saveTranslations('portletInstance', this.instanceId, 'title', this.titleTranslations))
        .then(() => this.$translationService.saveTranslations('portletInstance', this.instanceId, 'description', this.descriptionTranslations))
        .then(() => this.$refs?.portletInstancePreview?.save())
        .then(() => {
          this.$root.$emit('portlet-instance-saved', this.instanceId);
          this.$root.$emit('alert-message', message, 'success');
          this.close();
        })
        .finally(() => this.saving = false);
    },
    checkCKEdtiorDisplay() {
      if (this.$refs.portletInstanceDescriptionEditor?.editor
          && this.description !== this.$refs.portletInstanceDescriptionEditor.inputVal) {
        this.$refs.portletInstanceDescriptionEditor.editor.setData(this.description);
      }
    },
  },
};
</script>