<template>
    <div class="app-container">
        <!-- 对话框 新增/编辑 -->
        <el-dialog :title="isAdd?'新增':'编辑'" @closed="reset($refs.dialogForm)" :visible.sync="showDialog" :close-on-click-modal="false" width="600px"
                   v-el-drag-dialog>
            <el-form status-icon  :model="dialogForm" :inline="true" ref="dialogForm" :rules="rule" style="font-weight: bold;" size="small" label-position="right" label-width="80px" >
                    <el-form-item prop="beanName" label="bean名称">
                        <el-input v-model="dialogForm.beanName" size="mini" placeholder="bean名称"/>
                    </el-form-item>
                    <el-form-item prop="methodName" label="方法名称">
                        <el-input v-model="dialogForm.methodName" size="mini" placeholder="方法名称"/>
                    </el-form-item>
                    <el-form-item prop="methodParams" label="方法参数">
                        <el-input v-model="dialogForm.methodParams" size="mini" placeholder="方法参数"/>
                    </el-form-item>
                    <el-form-item prop="cronExpression" label="cron表达式">
                        <el-input v-model="dialogForm.cronExpression" size="mini" placeholder="cron表达式"/>
                    </el-form-item>
                    <el-form-item prop="status" label="状态">
                        <el-input v-model="dialogForm.status" size="mini" placeholder="状态（1正常0暂停）"/>
                    </el-form-item>
                    <el-form-item prop="remark" label="备注">
                        <el-input v-model="dialogForm.remark" size="mini" placeholder="备注"/>
                    </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="showDialog = false">取 消</el-button>
                <el-button size="mini" type="primary" @click="submitForm()" :loading="loadingSubmit">{{isAdd?'新增':'更新'}}</el-button>
            </div>
        </el-dialog>

        <!-- 搜索区域 -->
        <el-form :inline="true">
            <el-form-item>
                <el-select multiple filterable allow-create size="mini" v-model="params.sort"
                           placeholder="排序字段">
                    <el-option  label="任务ID" value="id"></el-option>
                    <el-option  label="bean名称" value="beanName"></el-option>
                    <el-option  label="方法名称" value="methodName"></el-option>
                    <el-option  label="方法参数" value="methodParams"></el-option>
                    <el-option  label="cron表达式" value="cronExpression"></el-option>
                    <el-option  label="状态（1正常0暂停）" value="status"></el-option>
                    <el-option  label="备注" value="remark"></el-option>
                    <el-option  label="创建时间" value="createTime"></el-option>
                    <el-option  label="更新时间" value="updateTime"></el-option>
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
                <el-button size="mini" type="primary" @click="showAddDialog({})" icon="el-icon-plus" v-if="havePermission('system:job:add')">新增</el-button>
            </el-form-item>
        </el-form>

        <!-- 分割线 -->

        <!-- 内容区域 -->
        <el-table v-loading="loadingTable" :data="records" highlight-current-row element-loading-text="加载中..." border fit>
                <el-table-column align="center" label="任务ID" prop="id"></el-table-column>
                <el-table-column align="center" label="bean名称" prop="beanName"></el-table-column>
                <el-table-column align="center" label="方法名称" prop="methodName"></el-table-column>
                <el-table-column align="center" label="方法参数" prop="methodParams"></el-table-column>
                <el-table-column align="center" label="cron表达式" prop="cronExpression"></el-table-column>
                <el-table-column align="center" label="状态" prop="status"></el-table-column>
                <el-table-column align="center" label="备注" prop="remark"></el-table-column>
                <el-table-column align="center" label="创建时间" prop="createTime"></el-table-column>
                <el-table-column align="center" label="更新时间" prop="updateTime"></el-table-column>
            <el-table-column align="center" label="操作"  min-width="120" fixed="right"  v-if="haveAnyPermission(['system:job:mod','system:job:del'])">
                <template slot-scope="scope">
                    <el-button @click="showEditDialog(scope.row.id)" size="mini" type="success" icon="el-icon-edit" :loading="loadingEdit"  v-if="havePermission('system:job:mod')" />
                    <el-button @click="removeOne(scope.row.id)" size="mini" type="danger" icon="el-icon-delete" v-if="havePermission('system:job:del')"/>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination style="padding-top: 10px"
           :current-page="params.current" :page-size="params.size"
           @size-change="params.size=$event;queryPage()" @current-change="queryPage"
           :page-sizes="[2, 6, 10, 20, 30, 50]"
           layout="total, sizes, prev, pager, next, jumper"
           :total="total" background/>
    </div>
</template>

<script>
  import elDragDialog from '@/directive/el-drag-dialog'
  import request from '@/api/system/job'
  import common from "@/mixins/common"

  export default {
    name: "Job",
    mixins: [common],
    directives: { elDragDialog },
    data() {
        return {
            rule:{
                beanName:[{required:true,message:"bean名称必须填写",trigger:'blur'}],
                methodName:[{required:true,message:"方法名称必须填写",trigger:'blur'}],
                cronExpression:[{required:true,message:"cron表达式必须填写",trigger:'blur'}],
                status:[{required:true,message:"状态（1正常0暂停）必须填写",trigger:'blur'}],
            },
            request: request,
        }
    },
    methods: {},
    created() {
    }
  }
</script>

<style>

</style>
