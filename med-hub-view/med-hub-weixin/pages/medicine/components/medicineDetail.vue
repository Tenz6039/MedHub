<!--选择多规格弹层-->
<template>
  <!-- 药品详情 -->
  <view class="dish_detail_pop" v-if="openDetailPop && medicineDetailes.type == 1">
    <image
      mode="aspectFill"
      class="div_big_image"
      :src="medicineDetailes.image"
    ></image>
    <view class="title">{{ medicineDetailes.name }}</view>
    <view class="desc">{{ medicineDetailes.description }}</view>
    <view class="but_item">
      <view class="price">
        <text class="ico">￥</text>
        {{ medicineDetailes.price.toFixed(2) }}
      </view>
      <view
        class="active"
        v-if="(!medicineDetailes.specifications || medicineDetailes.specifications.length === 0) && medicineDetailes.medicineNumber > 0"
      >
        <image
          src="../../../static/btn_red.png"
          @click="redMedicineFromCartAction(medicineDetailes, '普通')"
          class="dish_red"
          mode=""
        ></image>
        <text class="dish_number">{{ medicineDetailes.medicineNumber }}</text>
        <image
          src="../../../static/btn_add.png"
          class="dish_add"
          @click="addMedicineToCartAction(medicineDetailes, '普通')"
          mode=""
        ></image>
      </view>

      <view class="active" v-if="medicineDetailes.specifications && medicineDetailes.specifications.length > 0"
        ><view class="dish_card_add" @click="moreNormDataesHandle(medicineDetailes)"
          >选择规格</view
        ></view
      >
      <view
        class="active"
        v-if="
          medicineDetailes.medicineNumber === 0 && (!medicineDetailes.specifications || medicineDetailes.specifications.length === 0)
        "
      >
        <view class="dish_card_add" @click="addMedicineToCartAction(medicineDetailes, '普通')"
          >加入购物车</view
        >
      </view>
    </view>
    <view class="close" @click="dishClose"
      ><image
        class="close_img"
        src="../../../static/but_close.png"
        mode=""
      ></image
    ></view>
  </view>
  <!-- end -->
  <!-- 药品组合详情 -->
  <view class="dish_detail_pop" v-else-if="openDetailPop && medicineDetailes.type == 2">
    <scroll-view class="dish_items" scroll-y="true" scroll-top="0rpx">
      <view
        class="dish_item"
        v-for="(item, index) in medicineCombinationData"
        :key="index"
      >
        <image class="div_big_image" :src="item.image" mode=""></image>
        <view class="title">
          {{ item.name }}
          <text style="">X{{ item.copies }}</text>
        </view>
        <view class="desc">{{ item.description }}</view>
      </view>
    </scroll-view>
    <view class="but_item">
      <view class="price">
        <text class="ico">￥</text>
        {{ medicineDetailes.price }}
      </view>
      <view
        class="active"
        v-if="medicineDetailes.medicineNumber && medicineDetailes.medicineNumber > 0"
      >
        <image
          src="../../../static/btn_red.png"
          @click="redMedicineFromCartAction(medicineDetailes, '普通')"
          class="dish_red"
          mode=""
        ></image>
        <text class="dish_number">{{ medicineDetailes.medicineNumber }}</text>
        <image
          src="../../../static/btn_add.png"
          class="dish_add"
          @click="addMedicineToCartAction(medicineDetailes, '普通')"
          mode=""
        ></image>
      </view>
      <view class="active" v-else-if="medicineDetailes.medicineNumber == 0"
        ><view
          class="dish_card_add"
          @click="addMedicineToCartAction(medicineDetailes, '普通')"
          >加入购物车</view
        ></view
      >
    </view>
    <view class="close" @click="dishClose"
      ><image
        class="close_img"
        src="../../../static/but_close.png"
        mode=""
      ></image
    ></view>
  </view>
  <!-- end -->
</template>
<script>
export default {
  // 获取父级传的数据
  props: {
    medicineDetailes: {
      type: Object,
      default: () => ({}),
    },
    openDetailPop: {
      type: Boolean,
      default: false,
    },
    medicineCombinationData: {
      type: Array,
      default: () => [],
    },
  },
  watch: {
    openDetailPop(newVal) {
      console.log('药品详情弹窗状态:', newVal);
      console.log('药品详情数据:', this.medicineDetailes);
      console.log('药品类型:', this.medicineDetailes.type);
    }
  },
  methods: {
    // 加入购物车
    addMedicineToCartAction(obj, item) {
      console.log('药品详情组件 - 添加药品:', obj, item);
      this.$emit("addMedicineToCartAction", obj, item);
    },
    redMedicineFromCartAction(obj, item) {
      console.log('药品详情组件 - 减少药品:', obj, item);
      this.$emit("redMedicineFromCartAction", obj, item);
    },
    // 选择规格
    moreNormDataesHandle(obj) {
      this.$emit("moreNormDataesHandle", obj);
    },
    // 关闭药品详情
    dishClose() {
      this.$emit("dishClose");
    },
  },
};
</script>
<style lang="scss" scoped>
.dish_detail_pop {
  width: calc(100vw - 160rpx);
  box-sizing: border-box;
  position: absolute;
  top: 50%;
  left: 50%;
  padding: 40rpx;
  transform: translateX(-50%) translateY(-50%);
  background: #fff;
  border-radius: 20rpx;
  z-index: 100;
  .div_big_image {
    width: 100%;
    height: 320rpx;
    border-radius: 10rpx;
  }
  .title {
    font-size: 40rpx;
    line-height: 80rpx;
    text-align: center;
    font-weight: bold;
  }
  .dish_items {
    height: 60vh;
  }
  .but_item {
    display: flex;
    position: relative;
    flex: 1;
    .price {
      text-align: left;
      color: #e94e3c;
      line-height: 88rpx;
      box-sizing: border-box;
      font-size: 48rpx;
      font-weight: bold;
      .ico {
        font-size: 28rpx;
      }
    }
    .active {
      position: absolute;
      right: 0rpx;
      bottom: 20rpx;
      display: flex;
      .dish_add,
      .dish_red {
        display: block;
        width: 72rpx;
        height: 72rpx;
      }
      .dish_number {
        padding: 0 10rpx;
        line-height: 72rpx;
        font-size: 30rpx;
        font-family: PingFangSC, PingFangSC-Medium;
        font-weight: 500;
      }
      .dish_card_add {
        width: 200rpx;
        line-height: 60rpx;
        text-align: center;
        font-weight: 500;
        font-size: 28rpx;
        opacity: 1;
        background: $uni-color-primary;
        border-radius: 30rpx;
      }
    }
  }
}
.close {
  position: absolute;
  bottom: -180rpx;
  left: 50%;
  transform: translateX(-50%);
  .close_img {
    width: 88rpx;
    height: 88rpx;
  }
}
</style>