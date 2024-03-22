<template>
  <exo-drawer
    ref="drawer"
    v-model="drawer"
    allow-expand
    right>
    <template slot="title">
      {{ $t('layout.editApplicationTitle') }}
    </template>
    <template #content>
      <v-card
        max-width="100%"
        class="ma-4 overflow-hidden"
        flat>
        <div class="subtitle-1 font-weight-bold">
          {{ $t('layout.margins') }}
        </div>
        <v-list-item class="pa-0">
          <v-list-item-content class="my-auto">
            {{ $t('layout.top') }}
          </v-list-item-content>
          <layout-editor-border-input
            v-model="marginTop" />
        </v-list-item>
        <v-list-item class="pa-0">
          <v-list-item-content class="my-auto">
            {{ $t('layout.right') }}
          </v-list-item-content>
          <layout-editor-border-input
            v-model="marginRight" />
        </v-list-item>
        <v-list-item class="pa-0">
          <v-list-item-content class="my-auto">
            {{ $t('layout.bottom') }}
          </v-list-item-content>
          <layout-editor-border-input
            v-model="marginBottom" />
        </v-list-item>
        <v-list-item class="pa-0">
          <v-list-item-content class="my-auto">
            {{ $t('layout.left') }}
          </v-list-item-content>
          <layout-editor-border-input
            v-model="marginLeft" />
        </v-list-item>
        <div class="subtitle-1 font-weight-bold">
          {{ $t('layout.borders') }}
        </div>
        <v-list-item class="pa-0" dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.color') }}
          </v-list-item-content>
          <v-list-item-action class="my-auto">
            <layout-editor-color-picker
              v-model="borderColor" />
          </v-list-item-action>
        </v-list-item>
        <v-list-item class="pa-0" dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.radius') }}
          </v-list-item-content>
        </v-list-item>
        <layout-editor-border-radius-selector
          v-model="borderRadius" />
      </v-card>
    </template>
  </exo-drawer>
</template>
<script>
export default {
  data: () => ({
    drawer: false,
    initialized: false,
    section: null,
    container: null,
    marginTop: 20,
    marginRight: 20,
    marginBottom: 20,
    marginLeft: 20,
    borderRadius: 8,
    borderColor: '#FFFFFF',
  }),
  computed: {
    sectionId() {
      return this.section.storageId;
    },
    styleClasses() {
      return this.drawer && this.sectionId && {
        marginTop: this.marginTop,
        marginRight: this.marginRight,
        marginBottom: this.marginBottom,
        marginLeft: this.marginLeft,
        borderRadius: this.borderRadius,
        borderColor: this.borderColor,
      } || null;
    },
  },
  watch: {
    styleClasses(value, oldVal) {
      if (value && oldVal && this.sectionId) {
        this.$root.$emit('layout-section-history-add', this.sectionId);
        this.$layoutUtils.applyContainerStyle(this.section, this.container, value);
        this.$root.$emit('layout-modified', true);
      } else if (value && !oldVal) {
        this.initialized = true;
      }
    },
  },
  methods: {
    open(section, container) {
      this.section = section;
      this.container = container;
      this.initialized = false;

      this.$layoutUtils.parseContainerStyle(this.container, this.styleClasses);
      this.marginTop = this.container.marginTop || 0;
      this.marginRight = this.container.marginRight || 0;
      this.marginBottom = this.container.marginBottom || 0;
      this.marginLeft = this.container.marginLeft || 0;
      this.borderRadius = this.container.borderRadius || 8;
      this.borderColor = this.container.borderColor || '#FFFFFF';
      this.$nextTick(() => this.$refs.drawer.open());
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>