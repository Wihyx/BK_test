import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // ===== 前台路由 =====
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },                    // 首页
  { path: '/article/:id', name: 'ArticleDetail', component: () => import('../views/ArticleDetail.vue') },// 文章详情
  { path: '/category/:id', name: 'CategoryList', component: () => import('../views/ArticleList.vue') }, // 分类文章列表
  { path: '/tag/:id', name: 'TagList', component: () => import('../views/ArticleList.vue') },    // 标签文章列表
  { path: '/about', name: 'About', component: () => import('../views/About.vue') },              // 关于页
  { path: '/news', name: 'News', component: () => import('../views/News.vue') },                 // 订阅资讯页
  { path: '/recommend', name: 'Recommend', component: () => import('../views/Recommend.vue') },
  { path: '/search', name: 'SearchResult', component: () => import('../views/SearchResult.vue') },  // 推荐页

  // ===== 后台路由（需登录） =====
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/Login.vue') },// 后台登录页
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),   // 后台布局（侧边栏+顶栏）
    redirect: '/admin/dashboard',                           // 默认重定向到仪表盘
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue') },      // 仪表盘
      { path: 'articles', name: 'ArticleList', component: () => import('../views/admin/ArticleManage.vue') }, // 文章管理
      { path: 'articles/edit/:id?', name: 'ArticleEdit', component: () => import('../views/admin/ArticleEdit.vue') },// 文章编辑（:id 为空时新建）
      { path: 'categories', name: 'CategoryManage', component: () => import('../views/admin/CategoryManage.vue') },// 分类管理
      { path: 'comments', name: 'CommentManage', component: () => import('../views/admin/CommentManage.vue') },   // 评论管理
      { path: 'images', name: 'ImageManage', component: () => import('../views/admin/ImageManage.vue') },        // 图片管理
      { path: 'profile', name: 'ProfileEdit', component: () => import('../views/admin/ProfileEdit.vue') },       // 个人主页编辑
      { path: 'feeds', name: 'FeedManage', component: () => import('../views/admin/FeedManage.vue') },           // 订阅源管理
    ]
  }
]

const router = createRouter({
  history: createWebHistory(), // HTML5 History 模式
  routes
})

// 全局前置守卫：未登录用户访问后台页面时重定向到登录页
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    const user = localStorage.getItem('user')
    if (!user) {
      next('/admin/login')   // 无登录态 → 跳转登录页
      return
    }
  }
  next()
})

export default router
