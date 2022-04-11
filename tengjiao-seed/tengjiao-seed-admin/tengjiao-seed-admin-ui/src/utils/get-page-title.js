import defaultSettings from '@/settings'
import i18n from '@/lang'

const title = defaultSettings.title || 'Vue Element Admin'

/**
 * modified by rise 20210324
 * @param key
 * @returns {string}
 */
export default function getPageTitle (key) {
  const hasKey = i18n.te(`route.${key}`)
  const pageName = hasKey ? i18n.t(`route.${key}`) : key
  return `${pageName} - ${title}`
}
