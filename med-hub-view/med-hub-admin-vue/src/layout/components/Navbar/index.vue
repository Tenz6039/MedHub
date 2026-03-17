<template>
  <div class="navbar-wrapper">
    <div class="navbar">
      <div class="statusBox">
        <hamburger id="hamburger-container"
                   :is-active="sidebar.opened"
                   class="hamburger-container"
                   @toggleClick="toggleSideBar"
        />
        <span v-if="status===1"
              class="businessBtn"
        >营业中</span>
        <span v-else
              class="businessBtn closing"
        >打烊中</span>
      </div>

      <div :key="restKey"
           class="right-menu"
      >
        <div class="rightStatus">
          <audio ref="audioVo"
                 hidden
          >
            <source src="./../../../assets/preview.mp3" type="audio/mp3">
          </audio>
          <audio ref="audioVo2"
                 hidden
          >
            <source src="./../../../assets/reminder.mp3" type="audio/mp3">
          </audio>
          <span class="navicon operatingState" @click="handleStatus"><i />营业状态设置</span>
        </div>
        <div class="avatar-wrapper">
          <div class="user-dropdown"
               @mouseenter="toggleShow"
               @mouseleave="mouseLeaves"
          >
            <el-button type="primary"
                       :class="shopShow?'active':''"
            >
              {{ name }}<i class="el-icon-arrow-down" />
            </el-button>
          </div>
          <div v-show="shopShow"
               class="userInfo"
               @mouseenter="toggleShow"
               @mouseleave="mouseLeaves"
          >
            <div class="userList">
              <p class="amendPwdIcon"
                 @click="handlePwd"
              >
                修改密码<i />
              </p>
              <p class="outLogin"
                 @click="logout"
              >
                退出登录<i />
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 营业状态弹层 -->
    <el-dialog title="营业状态设置"
               :visible.sync="dialogVisible"
               width="25%"
               :show-close="false"
               :modal="true"
               :append-to-body="true"
               custom-class="business-status-dialog"
    >
      <el-radio-group v-model="setStatus">
        <el-radio :label="1">
          营业中
          <span>当前药房处于营业状态，自动接收任何订单，可点击打烊进入药房打烊状态。</span>
        </el-radio>
        <el-radio :label="0">
          打烊中
          <span>当前药房处于打烊状态，仅接受营业时间内的预定订单，可点击营业中手动恢复营业状态。</span>
        </el-radio>
      </el-radio-group>
      <span slot="footer"
            class="dialog-footer"
      >
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary"
                   @click="handleSave"
        >确 定</el-button>
      </span>
    </el-dialog>
    <!-- end -->
    <!-- 修改密码 -->
    <Password :dialog-form-visible="dialogFormVisible"
              @handleclose="handlePwdClose"
    />
    <!-- end -->
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import { AppModule } from '@/store/modules/app'
import { UserModule } from '@/store/modules/user'
import Breadcrumb from '@/components/Breadcrumb/index.vue'
import Hamburger from '@/components/Hamburger/index.vue'
import { getStatus, setStatus } from '@/api/users'
import Cookies from 'js-cookie'
import { debounce, throttle } from '@/utils/common'
import { setNewData, getNewData } from '@/utils/cookies'

// 接口
import { getCountUnread } from '@/api/inform'
// 修改密码弹层
import Password from '../components/password.vue'

@Component({
  name: 'Navbar',
  components: {
    Breadcrumb,
    Hamburger,
    Password,
  },
})
export default class extends Vue {
  private storeId = this.getStoreId
  private restKey: number = 0
  private websocket = null
  private newOrder = ''
  private message = ''
  private audioIsPlaying = false
  private audioPaused = false
  private statusValue = true
  private audioUrl: './../../../assets/preview.mp3'
  private shopShow = false
  private closeTimer: any = null
  private dialogVisible = false
  private status = 1
  private setStatus = 1
  private dialogFormVisible = false
  private ountUnread = 0
  // get ountUnread() {
  //   return Number(getNewData())
  // }
  get sidebar() {
    return AppModule.sidebar
  }

  get device() {
    return AppModule.device.toString()
  }

  getuserInfo() {
    return UserModule.userInfo
  }

  get name() {
    return (UserModule.userInfo as any).name
      ? (UserModule.userInfo as any).name
      : JSON.parse(Cookies.get('user_info') as any).name
  }

