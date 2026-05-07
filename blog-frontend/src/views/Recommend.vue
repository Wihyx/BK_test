<template>
  <div>
    <!-- 引入顶部导航栏 -->
    <Header />
    <div class="container page-main">
      <h2 class="page-title">推荐</h2>
      <!-- 加载中显示骨架屏 -->
      <div v-if="loading" class="loading-wrap"><el-skeleton :rows="5" animated /></div>
      <!-- 无推荐文章时的空状态 -->
      <div v-else-if="articles.length === 0" style="text-align:center;padding:60px;color:#888">暂无推荐文章</div>
      <!-- 推荐文章网格布局，两列展示 -->
      <div v-else class="recommend-grid">
        <!-- 每张推荐卡片：封面图、标题、摘要、阅读量、评论数等 -->
        <div v-for="item in articles" :key="item.id" class="rec-card glass-card" @click="$router.push(`/article/${item.id}`)">
          <div class="rec-cover">
            <img v-if="item.coverImage" :src="item.coverImage" />
            <div v-else class="rec-cover-ph"><el-icon :size="36"><Document /></el-icon></div>
            <!-- 推荐角标 -->
            <div class="rec-badge"><el-icon><Star /></el-icon> 推荐</div>
          </div>
          <div class="rec-body">
            <h3>{{ item.title }}</h3>
            <p>{{ item.summary || '暂无摘要' }}</p>
            <div class="rec-meta">
              <span><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
              <span><el-icon><ChatDotSquare /></el-icon> {{ item.commentCount || 0 }}</span>
              <span class="rec-time">{{ item.createdTime?.substring(0, 10) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleApi } from '../api'
import Header from '../components/Header.vue'

// 存储文章列表数据
const articles = ref([])
// 页面加载状态
const loading = ref(false)

// 加载已发布的文章，按阅读量从高到低排序，作为推荐列表
async function loadData() {
  loading.value = true
  try {
    const res = await articleApi.list({ page: 1, pageSize: 8, status: 1 })
    const list = (res.data.records || [])
    list.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    articles.value = list
  } finally { loading.value = false }
}

// 页面挂载时加载数据
onMounted(loadData)
</script>

<style scoped>
.page-title { font-size: 24px; color: #e0e0e0; margin-bottom: 20px; padding-left: 10px; border-left: 3px solid #409eff; }
.loading-wrap { padding: 40px; }
.recommend-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
.rec-card { padding: 0; overflow: hidden; border-radius: 12px; cursor: pointer; transition: transform .2s; }
.rec-card:hover { transform: translateY(-2px); }
.rec-cover { height: 200px; overflow: hidden; position: relative; }
.rec-cover img { width: 100%; height: 100%; object-fit: cover; }
.rec-cover-ph { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: rgba(255,255,255,.04); color: #555; }
.rec-badge { position: absolute; top: 12px; left: 12px; background: rgba(64,158,255,.8); color: #fff; font-size: 12px; padding: 4px 10px; border-radius: 4px; display: flex; align-items: center; gap: 4px; }
.rec-body { padding: 16px 20px; }
.rec-body h3 { font-size: 17px; color: #e0e0e0; margin-bottom: 6px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.rec-body p { font-size: 13px; color: #999; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.rec-meta { display: flex; align-items: center; gap: 14px; font-size: 13px; color: #888; margin-top: 10px; }
.rec-meta .el-icon { vertical-align: middle; margin-right: 2px; }
.rec-time { margin-left: auto; }
</style>
