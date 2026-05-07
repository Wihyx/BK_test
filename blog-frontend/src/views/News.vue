<template>
  <div>
    <Header />
    <div class="container page-main">
      <h2>订阅资讯</h2>
      <div v-if="loading"><el-skeleton :rows="5" animated /></div>
      <div v-else-if="feedArticles.length === 0" style="text-align:center;padding:60px;color:#888">暂无订阅资讯</div>
      <div v-else class="news-grid">
        <div v-for="item in pagedArticles" :key="item.id" class="news-item">
          <img v-if="item.image" :src="item.image" class="news-img" />
          <div class="news-body">
            <span class="news-source">{{ item.feedName }}</span>
            <a :href="item.link" target="_blank">{{ item.title }}</a>
            <span class="news-time">{{ item.pubDate?.substring(0, 16) }}</span>
          </div>
        </div>
      </div>
      <div v-if="total > pageSize" class="pagination-wrap">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { feedApi } from '../api'
import Header from '../components/Header.vue'

const feedArticles = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(30)

const total = computed(() => feedArticles.value.length)
const pagedArticles = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return feedArticles.value.slice(start, start + pageSize.value)
})

async function loadData() {
  loading.value = true
  try {
    const res = await feedApi.articles()
    feedArticles.value = res.data || []
  } finally { loading.value = false }
}

onMounted(loadData)
</script>

<style>
h2 { font-size: 24px; color: #e0e0e0; margin-bottom: 20px; padding-left: 10px; border-left: 3px solid #67c23a; }
.news-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; max-width: 71.4%; margin: 0 auto; }
.news-item { display: flex; flex-direction: column; overflow: hidden; background: rgba(80,80,80,0.45); border: 1px solid rgba(255,255,255,.06); border-radius: 10px; }
.news-img { width: 100%; height: 120px; object-fit: cover; }
.news-body { display: flex; flex-direction: column; gap: 4px; padding: 12px 14px; }
.news-item a { color: #409eff; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.news-source { color: #67c23a; font-size: 12px; }
.news-time { color: #ccc; font-size: 12px; margin-top: auto; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 30px; padding-bottom: 60px; }

body.light .news-item { background: rgba(255,255,255,.7); border: 1px solid rgba(0,0,0,.06); }
body.light h2 { color: #222; }
body.light .news-time { color: #666; }
</style>
