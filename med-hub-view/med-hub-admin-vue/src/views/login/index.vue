<template>
  <div class="login">
    <div class="login-box">
      <img src="@/assets/login/login-l.png" alt="">
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <div class="login-form-title">
            <img
              src="@/assets/login/icon_logo.png"
              style="width: 149px; height: 38px"
              alt=""
            >
            <!-- <span class="title-label">智慧购药系统</span> -->
          </div>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
              prefix-icon="iconfont icon-user"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="iconfont icon-lock"
              @keyup.enter.native="handleLogin"
            />
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button
              :loading="loading"
              class="login-btn"
              size="medium"
              type="primary"
              style="width: 100%"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import { Form as ElForm, Input } from 'element-ui'
import { UserModule } from '@/store/modules/user'
import { isValidUsername } from '@/utils/validate'

@Component({
  name: 'Login',
})
export default class extends Vue {
  private validateUsername = (rule: any, value: string, callback: Function) => {
    if (!value) {
      callback(new Error('请输入用户名'))
    } else {
      callback()
    }
  }
  private validatePassword = (rule: any, value: string, callback: Function) => {
    if (value.length < 6) {
      callback(new Error('密码必须在6位以上'))
    } else {
      callback()
    }
  }
  private loginForm = {
    username: 'admin',
    password: '123456',
  } as {
    username: String
    password: String
  }

  loginRules = {
    username: [{ validator: this.validateUsername, trigger: 'blur' }],
    password: [{ validator: this.validatePassword, trigger: 'blur' }],
  }
  private loading = false
  private redirect?: string

  @Watch('$route', { immediate: true })
  private onRouteChange(route: any) {}

  // 登录
  private handleLogin() {
    (this.$refs.loginForm as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true
        await UserModule.Login(this.loginForm as any)
          .then((res: any) => {
            if (String(res.code) === '1') {
              this.$router.push('/')
            } else {
              // this.$message.error(res.msg)
              this.loading = false
            }
          })
          .catch(() => {
            // this.$message.error('用户名或密码错误！')
            this.loading = false
          })
      } else {
        return false
      }
    })
  }
}
</script>

<style lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: url('~@/assets/login/login-background.png') no-repeat center center;
  background-size: cover;
  min-height: 100vh;
}

.login-box {
  width: 1000px;
  height: 474.38px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 122, 255, 0.12);
  display: flex;
  overflow: hidden;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  img {
    width: 60%;
    height: auto;
    object-fit: cover;
  }
}

.login-form {
  background: #FFFFFF;
  width: 40%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 30px;
  .el-form {
    width: 100%;
    max-width: 280px;
  }
  .el-form-item {
    margin-bottom: 24px;
  }
  .el-form-item.is-error .el-input__inner {
    border: 1px solid #FF3B30 !important;
    background: rgba(255, 59, 48, 0.02) !important;
  }
  .el-input__inner {
    border: 1px solid #E5E5EA;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 400;
    color: #1D1D1F;
    height: 44px;
    line-height: 44px;
    padding: 0 16px 0 40px;
    background: #FAFAFC;
    transition: all 0.24s cubic-bezier(0.25, 0.1, 0.25, 1);
    &::placeholder {
      color: #8E8E93;
    }
    &:focus {
      border-color: #007AFF;
      background: #FFFFFF;
      box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.1);
    }
    &:hover {
      border-color: #007AFF;
    }
  }
  .el-input__prefix {
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    .iconfont {
      color: #8E8E93;
      font-size: 16px;
    }
  }
  .el-input--prefix .el-input__inner {
    padding-left: 40px;
  }
  .el-form-item--medium .el-form-item__content {
    line-height: 44px;
  }
  .el-input--medium .el-input__icon {
    line-height: 44px;
  }
}

.login-btn {
  border-radius: 12px;
  padding: 12px 24px !important;
  margin-top: 16px;
  font-weight: 500;
  font-size: 14px;
  border: 0;
  color: #FFFFFF;
  background: #007AFF;
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
  transition: all 0.24s cubic-bezier(0.25, 0.1, 0.25, 1);
  &:hover,
  &:focus {
    background: #0A84FF;
    box-shadow: 0 6px 16px rgba(10, 132, 255, 0.4);
    transform: translateY(-1px);
  }
  &:active {
    background: #006FE6;
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.2);
    transform: translateY(0);
  }
}

.login-form-title {
  height: 38px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 32px;
  .title-label {
    font-weight: 600;
    font-size: 20px;
    color: #1D1D1F;
    margin-left: 10px;
  }
}

// 响应式适配
@media screen and (max-width: 1200px) {
  .login-box {
    width: 900px;
    height: 450px;
  }
}

@media screen and (max-width: 992px) {
  .login-box {
    width: 100%;
    height: 100vh;
    border-radius: 0;
    flex-direction: column;
    img {
      width: 100%;
      height: 200px;
      object-fit: cover;
    }
    .login-form {
      width: 100%;
      padding: 30px 20px;
    }
  }
}
</style>
