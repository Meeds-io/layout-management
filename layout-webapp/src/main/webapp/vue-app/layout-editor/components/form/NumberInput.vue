<template>
  <v-card
    class="d-flex flex-row align-center justify-center"
    max-width="88"
    flat>
    <v-btn
      icon
      @click="decrementNumber">
      <v-icon>fa-minus fa-sm</v-icon>
    </v-btn>
    <input
      v-if="editable"
      v-model="num"
      :aria-label="label"
      :step="step"
      :min="min"
      :max="max"
      :class="valid && 'text-color' || 'error-color'"
      type="text"
      class="layout-number-input pa-0 text-center">
    <div v-else>{{ num }}</div>
    <v-btn
      icon
      @click="incrementNumber">
      <v-icon>fa-plus fa-sm</v-icon>
    </v-btn>
  </v-card>
</template>
<script>
export default {
  props: {
    value: {
      type: String,
      default: null,
    },
    label: {
      type: String,
      default: null,
    },
    min: {
      type: Number,
      default: () => 0,
    },
    max: {
      type: Number,
      default: () => 20,
    },
    step: {
      type: Number,
      default: () => 4,
    },
    editable: {
      type: Boolean,
      default: false,
    },
    diff: {
      type: Number,
      default: () => 0,
    },
  },
  data: () => ({
    num: 20,
    valid: false,
  }),
  watch: {
    num: {
      immediate: true,
      handler() {
        if (this.min && Number(this.num) < Number(this.min)) {
          this.$emit('input', Number(this.min) + this.diff);
          this.valid = false;
        } else if (this.max && Number(this.num) > Number(this.max)) {
          this.$emit('input', Number(this.max) + this.diff);
          this.valid = false;
        } else {
          this.$emit('input', Number(this.num) + this.diff);
          this.valid = true;
        }
      },
    },
    valid: {
      immediate: true,
      handler() {
        this.$emit('valid', this.valid);
      },
    },
  },
  created() {
    this.num = (this.value || 0) - this.diff;
  },
  methods: {
    adjust() {
      if (this.min && Number(this.num) < Number(this.min)) {
        this.num = Number(this.min);
      } else if (this.max && Number(this.num) > Number(this.max)) {
        this.num = Number(this.max);
      }
    },
    decrementNumber() {
      this.adjust();
      if (Number(this.num) > this.min) {
        this.num = Number(this.num) - this.step;
      }
    },
    incrementNumber() {
      this.adjust();
      if (Number(this.num) < this.max) {
        this.num = Number(this.num) + this.step;
      }
    },
  },
};
</script>
