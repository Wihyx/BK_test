<template>
  <div>
    <!-- 工具栏：写文章按钮、全部/草稿箱筛选项、文章总数 -->
    <div class="toolbar">
      <el-button type="primary" @click="$router.push('/admin/articles/edit')"><el-icon><EditPen /></el-icon> 写文章</el-button>
      <el-button @click="activeTab = ''; loadData()">全部</el-button>
      <el-button @click="activeTab = (activeTab === '0' ? '' : '0'); loadData()"><el-icon><Box /></el-icon> 草稿箱{{ activeTab === '0' ? '(查看中)' : '' }}</el-button>
      <span style="color:#999;margin-left:12px">共 {{ total }} 篇文章</span>
    </div>
    <!-- 文章表格列表 -->
    <el-table :data="articles" border stripe v-loading="loading" empty-text="暂无文章">
      <el-table-column type="index" label="#" width="50" align="center" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <!-- 分类列通过映射表显示分类名称 -->
      <el-table-column prop="categoryId" label="分类" width="90" align="center">
        <template #default="{ row }">{{ categoryMap[row.categoryId] || '-' }}</template>
      </el-table-column>
      <!-- 阅读量和点赞数 -->
      <el-table-column label="阅读/点赞" width="110" align="center">
        <template #default="{ row }">{{ row.viewCount || 0 }} / {{ row.likeCount || 0 }}</template>
      </el-table-column>
      <!-- 文章状态：已发布 / 草稿 -->
      <el-table-column prop="status" label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success" size="small">已发布</el-tag>
          <el-tag v-else type="info" size="small">草稿</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="发布时间" width="165" />
      <!-- 操作列：编辑和删除 -->
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/admin/articles/edit/${row.id}`)">编辑</el-button>
          <!-- 删除前弹出确认提示 -->
          <el-popconfirm title="确定删除该文章？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页器 -->
    <div v-if="total > pageSize" class="pagination-wrap">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onActivated } from 'vue'
import { ElMessage } from 'element-plus'
import { articleApi, categoryApi } from '../../api'

// 存储文章列表数据
const articles = ref([])
// 存储分类列表
const categories = ref([])
// 表格加载状态
const loading = ref(false)
// 当前页码
const currentPage = ref(1)
// 每页显示条数
const pageSize = ref(10)
// 文章总数
const total = ref(0)

// 将分类id映射为分类名称，方便表格中显示
const categoryMap = computed(() => {
  const map = {}
  categories.value.forEach(c => map[c.id] = c.name)
  return map
})

// 当前活跃标签（空字符串=全部，'0'=草稿箱）
const activeTab = ref('')

// 加载文章列表，支持草稿箱筛选
async function loadData() {
  loading.value = true
  try {
    const params = { page: currentPage.value, pageSize: pageSize.value }
    if (activeTab.value === '0') params.status = 0
    const res = await articleApi.list(params)
    articles.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

// 删除指定文章
async function handleDelete(id) {
  await articleApi.delete(id)
  ElMessage.success('已删除')
  loadData()
}

// 页面挂载时加载文章列表和分类数据
onMounted(() => {
  loadData()
  categoryApi.list().then(res => categories.value = res.data || [])
})
// 页面被激活时（从其他tab切回来）重新加载数据
onActivated(() => { loadData() })
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; align-items: center; }
.pagination-wrap { text-align: center; margin-top: 20px; }
</style>
