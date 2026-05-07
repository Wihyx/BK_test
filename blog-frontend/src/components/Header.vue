<template>
  <!-- 顶部导航栏：包含Logo、首页链接、搜索框、音乐播放器、主题切换、背景切换 -->
  <header class="site-header">
    <div class="header-inner">
      <!-- 网站Logo，点击回到首页 -->
      <router-link to="/" class="logo">个人博客</router-link>
      <!-- 首页导航链接，当前在首页时高亮 -->
      <router-link to="/" class="nav-link" :class="{ active: route.path === '/' }">首页</router-link>
      <!-- 搜索框区域，按回车触发搜索 -->
      <div class="header-search">
        <el-input v-model="keyword" placeholder="搜索文章..." size="default" clearable @keyup.enter="search" :prefix-icon="Search" />
      </div>
      <!-- 歌词显示区域，只在开启歌词且有歌词时显示 -->
      <div class="lyrics-view" v-show="subtitleOn && lyricsLines.length > 0">
        <div class="lyrics-scroll" ref="lyricsRef">
          <!-- 逐行显示歌词，当前播放到的行高亮 -->
          <p v-for="(line, i) in lyricsLines" :key="i" :class="{ active: i === currentLrcIdx }">{{ line.text }}</p>
        </div>
      </div>
      <!-- 音乐播放器区域 -->
      <div class="music-player">
        <!-- 专辑封面，有封面图时旋转显示，没有则显示耳机图标 -->
        <div class="music-art">
          <img v-if="currentTrack?.cover" :src="currentTrack.cover" class="music-cover" :style="{ transform: `rotate(${coverRotation}deg)` }" />
          <el-icon v-else :size="22" style="color:#888"><Headset /></el-icon>
        </div>
        <div class="music-content">
          <!-- 当前播放歌曲名称，去掉.mp3后缀显示 -->
          <span class="music-name">{{ currentTrack?.name?.replace('.mp3','') || '未播放' }}</span>
          <div class="music-bottom-row">
            <!-- 播放控制按钮：上一首、播放/暂停、下一首 -->
            <span class="music-controls">
              <el-icon :size="20" style="cursor:pointer;color:#aaa;padding:0 2px" @click="playPrev"><component :is="'DArrowLeft'" /></el-icon>
              <el-icon :size="22" style="cursor:pointer;color:#fff;padding:0 2px" @click="toggleMusic"><component :is="musicPlaying ? 'VideoPause' : 'VideoPlay'" /></el-icon>
              <el-icon :size="20" style="cursor:pointer;color:#aaa;padding:0 2px" @click="playNext"><component :is="'DArrowRight'" /></el-icon>
            </span>
            <!-- 播放进度条：可点击跳转、可拖拽滑块 -->
            <div class="music-progress-inline">
              <div class="hp-track" ref="progressTrackRef" @click="clickProgress">
                <div class="hp-fill" :style="{ width: (musicProgress * 100) + '%' }"></div>
                <div class="hp-thumb" :style="{ left: (musicProgress * 100) + '%' }" @mousedown="startDrag"></div>
              </div>
            </div>
            <!-- 歌词开关按钮 -->
            <el-icon :size="18" style="cursor:pointer;color:#888;padding:2px" :class="{ 'sub-on': subtitleOn }" @click="subtitleOn = !subtitleOn"><ChatDotSquare /></el-icon>
            <!-- 播放列表弹出按钮 -->
            <el-icon :size="18" style="cursor:pointer;color:#888;padding:2px" @click="musicShowList = !musicShowList"><List /></el-icon>
          </div>
        </div>
        <!-- 播放列表弹出层，显示所有可播放歌曲 -->
        <div v-show="musicShowList" class="music-list-popup">
          <div v-for="(t, i) in playlist" :key="t.path" class="playlist-item" :class="{ active: trackIndex === i }" @click="playTrack(i)">
            <span class="playlist-idx">{{ i + 1 }}</span>
            <span class="playlist-name">{{ t.name.replace('.mp3','') }}</span>
            <span v-if="trackIndex === i" class="playlist-playing">▶</span>
          </div>
          <div v-if="playlist.length === 0" style="color:#999;padding:8px;text-align:center;font-size:12px">暂无音乐</div>
        </div>
      </div>
      <!-- 主题切换按钮：暗色/亮色模式切换 -->
      <span class="theme-toggle" @click="toggleTheme">
        <el-icon :size="18"><component :is="isLight ? 'Moon' : 'Sunny'" /></el-icon>
      </span>
      <!-- 背景图片切换弹出框 -->
      <el-popover placement="bottom-end" :width="320" trigger="click">
        <template #reference>
          <span class="bg-trigger"><el-icon :size="16"><Picture /></el-icon></span>
        </template>
        <div class="bg-picker">
          <!-- 上传本地图片作为背景 -->
          <el-upload :show-file-list="false" :http-request="handleBgUpload" :before-upload="beforeBgUpload" accept="image/*">
            <el-button size="small" type="primary" plain style="width:100%"><el-icon><Upload /></el-icon> 本地上传背景</el-button>
          </el-upload>
          <!-- 已有背景图列表，点击可切换 -->
          <div v-if="bgList.length > 0" class="bg-list">
            <div v-for="img in bgList" :key="img.name" class="bg-option" :class="{ active: currentBg === img.path }" @click="selectBg(img.path)">
              <el-image :src="img.path" fit="cover" style="width:56px;height:36px;border-radius:4px" lazy />
              <span class="bg-name">{{ img.name }}</span>
            </div>
          </div>
          <!-- 恢复默认背景和纯色模式开关 -->
          <div class="bg-reset">
            <el-button v-if="currentBg !== DEFAULT_BG" size="small" @click="resetBg">恢复默认背景</el-button>
            <el-button size="small" :type="solidMode ? 'warning' : ''" @click="toggleSolid">{{ solidMode ? '关闭纯色' : '纯色模式' }}</el-button>
          </div>
        </div>
      </el-popover>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { imageApi } from '../api'
