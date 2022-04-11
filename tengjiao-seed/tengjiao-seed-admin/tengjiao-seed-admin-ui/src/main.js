import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import Cookies from 'js-cookie'
import 'normalize.css/normalize.css' // CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueParticles from 'vue-particles'

// import locale from 'element-ui/lib/locale/lang/en' // lang i18n
import i18n from './lang'
import '@/styles/index.scss' // global css

// 图标
import '@/icons' // icon
// 通过 导航守卫 做 权限控制（开发静态页期间注释掉）
import '@/permission' // permission control
// 默认字体大小
//import defaultSettings from '@/settings'
//
//const { size } = defaultSettings
import { size } from '@/settings'

// set ElementUI lang to EN
// Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)
Vue.use(ElementUI, {
  // locale,
  size: Cookies.get('size') || size, // set element-ui default size
  i18n: (key, value) => i18n.t(key, value)
})
Vue.use(VueParticles) // 原子背景

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
