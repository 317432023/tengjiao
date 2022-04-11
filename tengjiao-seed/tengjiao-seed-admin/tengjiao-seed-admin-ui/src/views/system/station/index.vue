<template>
    <div class="app-container">

        <el-dialog :title="isAdd?'新增':'编辑'" @closed="reset($refs.dialogForm)" :visible.sync="showDialog" :close-on-click-modal="false" width="600px"
                   v-el-drag-dialog>
            <el-form status-icon  :model="dialogForm" :inline="true" ref="dialogForm" :rules="rule" style="font-weight: bold;" size="small" label-position="right" label-width="80px" >

                    <el-form-item prop="name" label="分站名称">
                        <el-input v-model="dialogForm.name" size="mini" placeholder="分站名称"/>
                    </el-form-item>
                    <el-form-item prop="domain" label="分站域名">
                        <el-input v-model="dialogForm.domain" size="mini" placeholder="分站域名"/>
                    </el-form-item>
                    <el-form-item prop="kfQq" label="客服QQ">
                        <el-input v-model="dialogForm.kfQq" size="mini" placeholder="客服QQ"/>
                    </el-form-item>

                    <!--
                    <el-form-item prop="regionId" label="区域码">
                        <el-input v-model="dialogForm.regionId" size="mini" placeholder="区域码"/>
                    </el-form-item>
                    <el-form-item prop="regionName" label="地名">
                        <el-input v-model="dialogForm.regionName" size="mini" placeholder="地名"/>
                    </el-form-item>
                      -->

                    <el-form-item label="禁用" prop="disabled">
                      <el-radio-group size="mini" v-model="dialogForm.disabled" fill="#009688" >
                        <el-radio-button size="mini" :label="1">是</el-radio-button>
                        <el-radio-button size="mini" :label="0">否</el-radio-button>
                      </el-radio-group>
                    </el-form-item>

                    <el-form-item label="过期时间" prop="expireTime">
                      <el-date-picker style="width: 192px"
                                      size="mini"
                                      v-model="dialogForm.expireTime"
                                      type="date" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd HH:mm:ss" @change="dialogForm.expireTime=$event"
                                      placeholder="选择日期"
                                      clearable>
                      </el-date-picker>
                    </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="showDialog = false">取 消</el-button>
                <el-button size="mini" type="primary" @click="submitForm()" :loading="loadingSubmit">{{isAdd?'新增':'更新'}}</el-button>
            </div>

        </el-dialog>

        <!-- 头部: header -->
        <el-form :inline="true">
            <el-form-item>
                <el-select multiple filterable allow-create size="mini" v-model="params.sort"
                           placeholder="排序字段">
                    <el-option label="" value="id"></el-option>
                    <el-option label="分站名称" value="name"></el-option>
                    <el-option label="分站域名" value="domain"></el-option>
                    <el-option label="客服QQ" value="kfQq"></el-option>
                    <el-option label="区域码" value="regionId"></el-option>
                    <el-option label="地名" value="regionName"></el-option>
                    <el-option label="是否禁用" value="disabled"></el-option>
                    <el-option label="过期时间" value="expireTime"></el-option>
                    <el-option label="创建时间" value="createTime"></el-option>
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
                <el-button size="mini" type="primary" @click="showAddDialog({})" icon="el-icon-plus" v-if="havePermission('system:station:add')">新增</el-button>
            </el-form-item>
        </el-form>

        <!-- 中间: center -->
        <!--表格-->
        <el-table v-loading="loadingTable" :data="records" highlight-current-row element-loading-text="加载中..." border fit>
            <!--<el-table-column align="center" label="主键" prop="id"></el-table-column>-->
            <el-table-column align="center" label="站点名称" prop="name"></el-table-column>
            <el-table-column align="center" label="站点域名" prop="domain"></el-table-column>
            <el-table-column align="center" label="客服QQ" prop="kfQq"></el-table-column>
            <el-table-column align="center" label="区域码" prop="regionId"></el-table-column>
            <el-table-column align="center" label="地名" prop="regionName"></el-table-column>
            <el-table-column align="center" label="禁用" prop="disabled" width="50">
              <template slot-scope="scope">
                <el-switch :value="scope.row.disabled===1" disabled
                           active-color="#ff4949"
                           inactive-color="#13ce66"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="过期时间" prop="expireTime"></el-table-column>
            <el-table-column align="center" label="创建时间" prop="createTime"></el-table-column>
            <el-table-column align="center" label="操作"  min-width="120" fixed="right"  v-if="haveAnyPermission(['system:station:mod','system:station:del'])">
                <template slot-scope="scope">
                    <el-button @click="showEditDialog(scope.row.id)" size="mini" type="success" icon="el-icon-edit" :loading="loadingEdit"  v-if="havePermission('system:station:mod')" />
                    <el-button @click="removeOne(scope.row.id)" size="mini" type="danger" icon="el-icon-delete" v-if="havePermission('system:station:del')"/>
                </template>
            </el-table-column>
        </el-table>

        <!-- 底部: footer -->
        <el-pagination style="padding-top: 10px"
          :current-page="params.current"
          :page-size="params.size"
          @current-change="queryPage"
          layout="total,sizes,prev, pager, next, jumper"
          :page-sizes="[2, 6, 10, 20, 30, 50]" @size-change="params.size=$event;queryPage()"
          :total="total" background><!-- layout="total, prev, pager, next" small  -->
        </el-pagination>

    </div>
</template>

<script>
import elDragDialog from '@/directive/el-drag-dialog'
import request from '@/api/system/station'
import common from '@/mixins/common'

export default {
  name: 'admin',
  mixins: [common],
  directives: { elDragDialog },
  data () {
    return {
      request: request,
      rule: {
        id: [{ required: true, message: '必须填写', trigger: 'blur' }],
        name: [{ required: true, message: '分站名称必须填写', trigger: 'blur' }],
        domain: [{ required: true, message: '分站域名必须填写', trigger: 'blur' }],
        kfQq: [{ required: false, message: '客服QQ必须填写', trigger: 'blur' }],
        /* regionId:[{required:true,message:"区域码必须填写",trigger:'blur'}],
                    regionName:[{required:true,message:"地名必须填写",trigger:'blur'}], */
        disabled: [{ required: true, message: '是否禁用必须选择', trigger: 'blur' }],
        expireTime: [{ required: true, message: '过期时间必须填写', trigger: 'blur' }]
      }
    }
  },
  methods: {},
  created () {
  }
}
</script>

<style>

</style>
