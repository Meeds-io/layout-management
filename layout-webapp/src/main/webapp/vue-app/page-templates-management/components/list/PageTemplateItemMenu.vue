<template>
  <v-menu
    v-model="menu"
    :left="!$vuetify.rtl"
    :right="$vuetify.rtl"
    bottom
    offset-y
    attach>
    <template #activator="{ on, attrs }">
      <v-btn
        :aria-label="$t('pageTemplates.menu.open')"
        icon
        small
        class="mx-auto"
        v-bind="attrs"
        v-on="on">
        <v-icon size="16" class="icon-default-color">fas fa-ellipsis-v</v-icon>
      </v-btn>
    </template>
    <v-hover v-if="menu" @input="hoverMenu = $event">
      <v-list
        class="pa-0"
        dense
        @mouseout="menu = false"
        @focusout="menu = false">
        <v-tooltip :disabled="!pageTemplate.system" bottom>
          <template #activator="{ on, attrs }">
            <div
              v-on="on"
              v-bind="attrs">
              <v-list-item
                :disabled="pageTemplate.system"
                dense
                @click="$root.$emit('page-templates-delete', pageTemplate)">
                <v-icon
                  :class="!pageTemplate.system && 'error--text' || 'disabled--text'"
                  size="13">
                  fa-trash
                </v-icon>
                <v-list-item-title
                  :class="!pageTemplate.system && 'error--text' || 'disabled--text'"
                  class="pl-3">
                  {{ $t('pageTemplate.label.delete') }}
                </v-list-item-title>
              </v-list-item>
            </div>
          </template>
          <span>{{ $t('pageTemplate.label.system.noDelete') }}</span>
        </v-tooltip>
      </v-list>
    </v-hover>
  </v-menu>
</template>
<script>
export default {
  props: {
    pageTemplate: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    menu: false,
    hoverMenu: false,
  }),
  computed: {
    pageTemplateId() {
      return this.pageTemplate?.id;
    },
  },
  watch: {
    menu() {
      if (this.menu) {
        this.$root.$emit('page-management-menu-opened', this.pageTemplateId);
      } else {
        this.$root.$emit('page-management-menu-closed', this.pageTemplateId);
      }
    },
    hoverMenu() {
      if (!this.hoverMenu) {
        window.setTimeout(() => {
          if (!this.hoverMenu) {
            this.menu = false;
          }
        }, 200);
      }
    },
  },
  created() {
    this.$root.$on('page-management-menu-opened', this.checkMenuStatus);
  },
  beforeDestroy() {
    this.$root.$off('page-management-menu-opened', this.checkMenuStatus);
  },
  methods: {
    checkMenuStatus(templateId) {
      if (this.menu && templateId !== this.pageTemplate.id) {
        this.menu = false;
      }
    },
  },
};
</script>