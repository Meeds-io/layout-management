<template>
  <v-hover @input="hover = $event">
    <v-list-item>
      <v-list-item-icon class="my-auto">
        <v-card
          width="30"
          class="transparent d-flex align-center justify-center"
          flat>
          <v-icon v-text="category.icon" />
        </v-card>
      </v-list-item-icon>
      <v-list-item-content>
        <v-list-item-title v-text="category.name" />
      </v-list-item-content>
      <v-list-item-action class="my-auto">
        <v-card
          width="30"
          class="transparent d-flex align-center justify-center"
          flat>
          <portlets-instance-category-menu v-if="hover" :category="category" />
        </v-card>
      </v-list-item-action>
    </v-list-item>
  </v-hover>
</template>
<script>
export default {
  props: {
    category: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    hover: false,
  }),
};
</script>