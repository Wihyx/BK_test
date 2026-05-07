<template>
  <!-- 根组件：动态渲染当前路由对应的页面组件，并用keep-alive缓存页面状态 -->
  <router-view v-slot="{ Component, route }">
    <keep-alive>
      <component :is="Component" :key="route.path" />
    </keep-alive>
  </router-view>
</template>

<script setup>
import { onMounted } from 'vue'

// 默认背景图路径
const DEFAULT_BG = '/uploads/bg/01.jpg'

// 将指定的背景图URL应用到页面body上
function applyBg(url) {
  document.body.style.setProperty('--bg-image', `url(${url})`)
}

// 应用初始化：从localStorage恢复用户的主题、背景、纯色模式设置
onMounted(() => {
  // 恢复主题（亮色/暗色）
  if (localStorage.getItem('theme') === 'light') {
    document.body.classList.add('light')
  }

  // 恢复背景图设置，没有则用默认背景
  const bg = JSON.parse(localStorage.getItem('bgImage') || 'null')
  applyBg((bg && bg.path) ? bg.path : DEFAULT_BG)

  // 恢复纯色模式（纯色模式下隐藏背景图，直接设置页面底色）
  if (localStorage.getItem('solidMode') === '1') {
    const isLight = document.body.classList.contains('light')
    document.body.style.setProperty('--bg-image', 'none')
    document.body.style.background = isLight ? '#f0f0f0' : '#2d2d2d'
  }
})
</script>
