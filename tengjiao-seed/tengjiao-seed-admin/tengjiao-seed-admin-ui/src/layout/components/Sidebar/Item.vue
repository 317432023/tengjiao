<script>
export default {
  name: 'MenuItem',
  functional: true,
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    },
    // 新增的属性 20210621
    isNest: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  render (h, context) {
    const { icon, title, isNest } = context.props
    const vnodes = []

    if (icon) {
      // 图标方式一：Element 自带图标
      // vnodes.push(<el-icon icon-class={icon} is-nest={isNest} />)
      // 图标方式二：引入SVG 图标
      vnodes.push(<svg-icon icon-class={icon} />)
    }

    if (title) {
      vnodes.push(<span slot='title'>{(title)}</span>)
    }
    return vnodes
  }
}
</script>
