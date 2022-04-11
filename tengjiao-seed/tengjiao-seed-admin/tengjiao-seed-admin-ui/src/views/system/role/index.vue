<template>
  <div class="app-container">
    <el-dialog :title="isAdd?'新增':'编辑'" @closed="reset($refs.dialogForm)" :close-on-click-modal="false" :visible.sync="showDialog" width="600px"
               v-el-drag-dialog>
      <el-form status-icon :model="dialogForm" :inline="true" ref="dialogForm" :rules="rule" style="font-weight: bold;" size="small" label-position="right" label-width="80px" >
        <el-form-item prop="code" label="角色代码">
            <el-input v-model="dialogForm.code" size="mini" placeholder="站点+角色代码 唯一"/>
        </el-form-item>
        <el-form-item prop="name" label="角色名">
            <el-input v-model="dialogForm.name" size="mini" placeholder=""/>
        </el-form-item>
        <el-form-item prop="remark" label="备注">
            <el-input v-model="dialogForm.remark" size="mini" placeholder=""/>
        </el-form-item>
        <el-form-item label="禁用" prop="disabled">
          <el-radio-group size="mini" v-model="dialogForm.disabled" fill="#009688">
            <el-radio-button size="mini" :label="1">是</el-radio-button>
            <el-radio-button size="mini" :label="0">否</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属站点" prop="stationId" >
          <el-select size="mini" v-model="dialogForm.stationId" placeholder="所属站点" style="width:450px" clearable
                     filterable :disabled="!isAdd">
            <el-option :label="item.name" :value="item.id" v-for="item in stations" :key="'dialogStation' + item.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="showDialog = false">取 消</el-button>
        <el-button size="mini" type="primary" @click="submitForm()" :loading="loadingSubmit">{{isAdd?'新增':'更新'}}</el-button>
      </div>
    </el-dialog>

    <el-dialog title="角色授权" :close-on-click-modal="false" :visible.sync="menuDialog" width="250px">
      <el-tree
        accordion
        ref="menu"
        :data="menuTree"
        :default-checked-keys="roleMenus"
        show-checkbox
        node-key="id">
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button size="mini" @click="menuDialog = false">取 消</el-button>
        <el-button size="mini" type="primary" @click="updateRoleMenus()" :loading="loadingSubmit">提交
        </el-button>
      </div>
    </el-dialog>

    <el-form :inline="true">
      <el-form-item>
        <el-select multiple filterable allow-create size="mini" v-model="params.sort"
                   placeholder="排序字段">
          <el-option label="主键" value="id"></el-option>
          <el-option label="角色代码( 站点+角色代码 唯一)" value="code"></el-option>
          <el-option label="角色名" value="name"></el-option>
          <el-option label="角色备注" value="remark"></el-option>
          <el-option label="是否禁用（0-启用,1-禁用）" value="disabled"></el-option>
          <el-option label="站点ID" value="stationId"></el-option>
          <el-option label="创建时间" value="createTime"></el-option>
          <el-option label="更新时间" value="updateTime"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select size="mini" v-model="params.asc" placeholder="排序方式" style="width: 80px">
          <el-option label="正序" :value="true"/>
          <el-option label="逆序" :value="false"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="params.text" prefix-icon="el-icon-search" size="mini"
                  @keyup.enter.native="queryPage()" placeholder="请输入查询内容"/>
      </el-form-item>
      <el-form-item>
        <el-button size="mini" type="success" @click="queryPage()" icon="el-icon-search">查询</el-button>
        <el-button size="mini" type="primary" @click="showAddDialog({})" icon="el-icon-plus" v-if="havePermission('system:role:add')">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 分割线 -->

    <!--表格-->
    <el-table v-loading="loadingTable" :data="records" highlight-current-row element-loading-text="加载中..." border fit>
      <!--<el-table-column align="center" label="主键" prop="id"></el-table-column>-->
      <el-table-column align="center" label="角色代码" prop="code"></el-table-column>
      <el-table-column align="center" label="站点ID" prop="stationId"></el-table-column>
      <el-table-column align="center" label="角色名" prop="name"></el-table-column>
      <el-table-column align="center" label="角色备注" prop="remark"></el-table-column>
      <el-table-column align="center" label="禁用" prop="disabled" width="50">
        <template slot-scope="scope">
          <el-switch :value="scope.row.disabled===1" disabled
                     active-color="#ff4949"
                     inactive-color="#13ce66"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime"></el-table-column>
      <el-table-column align="center" label="更新时间" prop="updateTime"></el-table-column>
      <el-table-column align="center" label="操作" min-width="120" max-width="120" fixed="right" v-if="haveAnyPermission(['system:role:mod','system:role:del'])">
        <template slot-scope="scope">
          <el-button @click="showEditDialog(scope.row.id)" size="mini" type="success"
                     icon="el-icon-edit"
                     :loading="loadingEdit"  v-if="havePermission('system:role:mod')" />
          <el-button @click="showMenuDialog(scope.row.id)" size="mini" type="warning"
                     icon="el-icon-user-solid"
                     :loading="loadingEdit" v-if="havePermission('system:role:grant')"/>
          <el-button @click="removeOne(scope.row.id)" size="mini" type="danger"
                     icon="el-icon-delete"
                     v-if="havePermission('system:role:del') && scope.row.id!=='1'"/>

        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination style="padding-top: 10px"
       :current-page="params.current"
       :page-size="params.size"
       @current-change="queryPage"
       @size-change="params.size=$event;queryPage()"
       :page-sizes="[2, 6, 10, 20, 30, 50]"
       layout="total, sizes, prev, pager, next, jumper"
       :total="total" background/><!--small-->

  </div>
