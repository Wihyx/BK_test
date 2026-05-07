<template>
  <div>
    <Header />
    <div class="container page-main">
      <h2 class="page-title">搜索：{{ keyword }}</h2>
      <div v-if="loading"><el-skeleton :rows="5" animated /></div>
      <div v-else-if="articles.length === 0 && feedResults.length === 0" style="text-align:center;padding:80px;color:#888">
        <el-empty description="未找到相关内容" />
        <p style="color:#999;margin-top:12px;font-size:13px">试试其他关键词，支持搜索文章标题和订阅资讯</p>
      </div>
      <div v-else>
        <!-- 本站文章结果 -->
        <div v-if="articles.length > 0">
          <h3 class="section-title">本站文章 ({{ total }})</h3>
          <div class="search-results">
            <div v-for="item in articles" :key="'a'+item.id" class="glass-card result-item" @click="$router.push(`/article/${item.id}`)">
              <div class="result-cover" v-if="item.coverImage"><img :src="item.coverImage" /></div>
              <div class="result-body">
                <h3>{{ item.title }}</h3>
                <p>{{ item.summary || '暂无摘要' }}</p>
                <div class="result-meta">
                  <span><el-icon><View /></el-icon> {{ item.viewCount || 0 }}</span>
                  <span><el-icon><ChatDotSquare /></el-icon> {{ item.commentCount || 0 }}</span>
                  <span class="result-time">{{ item.createdTime?.substring(0, 10) }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="total > pageSize" class="pagination-wrap">
            <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadData" />
          </div>
        </div>

        <!-- 订阅资讯结果 -->
        <div v-if="feedResults.length > 0" style="margin-top:30px">
          <h3 class="section-title feed-title">订阅资讯 ({{ feedResults.length }})</h3>
          <div class="search-results">
            <div v-for="item in feedResults" :key="'f'+item.id" class="glass-card result-item">
              <div class="result-body">
                <h3><a :href="item.link" target="_blank">{{ item.title }}</a></h3>
                <p>{{ item.summary || '' }}</p>
                <div class="result-meta">
                  <span class="feed-source">{{ item.feedName }}</span>
                  <span class="result-time">{{ item.pubDate?.substring(0, 16) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi, feedApi } from '../api'
import Header from '../components/Header.vue'

const route = useRoute()
const keyword = ref(route.query.q || '')
const articles = ref([])
const feedResults = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

async function loadData() {
  if (!keyword.value) return
  loading.value = true
  try {
    // 同时搜索文章和订阅资讯
    const [artRes, feedRes] = await Promise.all([
      articleApi.list({ page: page.value, pageSize: pageSize.value, status: 1, keyword: keyword.value }),
      feedApi.articles()
    ])
    articles.value = artRes.data.records || []
    total.value = artRes.data.total || 0
    // 在客户端对订阅资讯做本地过滤（后端暂不支持keyword搜索）
    const kw = keyword.value.toLowerCase()
    feedResults.value = (feedRes.data || []).filter(item => 
      (item.title && item.title.toLowerCase().includes(kw)) ||
      (item.summary && item.summary.toLowerCase().includes(kw))
    )
  } finally { loading.value = false }
}

onMounted(loadData)
</script>

<style>
.page-title { font-size: 24px; color: #e0e0e0; margin-bottom: 24px; padding-left: 10px; border-left: 3px solid #409eff; }
.section-title { font-size: 16px; color: #e0e0e0; margin-bottom: 12px; padding-left: 10px; border-left: 3px solid #409eff; }
.feed-title { border-left-color: #67c23a; }
.search-results { display: flex; flex-direction: column; gap: 12px; }
.result-item { display: flex; gap: 20px; padding: 20px; cursor: pointer; transition: transform .2s; }
.result-item:hover { transform: translateY(-2px); }
.result-cover { width: 180px; height: 120px; flex-shrink: 0; overflow: hidden; border-radius: 8px; }
.result-cover img { width: 100%; height: 100%; object-fit: cover; }
.result-body { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.result-body h3 { font-size: 17px; color: #e0e0e0; margin-bottom: 6px; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.result-body h3 a { color: #409eff; }
.result-body p { font-size: 14px; color: #ccc; line-height: 1.6; flex: 1; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.result-meta { display: flex; align-items: center; gap: 16px; font-size: 13px; color: #aaa; margin-top: 8px; }
.result-meta .el-icon { vertical-align: middle; }
.result-time { margin-left: auto; }
.feed-source { color: #67c23a; }
.pagination-wrap { text-align: center; margin-top: 30px; }

body.light .result-body h3 { color: #222; }
body.light .result-body p { color: #555; }
body.light .result-meta { color: #888; }
body.light .section-title { color: #222; }
body.light .page-title { color: #222; }
</style>
