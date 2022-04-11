<template>

  <el-form :model="dialogForm" :inline="false" ref="dialogForm" :rules="rule">
    <el-form-item label="输入旧密码" prop="password">
      <el-input v-model.trim="dialogForm.password" type="password" :show-password="true"/>
    </el-form-item>
    <el-form-item label="输入新密码" prop="newPassword">
      <el-input v-model.trim="dialogForm.newPassword" type="password" :show-password="true"/>
    </el-form-item>
    <el-form-item label="重输新密码" prop="reNewPassword">
      <el-input v-model.trim="dialogForm.reNewPassword" type="password" :show-password="true"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitForm" :loading="loadingSubmit">更新</el-button>
    </el-form-item>
  </el-form>

</template>

<script>
import request from '@/api/profile'
export default {
  /* props: {
      request: {
        type: Object
      },
    }, */
  data () {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.dialogForm.reNewPassword !== '') {
          this.$refs.dialogForm.validateField('reNewPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.dialogForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      request: request,

      rule: {
        password: [{ required: true, message: '密码必须填写', trigger: 'blur' }],
        newPassword: [{ required: true, message: '新密码必须填写', trigger: 'blur' }, {
          min: 6,
          max: 16,
          message: '长度在 6 到 16 个字符',
          trigger: 'blur'
        },
        { validator: validatePass, trigger: 'blur' }],
        reNewPassword: [{ required: true, message: '重复新密码必须填写', trigger: 'blur' }, {
          min: 6,
          max: 16,
          message: '长度在 6 到 16 个字符',
          trigger: 'blur'
        },
        { validator: validatePass2, trigger: 'blur' }]
      },

      // 增加编辑的内容体
      dialogForm: {},

      // 提交表单按钮Loading
      loadingSubmit: false
    }
  },
  methods: {

    submitForm () {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          console.log('修改密码:' + JSON.stringify(this.dialogForm))

          this.loadingSubmit = true

          this.request.modifyPassword(this.dialogForm).then(res => {
            this.loadingSubmit = false
            if (res.success) {
              // this.showDialog = false
              // this.$notify.success(res.message ? res.message : 'success')

              this.dialogForm.password = ''
              this.dialogForm.newPassword = ''
              this.dialogForm.reNewPassword = ''

              this.$message({
                message: 'User password has been updated successfully',
                type: 'success',
                duration: 5 * 1000
              })
            }
          })
        }
      })
    }

  }

}
</script>
