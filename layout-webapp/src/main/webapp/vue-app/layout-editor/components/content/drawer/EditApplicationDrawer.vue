<template>
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    allow-expand
    right>
    <template slot="title">
      {{ $t('layout.editApplicationTitle') }}
    </template>
    <template v-if="drawer" #content>
      <v-card
        max-width="100%"
        class="ma-4 overflow-hidden"
        flat>
        <div class="font-weight-bold">
          {{ $t('layout.applicationHeight') }}
        </div>
        <v-radio-group v-model="container.height" mandatory>
          <v-radio
            :label="$t('layout.applicationHeightScroll')"
            value="unset" />
          <v-radio
            :label="$t('layout.applicationHeightAuto')"
            value="auto" />
        </v-radio-group>
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    section: null,
    container: null,
  }),
  computed: {
    height() {
      return this.container?.height;
    },
  },
  watch: {
    height() {
      this.$root.$emit('layout-apply-grid-style');
    },
  },
  methods: {
    open(section, container) {
      this.section = section;
      this.container = container;
      this.$refs.drawer.open();
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>