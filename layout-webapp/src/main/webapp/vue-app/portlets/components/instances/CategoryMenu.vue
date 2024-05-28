<template>
  <v-menu
    v-model="menu"
    :left="!$vuetify.rtl"
    :right="$vuetify.rtl"
    :content-class="menuId"
    offset-y>
    <template #activator="{ on, attrs }">
      <v-btn
        :aria-label="$t('portlets.menu.open')"
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
        <v-list-item-group v-model="listItem">
          <v-tooltip :disabled="!category.system" bottom>
            <template #activator="{ on, attrs }">
              <div
                v-on="on"
                v-bind="attrs">
                <v-list-item
                  :disabled="category.system"
                  dense
                  @click="$root.$emit('portlets-instance-category-delete', category)">
                  <v-icon
                    :class="!category.system && 'error--text' || 'disabled--text'"
                    size="13">
                    fa-trash
                  </v-icon>
                  <v-list-item-title
                    :class="!category.system && 'error--text' || 'disabled--text'"
                    class="ps-2">
                    {{ $t('portlets.label.delete') }}
                  </v-list-item-title>
                </v-list-item>
              </div>
            </template>
            <span>{{ $t('portlets.label.category.system.noDelete') }}</span>
          </v-tooltip>
        </v-list-item-group>
      </v-list>
    </v-hover>
  </v-menu>
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
    menu: false,
    hoverMenu: false,
    listItem: null,
    menuId: `PortletInstanceCategoryMenu${parseInt(Math.random() * 10000)}`,
  }),
  computed: {
    categoryId() {
      return this.category?.id;
    },
    name() {
      return this.$te(this.category?.name) ? this.$t(this.category?.name) : this.category?.name;
    },
    editLayoutLink() {
      return `/portal/administration/layout-editor?categoryId=${this.categoryId}`;
    },
  },
  watch: {
    listItem() {
      if (this.menu) {
        this.menu = false;
        this.listItem = null;
      }
    },
    menu() {
      if (this.menu) {
        this.$root.$emit('portlets-instance-category-menu-opened', this.categoryId);
      } else {
        this.$root.$emit('portlets-instance-category-menu-closed', this.categoryId);
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
    this.$root.$on('portlets-instance-category-menu-opened', this.checkMenuStatus);
    document.addEventListener('click', this.closeMenuOnClick);
  },
  beforeDestroy() {
    this.$root.$off('portlets-instance-category-menu-opened', this.checkMenuStatus);
    document.removeEventListener('click', this.closeMenuOnClick);
  },
  methods: {
    closeMenuOnClick(e) {
      if (e.target && !e.target.closest(`.${this.menuId}`)) {
        this.menu = false;
      }
    },
    checkMenuStatus(instanceId) {
      if (this.menu && instanceId !== this.category.id) {
        this.menu = false;
      }
    },
  },
};
</script>