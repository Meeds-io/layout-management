<template>
  <v-btn
    class="d-flex align-center"
    color="primary"
    elevation="0"
    @click="$root.$emit('layout-save-page')">
    <span class="text-none">{{ $t('layout.save') }}</span>
  </v-btn>
</template>
