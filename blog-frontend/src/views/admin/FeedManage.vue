<template>
  <div>
    <el-row :gutter="20">
      <!-- 左半部分：RSS订阅源管理 + 文章同步 -->
      <el-col :span="12">
        <!-- RSS订阅源表格 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>RSS 订阅源</span>
              <div>
                <!-- 导入OPML文件（一种订阅源集合文件） -->
                <el-upload :show-file-list="false" :http-request="handleImport" accept=".opml,.xml" style="display:inline-block;margin-right:8px">
                  <el-button size="small">导入 OPML</el-button>
                </el-upload>
                <el-button size="small" type="primary" @click="openAddDialog"><el-icon><Plus /></el-icon> 添加</el-button>
              </div>
            </div>
          </template>
          <!-- 订阅源列表 -->
          <el-table :data="feeds" border stripe size="small" style="table-layout:auto" max-height="200">
            <el-table-column prop="name" label="名称" min-width="90" show-overflow-tooltip />
            <el-table-column prop="url" label="地址" min-width="190" show-overflow-tooltip />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="testFeed(row.url)">测试</el-button>
                <el-button size="small" type="primary" @click="openEditDialog(row)">改</el-button>
                <el-button size="small" type="danger" @click="handleDelete(row.id)">删</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <!-- 同步文章区域 -->
        <el-card style="margin-top:16px;margin-bottom:10px">
          <template #header>
            <div class="card-header">
              <span>同步文章 {{ articles.length }} 条</span>
              <div>
                <el-button size="small" type="danger" @click="clearArticles">清空</el-button>
                <el-button size="small" type="success" :loading="syncing" @click="syncFeeds">同步</el-button>
              </div>
            </div>
          </template>
          <!-- 同步到的文章列表 -->
          <el-table :data="articles" border stripe size="small" max-height="210">
            <el-table-column prop="feedName" label="来源" width="90" />
            <el-table-column prop="title" label="标题" show-overflow-tooltip>
              <template #default="{ row }"><a :href="row.link" target="_blank">{{ row.title }}</a></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <!-- 右半部分：音乐库管理 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>音乐库 {{ musicList.length }} 首</span>
              <div>
                <el-button size="small" @click="loadMusicList">刷新</el-button>
                <!-- 上传音乐文件 -->
                <el-upload :show-file-list="false" :http-request="handleMusicUp" accept="audio/*" style="display:inline-block">
                  <el-button size="small" type="success"><el-icon><Upload /></el-icon> 上传</el-button>
                </el-upload>
              </div>
            </div>
          </template>
          <!-- 音乐列表 -->
          <el-table :data="musicList" border stripe size="small" max-height="550">
            <el-table-column type="index" label="#" width="40"/>
            <el-table-column prop="name" label="名称" show-overflow-tooltip/>
            <el-table-column label="操作" width="60">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="deleteMusic(row.path)">删</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <!-- 添加/编辑订阅源弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '修改订阅源' : '添加订阅源'" width="450px">
      <el-form :model="dialog.form">
        <el-form-item label="名称"><el-input v-model="dialog.form.name" placeholder="网站名称" /></el-form-item>
        <el-form-item label="RSS地址"><el-input v-model="dialog.form.url" placeholder="https://example.com/rss" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveFeed">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { feedApi, imageApi } from '../../api'

// RSS订阅源列表
const feeds = ref([])
// 同步到的文章列表
const articles = ref([])
// 音乐文件列表
const musicList = ref([])
// 同步进行中状态
const syncing = ref(false)
// 弹窗数据：显示状态、是否编辑模式、表单数据
const dialog = ref({ visible: false, isEdit: false, form: { name: '', url: '' } })

// 打开新增订阅源弹窗
function openAddDialog() {
  dialog.value = { visible: true, isEdit: false, form: { name: '', url: '' } }
}
// 打开编辑订阅源弹窗，回填现有数据
function openEditDialog(row) {
  dialog.value = { visible: true, isEdit: true, form: { id: row.id, name: row.name, url: row.url } }
}
// 保存订阅源（新增或修改）
async function saveFeed() {
  if (!dialog.value.form.name || !dialog.value.form.url) return
  if (dialog.value.isEdit) {
    await feedApi.update(dialog.value.form)
  } else {
    await feedApi.save(dialog.value.form)
  }
  dialog.value.visible = false
  ElMessage.success(dialog.value.isEdit ? '已修改' : '已添加')
  loadFeeds()
}
// 加载所有RSS订阅源
async function loadFeeds() {
  try { const res = await feedApi.list(); feeds.value = res.data || [] } catch {}
}
// 加载已同步的文章
async function loadArticles() {
  try { const res = await feedApi.articles(); articles.value = res.data || [] } catch {}
}
// 删除指定订阅源
async function handleDelete(id) {
  await feedApi.delete(id)
  ElMessage.success('已删除')
  loadFeeds(); loadArticles()
}
// 手动触发RSS同步，从所有订阅源拉取最新文章
async function syncFeeds() {
  syncing.value = true
  try {
    const res = await feedApi.sync()
    ElMessage.success(`同步完成，新增 ${res.data?.count || 0} 条`)
    loadArticles()
  } finally { syncing.value = false }
}
// 清空所有同步到的文章
async function clearArticles() {
  await feedApi.clear()
  ElMessage.success('已清空')
  loadArticles()
}
// 测试某个RSS订阅源的URL是否可用
async function testFeed(url) {
  try {
    const res = await feedApi.test(url)
    if (res.code === 200) ElMessage.success(res.data)
    else ElMessage.error(res.msg)
  } catch { ElMessage.error('test failed') }
}
// 导入OPML文件（批量添加订阅源）
async function handleImport(options) {
  try {
    const res = await feedApi.importOpml(options.file)
    if (res.code === 200) {
      ElMessage.success(`导入成功，新增 ${res.data?.count || 0} 个订阅源`)
      loadFeeds()
    } else {
      ElMessage.error(res.msg)
    }
  } catch { ElMessage.error('import failed') }
}
// 加载音乐库文件列表
async function loadMusicList() {
  try { const res = await imageApi.listMusic(); musicList.value = res.data || [] } catch {}
}
// 上传音乐文件
async function handleMusicUp(options) {
  const res = await imageApi.upload(options.file, 'music')
  if (res.code === 200) { ElMessage.success('已上传'); loadMusicList() }
}
// 删除指定音乐文件
async function deleteMusic(path) {
  const name = path.split('/').pop()
  await imageApi.delete('music', name)
  ElMessage.success('已删除')
  loadMusicList()
}
// 页面挂载时加载所有数据
onMounted(() => { loadFeeds(); loadArticles(); loadMusicList() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header > div { display: flex; gap: 6px; }
.feed-row { align-items: stretch !important; }
:deep(.feed-row .el-col) { display: flex; flex-direction: column; }
:deep(.feed-row .el-card) { flex: 1; }
</style>
