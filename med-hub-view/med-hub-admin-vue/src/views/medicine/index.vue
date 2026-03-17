<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 10px">药品名称：</label>
        <el-input v-model="input"
                  placeholder="请填写药品名称"
                  style="width: 14%"
                  clearable
                  @clear="init"
                  @keyup.enter.native="initFun"
        />

        <label style="margin-right: 10px; margin-left: 20px">药品分类：</label>
        <el-select v-model="categoryId"
                   style="width: 14%"
                   placeholder="请选择"
                   clearable
                   @clear="init"
        >
          <el-option v-for="item in medicineCategoryList"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value"
          />
        </el-select>

        <label style="margin-right: 10px; margin-left: 20px">售卖状态：</label>
        <el-select v-model="medicineStatus"
                   style="width: 14%"
                   placeholder="请选择"
                   clearable
                   @clear="init"
        >
          <el-option v-for="item in saleStatus"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value"
          />
        </el-select>
        <el-button class="normal-btn continue"
                   @click="init(true)"
        >
          查询
        </el-button>

        <div class="tableLab">
          <span class="delBut non"
                @click="deleteHandle('批量', null)"
          >批量删除</span>
          <!-- <span class="blueBug non" @click="statusHandle('1')">批量启售</span>
          <span
            style="border: none"
            class="delBut non"
            @click="statusHandle('0')"
            >批量停售</span
          > -->
          <el-button type="primary"
                     class="continue"
                     style="margin-left: 15px"
                     @click="addMedicineType('add')"
          >
            + 新建药品
          </el-button>
        </div>
      </div>
      <el-table v-if="tableData.length"
                :data="tableData"
                stripe
                class="tableBox"
                @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection"
                         width="25"
        />
        <el-table-column prop="name"
                         label="药品名称"
        />
        <el-table-column prop="image"
                         label="图片"
        >
          <template slot-scope="{ row }">
            <el-image style="width: 80px; height: 40px; border: none; cursor: pointer"
                      :src="row.image"
            >
              <div slot="error"
                   class="image-slot"
              >
                <img src="./../../assets/noImg.png"
                     style="width: auto; height: 40px; border: none"
                >
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName"
                         label="药品分类"
        />
        <el-table-column label="售价">
          <template slot-scope="scope">
            <span style="margin-right: 10px">￥{{ (scope.row.price ).toFixed(2)*100/100 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="售卖状态">
          <template slot-scope="scope">
            <div class="tableColumn-status"
                 :class="{ 'stop-use': String(scope.row.status) === '0' }"
            >
              {{ String(scope.row.status) === '0' ? '停售' : '启售' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime"
                         label="最后操作时间"
        />
        <el-table-column label="操作"
                         width="250"
                         align="center"
        >
          <template slot-scope="scope">
            <el-button type="text"
                       size="small"
                       class="blueBug"
                       @click="addMedicineType(scope.row.id)"
            >
              修改
            </el-button>
            <el-button type="text"
                       size="small"
                       class="delBut"
                       @click="deleteHandle('单删', scope.row.id)"
            >
              删除
            </el-button>
            <el-button type="text"
                       size="small"
                       class="non"
                       :class="{
                         blueBug: scope.row.status == '0',
                         delBut: scope.row.status != '0'
                       }"
                       @click="statusHandle(scope.row)"
            >
              {{ scope.row.status == '0' ? '启售' : '停售' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else
             :is-search="isSearch"
      />
      <el-pagination v-if="counts > 10"
                     class="pageList"
                     :page-sizes="[10, 20, 30, 40]"
                     :page-size="pageSize"
                     layout="total, sizes, prev, pager, next, jumper"
                     :total="counts"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import HeadLable from '@/components/HeadLable/index.vue'
import {
  getMedicinePage,
  editMedicine,
  deleteMedicine,
  medicineStatusByStatus,
  medicineCategoryList
} from '@/api/medicine'
import InputAutoComplete from '@/components/InputAutoComplete/index.vue'
import Empty from '@/components/Empty/index.vue'
import { baseUrl } from '@/config.json'

@Component({
  name: 'MedicineType',
  components: {
    HeadLable,
    InputAutoComplete,
    Empty
  }
})
export default class extends Vue {
  private input: any = ''
  private counts: number = 0
  private page: number = 1
  private pageSize: number = 10
  private checkList: string[] = []
  private tableData: [] = []
  private medicineState = ''
  private medicineCategoryList = []
  private categoryId = ''
  private medicineStatus = ''
  private isSearch: boolean = false
  private saleStatus: any = [
    {
      value: 0,
      label: '停售'
    },
    {
      value: 1,
      label: '启售'
    }
  ]

  created() {
    this.init()
    this.getMedicineCategoryList()
  }

  initProp(val) {
    this.input = val
    this.initFun()
  }

  initFun() {
    this.page = 1
    this.init()
  }

  private async init(isSearch?) {
    this.isSearch = isSearch
    await getMedicinePage({
      page: this.page,
      pageSize: this.pageSize,
      name: this.input || undefined,
      categoryId: this.categoryId || undefined,
      status: this.medicineStatus
    })
      .then(res => {
        if (res.data.code === 1) {
          this.tableData = res.data && res.data.data && res.data.data.records
          this.counts = Number(res.data.data.total)
        }
      })
      .catch(err => {
        this.$message.error('请求出错了：' + err.message)
      })
  }

  // 添加
  private addMedicineType(st: string) {
    if (st === 'add') {
      this.$router.push({ path: '/medicine/add' })
    } else {
      this.$router.push({ path: '/medicine/add', query: { id: st } })
    }
  }

  // 删除
  private deleteHandle(type: string, id: any) {
    if (type === '批量' && id === null) {
      if (this.checkList.length === 0) {
        return this.$message.error('请选择删除对象')
      }
    }
    this.$confirm('确认删除该药品, 是否继续?', '确定删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      deleteMedicine(type === '批量' ? this.checkList.join(',') : id)
        .then(res => {
          if (res && res.data && res.data.code === 1) {
            this.$message.success('删除成功！')
            this.init()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(err => {
          this.$message.error('请求出错了：' + err.message)
        })
    })
  }
  //获取药品分类下拉数据
  private getMedicineCategoryList() {
    medicineCategoryList({
      type: 1
    })
      .then(res => {
        if (res && res.data && res.data.code === 1) {
          this.medicineCategoryList = (
            res.data &&
            res.data.data &&
            res.data.data
          ).map(item => {
            return { value: item.id, label: item.name }
          })
        }
      })
      .catch(() => {})
  }

  //状态更改
  private statusHandle(row: any) {
    let params: any = {}
    if (typeof row === 'string') {
      if (this.checkList.length === 0) {
        this.$message.error('批量操作，请先勾选操作药品！')
        return false
      }
      params.id = this.checkList.join(',')
      params.status = row
    } else {
      params.id = row.id
      params.status = row.status ? '0' : '1'
    }
    this.medicineState = params
    this.$confirm('确认更改该药品状态?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 起售停售---批量起售停售接口
      medicineStatusByStatus(this.medicineState)
        .then(res => {
          if (res && res.data && res.data.code === 1) {
            this.$message.success('药品状态已经更改成功！')
            this.init()
          } else {
            this.$message.error(res.data.msg)
          }
        })
        .catch(err => {
          this.$message.error('请求出错了：' + err.message)
        })
    })
  }

  // 全部操作
  private handleSelectionChange(val: any) {
    let checkArr: any[] = []
    val.forEach((n: any) => {
      checkArr.push(n.id)
    })
    this.checkList = checkArr
  }

  private handleSizeChange(val: any) {
    this.pageSize = val
    this.init()
  }

  private handleCurrentChange(val: any) {
    this.page = val
    this.init()
  }
}
</script>
<style lang="scss">
.el-table-column--selection .cell {
  padding-left: 10px;
}
</style>
<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 24px;
    .container {
      background: #FFFFFF;
      position: relative;
      z-index: 1;
      padding: 24px;
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
      transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);

      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
      }

      .normal-btn {
        background: #007AFF;
        color: white;
        margin-left: 20px;
        border-radius: 10px;
        font-weight: 500;
        padding: 10px 20px;
        transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);

        &:hover {
          background: #0A84FF;
          box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
        }
      }
      .tableBar {
        margin-bottom: 24px;
        padding-bottom: 20px;
        border-bottom: 1px solid rgba(60, 60, 67, 0.12);

        label {
          color: #1D1D1F;
          font-weight: 500;
        }

        .tableLab {
          display: inline-block;
          float: right;
          span {
            cursor: pointer;
            display: inline-block;
            font-size: 14px;
            padding: 0 20px;
            color: #6E6E73;
            transition: all 240ms cubic-bezier(0.25, 0.1, 0.25, 1);
            border-radius: 8px;
            height: 36px;
            line-height: 36px;

            &:hover {
              background: rgba(0, 0, 0, 0.04);
            }
          }
        }
      }
      .tableBox {
        width: 100%;
        border: 1px solid rgba(60, 60, 67, 0.08);
        border-radius: 12px;
        overflow: hidden;
      }
      .pageList {
        text-align: center;
        margin-top: 32px;
      }
    }
  }
}
</style>

