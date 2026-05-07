<template>
  <div>
    <el-card>
      <!-- 根据路由是否有id判断是编辑还是新建 -->
      <template #header><span>{{ isEdit ? '编辑文章' : '写新文章' }}</span></template>
      <el-form :model="form" label-width="80px" v-loading="saving">
        <!-- 文章标题 -->
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <!-- 所属分类下拉选择 -->
          <el-col :span="6">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width:100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- 发布状态：发布 / 草稿 -->
          <el-col :span="6">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">发布</el-radio>
                <el-radio :value="0">草稿</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <!-- 是否置顶开关 -->
          <el-col :span="6">
            <el-form-item label="置顶">
              <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <!-- 首页卡片显示类型：小卡(0) / 大卡(1) -->
          <el-col :span="6">
            <el-form-item label="卡片">
              <el-select v-model="form.cardType" style="width:100%">
                <el-option :value="0" label="小卡" />
                <el-option :value="1" label="大卡" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 文章摘要，留空自动截取开头 -->
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="文章摘要，留空自动截取开头" maxlength="300" show-word-limit />
        </el-form-item>
        <!-- 封面上传和预览区域 -->
        <el-form-item label="封面">
          <div class="cover-upload">
            <el-upload :show-file-list="false" :http-request="handleUpload" accept="image/*">
              <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
              <div v-else class="cover-placeholder"><el-icon :size="24"><Plus /></el-icon><span>上传封面</span></div>
            </el-upload>
            <el-button v-if="form.coverImage" size="small" type="danger" @click="form.coverImage = ''">移除</el-button>
          </div>
        </el-form-item>
        <!-- Markdown内容编辑区 -->
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="25" placeholder="支持 Markdown 语法&#10;---&#10;# 一级标题&#10;## 二级标题&#10;**加粗** *斜体*&#10;```代码块```&#10;![图片描述](url)&#10;[链接](url)" />
        </el-form-item>
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving"><el-icon><Check /></el-icon> 保存</el-button>
          <el-button @click="$router.push('/admin/articles')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { articleApi, categoryApi, uploadApi } from '../../api'

// 路由对象
const route = useRoute()
const router = useRouter()
// 根据路由是否有id参数判断是编辑还是新建
const isEdit = computed(() => !!route.params.id)
// 分类列表（用于下拉选择）
const categories = ref([])
// 保存按钮的加载状态
const saving = ref(false)
// 文章表单数据：标题、摘要、内容、封面图、分类、状态、置顶、卡片类型
const form = ref({ title: '', summary: '', content: '', coverImage: '', categoryId: null, status: 1, isTop: 0, cardType: 0 })

// 上传封面图到服务器，返回的URL绑定到表单
async function handleUpload(options) {
  const res = await uploadApi.upload(options.file)
  form.value.coverImage = res.data
}

// 保存文章：校验标题非空后，保存到服务器（新建或更新）
async function handleSave() {
  if (!form.value.title) { ElMessage.warning('请输入标题'); return }
  saving.value = true
  try {
    if (isEdit.value) form.value.id = route.params.id
    await articleApi.save(form.value)
    ElMessage.success('保存成功')
    router.push('/admin/articles')
  } finally {
    saving.value = false
  }
}

// 页面挂载时加载分类列表；如果是编辑模式则加载现有文章数据回填表单
onMounted(async () => {
  categoryApi.list().then(res => categories.value = res.data || [])
  if (isEdit.value) {
    const res = await articleApi.detail(route.params.id)
    form.value = res.data
    form.value.isTop = res.data.isTop ?? 0
  }
})
</script>

<style scoped>
.cover-upload { display: flex; align-items: flex-start; gap: 12px; }
.cover-preview { width: 200px; height: 130px; object-fit: cover; border-radius: 4px; cursor: pointer; }
.cover-placeholder { width: 200px; height: 130px; border: 2px dashed #ddd; border-radius: 4px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #999; cursor: pointer; }
.cover-placeholder:hover { border-color: #409eff; color: #409eff; }
</style>
