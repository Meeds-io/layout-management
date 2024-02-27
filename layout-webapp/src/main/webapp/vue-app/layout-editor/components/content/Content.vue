<template>
  <v-card
    class="singlePageApplication position-relative my-5"
    color="transparent"
    flat>
    <v-hover>
      <layout-editor-section slot-scope="{hover}" :hover="hover" />
    </v-hover>
  </v-card>
</template>
<script>
export default {
  props: {
    page: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    pageToEdit: null,
  }),
  watch: {
    page: {
      immediate: true,
      handler() {
        this.pageToEdit = this.page && JSON.parse(JSON.stringify(this.page)) || null;
      },
    },
  },
  created() {
    this.$root.$on('layout-save-page', this.save);
  },
  methods: {
    save() {
      // TODO
    },
  },
};
</script>
