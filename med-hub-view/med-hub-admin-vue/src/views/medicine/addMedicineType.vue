<template>
  <div :key="vueRest"
       class="addBrand-container"
  >
    <div :key="restKey"
         class="container"
    >
      <el-form ref="ruleForm"
               :model="ruleForm"
               :rules="rules"
               :inline="true"
               label-width="180px"
               class="demo-ruleForm"
      >
        <div>
          <el-form-item label="药品名称:"
                        prop="name"
          >
            <el-input v-model="ruleForm.name"
                      placeholder="请填写药品名称"
                      maxlength="20"
            />
          </el-form-item>
          <el-form-item label="药品分类:"
                        prop="categoryId"
          >
            <el-select v-model="ruleForm.categoryId"
                       placeholder="请选择药品分类"
            >
              <el-option v-for="(item, index) in medicineList"
                         :key="index"
                         :label="item.name"
                         :value="item.id"
              />
            </el-select>
          </el-form-item>
        </div>
        <div>
          <el-form-item label="药品价格:"
                        prop="price"
          >
            <el-input v-model="ruleForm.price"
                      placeholder="请设置药品价格"
            />
          </el-form-item>
        </div>
        <div>
          <el-form-item label="药品图片:"
                        prop="image"
          >
            <image-upload :prop-image-url="imageUrl"
                          @imageChange="imageChange"
            >
              图片大小不超过2M<br>仅能上传 PNG JPEG JPG类型图片<br>建议上传200*200或300*300尺寸的图片
            </image-upload>
          </el-form-item>
        </div>
        <div class="address">
          <el-form-item label="药品描述:"
                        prop="region"
          >
            <el-input v-model="ruleForm.description"
                      type="textarea"
                      :rows="3"
                      maxlength="200"
                      placeholder="药品描述，最长200字"
            />
          </el-form-item>
        </div>
        <div class="subBox address">
          <el-button @click="() => $router.back()">
            取消
          </el-button>
          <el-button type="primary"
                     class="continue"
                     @click="submitForm('ruleForm')"
          >
            保存
          </el-button>
          <el-button v-if="actionType == 'add'"
                     type="primary"
                     class="continue"
                     @click="submitForm('ruleForm', 'goAnd')"
          >
            保存并继续添加
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator'
import HeadLable from '@/components/HeadLable/index.vue'
import SelectInput from './components/SelectInput.vue'
import ImageUpload from '@/components/ImgUpload/index.vue'
// getFlavorList口味列表暂时不做 getDishTypeList
import {
  queryMedicineById,
  addMedicine,
  editMedicine,
  getCategoryList,
  commonDownload
} from '@/api/medicine'
import { baseUrl } from '@/config.json'
import { getToken } from '@/utils/cookies'
@Component({
  name: 'addShop',
  components: {
    HeadLable,
    ImageUpload
  }
})
export default class extends Vue {
  private restKey: number = 0
  private textarea: string = ''
  private value: string = ''
  private imageUrl: string = ''
  private actionType: string = ''
  private medicineList: string[] = []
  private vueRest = '1'
  private index = 0
  private inputStyle = { flex: 1 }
  private headers = {
    token: getToken()
  }
  private ruleForm = {
    name: '',
    id: '',
    price: '',
    code: '',
    image: '',
    description: '',
    status: true,
    categoryId: ''
  }

  get rules() {
    return {
      name: [
        {
          required: true,
          validator: (rule: any, value: string, callback: Function) => {
            if (!value) {
              callback(new Error('请输入药品名称'))
            } else {
              const reg = /^([A-Za-z0-9\u4e00-\u9fa5]){2,20}$/
              if (!reg.test(value)) {
                callback(new Error('药品名称输入不符，请输入2-20个字符'))
              } else {
                callback()
              }
            }
          },
          trigger: 'blur'
        }
      ],
      categoryId: [
        { required: true, message: '请选择药品分类', trigger: 'change' }
      ],
      image: {
        required: true,
        message: '药品图片不能为空'
      },
      price: [
        {
          required: true,
          // 'message': '请填写药品价格',
          validator: (rules: any, value: string, callback: Function) => {
            const reg = /^([1-9]\d{0,5}|0)(\.\d{1,2})?$/
            if (!reg.test(value) || Number(value) <= 0) {
              callback(
                new Error(
                  '药品价格格式有误，请输入大于零且最多保留两位小数的金额'
                )
              )
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ],
      code: [{ required: true, message: '请填写商品码', trigger: 'blur' }]
    }
  }

  created() {
    this.getDishList()
    this.actionType = this.$route.query.id ? 'edit' : 'add'
    if (this.$route.query.id) {
      this.init()
    }
  }

  mounted() {}

  private async init() {
    queryMedicineById(this.$route.query.id).then(res => {
      if (res && res.data && res.data.code === 1) {
        const data = res.data.data
        this.ruleForm.name = data.name
        this.ruleForm.id = data.id
        this.ruleForm.price = String(data.price)
        this.ruleForm.categoryId = data.categoryId
        this.ruleForm.description = data.description
        this.ruleForm.code = data.code
        this.ruleForm.status = data.status == '1'
        this.imageUrl = data.image
      } else {
        this.$message.error(res.data.msg)
      }
    })
  }

  private getDishList() {
    getCategoryList({ type: 1 }).then(res => {
      if (res.data.code === 1) {
        this.medicineList = res && res.data && res.data.data
      } else {
        this.$message.error(res.data.msg)
      }
    })
  }

  private submitForm(formName: any, st: any) {
    (this.$refs[formName] as any).validate((valid: any) => {
      console.log(valid, 'valid')
      if (valid) {
        if (!this.ruleForm.image) return this.$message.error('药品图片不能为空')
        let params: any = { ...this.ruleForm }
        params.status =
          this.actionType === 'add' ? 0 : this.ruleForm.status ? 1 : 0
        params.categoryId = this.ruleForm.categoryId
        if (this.actionType == 'add') {
          delete params.id
          addMedicine(params)
            .then(res => {
              if (res.data.code === 1) {
                this.$message.success('药品添加成功！')
                if (!st) {
                  this.$router.push({ path: '/medicine' })
                } else {
                  this.imageUrl = ''
                  this.ruleForm = {
                    name: '',
                    id: '',
                    price: '',
                    code: '',
                    image: '',
                    description: '',
                    status: true,
                    categoryId: ''
                  }
                  this.restKey++
                }
              } else {
                this.$message.error(res.data.desc || res.data.msg)
              }
            })
            .catch(err => {
              this.$message.error('请求出错了：' + err.message)
            })
        } else {
          delete params.createTime
          delete params.updateTime
          editMedicine(params)
            .then(res => {
              if (res && res.data && res.data.code === 1) {
                this.$router.push({ path: '/medicine' })
                this.$message.success('药品修改成功！')
              } else {
                this.$message.error(res.data.desc || res.data.msg)
              }
            })
            .catch(err => {
              this.$message.error('请求出错了：' + err.message)
            })
        }
      } else {
        return false
      }
    })
  }

  imageChange(value: any) {
    this.ruleForm.image = value
  }
}
</script>
<style lang="scss" scoped>
.addBrand-container {
  .el-form--inline .el-form-item__content {
    width: 293px;
  }

  .el-input {
    width: 350px;
  }

  .address {
    .el-form-item__content {
      width: 777px !important;
    }
  }
}
</style>

