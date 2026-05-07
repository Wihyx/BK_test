<template>
  <div>
    <!-- 引入顶部导航栏 -->
    <Header />
    <div class="container page-main">
      <!-- 页面标题：显示分类名或标签名 -->
      <h2 class="page-title">{{ type === 'category' ? '分类' : '标签' }}：{{ name }}</h2>
      <!-- 文章卡片列表，每张卡片点击跳转到文章详情 -->
      <div
        v-for="item in articles"
        :key="item.id"
        class="glass-card article-card"
        @click="$router.push(`/article/${item.id}`)"
      >
        <h3>{{ item.title }}</h3>
        <p class="summary">{{ item.summary || '暂无摘要' }}</p>
        <div class="meta"><span>{{ item.createdTime?.substring(0, 10) }}</span></div>
      </div>
      <!-- 无文章时的空状态 -->
      <div v-if="articles.length === 0" style="text-align:center;padding:60px;color:#888">暂无文章</div>
      <!-- 分页器，总数大于每页条数时才显示 -->
      <div v-if="total > pageSize" class="pagination-wrap">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '../api'
import Header from '../components/Header.vue'

// 当前路由对象
const route = useRoute()
// 存储文章列表数据
const articles = ref([])
// 当前页码
const currentPage = ref(1)
// 每页显示条数
const pageSize = ref(10)
// 文章总数（用于分页器）
const total = ref(0)
// 当前列表类型：category（按分类）或 tag（按标签）
const type = ref('')
// 分类或标签的名称
const name = ref('')

// 根据路由参数加载文章列表（支持分类筛选或标签筛选）
async function loadData() {
  const params = { page: currentPage.value, pageSize: pageSize.value }
  if (route.path.startsWith('/category')) params.categoryId = route.params.id
  if (route.path.startsWith('/tag')) params.tagId = route.params.id
  const res = await articleApi.list(params)
  articles.value = res.data.records || []
  total.value = res.data.total || 0
}

// 页面挂载时判断路由类型，然后加载数据
onMounted(() => {
  type.value = route.path.startsWith('/category') ? 'category' : 'tag'
  loadData()
})
</script>

<style scoped>
.page-title { margin-bottom: 20px; font-size: 22px; color: #e0e0e0; }
.article-card {
  margin-bottom: 12px;
  cursor: pointer;
  background: rgba(0,0,0,.36);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,.08);
  border-radius: 12px;
  padding: 20px 24px;
  transition: transform .2s, box-shadow .2s;
}
.article-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,.3); }
.article-card h3 { color: #e0e0e0; font-size: 17px; margin-bottom: 8px; }
.summary { color: #999; margin: 8px 0; font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.meta { color: #777; font-size: 13px; }
.pagination-wrap { text-align: center; margin: 30px 0; }
</style>
