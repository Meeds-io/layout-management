<template>
  <v-list
    :loading="loading"
    nav
    dense>
    <v-list-item-group
      v-model="selectedCategory"
      color="primary"
      mandatory>
      <v-list-item>
        <v-list-item-icon class="my-auto me-4">
          <v-card
            width="30"
            class="transparent d-flex align-center justify-center"
            flat>
            <v-icon class="icon-default-color">fa-braille</v-icon>
          </v-card>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>{{ $t('layout.portletInstance.category.all.name') }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <portlets-instance-category
        v-for="category in filteredCategories"
        :key="category.id"
        :category="category" />
    </v-list-item-group>
  </v-list>
</template>
<script>
export default {
  data: () => ({
    loading: false,
    selectedCategory: null,
    collator: new Intl.Collator(eXo.env.portal.language, {numeric: true, sensitivity: 'base'}),
  }),
  computed: {
    categories() {
      return this.$root.portletInstanceCategories || [];
    },
    filteredCategories() {
      const categories = this.categories?.filter?.(c => c.name)?.slice() || [];
      categories.sort((a, b) => this.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return categories;
    },
  },
  watch: {
    selectedCategory() {
      this.$root.$emit('portlets-instance-category-selected', this.filteredCategories[this.selectedCategory - 1]?.id || 0);
    },
  },
};
</script>