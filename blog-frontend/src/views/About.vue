<template>
  <div>
    <!-- 引入顶部导航栏组件 -->
    <Header />
    <div class="container page-main">
      <!-- 关于页面主体卡片 -->
      <div class="glass-card about-card">
        <!-- 头像和名称区域 -->
        <div class="about-header">
          <el-avatar :size="80" :src="profile.avatar" class="about-avatar">{{ profile.name?.[0] || '?' }}</el-avatar>
          <h1>{{ profile.name || '关于我' }}</h1>
        </div>
        <el-divider style="border-color:rgba(255,255,255,.08)" />
        <!-- 简介、技术栈、联系方式 -->
        <div class="about-content">
          <p>{{ profile.bio || '这里是我的个人原创博客。' }}</p>

          <h3>技术栈</h3>
          <!-- 以标签形式展示技术栈 -->
          <el-tag v-for="s in skills" :key="s" class="skill-tag" effect="dark">{{ s }}</el-tag>

          <h3>联系我</h3>
          <p v-if="profile.email">邮箱：{{ profile.email }}</p>
          <p v-if="profile.github">GitHub：<a :href="profile.github" target="_blank">{{ profile.github }}</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { profileApi } from '../api'
import Header from '../components/Header.vue'

// 存储从服务器获取的个人信息
const profile = ref({})
// 将技术栈字符串（逗号分隔）拆分成数组，方便用标签展示
const skills = computed(() => {
  if (profile.value.techStack) return profile.value.techStack.split(',').map(s => s.trim()).filter(Boolean)
  return []
})

// 页面挂载时请求个人信息数据
onMounted(async () => {
  try {
    const res = await profileApi.get()
    if (res.data) profile.value = res.data
  } catch {}
})
</script>

<style scoped>
.about-card {
  max-width: 700px;
  margin: 0 auto;
  background: rgba(0,0,0,.36);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,.08);
  border-radius: 12px;
  padding: 32px;
  color: #e0e0e0;
}
.about-header { text-align: center; padding: 20px 0; }
.about-avatar { margin-bottom: 16px; }
.about-header h1 { color: #e0e0e0; }
.about-content { line-height: 2; font-size: 16px; color: #ccc; }
.about-content h3 { margin: 24px 0 12px; color: #e0e0e0; }
.skill-tag { margin: 4px; }
</style>
