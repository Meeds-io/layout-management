<template>
  <tr>
    <!-- Illustration -->
    <td
      v-if="!$root.isMobile"
      align="center"
      width="70px">
      <layout-image-illustration
        :value="portlet"
        object-type="portlets"
        default-src="/layout/images/portlets/DefaultPreview.webp" />
    </td>
    <!-- name -->
    <td
      :width="$root.isMobile && '100%' || 'auto'"
      align="left">
      <v-card
        v-sanitized-html="name"
        class="text-truncate transparent"
        flat
        @click="$root.$emit('layout-illustration-preview', illustrationSrc)" />
    </td>
    <!-- description -->
    <td
      v-if="!$root.isMobile"
      align="left"
      class="text-truncate"
      v-sanitized-html="description"></td>
    <td
      align="center"
      width="50px">
      {{ portlet.instances }}
    </td>
    <td
      align="center"
      width="50px">
      <portlets-menu :portlet="portlet" />
    </td>
  </tr>
</template>
<script>
export default {
  props: {
    portlet: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    hoverMenu: false,
  }),
  computed: {
    portletId() {
      return this.portlet?.id;
    },
    name() {
      return this.$te(this.portlet?.name) ? this.$t(this.portlet?.name) : this.portlet?.name;
    },
    description() {
      return this.$te(this.portlet?.description) ? this.$t(this.portlet?.description) : this.portlet?.description;
    },
    illustrationId() {
      return this.portlet?.illustrationId;
    },
    illustrationSrc() {
      return this.illustrationId && `${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/attachments/portlets/${this.portletId}/${this.illustrationId}` || '/layout/images/portlets/DefaultPreview.webp';
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
};
</script>