<!--购买页-->
<template>
  <view class="customer-box">
    <view class="wrap">
      <view class="contion">
        <view class="orderPay">
          <view>
            <view v-if="timeout">订单已超时</view>
            <view v-else
              >支付剩余时间<text>{{ rocallTime }}</text></view
            >
          </view>
          <view class="money"
            >￥<text>{{ orderDataInfo.orderAmount }}</text></view
          >
          <view>{{ shopInfo().shopName }}-{{ orderDataInfo.orderNumber }}</view>
        </view>
      </view>
      <view class="box payBox">
        <view class="contion">
          <view class="example-body">
            <radio-group class="uni-list" @change="styleChange">
              <view class="uni-list-item">
                <view
                  class="uni-list-item__container"
                  v-for="(item, index) in payMethodList"
                  :key="item"
                >
                  <view class="uni-list-item__content">
                    <icon class="wechatIcon"></icon
                    ><text class="uni-list-item__content-title">{{
                      item
                    }}</text>
                  </view>
                  <view class="uni-list-item__extra">
                    <radio
                      :value="item"
                      color="#007AFF"
                      :checked="index == activeRadio"
                      class="radioIcon"
                    />
                  </view>
                </view>
              </view>
            </radio-group>
          </view>
        </view>
      </view>
      <view class="bottomBox btnBox">
        <button class="add_btn" type="primary" plain="true" @click="handleSave">
          确认支付
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState } from "vuex";
import { paymentOrder, cancelOrder, paySuccess } from "@/pages/api/api.js";
export default {
  data() {
    return {
      timeout: false,
      rocallTime: "",
      orderId: null,
      orderDataInfo: {},
      activeRadio: 0,
      payMethodList: ["微信支付"],
      times: null,
    };
  },
  created() {
    this.orderDataInfo = this.orderData();
  },
  mounted() {
    this.runTimeBack();
  },
  onLoad(options) {
    this.orderId = options.orderId;
  },
  methods: {
    ...mapState(["orderData", "shopInfo"]),
    // 支付详情
    async handleSave() {
      if (this.timeout) {
        cancelOrder(this.orderId).then((res) => {});
        uni.redirectTo({
          url: "/pages/details/index?orderId=" + this.orderId,
        });
      } else {
        // 如果支付成功进入成功页
        clearTimeout(this.times);
        const params = {
          orderNumber: this.orderDataInfo.orderNumber,
          payMethod: this.activeRadio === 0 ? 1 : 2,
        };
        
        try {
          // 1. 获取支付参数
          const res = await paymentOrder(params);
          if (res.code !== 1) {
            uni.showToast({
              title: res.msg || '获取支付参数失败',
              duration: 1000,
              icon: "none",
            });
            return;
          }
          
          // 2. 显示加载提示
          uni.showLoading({
            title: '正在调起支付...',
            mask: true
          });
          
          // 3. 调用微信支付（会弹出二维码）
          let payErr = null;
          let payRes = null;
          try {
            // 打印支付参数
            console.log('微信支付参数:', {
              ...res.data,
              package: res.data.packageStr
            });
            
            const result = await uni.requestPayment({
              ...res.data,
              package: res.data.packageStr, // package 为微信支付必须的字段
            });
            payRes = result;
            console.log('微信支付返回结果:', payRes);
          } catch (error) {
            payErr = error;
            console.error('微信支付出错:', error);
            console.error('错误详情:', JSON.stringify(error));
          }
          
          uni.hideLoading();
          
          // 4. 根据支付结果处理
          if (payErr) {
            // 用户取消支付或支付失败
            console.log('支付失败或取消:', payErr);
            const errMsg = payErr.errMsg || '支付失败';
            
            // 判断是否是用户主动取消
            if (errMsg.indexOf('cancel') > -1 || errMsg.indexOf('取消') > -1) {
              console.log('用户主动取消支付');
              await uni.showToast({ 
                title: "已取消支付", 
                icon: "none" 
              });
            } else {
              console.log('支付失败:', errMsg);
              await uni.showToast({ 
                title: "支付失败", 
                icon: "error" 
              });
            }
            
            setTimeout(() => {
              uni.redirectTo({
                url: "/pages/details/index?orderId=" + this.orderId,
              });
            }, 1500);
          } else {
            // 用户扫码支付成功
            console.log('====== 支付成功 ======');
            console.log('支付返回结果:', payRes);
            console.log('订单号:', this.orderDataInfo.orderNumber);
            
            // 5. 调用后端 paySuccess 接口，通知支付成功并发送管理员通知
            console.log('开始调用 paySuccess 接口...');
            const successRes = await paySuccess({
              outTradeNo: this.orderDataInfo.orderNumber
            });
            console.log('paySuccess 接口返回:', successRes);
            
            if (successRes.code === 1) {
              console.log('支付成功，准备跳转到成功页面');
              await uni.showToast({ 
                title: "支付成功", 
                icon: "success",
                duration: 2000
              });
              
              setTimeout(() => {
                // 跳转到支付成功页面
                console.log('跳转到支付成功页面，orderId:', this.orderId);
                uni.redirectTo({
                  url: "/pages/success/index?orderId=" + this.orderId,
                });
              }, 2000);
            } else {
              console.error('支付结果确认失败:', successRes);
              await uni.showToast({ 
                title: successRes.msg || '支付结果确认失败', 
                icon: "error" 
              });
            }
          }
        } catch (error) {
          uni.hideLoading();
          console.error('支付过程出错:', error);
          await uni.showToast({ 
            title: "支付异常", 
            icon: "error" 
          });
        }
      }
    },
    // // 订单倒计时
    runTimeBack() {
      const end = Date.parse(this.orderDataInfo.orderTime.replace(/-/g, "/"));
      const now = Date.parse(new Date());
      const m15 = 15 * 60 * 1000;
      const msec = m15 - (now - end);
      if (msec < 0) {
        this.timeout = true;
        clearTimeout(this.times);
      } else {
        let min = parseInt((msec / 1000 / 60) % 60);
        let sec = parseInt((msec / 1000) % 60);
        if (min < 10) {
          min = "0" + min;
        } else {
          min = min;
        }
        if (sec < 10) {
          sec = "0" + sec;
        } else {
          sec = sec;
        }
        this.rocallTime = min + ":" + sec;
        let that = this;
        if (min >= 0 && sec >= 0) {
          if (min === 0 && sec === 0) {
            this.timeout = true;
            clearTimeout(this.times);
            return;
          }
          this.times = setTimeout(function () {
            that.runTimeBack();
          }, 1000);
        }
      }
    },
  },
};
</script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style src="./../order/style.scss" lang="scss"></style>
<style>
</style>
