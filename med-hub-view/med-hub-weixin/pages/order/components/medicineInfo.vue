<!--备注、包装、发票弹-->
<template>
  <view class="box order_list">
    <!-- 备注、包装、发票 -->
    <view class="uniInfo">
      <!-- 备注 -->
      <view @click="goRemark">
        <uni-list>
          <uni-list-item showArrow title="备注" class="uniListItem">
            <template v-slot:footer>
              <text class="temarkText">{{
                remark ? remark : "填写药品需求、用药说明等"
              }}</text>
            </template>
          </uni-list-item>
        </uni-list>
      </view>
      <!-- end -->
      <!-- 包装数量 -->
      <view @click="openPopuos('bottom')">
        <uni-list>
          <uni-list-item showArrow title="包装数量" class="uniListItem">
            <template v-slot:footer>
              <text>已在店选择：{{ tablewareData }}</text>
            </template>
          </uni-list-item>
        </uni-list>
      </view>
      <!-- end -->
      <!-- 发票 -->
      <view class="invoiceBox">
        <uni-list>
          <uni-list-item title="发票" class="uniListItem">
            <template v-slot:footer>
              <text>请联系药房提供</text>
            </template>
          </uni-list-item>
        </uni-list>
      </view>
      <!-- end -->
      <view class="container">
        <!-- 包装弹层 -->
        <uni-popup ref="popup" @change="change" class="popupBox">
          <view class="popup-content">
            <view class="popupTitle">
              <text>按政府条例要求： </text>
              <text>药房不得主动向您提供一次性包装，请按需选择包装数量</text>
            </view>
            <view class="popupCon">
              <view class="popupBtn">
                <text @click="closePopup">取消</text>
                <text>选择本单包装</text>
                <text @click="handlePiker">确定</text>
              </view>
              <pikers
                :baseData="baseData"
                ref="piker"
                @changeCont="changeCont"
              ></pikers>
            </view>
            <view class="popupSet">
              <view>后续订单包装设置</view>
              <view>
                <radio-group @change="handleRadio">
                  <label v-for="item in radioGroup" :key="item">
                    <radio
                      :value="item"
                      color="#FFC200"
                      :checked="item == activeRadio"
                    />{{ item }}
                  </label>
                </radio-group>
              </view>
            </view>
          </view>
        </uni-popup>
        <!-- end -->
      </view>
    </view>
  </view>
</template>
<script>
import { editHoppingCart } from '../../api/api'
import Pikers from '@/components/uni-piker/index.vue'
export default {
  // 获取父级传的数据
  props: {
    // 进入备注页
    remark: {
      type: String,
      default: '',
    },
    // 选择的包装信息
    tablewareData: {
      type: String,
      default: '',
    },
    // 后续订单包装设置
    radioGroup: {
      type: Array,
      default: () => [],
    },
    // 当前选择的
    activeRadio: {
      type: String,
      default: '',
    },
    // 本单包装数据
    baseData: {
      type: Array,
      default: () => [],
    },
  },
  components: { Pikers },
  methods: {
    // 进入备注页面
    goRemark () {
      this.$emit("goRemark")
    },
    // 打开包装数量弹出层
    openPopuos (type) {
      this.$refs.popup.open(type)
    },
    change () {
      this.$emit("change")
    },
    // 取消本单包装
    closePopup (type) {
      this.$refs.popup.close(type)
    },
    // 确定本单包装
    handlePiker () {
      this.$emit('handlePiker')
      this.closePopup()
    },
    // 触发本单包装
    changeCont (val) {
      this.$emit("changeCont", val)
    },
    // 包装数量的后续订单包装设置
    handleRadio (e) {
      this.$emit("handleRadio", e)
    },
  },
};
</script>
<style src="./../style.scss" lang="scss" scoped></style>