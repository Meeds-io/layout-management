<template>
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    allow-expand
    right>
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
  watch: {
    drawer() {
      if (this.drawer) {
        this.$root.$emit('layout-application-drawer-opened');
      } else {
        this.$root.$emit('layout-application-drawer-closed');
      }
    },
  },
  created() {
    this.$applicationRegistryService.getCategories()
      .then(categories => this.categories = categories);
  },
  methods: {
    open() {
      this.$refs.drawer.open();
    },
    addApplication(application) {
      this.$root.$emit('layout-add-application', application);
      this.close();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>