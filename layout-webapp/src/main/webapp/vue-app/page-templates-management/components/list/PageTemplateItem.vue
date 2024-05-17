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
      align="left"
      class="text-truncate"
      width="auto"
      v-sanitized-html="name"></td>
    <!-- description -->
    <td
      v-if="!$root.isMobile"
      align="left"
      class="text-truncate"
      v-sanitized-html="description"></td>
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
  computed: {
    pageTemplateId() {
      return this.pageTemplate?.id;
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
  },
};
</script>