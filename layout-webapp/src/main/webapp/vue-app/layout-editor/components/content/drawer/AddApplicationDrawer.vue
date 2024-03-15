<template>
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    allow-expand
    right
    @closed="$root.$emit('layout-application-drawer-closed')">
    <template slot="title">
      {{ $t('layout.addApplicationTitle') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4 overflow-hidden"
        flat>
        <v-expansion-panels
          v-model="expanded"
          class="mt-n4"
          accordion
          focusable
          flat>
          <layout-editor-application-category-card
            v-for="(category, index) in categories"
            :key="category.id"
            :category="category"
            :expanded="expanded === index"
            @addApplication="addApplication" />
        </v-expansion-panels>
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    categories: [],
    expanded: 0,
  }),
  created() {
    this.$applicationRegistryService.getCategories()
      .then(categories => this.categories = categories);
  },
  methods: {
    open() {
      this.$refs.drawer.endLoading();
      this.$refs.drawer.open();
    },
    addApplication(application) {
      this.$refs.drawer.startLoading();
      this.$root.$emit('layout-add-application', application);
      window.setTimeout(() => {
        this.close();
      }, 200);
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>