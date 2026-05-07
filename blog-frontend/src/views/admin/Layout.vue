<template>
  <!-- 后台管理布局：左侧菜单 + 右侧内容区 -->
  <div class="admin-layout">
    <!-- 左侧侧边栏 -->
    <aside class="admin-aside">
      <div class="aside-header">博客管理后台</div>
      <!-- 菜单栏：根据当前路由高亮对应菜单项，点击跳转 -->
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/admin/dashboard"><el-icon><DataAnalysis /></el-icon><span>控制台</span></el-menu-item>
        <el-menu-item index="/admin/articles"><el-icon><Document /></el-icon><span>文章管理</span></el-menu-item>
        <el-menu-item index="/admin/categories"><el-icon><CollectionTag /></el-icon><span>分类管理</span></el-menu-item>
        <el-menu-item index="/admin/comments"><el-icon><ChatDotSquare /></el-icon><span>评论管理</span></el-menu-item>
        <el-menu-item index="/admin/images"><el-icon><Picture /></el-icon><span>图片管理</span></el-menu-item>
        <el-menu-item index="/admin/profile"><el-icon><User /></el-icon><span>个人信息</span></el-menu-item>
        <el-menu-item index="/admin/feeds"><el-icon><Connection /></el-icon><span>订阅</span></el-menu-item>
        <el-menu-item index="/"><el-icon><HomeFilled /></el-icon><span>返回前台</span></el-menu-item>
      </el-menu>
    </aside>
    <!-- 右侧区域：顶栏 + 内容主体 -->
    <div class="admin-right">
      <header class="admin-header">
        <!-- 欢迎语，显示用户昵称 -->
        <span>欢迎，{{ user?.nickname || user?.username }}</span>
        <!-- 退出登录按钮 -->
        <el-button type="danger" size="small" @click="logout">退出登录</el-button>
      </header>
      <main class="admin-main">
        <!-- 子路由页面渲染区，keep-alive 缓存页面状态 -->
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 当前路由对象
const route = useRoute()
const router = useRouter()
// 从localStorage中解析当前登录用户信息
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))

// 退出登录：清除用户信息并跳转到登录页
function logout() {
  localStorage.removeItem('user')
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}
.admin-aside {
  width: 220px;
  flex-shrink: 0;
  background: #304156;
}
.admin-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.aside-header {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255,255,255,.1);
}
.admin-header {
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,.08);
}
.admin-main {
  flex: 1;
  background: #f0f2f5;
  overflow-y: auto;
  padding: 20px;
}
</style>