import { useMusic } from '../musicStore'

// 获取全局音乐播放器实例
const m = useMusic()

// 路由对象，用于获取当前路由和跳转
const route = useRoute()
const router = useRouter()
// 搜索输入框绑定的关键词
const keyword = ref('')
// 默认背景图路径
const DEFAULT_BG = '/uploads/bg/01.jpg'

// 当前使用的背景图路径
const currentBg = ref(DEFAULT_BG)
// 服务器上的背景图列表
const bgList = ref([])
// 当前是否为亮色主题
const isLight = ref(false)
// 是否为纯色模式（不使用背景图）
const solidMode = ref(false)
// 进度条DOM引用，用于计算点击位置
const progressTrackRef = ref(null)

// 以下是从音乐Store中引用的响应式数据，通过代理方式绑定
const playlist = m.playlist; const trackIndex = m.currentIdx; const currentTrack = m.currentTrack
const musicPlaying = m.isPlaying; const musicProgress = m.progress; const musicCurrentTime = m.currentTime
const musicDuration = m.duration; const coverRotation = m.coverAngle; const subtitleOn = m.subtitleOn
const lyricsLines = m.lyricsLines; const currentLrcIdx = m.lyricIdx; const musicShowList = m.showList

// 播放列表中点击某一首歌
function playTrack(i) { m.select(i); loadLyrics(currentTrack.value?.name) }
// 播放/暂停切换
function toggleMusic() { m.toggle() }
// 下一首
function playNext() { m.next() }
// 上一首
function playPrev() { m.prev() }

// 从服务器加载指定歌曲的歌词文件，解析LRC格式的歌词
async function loadLyrics(name) {
  lyricsLines.value = []
  try {
    const res = await fetch('/api/music/lyrics?name=' + encodeURIComponent(name)).then(r => r.json())
    const lrc = (res && res.data) || ''
    if (!lrc) return
    const lines = []
    lrc.split('\n').forEach(line => {
      const m = line.match(/\[(\d+):(\d+)(?:\.(\d+))?\]\s*(.*)/)
      if (m) {
        const mins = parseInt(m[1]), secs = parseInt(m[2]), ms = m[3] ? parseInt(m[3].padEnd(3,'0')) : 0
        lines.push({ time: mins * 60 + secs + ms / 1000, text: m[4] || '' })
      }
    })
    lyricsLines.value = lines.sort((a,b) => a.time - b.time)
  } catch {}
}

// 点击进度条跳转到指定位置播放
function clickProgress(e) {
  if (!m.audio.duration) return
  const rect = progressTrackRef.value.getBoundingClientRect()
  musicProgress.value = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
  m.audio.currentTime = musicProgress.value * m.audio.duration
}

