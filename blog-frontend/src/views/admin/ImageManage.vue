<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图片管理</span>
          <!-- 上传按钮 -->
          <el-button type="primary" size="small" @click="showUpload = true">
            <el-icon><Upload /></el-icon> 上传图片
          </el-button>
        </div>
      </template>

      <!-- 按tab切换图片分类：插图 / 封面 / 背景 -->
      <el-tabs v-model="activeTab" @tab-change="loadImages">
        <el-tab-pane label="插图" name="images" />
        <el-tab-pane label="封面" name="covers" />
        <el-tab-pane label="背景" name="bg" />
      </el-tabs>

      <!-- 背景tab下显示当前使用的背景图信息 -->
      <div v-if="activeTab === 'bg' && bgImage" class="current-bg">
        <img :src="bgImage.path" class="current-bg-img" />
        <div class="current-bg-info">
          <strong>当前背景：</strong>{{ bgImage.name }}
          <el-button size="small" type="danger" plain @click="clearBg">清除（使用默认图）</el-button>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-wrap"><el-skeleton :rows="2" animated /></div>
      <!-- 无图片 -->
      <div v-else-if="images.length === 0" class="empty-wrap"><el-empty description="暂无图片" /></div>

      <!-- 图片网格展示 -->
      <div v-else class="image-grid">
        <div
          v-for="img in images"
          :key="img.name"
          class="image-item"
          :class="{ 'is-active': isBgActive(img) }"
          @click="previewImage = img"
        >
          <!-- 当前背景标记角标 -->
          <div v-if="isBgActive(img)" class="bg-badge"><el-icon><Check /></el-icon> 当前背景</div>
          <!-- 缩略图 -->
          <el-image :src="img.path" fit="cover" class="img-thumb" lazy>
            <template #error><div class="img-error"><el-icon :size="30"><Picture /></el-icon></div></template>
          </el-image>
          <!-- 图片名称和大小信息 -->
          <div class="img-info">
            <span class="img-name" :title="img.name">{{ img.name }}</span>
            <span class="img-size">{{ formatSize(img.size) }}</span>
          </div>
          <!-- 图片操作按钮（设为背景、复制链接、删除），鼠标悬停显示 -->
          <div class="img-actions">
            <el-tooltip content="设为背景" placement="top" v-if="activeTab === 'bg'">
              <el-button size="small" circle type="success" @click.stop="setBg(img)"><el-icon><Picture /></el-icon></el-button>
            </el-tooltip>
            <el-tooltip content="复制链接" placement="top">
              <el-button size="small" circle @click.stop="copyPath(img.path)"><el-icon><Link /></el-icon></el-button>
            </el-tooltip>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(img.name)">
              <template #reference><el-button size="small" circle type="danger" @click.stop><el-icon><Delete /></el-icon></el-button></template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 上传图片弹窗 -->
    <el-dialog v-model="showUpload" title="上传图片" width="500px">
      <el-upload class="upload-area" drag multiple :http-request="handleUpload" :before-upload="beforeUpload" accept="image/*" :show-file-list="false">
        <el-icon :size="40"><UploadFilled /></el-icon>
        <div class="upload-text">拖拽图片到此处或点击上传</div>
      </el-upload>
      <template #footer><el-button @click="showUpload = false">关闭</el-button></template>
    </el-dialog>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="showPreview" title="预览" width="700px" :destroy-on-close="true">
      <div class="preview-wrap" v-if="previewImage">
        <el-image :src="previewImage.path" fit="contain" style="max-height:500px" />
        <div class="preview-meta">
          <span>{{ previewImage.name }}</span>
          <span>{{ formatSize(previewImage.size) }}</span>
          <el-button size="small" @click="copyPath(previewImage.path)">复制链接</el-button>
          <el-button v-if="activeTab === 'bg'" size="small" type="success" @click="setBg(previewImage); showPreview = false">设为背景</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { imageApi } from '../../api'

