<template>
  <i :class="iconClass" :style="style"></i>
</template>

<script>
export default {
  name: 'ElIcon',
  props: {
    iconClass: {
      type: String,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  computed: {
    style () {
      return !this.isNest && !this.$store.state.app.sidebar.opened ? 'display:inline-block;position:relative;left:16px;' : ''
    }
  }
}
</script>

<style scoped>
</style>
