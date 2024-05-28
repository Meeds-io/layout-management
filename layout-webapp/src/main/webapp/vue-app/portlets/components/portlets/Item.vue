<template>
  <tr>
    <!-- Illustration -->
    <td
      v-if="!$root.isMobile"
      align="center"
      width="70px">
      <v-img
        :src="illustrationSrc"
        max-height="30"
        max-width="60"
        contain
        @error="illustrationSrc = defaultIllustrationSrc" />
    </td>
    <!-- name -->
    <td
      v-sanitized-html="name"
      :width="$root.isMobile && '100%' || 'auto'"
      align="left">
    </td>
    <!-- description -->
    <td
      v-if="!$root.isMobile"
      align="left"
      v-sanitized-html="description"></td>
    <td
      align="center"
      width="50px">
      <v-btn
        text
        @click="$root.$emit('portlets-instances-drawer', portlet.contentId)">
        {{ instancesCount }}
      </v-btn>
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
    defaultIllustrationSrc: '/layout/images/portlets/DefaultPortlet.png',
    illustrationSrc: null,
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
    instancesCount() {
      return this.$root.portletInstances.filter(a => a.contentId === this.portlet.contentId).length || 0;
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
  created() {
    this.illustrationSrc = `/${this.portlet.applicationName}/skin/DefaultSkin/portletIcons/${this.portlet.portletName}.png`;
  },
};
</script>