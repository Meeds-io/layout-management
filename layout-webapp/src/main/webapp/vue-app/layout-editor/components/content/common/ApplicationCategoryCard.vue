<template>
  <v-expansion-panel
    v-if="hasApplications"
    class="border-color border-radius mt-4">
    <v-expansion-panel-header>
      {{ categoryName }}
    </v-expansion-panel-header>
    <v-divider v-if="expanded" />
    <v-expansion-panel-content>
      <div
        v-for="application in applications"
        :key="application.id"
        class="d-flex flex-no-wrap justify-space-between border-radius border-color ApplicationCard ApplicationCardEmbedded">
        <layout-editor-application-card
          :application="application"
          class="flex-grow-1"
          @add="$emit('addApplication', application)" />
      </div>
    </v-expansion-panel-content>
  </v-expansion-panel>
</template>

<script>
export default {
  props: {
    expanded: {
      type: Boolean,
      default: false,
    },
    category: {
      type: Object,
      default: null,
    },
  },
  computed: {
    categoryName() {
      return this.category && this.category.name;
    },
    applications() {
      return this.category?.applications || [];
    },
    hasApplications() {
      return this.applications.length > 0;
    },
  },
};
</script>

