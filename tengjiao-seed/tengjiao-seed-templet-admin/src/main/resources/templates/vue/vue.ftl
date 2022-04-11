<template>
    <div class="app-container">
        <!-- 对话框 新增/编辑 -->
        <el-dialog :title="isAdd?'新增':'编辑'" @closed="reset($refs.dialogForm)" :visible.sync="showDialog" :close-on-click-modal="false" width="600px">
            <el-form status-icon  :model="dialogForm" :inline="true" ref="dialogForm" :rules="rule" style="font-weight: bold;" size="small" label-position="right" label-width="80px" >
                <#list table.fields as field>
                    <#if field.propertyName == "createTime" || field.propertyName == "updateTime"><#-- 创建时间和更新时间 略过  -->
                    <#elseif field.keyFlag && field.keyIdentityFlag><#-- 主键自增略过  -->
                    <#else>
                        <el-form-item prop="${field.propertyName}" label="${field.comment}">
                            <el-input v-model="dialogForm.${field.propertyName}" size="mini" placeholder="${field.comment}"/>
                        </el-form-item>
                    </#if>
                </#list>
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
                    <#list table.fields as field>
                        <el-option label="${field.comment}" value="${field.propertyName}"></el-option>
                    </#list>
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
                <el-button size="mini" type="primary" @click="showAddDialog({})" icon="el-icon-plus" v-if="havePermission('${entity?uncap_first}:add')">新增</el-button>
            </el-form-item>
        </el-form>

        <!-- 分割线 -->

        <!-- 内容区域 -->
        <el-table v-loading="loadingTable" :data="records" highlight-current-row element-loading-text="加载中..." border fit>
            <#list table.fields as field>
                <el-table-column align="center" label="${field.comment}" prop="${field.propertyName}"></el-table-column>
            </#list>
            <el-table-column align="center" label="操作"  min-width="120" fixed="right"  v-if="haveAnyPermission(['${entity?uncap_first}:mod','${entity?uncap_first}:del'])">
                <template slot-scope="scope">
                    <el-button @click="showEditDialog(scope.row.id)" size="mini" type="success" icon="el-icon-edit" :loading="loadingEdit"  v-if="havePermission('${entity?uncap_first}:mod')" />
                    <el-button @click="removeOne(scope.row.id)" size="mini" type="danger" icon="el-icon-delete" v-if="havePermission('${entity?uncap_first}:del')"/>
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
    import request from '../../api/${entity?lower_case}'
    import common from "../../mixins/common";

    export default {
        name: "admin",
        mixins: [common],
        data() {
            return {
                rule:{
                    <#list table.fields as field>
                    <#if field.propertyName == "createTime" || field.propertyName == "updateTime"><#-- 创建时间和更新时间 略过  -->
                    <#elseif field.keyFlag && field.keyIdentityFlag><#-- 主键自增略过  -->
                    <#else>
                    ${field.propertyName}:[{required:true,message:"${field.comment}必须填写",trigger:'blur'}],
                    </#if>
                    </#list>
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
