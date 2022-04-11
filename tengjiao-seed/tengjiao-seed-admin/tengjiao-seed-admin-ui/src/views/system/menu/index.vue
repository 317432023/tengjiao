<template>
    <div class="app-container">
        <el-dialog :title="isAdd?'新增':'编辑'" @closed="reset($refs.dialogForm)" :visible.sync="showDialog" :close-on-click-modal="false" width="610px"
                   v-el-drag-dialog>
            <el-form :model="dialogForm" :inline="true" ref="dialogForm" :rule="rule" style="font-weight: bold;" size="small"
                       label-position="right" label-width="80px">
                <el-form-item prop="type" label="类型">
                    <el-radio-group size="mini" v-model="dialogForm.type" fill="#009688" :disabled="!isAdd">
                        <el-radio-button size="mini" :label="2">目录</el-radio-button>
                        <el-radio-button size="mini" :label="0">菜单</el-radio-button>
                        <el-radio-button size="mini" :label="1">按钮</el-radio-button>
                    </el-radio-group>
                </el-form-item>
                <el-form-item prop="title" label="标题" >
                    <el-input v-model="dialogForm.title" size="mini" placeholder="目录|菜单|权限标题"/>
                </el-form-item>
                <el-form-item prop="pid" label="父级">
                    <tree-select v-model="dialogForm.pid" :options="menuTree" placeholder="不填则为顶级" style="width: 170px" :disabled="!isAdd && dialogForm.type===1"/>
                </el-form-item>
                <el-form-item prop="perm" label="权限">
                    <el-input v-model="dialogForm.perm" size="mini" placeholder="权限" :disabled="dialogForm.type!==1"/>
                </el-form-item>
                <el-form-item prop="name" label="名称">
                  <el-input v-model="dialogForm.name" size="mini" placeholder="路由名称" :disabled="dialogForm.type===1"/>
                </el-form-item>
                <el-form-item prop="path" label="路径">
                    <el-input v-model="dialogForm.path" size="mini" placeholder="URL兼路由路径" :disabled="dialogForm.type===1"/>
                </el-form-item>
                <el-form-item prop="pattern" label="规则">
                    <el-input v-model="dialogForm.pattern" size="mini" placeholder="接口URL规则，用于权限" :disabled="dialogForm.type!==1"/>
                </el-form-item>
                <el-form-item prop="sortNum" label="排序">
                    <el-input-number v-model="dialogForm.sortNum" controls-position="right" :min="0" :max="99999" size="mini" placeholder="排序序号"/>
                </el-form-item>
                <el-form-item label="图标">
                    <!-- 图标方式一：Element 自带图标 -->
                    <!--
                    <elicon-select v-model="dialogForm.icon" :disabled="dialogForm.type===1"></elicon-select>
                    -->

                    <!-- 图标方式二：引入SVG 图标 -->
                    <el-popover
                      placement="bottom-start"
                      width="460"
                      trigger="click"
                      @show="$refs['svgIconSelect'].reset()"
                    >
                      <svgicon-select ref="svgIconSelect" @selected="dialogForm.icon = $event" :disabled="dialogForm.type===1"/>
                      <el-input slot="reference" v-model="dialogForm.icon" placeholder="点击选择图标" readonly>
                        <svg-icon
                          v-if="dialogForm.icon"
                          slot="prefix"
                          :icon-class="dialogForm.icon"
                          class="el-input__icon"
                          style="height: 32px;width: 16px;"
                        />
                        <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                      </el-input>
                    </el-popover>

                </el-form-item>
                <el-form-item prop="open" label="展开">
                  <el-radio-group size="mini" v-model="dialogForm.open" fill="#009688" :disabled="dialogForm.type!==2">
                    <el-radio-button size="mini" :label="1">是</el-radio-button>
                    <el-radio-button size="mini" :label="0">否</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item prop="disabled" label="禁用">
                  <el-radio-group size="mini" v-model="dialogForm.disabled" fill="#009688" :disabled="dialogForm.type===1">
                    <el-radio-button size="mini" :label="1">是</el-radio-button>
                    <el-radio-button size="mini" :label="0">否</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item prop="hidden" label="隐藏">
                  <el-radio-group size="mini" v-model="dialogForm.hidden" fill="#009688" :disabled="dialogForm.type===1">
                    <el-radio-button size="mini" :label="1">是</el-radio-button>
                    <el-radio-button size="mini" :label="0">否</el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <el-form-item prop="breadcrumb" label="导航">
                  <el-radio-group size="mini" v-model="dialogForm.breadcrumb" fill="#009688" :disabled="dialogForm.type===1">
                    <el-radio-button size="mini" :label="1">是</el-radio-button>
                    <el-radio-button size="mini" :label="0">否</el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <el-form-item prop="affix" label="固定">
                  <el-radio-group size="mini" v-model="dialogForm.affix" fill="#009688" :disabled="dialogForm.type!==0">
                    <el-radio-button size="mini" :label="1">是</el-radio-button>
                    <el-radio-button size="mini" :label="0">否</el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <el-form-item prop="noCache" label="缓存">
                  <el-radio-group size="mini" v-model="dialogForm.noCache" fill="#009688" :disabled="dialogForm.type!==0">
                    <el-radio-button size="mini" :label="0">是</el-radio-button>
                    <el-radio-button size="mini" :label="1">否</el-radio-button>
                  </el-radio-group>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="showDialog = false">取 消</el-button>
                <el-button size="mini" type="primary" @click="submitForm()" :loading="loadingSubmit">
                  {{isAdd?'新增':'更新'}}
                </el-button>
            </div>
        </el-dialog>
        <el-form :inline="true">
            <el-form-item>
                <el-button size="mini" type="primary"
                           @click="showAddDialog({disabled:0,hidden:0,type: 1,sortNum:99})"
                           icon="el-icon-plus"
                           v-if="havePermission('system:menu:add')">新增菜单
                </el-button>
            </el-form-item>
        </el-form>
        <span class="divider-line"/>
        <!--表格-->
        <el-table class="table" row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}" v-loading="loadingTable"
                  :data="records" element-loading-text="加载中..." style="width: 100%" stripe border>
            <el-table-column label="标题" >
              <template slot-scope="scope">
                <!-- 图标方式一：Element 图标的显示 -->
                <!--
                <el-icon :icon-class="scope.row.icon" />&nbsp;&nbsp;
                 -->
                <!-- 图标方式二：SVG 格式引入图标的显示 -->
                <svg-icon :icon-class="scope.row.icon" />&nbsp;&nbsp;
                <span>{{scope.row.title}}</span>
              </template>

            </el-table-column>
            <el-table-column align="center" label="类型" prop="type" width="60">
                <template slot-scope="scope">
                    <el-tag type="success">{{scope.row.type===0?'菜单':(scope.row.type===2?'目录':'按钮')}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column align="center" label="URL路由" prop="path" width="160"/>
            <el-table-column align="center" label="权限" prop="perm" width="160"/>
            <el-table-column align="center" label="URL规则" prop="pattern" width="180"/>
            <el-table-column align="center" label="排序" prop="sortNum" width="50"/>

            <el-table-column align="center" label="展开" prop="open" width="50">
              <template slot-scope="scope" v-if="scope.row.type===2">
                <el-switch :value="scope.row.open===1" disabled/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="禁用" prop="disabled" width="50">
              <template slot-scope="scope" v-if="scope.row.type!==1">
                <el-switch :value="scope.row.disabled===1" disabled
                           active-color="#ff4949"
                           inactive-color="#13ce66"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="隐藏" prop="hidden" width="50">
              <template slot-scope="scope" v-if="scope.row.type!==1">
                <el-switch :value="scope.row.hidden===1" disabled/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="导航" prop="breadcrumb" width="50">
              <template slot-scope="scope" v-if="scope.row.type!==1">
                <el-switch :value="!scope.row.breadcrumb || scope.row.breadcrumb===1" disabled/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="固定" prop="affix" width="50">
              <template slot-scope="scope" v-if="scope.row.type===0">
                <el-switch :value="scope.row.affix===1" disabled/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="缓存" prop="noCache" width="50">
              <template slot-scope="scope" v-if="scope.row.type===0">
                <el-switch :value="!scope.row.noCache || scope.row.noCache===0" disabled/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="120" fixed="right"
                             v-if="haveAnyPermission(['system:menu:mod','system:menu:del'])">
                <template slot-scope="scope">
                    <el-button @click="showEditDialog(scope.row.id)" size="mini" type="success" icon="el-icon-edit"
                               :loading="loadingEdit" v-if="havePermission('system:menu:mod')"/>
                    <el-button @click="removeOne(scope.row.id)" size="mini" type="danger" icon="el-icon-delete"
                               v-if="havePermission('system:menu:del')"/>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import elDragDialog from '@/directive/el-drag-dialog'
import request from '@/api/system/menu'
import common from '@/mixins/common'
import conv2Tree from '@/utils/MenuUtils'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import eliconSelect from '@/components/ElIconSelect/index'
import svgiconSelect from '@/components/SvgIconSelect/index'

// import '@/utils/components.js' // 引入自定义组件

export default {
  name: 'Menu',
  components: { TreeSelect, eliconSelect, svgiconSelect },
  mixins: [common],
  directives: { elDragDialog },
  data () {
    return {
      hasChildren: true,
      params: {
        current: 1,
        size: 99999
      },
      rule: [],
      request: request,
      menuTree: []
    }
  },
  methods: {
    // 分页查询 (覆盖了common的queryPage方法)
    queryPage (curPage) {
      if (curPage) {
        this.params.current = curPage
      }
      this.loadingTable = true
      console.log('params = ' + JSON.stringify(this.params) + ', queryForm = ' + JSON.stringify(this.queryForm))
      this.request.page(this.params, this.queryForm).then(res => {
        if (res.success) {
          this.records = res.data.records
          this.total = res.data.total

          // 特殊处理：转列表为树
          console.log('菜单列表（转换前）' + JSON.stringify(this.records))
          this.records = conv2Tree(this.records, false)
          console.log('菜单列表（转换后）=>' + JSON.stringify(this.records))

          this.total = this.records.length
        } else {
          this.$notify.error(res.message)
        }
        this.loadingTable = false
      })
    },

    loadTree () {
      request.getTree().then(res => {
        this.menuTree = res.data
        console.log('菜单树[不含按钮]:' + JSON.stringify(this.menuTree))
      })
    }

  },
  created () {
    this.loadTree()
  }
}
</script>

<style scoped>

</style>
