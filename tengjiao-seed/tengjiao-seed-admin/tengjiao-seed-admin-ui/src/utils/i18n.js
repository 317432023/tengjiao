import i18n from '@/lang'

export function generateTitle (title) {
  // console.log(title)

  // const hasKey = this.$te('route.' + title)
  const hasKey = i18n.te('route.' + title)

  if (hasKey) {
    // 翻译router.meta.标题，用于面包屑侧边栏tagsview
    // $t :this method from vue-i18n, inject in @/lang/index.js
    // const translatedTitle = this.$t('route.' + title)
    const translatedTitle = i18n.t('route.' + title)

    return translatedTitle
  }
  return title
}
