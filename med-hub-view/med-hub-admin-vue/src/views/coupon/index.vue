<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar" style="display: inline-block; width: 100%">
        <label style="margin-right: 10px">优惠券名称：</label>
        <el-input
          v-model="name"
          placeholder="请填写优惠券名称"
          style="width: 15%"
          clearable
          @clear="init"
          @keyup.enter.native="init"
        />

        <label style="margin-right: 5px; margin-left: 20px">优惠券类型：</label>
        <el-select
          v-model="type"
          placeholder="请选择"
          clearable
          style="width: 15%"
          @clear="init"
        >
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <label style="margin-right: 5px; margin-left: 20px">状态：</label>
        <el-select
          v-model="status"
          placeholder="请选择"
          clearable
          style="width: 15%"
          @clear="init"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <div style="float: right">
          <el-button
            type="primary"
            class="continue"
            @click="addCouponFun('add')"
          >
            + 新建优惠券
          </el-button>
        </div>

        <el-button
          class="normal-btn continue"
          @click="init(true)"
        >
          查询
        </el-button>
      </div>
      <el-table
        v-if="tableData.length"
        :data="tableData"
        stripe
        class="tableBox"
      >
        <el-table-column prop="name" label="优惠券名称" />
        <el-table-column prop="type" label="优惠券类型">
          <template slot-scope="scope">
            <span>{{ scope.row.type == '1' ? '满减券' : '打折券' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="优惠规则">
          <template slot-scope="scope">
            <span v-if="scope.row.type == 1">
              满{{ scope.row.minAmount }}减{{ scope.row.discountValue }}
            </span>
            <span v-else>
              {{ scope.row.discountValue * 10 }}折
              <span v-if="scope.row.maxDiscountAmount">
                (最高抵{{ scope.row.maxDiscountAmount }}元)
              </span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发行总量">
          <template slot-scope="scope">
            <span>{{ scope.row.totalCount == -1 ? '不限量' : scope.row.totalCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="issuedCount" label="已领取数量" />
        <el-table-column label="剩余数量">
          <template slot-scope="scope">
            <span v-if="scope.row.totalCount == -1">不限量</span>
            <span v-else>{{ scope.row.totalCount - scope.row.issuedCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userLimit" label="单用户限领">
          <template slot-scope="scope">
            <span>{{ scope.row.userLimit == -1 ? '不限' : scope.row.userLimit }}</span>
          </template>
        </el-table-column>
        <el-table-column label="有效期">
          <template slot-scope="scope">
            <span>{{ scope.row.startTime }} 至 {{ scope.row.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <div
              class="tableColumn-status"
              :class="{ 'stop-use': String(scope.row.status) === '0' }"
            >
              {{ String(scope.row.status) === '0' ? '禁用' : '启用' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="操作时间" />
        <el-table-column
          label="操作"
          width="200"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              class="blueBug"
              @click="editHandle(scope.row)"
            >
              修改
            </el-button>
            <el-button
              type="text"
              size="small"
              class="delBut"
              @click="deleteHandle(scope.row.id)"
            >
              删除
            </el-button>
            <el-button
              type="text"
              size="small"
              class="non"
              :class="{
                blueBug: scope.row.status == '0',
                delBut: scope.row.status != '0'
              }"
              @click="statusHandle(scope.row)"
            >
              {{ scope.row.status == '1' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else :is-search="isSearch" />
      <el-pagination
        v-if="counts > 10"
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="couponData.title"
      :visible.sync="couponData.dialogVisible"
      width="40%"
      :before-close="handleClose"
    >
      <el-form
        ref="couponData"
        :model="couponData"
        class="demo-form-inline"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="优惠券名称：" prop="name">
          <el-input
            v-model="couponData.name"
            placeholder="请输入优惠券名称"
            maxlength="100"
          />
        </el-form-item>

        <el-form-item label="优惠券类型：" prop="type">
          <el-select
            v-model="couponData.type"
            placeholder="请选择优惠券类型"
            style="width: 100%"
            :disabled="action === 'edit'"
          >
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <!-- 满减条件 -->
        <el-form-item
          v-if="couponData.type == 1"
          label="满减条件："
          prop="minAmount"
        >
          <el-input
            v-model="couponData.minAmount"
            placeholder="请输入满减条件（0 表示无门槛）"
            type="number"
          />
        </el-form-item>

        <!-- 优惠金额/折扣率 -->
        <el-form-item label="优惠金额/折扣率：" prop="discountValue">
          <el-input
            v-model="couponData.discountValue"
            :placeholder="couponData.type == 1 ? '请输入优惠金额' : '请输入折扣率（0-1）'"
            type="number"
          />
        </el-form-item>

        <!-- 最大优惠金额（仅打折券） -->
        <el-form-item
          v-if="couponData.type == 2"
          label="最大优惠金额："
          prop="maxDiscountAmount"
        >
          <el-input
            v-model="couponData.maxDiscountAmount"
            placeholder="请输入最大优惠金额"
            type="number"
          />
        </el-form-item>

        <el-form-item label="优惠券描述：" prop="description">
          <el-input
            v-model="couponData.description"
            type="textarea"
            placeholder="请输入优惠券描述（0-500 字符）"
            maxlength="500"
            :rows="3"
          />
        </el-form-item>

        <el-form-item label="发行总量：" prop="totalCount">
          <el-input
            v-model="couponData.totalCount"
            placeholder="请输入发行总量（-1 表示不限量）"
            type="number"
          />
        </el-form-item>

        <el-form-item label="单用户限领：" prop="userLimit">
          <el-input
            v-model="couponData.userLimit"
            placeholder="请输入单用户限领数量（-1 表示不限）"
            type="number"
          />
        </el-form-item>

        <el-form-item label="有效期开始时间：" prop="startTime">
          <el-date-picker
            v-model="couponData.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            style="width: 100%"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="有效期结束时间：" prop="endTime">
          <el-date-picker
            v-model="couponData.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            style="width: 100%"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>

        <el-form-item v-if="action === 'edit'" label="状态：" prop="status">
          <el-select
            v-model="couponData.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button
          size="medium"
          @click="
            ;(couponData.dialogVisible = false), $refs.couponData.resetFields()
          "
        >
          取 消
        </el-button>
        <el-button
          type="primary"
          class="continue"
          size="medium"
          @click="submitForm()"
        >
          确 定
        </el-button>
        <el-button
          v-if="action != 'edit'"
          type="primary"
          class="continue"
          size="medium"
          @click="submitForm('go')"
        >
          保存并继续添加
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import {
  getCouponPage,
  deleteCoupon,
  editCoupon,
  addCoupon,
  queryCouponById,
  enableOrDisableCoupon
} from '@/api/coupon'
import Empty from '@/components/Empty/index.vue'

@Component({
  name: 'Coupon',
  components: {
    Empty
  }
})
export default class extends Vue {
  private name: string = ''
  private type: string = ''
  private status: string = ''
  private counts: number = 0
  private page: number = 1
  private pageSize: number = 10
  private tableData: any[] = []
  private isSearch: boolean = false
  private action: string = ''
  private typeOptions: any[] = [
    { value: 1, label: '满减券' },
    { value: 2, label: '打折券' }
  ]
  private statusOptions: any[] = [
    { value: 1, label: '启用' },
    { value: 0, label: '禁用' }
  ]

  private couponData: any = {
    dialogVisible: false,
    title: '',
    id: null,
    name: '',
    type: 1,
    minAmount: 0,
    discountValue: null,
    maxDiscountAmount: null,
    description: '',
    totalCount: -1,
    userLimit: 1,
    startTime: '',
    endTime: '',
    status: 1
  }

  private rules: any = {
    name: [
      { required: true, message: '请输入优惠券名称', trigger: 'blur' },
      { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
    ],
    type: [{ required: true, message: '请选择优惠券类型', trigger: 'change' }],
    discountValue: [
      { required: true, message: '请输入优惠金额/折扣率', trigger: 'blur' }
    ],
    startTime: [
      { required: true, message: '请选择开始时间', trigger: 'change' }
    ],
    endTime: [
      { required: true, message: '请选择结束时间', trigger: 'change' }
    ],
    totalCount: [
      {
        validator: (rule: any, value: any, callback: any) => {
          if (value !== null && value !== undefined && value < -1) {
            callback(new Error('发行总量不能小于 -1'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }

  mounted() {
    this.init()
  }

  // 初始化
  init(isSearchFlag = false) {
    if (isSearchFlag) {
      this.page = 1
    }
    this.isSearch = isSearchFlag
    this.getTableData()
  }

  // 获取表格数据
  async getTableData() {
    const params = {
      page: this.page,
      pageSize: this.pageSize,
      name: this.name,
      type: this.type ? Number(this.type) : null,
      status: this.status ? Number(this.status) : null
    }

    try {
      const res: any = await getCouponPage(params)
      if (String(res.data.code) === '1') {
        this.tableData = res.data.data.records || []
        this.counts = Number(res.data.data.total)
      } else {
        this.$message.error(res.data.msg || '查询失败')
      }
    } catch (error) {
      console.log(error, 'err')
      this.$message.error('查询失败：' + error.message)
    }
  }

  // 分页 - 每页数量变化
  handleSizeChange(size: number) {
    this.pageSize = size
    this.page = 1
    this.getTableData()
  }

  // 分页 - 页码变化
  handleCurrentChange(page: number) {
    this.page = page
    this.getTableData()
  }

  // 新增优惠券
  addCouponFun(action: string) {
    this.action = action
    this.couponData = {
      dialogVisible: true,
      title: '新建优惠券',
      id: null,
      name: '',
      type: 1,
      minAmount: 0,
      discountValue: null,
      maxDiscountAmount: null,
      description: '',
      totalCount: -1,
      userLimit: 1,
      startTime: '',
      endTime: '',
      status: 1
    }
  }

  // 编辑优惠券
  async editHandle(row: any) {
    this.action = 'edit'
    try {
      const res: any = await queryCouponById(row.id)
      if (String(res.data.code) === '1') {
        this.couponData = {
          dialogVisible: true,
          title: '修改优惠券',
          ...res.data.data
        }
      } else {
        this.$message.error(res.data.msg || '查询详情失败')
      }
    } catch (error) {
      this.$message.error('查询详情失败')
    }
  }

  // 删除优惠券
  async deleteHandle(id: string) {
    try {
      const confirmRes = await this.$confirm(
        '确定要删除该优惠券吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      if (confirmRes === 'confirm') {
        const res: any = await deleteCoupon(id)
        if (String(res.data.code) === '1') {
          this.$message.success('删除成功')
          this.init()
        } else {
          this.$message.error(res.data.msg || '删除失败')
        }
      }
    } catch (error) {
      if (error !== 'cancel') {
        this.$message.error('删除失败')
      }
    }
  }

  // 修改状态
  async statusHandle(row: any) {
    const newStatus = row.status == '1' ? 0 : 1
    const statusText = newStatus == 1 ? '启用' : '禁用'

    try {
      const confirmRes = await this.$confirm(
        `确定要${statusText}该优惠券吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      if (confirmRes === 'confirm') {
        const res: any = await enableOrDisableCoupon({
          id: row.id,
          status: newStatus
        })
        if (String(res.data.code) === '1') {
          this.$message.success(`${statusText}成功`)
          this.init()
        } else {
          this.$message.error(res.data.msg || `${statusText}失败`)
        }
      }
    } catch (error) {
      if (error !== 'cancel') {
        this.$message.error(`${statusText}失败`)
      }
    }
  }

  // 提交表单
  submitForm(goOn?: string) {
    ;(this.$refs.couponData as any).validate(async (valid: boolean) => {
      if (valid) {
        try {
          // 验证
          if (this.couponData.type == 2) {
            if (
              !this.couponData.maxDiscountAmount ||
              this.couponData.maxDiscountAmount <= 0
            ) {
              this.$message.error('打折券必须设置最大优惠金额')
              return
            }
            if (
              this.couponData.discountValue < 0 ||
              this.couponData.discountValue > 1
            ) {
              this.$message.error('打折券折扣率必须在 0-1 之间')
              return
            }
          } else {
            if (
              !this.couponData.discountValue ||
              this.couponData.discountValue <= 0
            ) {
              this.$message.error('满减券优惠金额必须大于 0')
              return
            }
          }

          // 验证有效期
          if (
            this.couponData.startTime &&
            this.couponData.endTime &&
            new Date(this.couponData.startTime) >=
              new Date(this.couponData.endTime)
          ) {
            this.$message.error('结束时间必须晚于开始时间')
            return
          }

          const params = { ...this.couponData }
          // 转换类型
          params.type = Number(params.type)
          params.minAmount = params.minAmount ? Number(params.minAmount) : 0
          params.discountValue = Number(params.discountValue)
          params.maxDiscountAmount = params.maxDiscountAmount
            ? Number(params.maxDiscountAmount)
            : null
          params.totalCount = params.totalCount ? Number(params.totalCount) : -1
          params.userLimit = params.userLimit ? Number(params.userLimit) : 1

          let res: any
          if (this.action === 'edit') {
            res = await editCoupon(params)
          } else {
            res = await addCoupon(params)
          }

          if (String(res.data.code) === '1') {
            this.$message.success(this.action === 'edit' ? '修改成功' : '添加成功')
            this.couponData.dialogVisible = false
            ;(this.$refs.couponData as any).resetFields()

            if (goOn !== 'go') {
              this.init()
            } else {
              // 保存并继续添加
              this.addCouponFun('add')
            }
          } else {
            this.$message.error(res.data.msg || (this.action === 'edit' ? '修改失败' : '添加失败'))
          }
        } catch (error) {
          this.$message.error(this.action === 'edit' ? '修改失败' : '添加失败')
        }
      } else {
        return false
      }
    })
  }

  // 关闭弹窗
  handleClose() {
    this.couponData.dialogVisible = false
    ;(this.$refs.couponData as any).resetFields()
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
}

.container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.tableBar {
  margin-bottom: 20px;
}

.tableBox {
  width: 100%;
}

.pageList {
  margin-top: 20px;
  text-align: right;
}

.tableColumn-status {
  color: #67c23a;
}

.tableColumn-status.stop-use {
  color: #f56c6c;
}
</style>
