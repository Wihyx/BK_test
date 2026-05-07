<template>
  <div>
    <el-row :gutter="20">
      <!-- 左半部分：分类管理 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>分类管理</span>
              <el-button type="primary" size="small" @click="openCategoryDialog()"><el-icon><Plus /></el-icon> 新增</el-button>
            </div>
          </template>
          <!-- 分类表格：序号、名称、排序值、删除操作 -->
          <el-table :data="categories" border stripe v-loading="catLoading" empty-text="暂无分类">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="sort" label="排序" width="80" align="center" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-popconfirm title="确定删除？" @confirm="deleteCategory(row.id)">
                  <template #reference>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <!-- 右半部分：标签管理 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>标签管理</span>
              <el-button type="success" size="small" @click="openTagDialog()"><el-icon><Plus /></el-icon> 新增</el-button>
            </div>
          </template>
          <!-- 加载中状态 -->
          <div v-if="tagLoading" v-loading="tagLoading" style="height:100px" />
          <!-- 无标签提示 -->
          <div v-else-if="tags.length === 0" style="text-align:center;padding:30px;color:#999">暂无标签</div>
          <!-- 标签以Tag形式展示，点击关闭可删除 -->
          <div v-else class="tag-list">
            <el-tag v-for="t in tags" :key="t.id" closable :disable-transitions @close="deleteTag(t.id)" style="margin:4px">{{ t.name }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/编辑分类弹窗 -->
    <el-dialog v-model="catDialog.visible" :title="catDialog.isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="catDialog.form" label-width="60px">
        <el-form-item label="名称"><el-input v-model="catDialog.form.name" placeholder="分类名称" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="catDialog.form.sort" :min="0" :max="999" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="catDialog.form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory" :loading="catSaving">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增标签弹窗 -->
    <el-dialog v-model="tagDialog.visible" title="新增标签" width="400px">
      <el-form :model="tagDialog.form">
        <el-form-item label="名称"><el-input v-model="tagDialog.form.name" placeholder="标签名称" @keyup.enter="saveTag" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveTag" :loading="tagSaving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { categoryApi, tagApi } from '../../api'

// 分类和标签的数据列表
const categories = ref([])
const tags = ref([])
// 分类加载状态
const catLoading = ref(false)
// 标签加载状态
const tagLoading = ref(false)
// 分类保存状态
const catSaving = ref(false)
// 标签保存状态
const tagSaving = ref(false)

// 分类弹窗数据：是否显示、是否编辑模式、表单数据
const catDialog = reactive({ visible: false, isEdit: false, form: { name: '', sort: 0, description: '' } })
// 标签弹窗数据
const tagDialog = reactive({ visible: false, form: { name: '' } })

// 从服务器加载分类列表
async function loadCategories() {
  catLoading.value = true
  try {
    const res = await categoryApi.list()
    categories.value = res.data || []
  } finally { catLoading.value = false }
}

// 从服务器加载标签列表
async function loadTags() {
  tagLoading.value = true
  try {
    const res = await tagApi.list()
    tags.value = res.data || []
  } finally { tagLoading.value = false }
}

// 打开新增分类弹窗
function openCategoryDialog() {
  catDialog.isEdit = false
  catDialog.form = { name: '', sort: 0, description: '' }
  catDialog.visible = true
}

// 保存分类（新增）
async function saveCategory() {
  if (!catDialog.form.name) { ElMessage.warning('请输入名称'); return }
  catSaving.value = true
  try {
    await categoryApi.save(catDialog.form)
    catDialog.visible = false
    ElMessage.success('新增成功')
    loadCategories()
  } finally { catSaving.value = false }
}

// 删除指定分类
async function deleteCategory(id) {
  await categoryApi.delete(id)
  ElMessage.success('已删除')
  loadCategories()
}

// 打开新增标签弹窗
function openTagDialog() {
  tagDialog.form = { name: '' }
  tagDialog.visible = true
}

// 保存标签（新增）
async function saveTag() {
  if (!tagDialog.form.name) { ElMessage.warning('请输入名称'); return }
  tagSaving.value = true
  try {
    await tagApi.save(tagDialog.form)
    tagDialog.visible = false
    ElMessage.success('新增成功')
    loadTags()
  } finally { tagSaving.value = false }
}

// 删除指定标签
async function deleteTag(id) {
  await tagApi.delete(id)
  ElMessage.success('已删除')
  loadTags()
}

// 页面挂载时加载分类和标签列表
onMounted(() => { loadCategories(); loadTags() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.tag-list { min-height: 60px; padding: 8px 0; }
</style>
