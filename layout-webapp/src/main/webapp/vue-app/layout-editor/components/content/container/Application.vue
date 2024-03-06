<template>
  <div
    v-if="content"
    ref="content"
    v-html="content"></div>
</template>
<script>
export default {
  props: {
    container: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    applicationContent: null,
  }),
  computed: {
    contentId() {
      return this.container?.contentId;
    },
    applicationName() {
      return this.contentId?.split?.('/')?.[1];
    },
    storageId() {
      return this.container?.randomId === this.container?.storageId ? null : this.container?.storageId;
    },
    nodeId() {
      return this.$root.nodeId;
    },
    content() {
      return this.applicationContent?.content?.replaceAll?.('\n', '')?.replace('<script>', '<script type="text/javascript">');
    },
    jsModule() {
      return this.applicationContent?.jsModule;
    },
    cssModule() {
      return this.applicationContent?.cssModule;
    },
    cssModuleId() {
      return this.contentId?.replace?.('/', '_');
    },
  },
  watch: {
    jsModule() {
      this.runJsModule();
    },
    cssModule() {
      this.addCssModule();
    },
  },
  created() {
    if (this.applicationName) {
      this.$applicationLayoutService.getApplicationRenderContent(this.applicationName, this.nodeId, this.storageId)
        .then(applicationContent => this.applicationContent = applicationContent);
    }
  },
  methods: {
    runJsModule() {
      if (!this.$refs?.content) {
        window.setTimeout(() => this.runJsModule(), 50);
        return;
      }
      const script = this.$refs?.content.querySelector('script');
      if (script) {
        window.eval(script.innerText?.replaceAll?.('\n', '')?.replaceAll?.('\r', ''));
        return;
      } else if (this.jsModule) {
        window.require([`PORTLET/${this.jsModule}`], app => app?.init?.());
      }
    },
    addCssModule() {
      if (this.cssModule && !document.querySelector(`link[href="${this.cssModule}"]`)) {
        const link = document.createElement('link');
        link.id = this.cssModuleId;
        link.type = 'text/css';
        link.rel = 'stylesheet';
        link.href = this.cssModule;
        document.head.appendChild(link);
      }
    },
  },
};
</script>