</template>

<script>
import elDragDialog from '@/directive/el-drag-dialog'
import request from '@/api/system/role'
import common from '@/mixins/common'
import commonApi from '@/api/common'

export default {
  name: 'Role',
  mixins: [common],
  directives: { elDragDialog },
  data () {
    return {
      request: request,
      rule: {
        id: [{ required: true, message: '主键必须填写', trigger: 'blur' }],
        code: [{ required: true, message: '角色代码( 站点+角色代码 唯一)必须填写', trigger: 'blur' }],
        name: [{ required: true, message: '角色名必须填写', trigger: 'blur' }],
        remark: [{ required: true, message: '角色备注必须填写', trigger: 'blur' }],
        disabled: [{ required: true, message: '是否禁用（0-启用,1-禁用）必须填写', trigger: 'blur' }],
        stationId: [{ required: true, message: '站点ID必须填写', trigger: 'blur' }]
      },
      stations: [],
      // 所有菜单的权限树
      menuTree: [],
      menuDialog: false,
      // 角色所拥有的菜单
      roleMenus: [],
      currentRoleId: ''
    }
  },
  methods: {
    loadStations () {
      commonApi.listAllStation().then(res => {
        this.stations = res.data
      })
    },
    showMenuDialog (id) {
      request.getRoleMenus(id).then(res => {
        this.roleMenus = res.data
        this.menuDialog = true
        this.currentRoleId = id
        this.$nextTick(() => {
          // 重新设置
          this.$refs.menu.setCheckedKeys(this.roleMenus)
        })
      })
    },
    loadTree () {
      request.getMenuTree().then(res => {
        this.menuTree = res.data
      })
    },
    updateRoleMenus () {
      this.loadingSubmit = true
      this.$nextTick(() => {
        const keys = this.$refs.menu.getCheckedKeys()
        const halfKeys = this.$refs.menu.getHalfCheckedKeys()
        const menuIds = halfKeys.concat(keys)
        console.log('本次提交的权限列表=>' + JSON.stringify(menuIds))
        request.updateRoleMenus({ roleId: this.currentRoleId, menuIds }).then(res => {
          if (res.success) {
            this.menuDialog = false
            this.$message.success(res.message)
          }
        })
          .catch(err => console.error(err))
          .finally(() => { this.loadingSubmit = false })
      })
    }
  },
  created () {
    // 会先调用common#queryPage
    this.loadStations()
    this.loadTree()
  }
}
</script>

<style>

</style>
