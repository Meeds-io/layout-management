<template>
  <v-card :id="applicationId" flat>
    <div class="d-flex flex-no-wrap">
      <v-avatar
        class="ApplicationCardImage mx-1 my-auto"
        size="45"
        tile>
        <i :class="applicationIcon"></i>
      </v-avatar>
      <div class="flex-grow-1 ApplicationCardBody text-truncate">
        <div
          :title="applicationName"
          class="text-truncate subtitle-1 px-1 pt-4 text-color ApplicationCardTitle">
          {{ applicationName }}
        </div>
        <v-card-subtitle
          :title="applicationDescription"
          class="text-truncate subtitle-2 px-1 pt-0 text-sub-title ApplicationCardDescription">
          {{ applicationDescription || applicationName }}
        </v-card-subtitle>
      </div>
      <div class="ApplicationCardAction">
        <v-btn
          text
          height="100%"
          width="100%px"
          class="primary--text"
          @click="$emit('add')">
          <v-icon size="36">fa-plus</v-icon>
        </v-btn>
      </div>
    </div>
  </v-card>
</template>
<script>
export default {
  props: {
    application: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    displayActionMenu: false,
  }),
  computed: {
    applicationId() {
      return this.application?.contentId?.split?.('/')?.[1];
    },
    applicationName() {
      return this.application?.displayName || this.application?.applicationName;
    },
    applicationDescription() {
      return this.application?.description;
    },
    applicationIcon() {
      if (!this.applicationId) {
        return '';
      }
      const iconSuffix = `${this.applicationId.charAt(0).toUpperCase()}${this.applicationId.substring(1)}`;
      return `uiIconApp${iconSuffix} uiIconDefaultApp`;
    },
  },
};
</script>

