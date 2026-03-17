<template>
  <div class="container">
    <h2 class="homeTitle">
      药品总览<span><router-link to="medicine">药品管理</router-link></span>
    </h2>
    <div class="orderviewBox">
      <ul>
        <li>
          <span class="status"><i class="iconfont icon-open" />已启售</span>
          <span class="num">{{ medicineData.sold }}</span>
        </li>
        <li>
          <span class="status"><i class="iconfont icon-stop" />已停售</span>
          <span class="num">{{ medicineData.discontinued }}</span>
        </li>
        <li class="add">
          <router-link to="/medicine/add">
            <i />
            <p>新增药品</p>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator'
import orderList from './orderList.vue'
@Component({
  components: { orderList },
  name: 'cuisineview',
})
export default class extends Vue {
  @Prop() private medicineData!: any
}
</script>
