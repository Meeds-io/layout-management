<template>
  <div class="d-flex flex-row my-5">
    <portlets-instance-categories
      ref="list"
      :keyword="keyword"
      class="card-border-radius hidden-sm-and-down me-5" />
    <portlets-instance-list
      ref="list"
      :keyword="keyword"
      class="card-border-radius overflow-hidden" />
  </div>
</template>
<script>
export default {
  props: {
    keyword: {
      type: String,
      default: null,
    },
  }
};
</script>