// 进度条拖拽状态标记
let dragState = false
// 开始拖拽进度条上的滑块
function startDrag(e) {
  dragState = true; document.addEventListener('mousemove', onDrag); document.addEventListener('mouseup', stopDrag)
}
// 拖拽过程中更新进度条位置
function onDrag(e) {
  if (!m.audio.duration) return
  const rect = progressTrackRef.value.getBoundingClientRect()
  musicProgress.value = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
}
// 结束拖拽，进度确定
function stopDrag() {
  document.removeEventListener('mousemove', onDrag); document.removeEventListener('mouseup', stopDrag); dragState = false
  m.seek(musicProgress.value)
}

// 从服务器加载音乐播放列表
async function loadPlaylist() {
  try {
    const res = await imageApi.listMusic()
    playlist.value = (res.data || []).filter(f => f.name.endsWith('.mp3'))
    if (playlist.value.length > 0 && trackIndex.value < 0) {
      if (m.audio.src) {
        const srcPath = m.audio.src
        const idx = playlist.value.findIndex(p => srcPath.endsWith(p.path.substring(p.path.lastIndexOf('/'))))
        if (idx >= 0) { trackIndex.value = idx; currentTrack.value = playlist.value[idx]; return }
      }
      trackIndex.value = 0; currentTrack.value = playlist.value[0]
    }
  } catch {}
}

// 上传音乐文件到服务器
async function handleMusicUpload(options) {
  const res = await imageApi.upload(options.file, 'music')
  if (res.code === 200) { ElMessage.success('已上传'); loadPlaylist() }
}

// 切换暗色/亮色主题
function toggleTheme() {
  isLight.value = !isLight.value; document.body.classList.toggle('light', isLight.value)
  localStorage.setItem('theme', isLight.value ? 'light' : 'dark')
  if (solidMode.value) applySolidBg()
}

// 切换纯色模式与背景图模式
function toggleSolid() {
  solidMode.value = !solidMode.value; localStorage.setItem('solidMode', solidMode.value ? '1' : '0')
  solidMode.value ? applySolidBg() : applyBg(currentBg.value)
}

// 应用纯色背景（亮色用浅灰，暗色用深灰）
function applySolidBg() {
  document.body.style.setProperty('--bg-image', 'none'); document.body.style.background = isLight.value ? '#f0f0f0' : '#2d2d2d'
}

// 应用指定的背景图URL到页面
function applyBg(url) {
  currentBg.value = url
  if (!solidMode.value) { document.body.style.setProperty('--bg-image', url ? `url(${url})` : 'none'); document.body.style.background = '' }
}

// 用户选择一张背景图，保存到本地存储并应用
function selectBg(path) { localStorage.setItem('bgImage', JSON.stringify({ path })); applyBg(path); ElMessage.success('背景已切换') }
// 恢复为默认背景图
function resetBg() { localStorage.removeItem('bgImage'); applyBg(DEFAULT_BG); ElMessage.success('已恢复默认背景') }
// 上传背景图前的校验：只允许图片文件，大小不超过10MB
function beforeBgUpload(file) {
  if (!file.type.startsWith('image/')) { ElMessage.error('请选择图片文件'); return false }
  if (file.size > 10 * 1024 * 1024) { ElMessage.error('图片不超过 10MB'); return false }
  return true
}
// 处理背景图上传，上传成功后自动应用
async function handleBgUpload(options) {
  const res = await imageApi.upload(options.file, 'bg')
  if (res.code === 200) { selectBg(res.data); loadBgList() }
}
// 加载服务器上的背景图列表
async function loadBgList() { try { const res = await imageApi.list('bg'); bgList.value = res.data || [] } catch {} }
// 搜索跳转：携带关键词跳转到首页
function search() {
  if (keyword.value.trim()) router.push({ path: '/search', query: { q: keyword.value.trim() } })
}

// 页面挂载时，从本地存储恢复用户的主题、背景等设置
onMounted(() => {
  isLight.value = localStorage.getItem('theme') === 'light'; document.body.classList.toggle('light', isLight.value)
  solidMode.value = localStorage.getItem('solidMode') === '1'
  if (solidMode.value) applySolidBg()
  const saved = JSON.parse(localStorage.getItem('bgImage') || 'null')
  if (saved && saved.path) applyBg(saved.path); else applyBg(DEFAULT_BG)
  loadBgList(); loadPlaylist()
})
</script>

