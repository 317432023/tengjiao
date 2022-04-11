<template>
  <!-- 登录容器 -->
  <div class="login-container">
    <!-- 登录区域 -->
    <div class="login-box">

      <!-- 头像 -->
      <!--
      <div class="avatar-box">

        <img src="https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif" class="avatar-img"/>

      </div>
       -->
      <h3 class="login-title">{{title?title:'系统登录'}}</h3>

      <!-- 登录表单 -->
      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" label-width="0px" class="login-form" @keyup.enter.native="submitForm('loginForm')">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" :show-password="false"/>
        </el-form-item>
        <el-form-item prop="captcha" class="captcha-box">
          <el-input v-model="loginForm.captcha" placeholder="图像码" class="captcha" prefix-icon="el-icon-mobile"/>
          <div class="code-image">
            <!-- <img src="../../assets/img/captcha.png" alt="" class="captcha-img"> -->
            <img :src="captchaData" alt="验证码" class="img" @click="refreshCaptcha()">
            <!-- <el-image fit="fill" :src="captchaUri" @click="refreshCaptcha()"/> -->
          </div>
        </el-form-item>
        <el-checkbox size="normal" class="login-remember" v-model="rememberMe">记住我</el-checkbox>
        <el-form-item class="login-button">
          <el-button type="primary" size="normal" @click="submitForm('loginForm')" :loading="loading">
              <span v-if="!loading">登 录</span>
              <span v-else>正 在 登 录...</span>
          </el-button>
          <el-button @click="resetForm('loginForm')" v-show="!loading">重置</el-button>
        </el-form-item>

      </el-form>
    </div>
    <!-- 原子背景 -->
    <vue-particles
      class="bg"
      color="#dedede"
      :particleOpacity="0.7"
      :particlesNumber="80"
      shapeType="star"
      :particleSize="4"
      linesColor="#888888"
      :linesWidth="2"
      :lineLinked="true"
      :lineOpacity="0.4"
      :linesDistance="150"
      :moveSpeed="3"
      :hoverEffect="true"
      hoverMode="grab"
      :clickEffect="true"
      clickMode="push"/>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import encrypt from '@/utils/encrypt'
import { title } from '@/settings'

export default {
  name: 'Login',
  data () {
    return {
      title: title, // 从 settings.js 导入而得
      loading: false,
      loginForm: {
        username: '',
        password: '',
        captcha: ''
      },
      rememberMe: true,
      captchaUrl: process.env.VUE_APP_BASE_API + '/captcha?',
      captchaData: '',

      date: Date.now(),
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        captcha: [
          { required: true, message: '请输入图像验证码', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 提交表单
    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (!this.captchaData) {
            this.$message.info('请点击获取验证码')
            return false
          }
          this.login(formName)
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    login (formName) {
      // 提交表单 alert('submit!');
      this.loading = true
      const user = { ...this[formName] }
      if (user.password.length < 50) {
        user.password = encrypt(user.password)
        console.log('VUE_APP_SECRET=' + process.env.VUE_APP_SECRET)
        console.log('encrypt(password)=' + user.password)
      }
      this.$store.dispatch('security/login', user).then(res => {
        this.loading = false
        if (res.success) {
          // 记住账号密码
          Cookies.set('admin', JSON.stringify({
            rememberMe: this.rememberMe,
            username: this.rememberMe ? user.username : '',
            password: this.rememberMe ? this[formName].password : ''
          }))

          const path = this.$route.query.redirect
          // this.$router.replace(path || '/dashboard')
          this.$router.push(path || '/dashboard')
          this.$message.success(res.message)
        } else {
          this.loginForm.captcha = ''
          this.$message.error(res.message)
        }
      }).catch(e => {
        console.error(e.message) // for debug
        this.captchaData = ''
        this.loginForm.captcha = ''
        this.loading = false
      })
    },
    // 重置表单
    resetForm (formName) {
      this.$refs[formName].resetFields()
    },
    // 从cookie中加载用户回显表单
    loadCookiesUser () {
      let userInfo = Cookies.get('admin')
      if (userInfo) {
        userInfo = JSON.parse(userInfo)
        this.$set(this.loginForm, 'username', userInfo.username)
        this.$set(this.loginForm, 'password', userInfo.password)
        this.rememberMe = userInfo.rememberMe
      }
    },
    // 刷新验证码
    async refreshCaptcha () {
      this.date = Date.now()

      let content
      const [res, e] = await Promise.all([
        this.$store.dispatch('security/captcha', { width: 160, height: 40 })
      ]).then(res => res).catch(e => e)
      /* await this.$store.dispatch('security/captcha', { width: 200, height: 80, mode: 2 }).then(res => {
        if (res.success) {
          const { data } = res
          content = data.content
        } else {
          console.log(res.message)
        }

      }).catch(e => {
        console.log(e.message) // for debug
      }) */

      if (e) {
        console.error(e.message) // for debug
      } else if (res.success) {
        const { data } = res
        content = data.content
      } else {
        console.log(res.message)
      }

      this.captchaData = content
      this.loginForm.captcha = ''
    }
  },
  computed: {
    captchaUri () {
      return this.captchaUrl + this.date
    }

  },
  created () {
    this.loadCookiesUser()
  }

}
</script>

<style lang="less" scoped>
  .login-container {
    height: 100%;
    background-color: #000; /* #0ff */

    .login-box {
      z-index: 1;

      width: 320px;
      height: 350px;
      position: absolute;
      /*top: 50%;
      left: 50%;
      margin-left: -160px;
      margin-top: -175px;*/
      left: calc((100% - 320px) / 2);
      top: calc((100% - 350px) / 2);

      background-color: #fff;
      border-radius: 15px;
      background-clip: padding-box;
      box-sizing: border-box;
      border: 1px solid #eaeaea;
      box-shadow: 0 0 25px #cac6c6;

      /* 头像 */
      /*
      .avatar-box{
        width: 130px;
        height: 130px;
        border: 1px solid #eee;
        border-radius: 50%;
        margin: -71px auto;
        background-color: #fff;
        padding: 5px;

        .avatar-img{
          width: 100%;
          height: 100%;
          border-radius: 50%;
          background-color: #eee;
        }
      }*/

      .login-title {
        margin: 15px auto 20px auto;
        text-align: center;
        color: #505458;
      }

      .login-form {
        position: absolute;
        bottom: 0;
        width: 100%;
        padding: 0 20px;
        box-sizing: border-box;

        .captcha-box {
          display: flex;
          .captcha {
            width: 40%;
            justify-content: left;
            padding-right: 5px;
          }
          .code-image {
            width: 60%;
            min-width: 160px;
            display: inline-block;
            height: 40px;
            text-align: center;

            .img {
              height: 40px;
              cursor: pointer;
              vertical-align: middle;
            }
          }

        }

        .login-remember {
          text-align: left;
          margin: 0 0 15px 0;
          float: left;
        }

        .login-button {
          display: flex;
          justify-content: flex-end;
        }

      }
    }
    .bg {
      z-index: 0;
      height: 100%;
      width: 100%;
      position: absolute;
      top: 0;
      background-size: cover;
      background-color: #2c3e50;
      /* 背景图垂直、水平均居中 */
      background-position: center center;
      /* 当内容高度大于图片高度时，背景图像的位置相对于viewport固定 */
      background-attachment: fixed;
    }
  }
</style>
