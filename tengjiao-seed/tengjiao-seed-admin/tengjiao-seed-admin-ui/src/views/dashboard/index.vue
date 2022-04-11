<!--<template>
  <div class="app-container">
      欢迎页面
  </div>
</template>

<script>
  export default {
    name: 'Dashboard'
  }
</script>

<style scoped>

</style>-->
<template>
  <div class="dashboard-container">
    <component :is="currentRole" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import adminDashboard from './admin'
import editorDashboard from './editor'

export default {
  name: 'Dashboard',
  components: { adminDashboard, editorDashboard },
  data () {
    return {
      currentRole: 'editorDashboard'
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  created () {
    if (!this.roles.includes('editor')) {
      this.currentRole = 'adminDashboard'
    }
  }
}
</script>