<style scoped>
.site-header { background: rgba(0,0,0,.55) !important; backdrop-filter: blur(14px); border-bottom: 1px solid rgba(255,255,255,.08); position: sticky; top: 0; z-index: 100; padding: 0 20px; display: flex; flex-direction: column; height: 56px; overflow: visible; }
.header-inner { max-width: 1400px; margin: 0 auto; display: flex; align-items: center; height: 56px; gap: 20px; width: 100%; flex-shrink: 0; }
.logo { font-size: 20px; font-weight: 700; color: #409eff !important; white-space: nowrap; }
.header-nav { display: flex; gap: 4px; }
.nav-link { padding: 6px 14px; border-radius: 8px; color: #aaa !important; font-size: 14px; transition: all .2s; }
.nav-link:hover { color: #fff !important; background: rgba(255,255,255,.06); }
.nav-link.active { color: #409eff !important; background: rgba(64,158,255,.1); }
.header-search { width: 280px; margin-right: auto; }
.header-search :deep(.el-input__wrapper) { border-radius: 24px; padding: 1px 16px; }
.lyrics-view { flex: 1; overflow: hidden; height: 56px; display: flex; align-items: center; position: relative; }
.lyrics-scroll { overflow: hidden; padding: 0 10px; }
.lyrics-scroll p { font-size: 12px; color: #666; padding: 2px 0; transition: color .3s; text-align: center; white-space: nowrap; }
.lyrics-scroll p.active { color: #67c23a; font-size: 14px; font-weight: 600; }
.music-player { display: flex; align-items: center; gap: 6px; position: relative; overflow: visible; flex-shrink: 0; margin-left: -20px; }
.music-art { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; overflow: hidden; }
.music-cover { width: 100%; height: 100%; object-fit: cover; }
.music-content { display: flex; flex-direction: column; flex-shrink: 0; position: relative; gap: 2px; }
.music-name { font-size: 14px; color: #ccc; max-width: 140px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.music-bottom-row { display: flex; align-items: center; gap: 8px; }
.music-controls { display: flex; align-items: center; gap: 6px; }
.music-progress-inline { width: 60px; }
.music-progress-inline .hp-track { width: 100%; height: 3px; background: rgba(255,255,255,.06); cursor: pointer; position: relative; border-radius: 2px; }
.music-progress-inline .hp-fill { height: 100%; background: #67c23a; border-radius: 2px; }
.music-progress-inline .hp-thumb { position: absolute; top: 50%; width: 5px; height: 5px; border-radius: 50%; background: #67c23a; transform: translate(-50%, -50%); opacity: 0; cursor: grab; }
.music-bottom-row:hover .hp-thumb { opacity: 1; }
.music-list-popup { position: absolute; top: 100%; right: 0; background: #2d2d2d; border: 1px solid rgba(255,255,255,.08); border-radius: 8px; padding: 6px 0; width: 200px; max-height: 200px; overflow-y: auto; z-index: 200; box-shadow: 0 4px 16px rgba(0,0,0,.4); }
.playlist-item { display: flex; align-items: center; gap: 8px; padding: 7px 12px; cursor: pointer; font-size: 12px; }
.playlist-item:hover { background: rgba(255,255,255,.06); }
.playlist-item.active { color: #409eff; }
.playlist-idx { width: 18px; text-align: center; color: #666; font-size: 11px; }
.playlist-name { color: #ccc; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }
.playlist-playing { color: #67c23a; }
.playlist-upload { padding: 6px 12px; border-top: 1px solid rgba(255,255,255,.06); }
.theme-toggle { font-size: 18px; cursor: pointer; padding: 4px 8px; border-radius: 6px; user-select: none; }
.theme-toggle:hover { background: rgba(255,255,255,.06); }
.bg-trigger { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; cursor: pointer; border-radius: 8px; color: #aaa; }
.bg-trigger:hover { background: rgba(255,255,255,.08); color: #fff; }
.bg-picker { display: flex; flex-direction: column; gap: 10px; }
.bg-list { display: flex; flex-direction: column; gap: 6px; max-height: 200px; overflow-y: auto; }
.bg-option { display: flex; align-items: center; gap: 10px; padding: 6px 8px; border-radius: 6px; cursor: pointer; transition: background .15s; }
.bg-option:hover { background: rgba(0,0,0,.05); }
.bg-option.active { background: rgba(64,158,255,.1); }
.bg-name { font-size: 12px; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }
.bg-reset { padding-top: 4px; border-top: 1px solid #eee; }
</style>

<style>
.sub-on { color: #67c23a !important; }
</style>
