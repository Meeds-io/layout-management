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
  <v-card
    :max-width="maxWidth"
    :class="parentClass"
    class="transparent layout-sections-parent full-width mx-auto"
    flat>
    <layout-editor-container-extension
      :container="$root.draftLayout"
      class="layout-page-body d-flex no-border-radius" />
    <layout-editor-application-edit-drawer
      ref="applicationPropertiesDrawer" />
    <layout-editor-application-category-select-drawer
      ref="applicationCategoryDrawer" />
    <layout-editor-application-add-drawer
      ref="applicationDrawer" />
    <layout-editor-portlet-edit-dialog />
    <layout-image-illustration-preview />
    <changes-reminder
      ref="changesReminder"
      :reminder="reminder"
      @opened="changesReminderOpened = true">
      <div>{{ $t('layout.site.reminder.description.part1') }}</div>
      <div>{{ $t('layout.site.reminder.description.part2') }}</div>
      <div>{{ $t('layout.site.reminder.description.part3') }}</div>
      <div>{{ $t('layout.site.reminder.description.part4') }}</div>
    </changes-reminder>
  </v-card>
</template>
<script>
export default {
  data: () => ({
    isCompatible: true,
    changesReminderOpened: false,
    loading: 1,
  }),
  computed: {
    mobileDisplayMode() {
      return this.$root.mobileDisplayMode;
    },
    maxWidth() {
      return this.mobileDisplayMode && '500px !important' || 'initial';
    },
    parentClass() {
      return this.mobileDisplayMode && 'layout-mobile-view elevation-3 mt-3' || 'layout-desktop-view';
    },
    layout() {
      return this.$root.layout;
    },
    reminder() {
      return {
        name: 'LayoutEditorChangesAnnounced' ,
        title: this.$t('layout.reminder.title'),
        img: '/layout/images/EditLayout.gif',
      };
    },
  },
  watch: {
    loading(newVal, oldVal) {
      if (newVal - oldVal > 0) {
        document.dispatchEvent(new CustomEvent('displayTopBarLoading'));
      } else if (newVal - oldVal < 0) {
        document.dispatchEvent(new CustomEvent('hideTopBarLoading'));
      }
    },
    layout(oldVal, newVal) {
      if (this.layout) {
        const layout = JSON.parse(JSON.stringify(this.layout));
        this.setLayout(layout);
      }
      if (!oldVal) {
        window.setTimeout(() => document.dispatchEvent(new CustomEvent('hideTopBarLoading')), 200);
        if (newVal) {
          this.$root.$applicationLoaded();
        }
      }
    },
    mobileDisplayMode() {
      this.switchDisplayMode();
    },
    isCompatible() {
      this.openChangesReminder();
    },
  },
  created() {
    this.$root.$on('layout-add-application-category-drawer', this.openApplicationCategoryDrawer);
    this.$root.$on('layout-section-history-add', this.addHistory);
    this.$root.$on('layout-section-history-undo', this.undoFromHistory);
    this.$root.$on('layout-section-history-redo', this.redoFromHistory);
    this.$root.$on('layout-site-saved', this.handleSiteSaved);
    this.$root.$on('layout-save-draft', this.saveDraft);
    document.addEventListener('keydown', this.restoreHistory);
    this.resetHistory();
  },
  mounted() {
    this.openChangesReminder();
  },
  methods: {
    openChangesReminder() {
      if (!this.isCompatible && !this.changesReminderOpened && this.$refs.changesReminder) {
        this.$refs.changesReminder.open();
      }
    },
    switchDisplayMode() {
      if (this.$root.displayMode === 'mobile') {
        this.$layoutUtils.applyMobileStyle(this.$root.draftLayout);
      } else {
        this.$layoutUtils.applyDesktopStyle(this.$root.draftLayout);
      }
    },
    setLayout(layout) {
      this.initContainer(layout);
      const compatible = this.$layoutUtils.parseSite(layout);
      if (this.$root.draftLayout) {
        Object.assign(this.$root.draftLayout, layout);
      } else {
        this.$root.draftLayout = layout;
      }
      this.isCompatible = compatible;
    },
    initContainer(container) {
      if (container.children) {
        container.children.filter(c => c).forEach(this.initContainer);
      } else {
        container.children = [];
      }
    },
    openApplicationCategoryDrawer(sectionId) {
      this.$root.selectedSectionId = sectionId;
      this.$refs.applicationCategoryDrawer.open();
    },
    handleSiteSaved() {
      document.dispatchEvent(new CustomEvent('alert-message', {detail: {
        alertLink: `/portal/${this.$root.siteName}`,
        alertMessage: this.$t('layout.siteSavedSuccessfully'),
        alertLinkText: this.$t('layout.view'),
        alertLinkTarget: '_blank',
        alertType: 'success',
      }}));
    },
    addHistory() {
      if (!this.$root.sectionHistory) {
        this.$root.sectionHistory = [JSON.parse(JSON.stringify(this.$root.draftLayout))];
      } else {
        this.$root.sectionHistory.push(JSON.parse(JSON.stringify(this.$root.draftLayout)));
      }
      this.$root.sectionRedo = [];
    },
    restoreHistory(event) {
      if (event.ctrlKey) {
        if (event.keyCode === 90) {
          this.undoFromHistory();
        } else if (event.keyCode === 89) {
          this.redoFromHistory();
        }
      }
    },
    undoFromHistory() {
      if (this.$root.sectionHistory?.length) {
        const draftLayout = this.$root.sectionHistory.pop();
        this.$root.sectionRedo.push(JSON.parse(JSON.stringify(this.$root.draftLayout)));
        this.$root.draftLayout = draftLayout;
      }
    },
    redoFromHistory() {
      if (this.$root.sectionRedo?.length) {
        const draftLayout = this.$root.sectionRedo.pop();
        this.$root.sectionHistory.push(JSON.parse(JSON.stringify(this.$root.draftLayout)));
        this.$root.draftLayout = draftLayout;
      }
    },
    resetHistory() {
      this.$root.sectionHistory = [];
      this.$root.sectionRedo = [];
    },
    saveDraft(layout) {
      this.resetHistory();

      this.loading++;
      const layoutToUpdate = this.$layoutUtils.cleanAttributes(layout || JSON.parse(JSON.stringify(this.$root.draftLayout)), false, true);
      return this.$siteLayoutService.updateSiteLayout(this.$root.siteType, this.$root.siteName, layoutToUpdate, 'contentId')
        .then(layout => {
          this.setLayout(layout);
          this.$root.$emit('layout-draft-saved');
        })
        .catch(e => {
          this.$root.$emit('alert-message', this.$te(e.message) ? this.$t(e.message) : this.$t('layout.siteSavingError'), 'error');
          this.$root.$emit('layout-draft-save-error');
        })
        .finally(() => {
          window.setTimeout(() => this.loading--, 200);
          this.$root.$emit('coediting-set-lock');
        });
    },
  },
};
</script>
