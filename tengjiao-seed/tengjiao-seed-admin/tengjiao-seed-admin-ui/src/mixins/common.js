
export default {
  data () {
    return {
      // page sizes 下拉框
      pagesizes: [2, 5, 10, 20, 30, 50],

      // 表格数据
      records: [],
      // 总记录数
      total: 0,
      // 表格查询参数
      params: {
        current: 1,
        size: 10,
        // 模糊查询的文本
        text: '',
        // 根据什么排序
        sort: [],
        asc: true
      },
      // 查询内容体
      queryForm: {},

      // 是否为添加的弹窗
      isAdd: true,
      request: {
        add: () => {
        },
        mod: () => {
        },
        delete: () => {
        },
        page: () => {
        },
        getById: () => {
        },
        forbid: () => {
        }
      },
      // 表格Loading
      loadingTable: false,
      // 提交表单按钮Loading
      loadingSubmit: false,
      // 编辑数据Loading
      loadingEdit: false,
      // 增加编辑的内容体
      dialogForm: {},
      // 弹窗是否隐藏
      showDialog: false,
      // 按钮权限
      perm: []
    }
  },
  methods: {

    havePermission (perm) {
      return this.perm.indexOf(perm) !== -1
    },

    haveAnyPermission (perm) {
      for (let i = 0, len = perm.length; i < len; i++) {
        if (this.havePermission(perm[i])) {
          return true
        }
      }
      return false
    },

    // 删除数据
    removeOne (id) {
      this.$confirm('此操作将永久删除此纪录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(res => {
        this.request.delete(id).then(res => {
          if (res.success) {
            this.$notify.success(res.message || 'success')
            this.queryPage()
          }
        })
      })
    },

    // 禁用记录
    forbidOne (id) {
      this.$confirm('此操作将禁用此纪录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(res => {
        this.request.forbid(id).then(res => {
          if (res.success) {
            this.$notify.success(res.message || 'success')
            this.queryPage()
          }
        })
      })
    },

    // 启用记录
    enableOne (id) {
      this.$confirm('此操作将启用此纪录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(res => {
        this.request.enable(id).then(res => {
          if (res.success) {
            this.$notify.success(res.message || 'success')
            this.queryPage()
          }
        })
      })
    },

    // 显示添加模态框
    showAddDialog (dialogForm) {
      this.isAdd = true
      this.dialogForm = dialogForm
      this.showDialog = true
    },
    // 编辑数据
    showEditDialog (id) {
      this.isAdd = false
      this.request.getById(id).then(res => {
        this.dialogForm = res.data
        this.showDialog = true
      })
    },

    // 分页查询
    queryPage (curPage) {
      if (curPage) {
        this.params.current = curPage
      }
      this.loadingTable = true
      console.log('params = ' + JSON.stringify(this.params) + ', queryForm = ' + JSON.stringify(this.queryForm))
      this.request.page(this.params, this.queryForm).then(res => {
        if (res.success) {
          this.records = res.data.records
          this.total = Number(res.data.total)
        } else {
          this.$notify.error(res.message)
        }
        this.loadingTable = false
      })
    },

    // 重置查询表单
    reset (form) {
      if (form) {
        console.log('重置表单')
        form.resetFields()
      } else {
        // 清除查询表单
        console.log('默认清除查询表单内容')
        for (const key in this.queryForm) {
          this.queryForm[key] = ''
        }
      }
      this.loadingSubmit = false
    },

    // 提交表单
    submitForm () {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          this.loadingSubmit = true
          if (this.isAdd) {
            console.log('添加记录:' + JSON.stringify(this.dialogForm))
            this.request.add(this.dialogForm).then(res => {
              this.loadingSubmit = false
              if (res.success) {
                this.showDialog = false
                this.queryPage()
                this.$notify.success(res.message || 'success')
              }
            })
          } else {
            console.log('修改记录:' + JSON.stringify(this.dialogForm))
            this.request.mod(this.dialogForm).then(res => {
              this.loadingSubmit = false
              if (res.success) {
                this.showDialog = false
                this.$notify.success(res.message || 'success')
                this.queryPage()
              }
            })
          }
        }
      })
    },

    // 图片字符串转数组（用于el-image）
    srcList(imgStrs,staticPrefix) {
      let fullImgArr = []
      let imgArr = imgStrs.split('|')
      imgArr.forEach((e,i)=>{
        if(e.startsWith('http://') || e.startsWith('https://')) {
          fullImgArr.push(e)
        } else {
          fullImgArr.push(e.startsWith('/')?staticPrefix+e : staticPrefix+'/'+e)
        }
      });
      return fullImgArr
    }

  },

  created () {
    this.queryPage()
    if (this.$route.meta.perm) {
      this.perm = this.$route.meta.perm
      console.log('当前登录用户在该路由拥有权限：' + JSON.stringify(this.perm))
    }
  }

}