// 默认背景图路径
const DEFAULT_BG = '/uploads/bg/01.jpg'
// 当前激活的tab：images / covers / bg
const activeTab = ref('images')
// 当前tab下的图片列表
const images = ref([])
// 当前使用的背景图信息（从localStorage读取）
const bgImage = ref(JSON.parse(localStorage.getItem('bgImage') || 'null'))
// 加载状态
const loading = ref(false)
// 上传弹窗显示状态
const showUpload = ref(false)
// 预览弹窗显示状态
const showPreview = ref(false)
// 当前预览的图片对象
const previewImage = ref(null)

// 根据当前tab加载图片列表
async function loadImages() {
  loading.value = true
  try { const res = await imageApi.list(activeTab.value); images.value = res.data || [] }
  finally { loading.value = false }
}

// 上传前校验：只允许图片文件，大小不超10MB
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt10M) { ElMessage.error('图片大小不能超过 10MB'); return false }
  return true
}

// 处理图片上传
async function handleUpload(options) {
  const res = await imageApi.upload(options.file, activeTab.value)
  if (res.code === 200) { ElMessage.success('上传成功'); showUpload.value = false; loadImages() }
}

// 删除指定名称的图片（如果该图是当前背景则先清除）
async function handleDelete(name) {
  if (isBgActive({ name })) clearBg()
  await imageApi.delete(activeTab.value, name)
  ElMessage.success('已删除')
  loadImages()
}

// 将指定图片设为网站背景
function setBg(img) {
  bgImage.value = img
  localStorage.setItem('bgImage', JSON.stringify(img))
  document.body.style.setProperty('--bg-image', `url(${img.path})`)
  ElMessage.success('背景已设置')
}

// 清除当前背景设置，恢复默认背景图
function clearBg() {
  bgImage.value = null
  localStorage.removeItem('bgImage')
  document.body.style.setProperty('--bg-image', `url(${DEFAULT_BG})`)
  ElMessage.success('已恢复默认背景')
}

// 判断某图片是否为当前设置的背景
function isBgActive(img) { return bgImage.value && bgImage.value.path === img.path }

// 复制图片完整URL到剪贴板
async function copyPath(path) {
  await navigator.clipboard.writeText(window.location.origin + path)
  ElMessage.success('链接已复制')
}

// 将字节大小格式化为 B / KB / MB
function formatSize(bytes) {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}

// 页面挂载时加载图片列表
onMounted(loadImages)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.loading-wrap { padding: 40px; }
.empty-wrap { padding: 40px; }
.current-bg { display: flex; align-items: center; gap: 16px; padding: 12px 16px; margin-bottom: 16px; background: #f0f9eb; border: 1px solid #c2e7b0; border-radius: 6px; }
.current-bg-img { width: 80px; height: 50px; object-fit: cover; border-radius: 4px; }
.current-bg-info { flex: 1; display: flex; align-items: center; gap: 12px; font-size: 13px; }
.image-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; }
.image-item { border: 2px solid #eee; border-radius: 6px; overflow: hidden; cursor: pointer; transition: box-shadow .2s, border-color .2s; position: relative; }
.image-item:hover { box-shadow: 0 2px 12px rgba(0,0,0,.1); }
.image-item.is-active { border-color: #67c23a; }
.bg-badge { position: absolute; top: 6px; left: 6px; z-index: 5; background: #67c23a; color: #fff; font-size: 11px; padding: 2px 6px; border-radius: 3px; display: flex; align-items: center; gap: 2px; }
.img-thumb { width: 100%; height: 140px; display: block; }
.img-thumb :deep(img) { object-fit: cover; }
.img-error { width: 100%; height: 140px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; color: #ccc; }
.img-info { padding: 8px 10px; display: flex; justify-content: space-between; font-size: 12px; color: #666; }
.img-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-right: 8px; }
.img-size { color: #999; white-space: nowrap; }
.img-actions { position: absolute; top: 6px; right: 6px; display: none; gap: 4px; }
.image-item:hover .img-actions { display: flex; }
.upload-area { padding: 20px 0; }
.upload-text { margin-top: 12px; color: #999; }
.preview-wrap { text-align: center; }
.preview-meta { margin-top: 16px; display: flex; justify-content: center; align-items: center; gap: 16px; color: #999; font-size: 13px; }
</style>
