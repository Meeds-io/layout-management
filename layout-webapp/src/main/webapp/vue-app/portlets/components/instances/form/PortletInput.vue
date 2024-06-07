<!--

 This file is part of the Meeds project (https://meeds.io/).

 Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

-->
<template>
  <div>
    <div class="mb-2">{{ $t('layout.portlet') }}</div>
    <v-autocomplete
      ref="autocomplete"
      v-model="contentId"
      :items="sortedPortlets"
      :disabled="disabled"
      :placeholder="$t('layout.portlet.placeholder')"
      class="portlet-instance-category-autocomplete ma-0 pa-0"
      item-value="contentId"
      item-text="name"
      outlined
      dense
      chips />
  </div>
</template>
<script>
export default {
  props: {
    value: {
      type: Number,
      default: () => 0,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    contentId: null,
  }),
  computed: {
    sortedPortlets() {
      const portlets = this.$root.portlets?.filter?.(c => c.name) || [];
      portlets.sort((a, b) => this.$root.collator.compare(a.name.toLowerCase(), b.name.toLowerCase()));
      return portlets;
    },
  },
  watch: {
    value: {
      immediate: true,
      handler() {
        this.contentId = this.value;
      },
    },
    contentId() {
      if (this.contentId !== this.value) {
        this.$emit('input', this.contentId);
      }
    },
  },
  created() {
    document.addEventListener('click', this.closeMenu);
  },
  beforeDestroy() {
    document.removeEventListener('click', this.closeMenu);
  },
  methods: {
    closeMenu(event) {
      if (this?.$refs?.autocomplete
          && !event?.target?.closest?.('.portlet-instance-category-autocomplete')) {
        this.$refs.autocomplete.blur();
      }
    },
  }
};
</script>