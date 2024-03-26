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
        class="ma-4"
        flat>
        <div class="subtitle-1 font-weight-bold">
          {{ $t('layout.margins') }}
        </div>
        <div
          :class="marginChoice === 'same' && 'flex-row' || 'flex-column'"
          class="d-flex">
          <v-radio-group v-model="marginChoice" class="my-auto text-no-wrap ms-n1">
            <v-radio
              :label="$t('layout.sameForAllSides')"
              value="same"
              class="mx-0" />
            <v-radio
              :label="$t('layout.differentForEachSide')"
              value="different"
              class="mx-0" />
          </v-radio-group>
          <v-list-item class="pe-0 ps-7 py-0" dense>
            <v-list-item-content v-if="marginChoice === 'different'" class="my-auto">
              {{ $t('layout.top') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="marginTop"
              :diff="-20"
              :class="marginChoice === 'different' && 'my-auto' || 'mb-auto ms-auto'" />
          </v-list-item>
          <v-list-item
            v-if="marginChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.right') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="marginRight"
              :diff="-20"
              class="my-auto" />
          </v-list-item>
          <v-list-item
            v-if="marginChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.bottom') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="marginBottom"
              :diff="-20"
              class="my-auto" />
          </v-list-item>
          <v-list-item
            v-if="marginChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.left') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="marginLeft"
              :diff="-20"
              class="my-auto" />
          </v-list-item>
        </div>
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.borderColor') }}
          </div>
          <v-switch
            v-model="enableBorderColor"
            class="ms-auto my-auto me-n2" />
        </div>
        <v-list-item
          v-if="enableBorderColor"
          class="pa-0"
          dense>
          <v-list-item-content class="my-auto">
            {{ $t('layout.color') }}
          </v-list-item-content>
          <v-list-item-action class="my-auto me-0 ms-auto">
            <layout-editor-color-picker
              v-model="borderColor"
              class="my-auto" />
          </v-list-item-action>
        </v-list-item>
        <div class="d-flex align-center mt-4">
          <div class="subtitle-1 font-weight-bold me-auto">
            {{ $t('layout.borderRadius') }}
          </div>
          <v-switch
            v-model="enableBorderRadius"
            class="ms-auto my-auto me-n2" />
        </div>
        <div
          v-if="enableBorderRadius"
          :class="radiusChoice === 'same' && 'flex-row' || 'flex-column'"
          class="d-flex">
          <v-radio-group v-model="radiusChoice" class="my-auto text-no-wrap ms-n1">
            <v-radio
              :label="$t('layout.sameForAllCorners')"
              value="same"
              class="mx-0" />
            <v-radio
              :label="$t('layout.differentForEachCorner')"
              value="different"
              class="mx-0" />
          </v-radio-group>
          <v-list-item class="pe-0 ps-7 py-0" dense>
            <v-list-item-content v-if="radiusChoice === 'different'" class="my-auto">
              {{ $t('layout.topRight') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="radiusTopRight"
              :class="radiusChoice === 'different' && 'my-auto' || 'mb-auto ms-auto'" />
          </v-list-item>
          <v-list-item
            v-if="radiusChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.topLeft') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="radiusTopLeft"
              class="my-auto" />
          </v-list-item>
          <v-list-item
            v-if="radiusChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.bottomRight') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="radiusBottomRight"
              class="my-auto" />
          </v-list-item>
          <v-list-item
            v-if="radiusChoice === 'different'"
            class="pe-0 ps-7 py-0"
            dense>
            <v-list-item-content class="my-auto">
              {{ $t('layout.bottomLeft') }}
            </v-list-item-content>
            <layout-editor-border-input
              v-model="radiusBottomLeft"
              class="my-auto" />
          </v-list-item>
        </div>
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
    marginChoice: 'same',
    marginTop: 20,
    marginRight: 20,
    marginBottom: 20,
    marginLeft: 20,
    enableBorderRadius: true,
    radiusChoice: 'same',
    radiusTopRight: 4,
    radiusTopLeft: 4,
    radiusBottomRight: 4,
    radiusBottomLeft: 4,
    enableBorderColor: true,
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
        radiusTopRight: this.radiusTopRight,
        radiusTopLeft: this.radiusTopLeft,
        radiusBottomRight: this.radiusBottomRight,
        radiusBottomLeft: this.radiusBottomLeft,
        borderColor: this.borderColor,
      } || null;
    },
  },
  watch: {
    marginTop() {
      if (this.marginChoice === 'same') {
        this.marginRight = this.marginTop;
        this.marginBottom = this.marginTop;
        this.marginLeft = this.marginTop;
      }
    },
    radiusTopRight() {
      if (this.radiusChoice === 'same') {
        this.radiusTopLeft = this.radiusTopRight;
        this.radiusBottomRight = this.radiusTopRight;
        this.radiusBottomLeft = this.radiusTopRight;
      }
    },
    enableBorderColor(val) {
      if (val) {
        if (!this.borderColor) {
          this.borderColor = '#FFFFFF';
        }
      } else {
        this.borderColor = null;
      }
    },
    enableBorderRadius(val) {
      if (val) {
        if (!this.radiusTopRight) {
          this.radiusTopRight = 4;
          this.radiusTopLeft = 4;
          this.radiusBottomRight = 4;
          this.radiusBottomLeft = 4;
        }
      } else {
        this.radiusTopRight = null;
        this.radiusTopLeft = null;
        this.radiusBottomRight = null;
        this.radiusBottomLeft = null;
      }
    },
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
      this.initialized = false;

      this.section = section;
      this.container = container;
      this.$layoutUtils.parseContainerStyle(this.container, this.styleClasses);

      this.marginTop = this.container.marginTop || 0;
      this.marginRight = this.container.marginRight || 0;
      this.marginBottom = this.container.marginBottom || 0;
      this.marginLeft = this.container.marginLeft || 0;

      this.radiusTopRight = this.container.radiusTopRight;
      this.radiusTopLeft = this.container.radiusTopLeft;
      this.radiusBottomRight = this.container.radiusBottomRight;
      this.radiusBottomLeft = this.container.radiusBottomLeft;
      this.enableBorderRadius = this.radiusBottomLeft === 0 || !!this.radiusBottomLeft;

      this.borderColor = this.container.borderColor;
      this.enableBorderColor = !!this.borderColor;

      this.marginChoice = this.marginTop === this.marginRight
        && this.marginRight === this.marginLeft
        && this.marginLeft === this.marginBottom ? 'same' : 'different';
      this.radiusChoice = this.radiusTopRight === this.radiusTopLeft
        && this.radiusBottomRight === this.radiusTopLeft
        && this.radiusTopLeft === this.radiusBottomLeft
        && this.radiusBottomLeft === this.radiusTopRight ? 'same' : 'different';

      this.$nextTick(() => this.$refs.drawer.open());
    },
    close() {
      this.$refs.drawer.close();
    },
  },
};
</script>