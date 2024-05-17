<template>
  <tr>
    <!-- Illustration -->
    <td align="center" width="70px">
      <div :aria-label="$t('pageTemplate.label.preview', {0: name})">
        <v-img
          :src="illustrationSrc"
          max-height="30"
          max-width="60" />
      </div>
    </td>
    <!-- name -->
    <td
      :width="$root.isMobile && '100%' || 'auto'"
      align="left"
      class="text-truncate"
      v-sanitized-html="name"></td>
    <!-- description -->
    <td
      v-if="!$root.isMobile"
      align="left"
      class="text-truncate"
      v-sanitized-html="description"></td>
    <td
      v-if="!$root.isMobile"
      align="left"
      class="text-truncate text-center"
      width="120px">
      {{ category }}
    </td>
    <td
      v-if="!$root.isMobile"
      align="center"
      width="50px">
      <v-switch
        v-model="enabled"
        :loading="loading"
        class="mt-0 mx-auto"
        @click="changeStatus" />
    </td>
    <td
      v-if="!$root.isMobile"
      align="center"
      width="50px">
      <page-templates-management-item-menu :page-template="pageTemplate" />
    </td>
  </tr>
</template>
<script>
export default {
  props: {
    pageTemplate: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    hoverMenu: false,
  }),
  computed: {
    pageTemplateId() {
      return this.pageTemplate?.id;
    },
    enabled() {
      return !this.pageTemplate?.disabled;
    },
    name() {
      return this.$te(this.pageTemplate?.name) ? this.$t(this.pageTemplate?.name) : this.pageTemplate?.name;
    },
    description() {
      return this.$te(this.pageTemplate?.description) ? this.$t(this.pageTemplate?.description) : this.pageTemplate?.description;
    },
    illustrationId() {
      return this.pageTemplate?.illustrationId;
    },
    illustrationSrc() {
      return this.illustrationId && `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/attachments/pageTemplate/${this.pageTemplateId}/${this.illustrationId}` || '/layout/images/page-templates/DefaultPreview.webp';
    },
    category() {
      const i18nKey = `layout.pageTemplate.category.${this.pageTemplate?.category}`;
      return this.pageTemplate?.category
        && (this.$te(i18nKey) ? this.$t(i18nKey) : this.pageTemplate.category)
        || this.$t('layout.pageTemplate.category.customized');
    },
  },
  watch: {
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
  methods: {
    changeStatus() {
      this.$root.$emit('close-alert-message');
      this.loading = true;
      this.$pageTemplateService.getPageTemplate(this.pageTemplate.id)
        .then(pageTemplate => this.$pageTemplateService.updatePageTemplate({
          ...pageTemplate,
          disabled: this.enabled,
        }))
        .then(() => {
          this.$root.$emit('page-templates-refresh');
          this.$root.$emit('alert-message', this.$t('pageTemplate.status.update.success'), 'success');
        })
        .catch(() => this.$root.$emit('alert-message', this.$t('pageTemplate.status.update.error'), 'error'))
        .finally(() => this.loading = false);
    },
  },
};
</script>