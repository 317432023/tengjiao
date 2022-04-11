const MenuUtils = {

  SOURCE_LIST_SCHEMA: {
    /* 平的菜单列表 属性名 */
    FLAT_ID_PROP: 'id',
    FLAT_PID_PROP: 'pid', // 平的菜单列表 父id属性名
    FLAT_TITLE_PROP: 'title',
    FLAT_PATH_PROP: 'path',
    FLAT_PERM_PROP: 'perm',
    FLAT_PATTERN_PROP: 'pattern',
    FLAT_ICON_PROP: 'icon',
    FLAT_DISABLED_PROP: 'disabled',
    FLAT_TYPE_PROP: 'type', // 平的菜单列表 中类型属性名
    FLAT_OPEN_PROP: 'open', // 平的菜单列表 中是否展开属性名
    FLAT_SORT_NUM_PROP: 'sortNum', // 平的菜单列表 中排序号属性名

    FLAT_MENU_TYPE_VAL: [0, 2], // 平的菜单和目录类型值
    FLAT_PERM_TYPE_VAL: 1, // 平的操作权限类型值
    FLAT_ROOT_ID: 0 // 平的根节点ID
  },
  TARGET_TREE_SCHEMA: {
    /* 欲生成的菜单树 属性名 */
    ID_PROP: 'id',
    PID_PROP: 'pid',
    CHILDREN_PROP: 'children',
    TITLE_PROP: 'title',
    PATH_PROP: 'path',
    PERM_PROP: 'perm',
    PATTERN_PROP: 'pattern',
    ICON_PROP: 'icon',
    DISABLED_PROP: 'disabled',
    TYPE_PROP: 'type',
    OPEN_PROP: 'open',
    SORT_NUM_PROP: 'sortNum'
  },

  /**
   * 将flat菜单列表转换成菜单树
   */
  conv2Tree: function (rawFlatMenuList, filterLeaf = true, source = this.SOURCE_LIST_SCHEMA, target = this.TARGET_TREE_SCHEMA, copyOtherProperties = true) {
    const copiedSource = JSON.parse(JSON.stringify(this.SOURCE_LIST_SCHEMA))
    const copiedTarget = JSON.parse(JSON.stringify(this.TARGET_TREE_SCHEMA))

    // 对象合并/设置映射关系
    for (const key in source) {
      if (Object.prototype.hasOwnProperty.call(source, key)) {
        copiedSource[key] = source[key]
      }
    }
    for (const key in target) {
      if (Object.prototype.hasOwnProperty.call(target, key)) {
        copiedTarget[key] = target[key]
      }
    }

    // 根据情况过滤掉权限
    const flatMenuList = filterLeaf
      ? rawFlatMenuList.filter(it => it[copiedSource.FLAT_TYPE_PROP] !== copiedSource.FLAT_PERM_TYPE_VAL) : rawFlatMenuList
    // 将 flatMenuList 转为 menuTree
    const menuTree = []
    // 递归调用
    this.$fillMenuTree(menuTree, flatMenuList, copiedSource.FLAT_ROOT_ID, copiedSource, copiedTarget, copyOtherProperties)
    return menuTree
  },

  /**
   * 递归填充菜单树
   */
  $fillMenuTree: function (children, flatMenuList, pid, source, target, copyOtherProperties) {
    for (let i = 0, len = flatMenuList.length; i < len; i++) {
      if (flatMenuList[i][source.FLAT_PID_PROP] === pid) {
        const flatMenuItem = flatMenuList[i]// flatMenuList.splice(i, 1)[0]

        const menuItem = this.createMenuItem(flatMenuItem, source, target, copyOtherProperties)
        children.push(menuItem)
        this.$fillMenuTree(menuItem[target.CHILDREN_PROP], flatMenuList, flatMenuItem[source.FLAT_ID_PROP], source, target)
      }
    }
  },
  /**
   * 根据平的 菜单项 创建 符合菜单树的菜单项
   */
  createMenuItem: function (flatMenuItem, source = this.SOURCE_LIST_SCHEMA, target = this.TARGET_TREE_SCHEMA, copyOtherProperties = true) {
    const menuItem = {
      toString: function () {
        return this[target.ID_PROP]
      }
    }
    menuItem[target.ID_PROP] = flatMenuItem[source.FLAT_ID_PROP]
    menuItem[target.PID_PROP] = flatMenuItem[source.FLAT_PID_PROP]
    menuItem[target.TITLE_PROP] = flatMenuItem[source.FLAT_TITLE_PROP]
    menuItem[target.PATH_PROP] = flatMenuItem[source.FLAT_PATH_PROP]
    menuItem[target.PERM_PROP] = flatMenuItem[source.FLAT_PERM_PROP]
    menuItem[target.PATTERN_PROP] = flatMenuItem[source.FLAT_PATTERN_PROP]

    menuItem[target.ICON_PROP] = flatMenuItem[source.FLAT_ICON_PROP]
    menuItem[target.DISABLED_PROP] = flatMenuItem[source.FLAT_DISABLED_PROP]
    menuItem[target.CHILDREN_PROP] = []

    menuItem[target.TYPE_PROP] = flatMenuItem[source.FLAT_TYPE_PROP]
    menuItem[target.OPEN_PROP] = flatMenuItem[source.FLAT_OPEN_PROP]
    menuItem[target.SORT_NUM_PROP] = flatMenuItem[source.FLAT_SORT_NUM_PROP]

    if (copyOtherProperties) {
      for (const prop in flatMenuItem) {
        if (Object.prototype.hasOwnProperty.call(flatMenuItem, prop) && !Object.prototype.hasOwnProperty.call(source, prop)) {
          menuItem[prop] = flatMenuItem[prop]
        }
      }
    }

    return menuItem
  }

}

export default function (menus, filterLeaf = true, source = MenuUtils.SOURCE_LIST_SCHEMA, target = MenuUtils.TARGET_TREE_SCHEMA) {
  return MenuUtils.conv2Tree(menus, filterLeaf, source, target)
}
