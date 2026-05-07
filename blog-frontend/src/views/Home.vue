<!-- ==================== HTML 模板部分 ==================== -->
<!-- 整个页面的根元素，包含首页所有内容 -->
<template>
  <!-- 首页最外层容器，设置最小高度为全屏并控制层级 -->
  <div class="home-page">
    <!-- 页头组件：显示网站logo、搜索框等公共头部内容 -->
    <Header />
    <!-- 首页的主体网格布局，分为左/中/右三栏 -->
    <div class="home-grid">
      <!-- ========== 左栏：用户个人信息卡片 + 侧边导航菜单 ========== -->
      <aside class="col col-left">
        <!-- 毛玻璃效果卡片，展示博主信息和导航 -->
        <div class="glass-card user-card">
          <!-- 点击头像跳转到"关于我"页面 -->
          <router-link to="/about">
            <!-- el-avatar 头像组件：显示博主头像图片，如果没有图片则显示名字首字母，默认显示"U" -->
            <el-avatar :size="72" :src="profile.avatar" class="user-avatar">{{ profile.name?.[0] || 'U' }}</el-avatar>
          </router-link>
          <!-- 显示博主昵称，如果后端没返回数据则显示默认文字"博主" -->
          <h2 class="user-name">{{ profile.name || '博主' }}</h2>
          <!-- 显示博主个人简介（个性签名），兜底文字"用文字记录思考" -->
          <p class="user-bio">{{ profile.bio || '用文字记录思考' }}</p>
          <!-- 统计区域：展示文章总数、评论总数、标签数量 -->
          <div class="user-stats">
            <div class="stat"><strong>{{ total }}</strong><span>文章</span></div>
            <div class="stat"><strong>{{ commentTotal }}</strong><span>评论</span></div>
            <div class="stat"><strong>0</strong><span>标签</span></div>
          </div>
          <!-- 分割线，视觉上分隔个人信息区和导航区 -->
          <el-divider style="border-color:rgba(255,255,255,.12)" />
          <!-- 侧边导航菜单，点击可跳转到不同页面 -->
          <nav class="side-nav">
            <!-- 首页链接：通过 route.path === '/' 来判断当前是否在首页，是则高亮 -->
            <router-link to="/" class="nav-item" :class="{ active: route.path === '/' }"><el-icon><HomeFilled /></el-icon> 首页</router-link>
            <router-link to="/recommend" class="nav-item" :class="{ active: route.path === '/recommend' }"><el-icon><Star /></el-icon> 推荐</router-link>
            <a href="https://www.xygalaxy.com/" target="_blank" class="nav-item"><el-icon><Guide /></el-icon> 导航</a>
            <router-link to="/news" class="nav-item" :class="{ active: route.path === '/news' }"><el-icon><Notebook /></el-icon> 资讯</router-link>
            <!-- 管理后台链接：没有任何高亮逻辑，始终显示普通样式 -->
            <router-link to="/admin/dashboard" class="nav-item"><el-icon><Setting /></el-icon> 管理</router-link>
          </nav>
        </div>
      </aside>

      <!-- ========== 中栏：主要内容区（英雄标题 + 文章卡片列表） ========== -->
      <div class="col col-content">

        <!-- 顶部英雄区域：显示博客标题和随机名言/语录 -->
        <div class="hero-section">
          <!-- 博客主标题 -->
          <h1 class="hero-title">混水摸愉~</h1>
          <!-- 动态显示一句随机名言（双引号包裹，斜体展示） -->
          <p class="hero-quote">"{{ quoteText }}"</p>
        </div>

        <!-- 文章卡片网格：遍历 cardList 数组，把每篇文章渲染成一张卡片 -->
        <div class="articles-grid">
          <div
            v-for="(item, index) in cardList"
            :key="item.id"
            :class="['card-item', item.cardType === 1 ? 'card-large' : 'card-small']"
            @click="$router.push(`/article/${item.id}`)"
          >
            <!-- 卡片封面图片区域 -->
            <div class="card-cover">
              <!-- 如果有封面图就显示图片，否则显示一个灰色占位图标 -->
              <img v-if="item.coverImage" :src="item.coverImage" />
              <div v-else class="card-cover-ph">
                <el-icon :size="24"><Document /></el-icon>
              </div>
            </div>
            <!-- 卡片正文区域 -->
            <div class="card-body">
              <!-- 文章标题 -->
              <h3>{{ item.title }}</h3>
              <!-- 文章摘要，没有则显示"暂无摘要" -->
              <p>{{ item.summary || '暂无摘要' }}</p>
              <!-- 卡片底部元信息行：浏览量、评论数、点赞数、发布日期 -->
              <div class="card-meta">
                <span class="card-stats">
                  <!-- 浏览量 -->
                  <el-icon><View /></el-icon> {{ item.viewCount || 0 }}
                  <!-- 评论数，左边多加12px间距与浏览量区分 -->
                  <el-icon style="margin-left:12px"><ChatDotSquare /></el-icon> {{ item.commentCount || 0 }}
                  <!-- 点赞图标：已点过赞显示实心星(StarFilled)，没点过显示空心星(Star)。@click.stop 阻止事件冒泡防止触发卡片跳转 -->
                  <el-icon style="margin-left:10px;cursor:pointer" @click.stop="toggleLike(item)"><component :is="item.liked ? 'StarFilled' : 'Star'" /></el-icon> <span style="font-size:12px;color:#999">{{ item.likeCount || 0 }}</span>
                </span>
                <!-- 文章发布日期，截取前10位即 YYYY-MM-DD 格式 -->
                <span class="card-time">{{ item.createdTime?.substring(0, 10) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== 右栏：最新文章列表 + 热门资讯列表 ========== -->
      <aside class="col col-right">
        <!-- 第一张右侧卡片：最新文章 -->
        <div class="glass-card right-card">
          <h3 class="right-title">最新文章</h3>
          <!-- 最新文章列表：显示前6篇文章（来自latestList计算属性） -->
          <div class="latest-list">
            <div
              v-for="(item, idx) in latestList"
              :key="item.id"
              class="latest-item"
              @click="$router.push(`/article/${item.id}`)"
            >
              <!-- 序号圆圈，idx从0开始，所以+1后显示1~6 -->
              <div class="latest-num">{{ idx + 1 }}</div>
              <div class="latest-info">
                <!-- 文章标题 -->
                <span class="latest-title">{{ item.title }}</span>
                <!-- 元信息行：浏览量、评论数、发布日期 -->
                <div class="latest-meta">
                  <span><el-icon><View /></el-icon>{{ item.viewCount || 0 }}</span>
                  <span><el-icon><ChatDotSquare /></el-icon>{{ item.commentCount || 0 }}</span>
                  <!-- 发布时间，截取日期部分 -->
                  <span class="latest-time">{{ item.createdTime?.substring(0, 10) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 第二张右侧卡片：热门资讯（来自RSS订阅源） -->
        <div class="glass-card right-card">
          <h3 class="right-title">热门资讯</h3>
          <!-- 订阅源分类标签：点击某个标签可以筛选该来源的资讯，再次点击取消筛选 -->
          <div class="feed-cats">
            <span
              v-for="cat in feedCats"
              :key="cat"
              class="feed-cat-tag"
              :class="{ active: cat === selectedCat }"
              @click="selectedCat = (selectedCat === cat ? '' : cat)"
            >{{ cat }}</span>
          </div>
          <!-- 热门资讯列表：展示筛选后的前6条资讯 -->
          <div class="latest-list">
            <div
              v-for="(item, idx) in filteredFeedList"
              :key="'hf' + item.id"
              class="latest-item"
            >
              <!-- 热门序号使用红色样式(hot-num) -->
              <div class="latest-num hot-num">{{ idx + 1 }}</div>
              <div class="latest-info">
                <!-- 资讯标题是一个可点击的外链，点击在新标签页打开原文 -->
                <a :href="item.link" target="_blank" class="latest-title">{{ item.title }}</a>
                <div class="latest-meta">
                  <!-- 来源标签：显示该资讯来自哪个订阅源（如"掘金""知乎"等），绿色字体 -->
                  <span class="feed-source-tag">{{ item.feedName }}</span>
                  <!-- 资讯发布日期，截取前16位即 YYYY-MM-DD HH:mm 格式 -->
                  <span class="latest-time">{{ item.pubDate?.substring(0, 16) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<!-- ==================== 脚本逻辑部分 ==================== -->
<!-- <script setup> 是Vue3的语法糖写法，写在这里的变量/函数会自动暴露给模板使用 -->
<script setup>
// 从 vue 核心库导入三个功能：
// ref: 创建一个能自动更新页面的"响应式变量"（类似一个带 .value 的盒子）
// computed: 创建一个"自动计算的值"，它依赖的变量变了它就会自动重新算
// onMounted / onActivated: 两个"生命周期钩子"——在页面挂载/重新激活时执行代码
import { ref, onMounted, computed, onActivated } from 'vue'
import { useRoute } from 'vue-router'
// 从 api 模块导入四个接口调用对象，分别负责文章、名言、个人信息、RSS资讯的后端请求
import { articleApi, quoteApi, profileApi, feedApi } from '../api'
// 导入页头组件 Header.vue
import Header from '../components/Header.vue'

// 获取当前路由对象，用于判断当前页面路径（比如高亮侧边栏选中项）
const route = useRoute()

// ---------- 响应式数据（页面展示的数据都存这里） ----------

// 文章列表数组：存储从后端获取的所有文章数据，初始为空数组
const articles = ref([])
// RSS订阅资讯数组：存储从后端获取的所有外部订阅源文章
const feedArticles = ref([])
// 当前选中的订阅源分类名：用于右侧热门资讯的筛选，空字符串表示不筛选（显示全部）
const selectedCat = ref('')
// 文章总数：显示在左侧用户卡片中
const total = ref(0)
// 随机名言文本：显示在页面标题下方
const quoteText = ref('')
// 博主个人信息对象：包含 name(昵称)、avatar(头像URL)、bio(个人简介)
const profile = ref({ name: '', avatar: '', bio: '' })


// ---------- 计算属性（根据已有数据自动算出来的值，不能手动改） ----------

// cardList: 文章卡片列表，直接返回 articles 的值。看起来多余，但保留它是为了将来可能做筛选/排序时只需改这里
const cardList = computed(() => articles.value)
// latestList: 最新文章列表，取 articles 的前6条显示在右侧"最新文章"栏
const latestList = computed(() => articles.value.slice(0, 6))
// hotFeedList: 热门资讯列表，取 feedArticles 的前6条作为默认展示
const hotFeedList = computed(() => feedArticles.value.slice(0, 6))
// filteredFeedList: 按订阅源分类筛选后的资讯列表。如果 selectedCat 为空（没有选中分类）就直接返回 hotFeedList；否则只保留 feedName 匹配选中分类的资讯，同样最多取6条
const filteredFeedList = computed(() => {
  if (!selectedCat.value) return hotFeedList.value
  return feedArticles.value.filter(f => f.feedName === selectedCat.value).slice(0, 6)
})
// feedCats: 从所有订阅资讯中提取所有不重复的订阅源名称（feedName），做成标签按钮。[...new Set(xxx)] 是JS去重技巧
const feedCats = computed(() => [...new Set(feedArticles.value.map(f => f.feedName))])
// commentTotal: 评论总数，遍历所有文章把每篇的 commentCount 累加起来，没有评论数则按0算
const commentTotal = computed(() => articles.value.reduce((sum, a) => sum + (a.commentCount || 0), 0))


// ---------- 方法（页面中可被调用的函数） ----------

// loadData: 页面初始化时调用的数据加载函数，负责从后端拉取所有需要的数据
async function loadData() {
  // 第一块 try-catch：获取文章列表数据
  try {
  // 调用文章API的list接口，如果URL带有关键词参数则传入
    const res = await articleApi.list({ page: 1, pageSize: 12, status: 1 })
    // 处理返回数据：遍历 records 确保每篇文章的 coverImage 字段存在（为null也ok，避免undefined导致模板报错）
    articles.value = (res.data.records || []).map(a => ({
      ...a,
      coverImage: a.coverImage || null
    }))
    // 保存文章总数，用于左侧统计卡片展示
    total.value = res.data.total || 0
  } catch {}
  // 第二块 try-catch：获取一句随机名言/语录
  try {
    const q = await quoteApi.random()
    // 把名言存到 quoteText，如果接口没返回数据则使用默认英文名言
    quoteText.value = q.data || 'Stay hungry, stay foolish.'
  } catch {}
  // 第三块 try-catch：获取博主个人信息
  try {
    const p = await profileApi.get()
    // 如果后端有返回数据就用后端的，否则保持初始的空对象（模板里有兜底默认值）
    if (p.data) profile.value = p.data
  } catch {}
  // 第四块 try-catch：获取RSS订阅资讯数据
  try {
    const fres = await feedApi.articles()
    // 把订阅源的文章列表存到 feedArticles
    feedArticles.value = fres.data || []
  } catch {}
}

// toggleLike: 点赞/取消点赞切换函数，接收一篇文章对象 item
function toggleLike(item) {
  // 调用文章API的like接口，向服务器发送点赞请求
  articleApi.like(item.id).then(res => {
    // 如果服务器返回了新的点赞数就用新值，否则在现有基础上+1（兜底）
    item.likeCount = res.data || (item.likeCount || 0) + 1
    // 标记该文章为"已点赞"状态，前端图标会从空心星变为实心星
    item.liked = true
  }).catch(() => {})  // 点赞失败时静默处理，不打扰用户
}

// onMounted: 组件第一次挂载到页面上时执行 loadData 加载数据
onMounted(loadData)
// onActivated: 当组件从"被缓存"状态重新激活时（比如从文章详情页返回首页），重新加载数据确保最新
onActivated(() => { loadData() })
</script>

<!-- ==================== 样式部分（scoped 表示这些样式只作用于当前组件，不会污染其他页面） ==================== -->
<style scoped>
/* 首页根容器：最小高度撑满整个屏幕，设置层级防止被背景遮挡 */
.home-page { min-height: 100vh; position: relative; z-index: 1; }

/* 三栏网格布局：左250px、中自适应、右290px，间距28px，最大宽度1400px居中 */
.home-grid {
  display: grid;
  grid-template-columns: 250px 1fr 290px;
  gap: 28px;
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
  align-items: stretch;
}

/* ---- 左栏样式：用户卡片 + 导航 ---- */
/* 左栏设为粘性定位，顶部留76px（给固定头部让位），滚动时左栏会"粘"在屏幕上 */
.col-left { grid-column: 1; position: sticky; top: 76px; align-self: start; }
/* 左栏卡片高度撑满可视区域减去头部，用flex纵向排列 */
.col-left .glass-card { min-height: calc(100vh - 116px); display: flex; flex-direction: column; }
/* 毛玻璃卡片通用样式：半透明黑色背景+模糊+细边框+圆角 */
.glass-card {
  background: rgba(0, 0, 0, 0.36);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,.08);
  border-radius: 12px;
  padding: 24px;
  color: #e0e0e0;
}
/* 用户信息卡片内容居中 */
.user-card { text-align: center; }
/* 用户头像：加白色半透明边框，与下方内容留12px间距，居中显示 */
.user-avatar { border: 3px solid rgba(255,255,255,.2); margin-bottom: 12px; display: block; margin-left: auto; margin-right: auto; }
.user-name { font-size: 20px; font-weight: 600; margin-bottom: 6px; }
.user-bio { font-size: 13px; color: #aaa; margin-bottom: 16px; line-height: 1.5; }
/* 统计数字区域：三个数字横向居中排列 */
.user-stats { display: flex; justify-content: center; gap: 24px; margin-bottom: 4px; }
.stat { text-align: center; }
.stat strong { display: block; font-size: 18px; color: #409eff; }  /* 统计数字用蓝色加粗 */
.stat span { font-size: 11px; color: #999; }
/* 导航菜单：纵向排列，每项间距2px */
.side-nav { display: flex; flex-direction: column; gap: 2px; }
/* 每个导航项：横向排列图标和文字，内边距15-16px，圆角8px */
.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px 16px;
  border-radius: 8px;
  color: #bbb;
  font-size: 15px;
  transition: all .2s;  /* 所有变化加0.2秒过渡动画 */
}
/* 鼠标悬停时：背景变亮、文字变白 */
.nav-item:hover { background: rgba(255,255,255,.08); color: #fff; }
/* 当前激活的导航项：蓝色半透明背景、蓝色文字 */
.nav-item.active { background: rgba(64,158,255,.15); color: #409eff; }

/* ---- 中栏主内容区样式 ---- */
.col-content { grid-column: 2; }

/* 顶部英雄区域：博客标题和名言 */
.hero-section {
  text-align: center;
  padding: 40px 0 20px;
}
/* 博客大标题：白色、阴影增加立体感、字间距2px */
.hero-title {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 12px rgba(0,0,0,.4);
  letter-spacing: 2px;
}
/* 名言引用文字：半透明白色、斜体 */
.hero-quote {
  margin-top: 12px;
  font-size: 15px;
  color: rgba(255,255,255,.7);
  font-style: italic;
}

/* ---- 文章卡片网格 ---- */
/* 卡片列表采用纵向弹性布局，每张卡片间距20px */
.articles-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 10px;
}

/* 每张卡片的通用样式：手型光标、圆角、溢出隐藏、鼠标悬浮上移2px出现阴影 */
.card-item {
  cursor: pointer;
  border-radius: 10px;
  overflow: hidden;
  transition: transform .2s, box-shadow .2s;
  background: rgba(255,255,255,.82);
  font-family: 'Microsoft YaHei', '微软雅黑', sans-serif;
  color: #222;
  width: 100%;
}
/* 鼠标悬浮卡片时：向上浮2px + 加深阴影 */
.card-item:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,.25); }

/* 大卡片：纵向排列（封面图在上，文字在下） */
.card-large {
  display: flex;
  flex-direction: column;
}
/* 大卡片的封面图区域：宽100%，高度215px */
.card-large .card-cover {
  width: 100%;
  height: 215px;
  overflow: hidden;
}
/* 封面图片填满容器，保持比例裁剪 */
.card-large .card-cover img {
  width: 100%; height: 100%; object-fit: cover;
}
/* 无封面图时的灰色占位区域 */
.card-large .card-cover-ph {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  background: #f0f0f0; color: #ccc;
}
/* 大卡片正文区域 */
.card-large .card-body { padding: 18px 24px; }
/* 大卡片标题：最多显示1行，超出显示省略号（-webkit-line-clamp是老式截断写法，但主流浏览器都支持） */
.card-large .card-body h3 { font-size: 18px; margin-bottom: 6px; color: #222; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
/* 大卡片摘要：最多显示2行，超出省略 */
.card-large .card-body p { font-size: 14px; color: #666; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 8px; }

/* 小卡片：横向排列（封面图在左，文字在右） */
.card-small {
  display: flex;
  flex-direction: row;
  height: 135px;
}
/* 小卡片封面图：宽度占1/5，固定135px高度，不允许收缩 */
.card-small .card-cover {
  width: calc(100% / 5);
  height: 135px;
  flex-shrink: 0;
  overflow: hidden;
}
.card-small .card-cover img {
  width: 100%; height: 100%; object-fit: cover;
}
.card-small .card-cover-ph {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  background: #f0f0f0; color: #ccc;
}
/* 小卡片正文：占剩余空间、纵向排列、不允许内容撑开宽度(min-width:0) */
.card-small .card-body {
  flex: 1;
  padding: 10px 16px;
  display: flex; flex-direction: column;
  min-width: 0;
}
.card-small .card-body h3 { font-size: 15px; margin-bottom: 4px; color: #222; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.card-small .card-body p { font-size: 13px; color: #888; display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }

/* 卡片底部元信息行：左侧数据统计，右侧时间，弹性布局两端对齐 */
.card-meta { display: flex; justify-content: space-between; align-items: center; margin-top: auto; }
/* 卡片统计区（浏览/评论/点赞图标+数字） */
.card-stats { display: flex; align-items: center; gap: 2px; font-size: 12px; color: #999; }
.card-stats .el-icon { font-size: 14px; }
/* 发布日期 */
.card-time { font-size: 11px; color: #aaa; }

/* ---- 右栏样式 ---- */
/* 右栏粘性定位，高度撑满可视区域减去头部，纵向弹性排列 */
.col-right { grid-column: 3; position: sticky; top: 76px; align-self: start; display: flex; flex-direction: column; height: calc(100vh - 116px); }
/* 右侧卡片：内容溢出时隐藏，纵向弹性排列 */
.right-card { padding: 20px; display: flex; flex-direction: column; overflow: hidden; }
/* 两张右侧卡片各占一半高度 */
.right-card:first-child { flex: 1; }
.right-card:last-child { flex: 1; }
.right-card + .right-card { margin-top: 20px; }
/* 文章列表区域：超出部分可以滚动 */
.right-card .latest-list { flex: 1; overflow-y: auto; }
/* 右侧卡片标题：底部加一条细分割线 */
.right-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,.08); color: #e0e0e0; }

/* 最新/热门文章列表 */
.latest-list { display: flex; flex-direction: column; gap: 2px; }
/* 每条列表项：横向排列，手型光标，悬停有背景色变化 */
.latest-item {
  display: flex;
  gap: 10px;
  padding: 10px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background .2s;
  align-items: flex-start;
}
.latest-item:hover { background: rgba(255,255,255,.06); }
/* 序号圆圈：24x24的蓝色方块，数字居中 */
.latest-num {
  width: 24px; height: 24px;
  border-radius: 6px;
  background: rgba(64,158,255,.15);
  color: #409eff;
  font-size: 12px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
/* 热门资讯的序号用红色样式 */
.hot-num { background: rgba(245,108,108,.15); color: #f56c6c; }
/* 订阅来源标签：绿色小字 */
.feed-source-tag { font-size: 10px; color: #67c23a; }
/* 分类标签容器：弹性换行排列 */
.feed-cats { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 12px; }
/* 单个分类标签：绿色半透明背景，手型光标 */
.feed-cat-tag {
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 4px;
  background: rgba(103,194,58,.1);
  color: #67c23a;
  cursor: pointer;
  transition: all .2s;
}
.feed-cat-tag:hover { background: rgba(103,194,58,.25); }
/* 选中状态的标签：实心绿色背景、白色文字 */
.feed-cat-tag.active { background: #67c23a; color: #fff; }
/* 列表信息区域：占剩余空间，不允许撑开 */
.latest-info { flex: 1; min-width: 0; }
/* 文章标题：单行显示，超出用省略号 */
.latest-title { display: block; font-size: 13px; color: #ccc; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 3px; }
/* 如果标题是a标签（外链），用蓝色显示 */
a.latest-title { color: #409eff; }
a.latest-title:hover { text-decoration: underline; }
/* 元信息行：图标和数字横向排列 */
.latest-meta { display: flex; align-items: center; gap: 8px; font-size: 11px; color: #ccc; }
.latest-meta .el-icon { font-size: 12px; vertical-align: middle; }
/* 发布时间：靠右对齐 */
.latest-time { color: #eee; margin-left: auto; }

/* 响应式布局：屏幕宽度小于900px时，三栏变为单栏堆叠 */
@media (max-width: 900px) {
  .home-grid { grid-template-columns: 1fr; }  /* 只显示一列 */
  .col-left, .col-content, .col-right { grid-column: 1; }  /* 所有列都放到第一列位置 */
  .card-large { flex: 0 0 100%; }
  .card-small { flex: 0 0 calc(50% - 5px); }  /* 小卡片每行显示两个 */
}
</style>
