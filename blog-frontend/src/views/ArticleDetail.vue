<template>
  <div>
    <!-- 引入顶部导航栏 -->
    <Header />
    <div class="container page-main">
      <!-- 加载中状态：显示骨架屏 -->
      <div v-if="loading" class="loading-wrap">
        <el-skeleton :rows="10" animated />
      </div>
      <!-- 文章详情区域（加载完成且有文章数据时显示） -->
      <template v-else-if="article">
        <!-- 文章内容卡片 -->
        <el-card class="detail-card">
          <!-- 面包屑导航 -->
          <div class="breadcrumb">
            <router-link to="/">首页</router-link> / {{ article.title }}
          </div>
          <!-- 文章标题 -->
          <h1 class="title">{{ article.title }}</h1>
          <!-- 文章元信息：发布时间、阅读量、分类 -->
          <div class="meta">
            <span><el-icon><Calendar /></el-icon> {{ article.createdTime?.substring(0, 10) }}</span>
            <span><el-icon><View /></el-icon> {{ article.viewCount || 0 }}</span>
            <span v-if="categories.find(c => c.id === article.categoryId)"><el-icon><CollectionTag /></el-icon> {{ categories.find(c => c.id === article.categoryId)?.name }}</span>
          </div>
          <!-- 文章正文，将Markdown内容渲染成HTML -->
          <div class="content" v-html="renderedContent"></div>
        </el-card>

        <!-- 评论区 -->
        <el-card class="comment-section">
          <template #header>
            <span><el-icon><ChatDotSquare /></el-icon> 评论 ({{ comments.length }})</span>
          </template>
          <!-- 评论发表表单 -->
          <div class="comment-form">
            <el-input v-model="commentForm.nickname" placeholder="昵称 *" style="width:200px;margin-bottom:12px" />
            <el-input v-model="commentForm.email" placeholder="邮箱（选填）" style="width:280px;margin-bottom:12px" />
            <el-input v-model="commentForm.content" type="textarea" :rows="4" placeholder="写下你的评论... *" />
            <!-- 昵称和内容不能为空才可提交 -->
            <el-button type="primary" class="submit-btn" :disabled="!commentForm.nickname || !commentForm.content" @click="submitComment">发表评论</el-button>
          </div>
          <!-- 暂无评论提示 -->
          <div v-if="comments.length === 0" style="text-align:center;color:#999;padding:20px">暂无评论</div>
          <!-- 评论列表，每条显示头像、昵称、时间和内容 -->
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div class="comment-header">
              <el-avatar :size="32">{{ c.nickname?.[0] }}</el-avatar>
              <div>
                <strong>{{ c.nickname }}</strong>
                <span class="comment-time">{{ c.createdTime?.substring(0, 16) }}</span>
              </div>
            </div>
            <p class="comment-content">{{ c.content }}</p>
          </div>
        </el-card>
      </template>
      <!-- 文章不存在或加载失败时的空状态 -->
      <el-card v-else class="empty-card">
        <el-empty description="文章不存在或已删除">
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        </el-empty>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { marked } from 'marked'
import { articleApi, commentApi, categoryApi } from '../api'
import Header from '../components/Header.vue'

// 当前路由对象，用于获取URL中的文章ID
const route = useRoute()
// 存储当前文章的数据对象
const article = ref(null)
// 存储评论列表
const comments = ref([])
// 存储所有分类，用于显示文章所属分类名
const categories = ref([])
// 页面加载状态
const loading = ref(true)
// 评论表单数据（昵称、邮箱、内容）
const commentForm = ref({ nickname: '', email: '', content: '' })

// 将文章Markdown内容转换为HTML，breaks: true 表示换行也转为<br>
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return marked(article.value.content, { breaks: true })
})

// 根据路由中的文章ID加载文章详情
async function loadDetail() {
  loading.value = true
  try {
    const res = await articleApi.detail(route.params.id)
    article.value = res.data
  } catch {
    article.value = null
  } finally {
    loading.value = false
  }
}

// 加载当前文章的所有评论
async function loadComments() {
  const res = await commentApi.list({ articleId: route.params.id })
  comments.value = res.data?.records || []
}

// 提交评论，成功后清空输入框并刷新评论列表
async function submitComment() {
  if (!commentForm.value.nickname || !commentForm.value.content) return
  await commentApi.save({ ...commentForm.value, articleId: route.params.id })
  commentForm.value.content = ''
  loadComments()
}

// 页面挂载时并行加载文章详情、评论和分类数据
onMounted(() => {
  loadDetail()
  loadComments()
  categoryApi.list().then(res => categories.value = res.data || [])
})
</script>

<style scoped>
.loading-wrap { padding: 40px; }
.empty-card :deep(.el-card) { background: rgba(0,0,0,.36); border-color: rgba(255,255,255,.08); }
.detail-card { margin-bottom: 20px; }
.detail-card :deep(.el-card__body) { background: rgba(0,0,0,.36); border: 1px solid rgba(255,255,255,.08); border-radius: 12px; backdrop-filter: blur(12px); padding: 32px; }
.comment-section :deep(.el-card__body) { background: rgba(0,0,0,.36); border: 1px solid rgba(255,255,255,.08); border-radius: 12px; backdrop-filter: blur(12px); }
.breadcrumb { font-size: 13px; color: #888; margin-bottom: 16px; }
.breadcrumb a { color: #409eff; }
.title { font-size: 28px; margin-bottom: 16px; color: #e0e0e0; }
.meta { color: #888; font-size: 14px; margin-bottom: 24px; display: flex; gap: 20px; flex-wrap: wrap; }
.meta .el-icon { vertical-align: middle; margin-right: 4px; }
.content { line-height: 1.8; font-size: 16px; color: #ccc; }
.content :deep(h1), .content :deep(h2), .content :deep(h3) { margin: 24px 0 12px; color: #e0e0e0; }
.content :deep(p) { margin-bottom: 16px; }
.content :deep(img) { max-width: 100%; border-radius: 4px; margin: 16px 0; }
.content :deep(pre) { background: rgba(0,0,0,.3); padding: 16px; border-radius: 6px; overflow-x: auto; margin: 16px 0; border: 1px solid rgba(255,255,255,.06); }
.content :deep(code) { background: rgba(255,255,255,.08); padding: 2px 6px; border-radius: 3px; font-size: 14px; color: #e0e0e0; }
.content :deep(pre code) { background: none; padding: 0; }
.content :deep(blockquote) { border-left: 4px solid #409eff; padding-left: 16px; color: #aaa; margin: 16px 0; }
.content :deep(table) { width: 100%; border-collapse: collapse; margin: 16px 0; }
.content :deep(th), .content :deep(td) { border: 1px solid rgba(255,255,255,.1); padding: 8px 12px; text-align: left; color: #ccc; }
.content :deep(th) { background: rgba(255,255,255,.06); }
.comment-section { margin-bottom: 20px; }
.comment-form { margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid rgba(255,255,255,.08); }
.comment-form .el-input { margin-right: 12px; }
.submit-btn { margin-top: 8px; }
.comment-item { padding: 16px 0; border-bottom: 1px solid rgba(255,255,255,.06); }
.comment-item:last-child { border-bottom: none; }
.comment-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; color: #ccc; }
.comment-time { color: #777; font-size: 12px; margin-left: 8px; }
.comment-content { color: #bbb; font-size: 15px; line-height: 1.6; padding-left: 42px; }
</style>
