/**
 * @deprecated 本组件废弃
 */

import promptMessages from './promptMessages.vue'

promptMessages.install = function (Vue) {
  Vue.component(promptMessages.name, promptMessages)
}

export default promptMessages