  get getStoreId() {
    let storeId = ''
    if (UserModule.storeId) {
      storeId = UserModule.storeId
    } else if ((UserModule.userInfo as any).stores != null) {
      storeId = (UserModule.userInfo as any).stores[0].storeId
    }
    return storeId
  }
  mounted() {
    document.addEventListener('click', this.handleClose)
    //console.log(this.$store.state.app.statusNumber)
    // const msg = {
    //   data: {
    //     type: 2,
    //     content: '订单1653904906519客户催单，已下单23分钟，仍未接单。',
    //     details: '434'
    //   }
    // }
    this.getStatus()
  }
  created() {
    this.webSocket()
  }
  onload() {
  }
  destroyed() {
    this.websocket.close() //离开路由之后断开websocket连接
  }

  // 添加新订单提示弹窗
  webSocket() {
    const that = this as any
    let clientId = Math.random().toString(36).substr(2)
    let socketUrl = process.env.VUE_APP_SOCKET_URL + clientId
    console.log(socketUrl, 'socketUrl')
    if (typeof WebSocket == 'undefined') {
      that.$notify({
        title: '提示',
        message: '当前浏览器无法接收实时报警信息，请使用谷歌浏览器！',
        type: 'warning',
        duration: 0,
      })
    } else {
      this.websocket = new WebSocket(socketUrl)
      // 监听socket打开
      this.websocket.onopen = function () {
        console.log('浏览器WebSocket已打开')
      }
      // 监听socket消息接收
      this.websocket.onmessage = function (msg) {
        // 转换为json对象
        that.$refs.audioVo.currentTime = 0
        that.$refs.audioVo2.currentTime = 0

        console.log(msg, JSON.parse(msg.data), 'msg')
        // const h = this.$createElement
        const jsonMsg = JSON.parse(msg.data)
        if (jsonMsg.type === 1) {
          that.$refs.audioVo.play()
        } else if (jsonMsg.type === 2) {
          that.$refs.audioVo2.play()
        }
        that.$notify({
          title: jsonMsg.type === 1 ? '待接单' : '催单',
          duration: 0,
          dangerouslyUseHTMLString: true,
          onClick: () => {
            that.$router
              .push(`/order?orderId=${jsonMsg.orderId}`)
              .catch((err) => {
                console.log(err)
              })
            setTimeout(() => {
              location.reload()
            }, 100)
          },
          // 这里也可以把返回信息加入到message中显示
          message: `${
            jsonMsg.type === 1
              ? `<span>您有1个<span style=color:#419EFF>订单待处理</span>,${jsonMsg.content},请及时接单</span>`
              : `${jsonMsg.content}<span style='color:#419EFF;cursor: pointer'>去处理</span>`
          }`,
        })
      }
      // 监听socket错误
      this.websocket.onerror = function () {
        that.$notify({
          title: '错误',
          message: '服务器错误，无法接收实时报警信息',
          type: 'error',
          duration: 0,
        })
      }
      // 监听socket关闭
      this.websocket.onclose = function () {
        console.log('WebSocket已关闭')
      }
    }
  }

  private toggleSideBar() {
    AppModule.ToggleSideBar(false)
  }
  // 退出
  private async logout() {
    this.$store.dispatch('LogOut').then(() => {
      // location.href = '/'
      this.$router.replace({ path: '/login' })
    })
    // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
  }
  // 获取未读消息
  async getCountUnread() {
    const { data } = await getCountUnread()
    if (data.code === 1) {
      // this.ountUnread = data.data
      AppModule.StatusNumber(data.data)
      // setNewData(data.data)
      // this.$message.success('操作成功！')
    } else {
      this.$message.error(data.msg)
    }
  }
  // 营业状态
  async getStatus() {
    const { data } = await getStatus()
    this.status = data.data
    this.setStatus = this.status
  }
  // 下拉菜单显示
  toggleShow() {
    if (this.closeTimer) {
      clearTimeout(this.closeTimer)
      this.closeTimer = null
    }
    this.shopShow = true
  }
  // 下拉菜单隐藏
  mouseLeaves() {
    this.closeTimer = setTimeout(() => {
      this.shopShow = false
    }, 200)
  }
  // 触发空白处下来菜单关闭
  handleClose() {
    // clearTimeout(this.leave)
    // this.shopShow = false
  }
  // 设置营业状态
  handleStatus() {
    this.dialogVisible = true
  }
  // 营业状态设置
  async handleSave() {
    const { data } = await setStatus(this.setStatus)
    if (data.code === 1) {
      this.dialogVisible = false
      this.getStatus()
      this.$message.success('营业状态设置成功')
    } else {
      this.$message.error(data.msg || '营业状态设置失败')
    }
  }
  // 修改密码
  handlePwd() {
    this.dialogFormVisible = true
  }
  // 关闭密码编辑弹层
  handlePwdClose() {
    this.dialogFormVisible = false
  }
}
</script>

<style lang="scss" scoped>
.navbar-wrapper {
  position: relative;
  z-index: 1002;
}

.navbar {
  height: 64px;
  position: relative;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(60, 60, 67, 0.12);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.02);

  .statusBox {
    float: left;
    height: 100%;
    align-items: center;
    display: flex;
  }
  .hamburger-container {
    padding: 0 16px 0 20px;
    cursor: pointer;
    transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);
    -webkit-tap-highlight-color: transparent;
    height: 100%;
    display: flex;
    align-items: center;

    &:hover {
      background: rgba(0, 0, 0, 0.04);
    }
  }

  .breadcrumb-container {
    float: left;
  }
  .right-menu {
    float: right;
    margin-right: 24px;
    color: #1D1D1F;
    font-size: 14px;
    height: 100%;
    display: flex;
    align-items: center;

    span {
      padding: 0 10px;
      width: 130px;
      display: inline-block;
      cursor: pointer;
      height: 36px;
      line-height: 36px;
      border-radius: 8px;
      transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);

      &:hover {
        background: rgba(0, 0, 0, 0.04);
      }
    }
    .amendPwdIcon {
      i {
        width: 18px;
        height: 18px;
        background: url(./../../../assets/icons/btn_gaimi@2x.png) no-repeat;
        background-size: contain;
        margin-top: 8px;
      }
    }
    .outLogin {
      i {
        width: 18px;
        height: 18px;
        background: url(./../../../assets/icons/btn_close@2x.png) no-repeat 100%
          100%;
        background-size: contain;
        margin-top: 8px;
      }
    }
    .outLogin {
      cursor: pointer;
    }

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }
  }
  .rightStatus {
    height: 100%;
    line-height: 64px;
    display: flex;
    align-items: center;
    float: left;
  }
  .avatar-wrapper {
    margin-top: 3px;
    margin-left: 18px;
    position: relative;
    float: right;
    width: 120px;
    text-align: left;

    .user-dropdown {
      position: relative;
      display: inline-block;
      width: 100%;
    }

    .user-avatar {
      cursor: pointer;
      width: 40px;
      height: 40px;
      border-radius: 10px;
    }

    .el-icon-caret-bottom {
      cursor: pointer;
      position: absolute;
      right: -20px;
      top: 25px;
      font-size: 12px;
    }

    .el-button--primary {
      background: rgba(0, 0, 0, 0.04);
      border-radius: 8px;
      padding-top: 0px;
      padding-bottom: 0px;
      position: relative;
      width: 120px;
      padding-left: 12px;
      text-align: left;
      border: 0 none;
      height: 32px;
      line-height: 32px;
      color: #1D1D1F;
      font-weight: 500;
      transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);

      &:hover {
        background: rgba(0, 0, 0, 0.08);
      }

      &.active {
        background: rgba(0, 0, 0, 0.08);
        border: 0 none;
        .el-icon-arrow-down {
          transform: rotate(-180deg);
        }
      }
    }
  }
  .businessBtn {
    height: 24px;
    line-height: 22px;
    background: #34C759;
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 999px;
    display: inline-block;
    padding: 0 10px;
    color: #fff;
    font-size: 12px;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(52, 199, 89, 0.3);
  }
  .closing {
    background: #8E8E93;
    box-shadow: 0 2px 8px rgba(142, 142, 147, 0.3);
  }
  .navicon {
    i {
      display: inline-block;
      width: 18px;
      height: 18px;
      vertical-align: sub;
      margin: 0 4px 0 0;
    }
  }
  .operatingState {
    i {
      background: url('./../../../assets/icons/time.png') no-repeat;
      background-size: contain;
    }
  }
  .mesCenter {
    i {
      background: url('./../../../assets/icons/msg.png') no-repeat;
      background-size: contain;
    }
  }
}
</style>
<style lang="scss">
.el-notification {
  width: 419px !important;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);

  .el-notification__title {
    margin-bottom: 14px;
    color: #1D1D1F;
    font-weight: 600;
  }
  .el-notification__content {
    color: #6E6E73;
  }
}
.business-status-dialog {
  .el-dialog {
    min-width: auto !important;
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    z-index: 3000 !important;
  }
  .el-dialog__wrapper {
    z-index: 2999 !important;
  }
  .v-modal {
    z-index: 2998 !important;
  }
  .el-dialog__header {
    height: 64px;
    line-height: 64px;
    background: #FAFAFC;
    padding: 0 24px;
    font-size: 17px;
    color: #1D1D1F;
    border: 0 none;
    border-radius: 20px 20px 0 0;
    font-weight: 600;
  }
  .el-dialog__body {
    padding: 20px 24px 24px;
    .el-radio,
    .el-radio__input {
      white-space: normal;
    }
    .el-radio__label {
      padding-left: 5px;
      color: #1D1D1F;
      font-weight: 600;
      span {
        display: block;
        line-height: 20px;
        padding-top: 12px;
        color: #6E6E73;
        font-weight: 400;
      }
    }
    .el-radio__input.is-checked .el-radio__inner {
      &::after {
        background: #007AFF;
      }
    }
    .el-radio-group {
      & > .is-checked {
        border: 1px solid #007AFF;
        background: rgba(0, 122, 255, 0.05);
      }
    }
    .el-radio {
      width: 100%;
      background: #FAFAFC;
      border: 1px solid rgba(60, 60, 67, 0.12);
      border-radius: 12px;
      padding: 16px 20px;
      margin-top: 16px;
      transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);

      &:hover {
        border-color: rgba(0, 122, 255, 0.3);
      }
    }
    .el-radio__input.is-checked + .el-radio__label {
      span {
      }
    }
  }
  .el-badge__content.is-fixed {
    top: 24px;
    right: 2px;
    width: 18px;
    height: 18px;
    font-size: 10px;
    line-height: 16px;
    font-size: 10px;
    border-radius: 50%;
    padding: 0;
  }
  .badgeW {
    .el-badge__content.is-fixed {
      width: 30px;
      border-radius: 20px;
    }
  }
}
.el-icon-arrow-down {
  background: url('./../../../assets/icons/up.png') no-repeat 50% 50%;
  background-size: contain;
  width: 8px;
  height: 8px;
  transform: rotate(0eg);
  margin-left: 16px;
  position: absolute;
  right: 16px;
  top: 12px;
  &:before {
    content: '';
  }
}

.userInfo {
  background: #fff;
  position: fixed;
  top: 68px;
  right: 24px;
  z-index: 999999 !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  width: 120px;
  border-radius: 12px;
  line-height: 32px;
  padding: 5px 0;
  border: 1px solid rgba(60, 60, 67, 0.12);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.98);

  .userList {
    width: 100%;
    padding: 0;
  }
  p {
    cursor: pointer;
    height: 36px;
    line-height: 36px;
    padding: 0 12px;
    border-radius: 8px;
    transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);
    margin: 0 4px;
    i {
      margin-left: 10px;
      vertical-align: middle;
      margin-top: 4px;
      float: right;
    }
    &:hover {
      background: rgba(0, 0, 0, 0.04);
    }
  }
}
.msgTip {
  color: #007AFF;
  padding: 0 5px;
  font-weight: 500;
}
</style>
<style lang="scss">
.business-status-dialog {
  .el-dialog {
    min-width: auto !important;
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    z-index: 3000 !important;
  }
  .el-dialog__header {
    height: 64px;
    line-height: 64px;
    background: #FAFAFC;
    padding: 0 24px;
    font-size: 17px;
    color: #1D1D1F;
    border: 0 none;
    border-radius: 20px 20px 0 0;
    font-weight: 600;
  }
  .el-dialog__body {
    padding: 20px 24px 24px;
    .el-radio,
    .el-radio__input {
      white-space: normal;
    }
    .el-radio__label {
      padding-left: 5px;
      color: #1D1D1F;
      font-weight: 600;
      span {
        display: block;
        line-height: 20px;
        padding-top: 12px;
        color: #6E6E73;
        font-weight: 400;
      }
    }
    .el-radio__input.is-checked .el-radio__inner {
      &::after {
        background: #007AFF;
      }
    }
    .el-radio-group {
      & > .is-checked {
        border: 1px solid #007AFF;
        background: rgba(0, 122, 255, 0.05);
      }
    }
    .el-radio {
      width: 100%;
      background: #FAFAFC;
      border: 1px solid rgba(60, 60, 67, 0.12);
      border-radius: 12px;
      padding: 16px 20px;
      margin-top: 16px;
      transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);

      &:hover {
        border-color: rgba(0, 122, 255, 0.3);
      }
    }
    .el-radio__input.is-checked + .el-radio__label {
      span {
      }
    }
  }
}
</style>
