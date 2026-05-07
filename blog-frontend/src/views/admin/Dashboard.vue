<template>
  <div>
    <!-- 第一行：四个统计卡片（文章数、评论数、分类数、标签数） -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon :size="24"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 第二行：左侧ECharts图表 + 右侧名言管理 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="16">
        <el-card>
          <template #header><span>近7日访问趋势</span></template>
          <!-- ECharts图表容器 -->
          <div ref="chartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>每日名言</span>
              <!-- 新增名言按钮 -->
              <el-button size="small" type="primary" @click="openQuoteDialog()"><el-icon><Plus/></el-icon></el-button>
            </div>
          </template>
          <!-- 名言表格，最多显示8条 -->
          <el-table :data="quotes" border stripe size="small" style="width:100%" max-height="340">
            <el-table-column type="index" width="40" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column label="操作" width="60" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="deleteQuote(row.id)">删</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增名言弹窗 -->
    <el-dialog v-model="quoteDialog.visible" title="新增名言" width="450px">
      <el-input v-model="quoteDialog.form.content" type="textarea" :rows="3" placeholder="输入名言..." />
      <template #footer>
        <el-button @click="quoteDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveQuote">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { adminApi, quoteApi } from '../../api'

// ECharts图表的DOM引用
const chartRef = ref(null)
// 四个统计卡片的数据：标签、数值、图标、颜色
const cards = reactive([
  { label: '文章总数', value: 0, icon: 'Document', color: '#409eff' },
  { label: '评论总数', value: 0, icon: 'ChatDotSquare', color: '#67c23a' },
  { label: '分类总数', value: 0, icon: 'CollectionTag', color: '#e6a23c' },
  { label: '标签总数', value: 0, icon: 'PriceTag', color: '#f56c6c' }
])
// 名言列表
const quotes = ref([])
// 名言弹窗数据：是否显示和表单内容
const quoteDialog = reactive({ visible: false, form: { content: '' } })

// 从服务器加载统计数据（文章数、评论数等）
async function loadStats() {
  try {
    const res = await adminApi.dashboard()
    const d = res.data || {}
    cards[0].value = d.articleCount ?? 0
    cards[1].value = d.commentCount ?? 0
    cards[2].value = d.categoryCount ?? 0
    cards[3].value = d.tagCount ?? 0
  } catch {}
}

// 加载名言列表，最多取前8条
async function loadQuotes() {
  try {
    const res = await quoteApi.list()
    quotes.value = (res.data || []).slice(0, 8)
  } catch {}
}

// 打开新增名言弹窗
function openQuoteDialog() {
  quoteDialog.form = { content: '' }
  quoteDialog.visible = true
}

// 保存新增的名言到服务器
async function saveQuote() {
  if (!quoteDialog.form.content) return
  await quoteApi.save(quoteDialog.form)
  ElMessage.success('已添加')
  quoteDialog.visible = false
  loadQuotes()
}

// 删除指定ID的名言
async function deleteQuote(id) {
  await quoteApi.delete(id)
  ElMessage.success('已删除')
  loadQuotes()
}

// 页面挂载时：加载统计数据、名言列表，并初始化ECharts图表
onMounted(() => {
  loadStats()
  loadQuotes()

  // 初始化ECharts折线图，展示近7日访问趋势（当前为模拟数据）
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['04-25', '04-26', '04-27', '04-28', '04-29', '04-30', '05-01'], axisLabel: { color: '#999' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#e0e0e0' } } },
    series: [{ data: [5, 8, 3, 12, 9, 15, 7], type: 'line', smooth: true, areaStyle: { color: 'rgba(64,158,255,.1)' }, lineStyle: { color: '#409eff' }, itemStyle: { color: '#409eff' } }],
    grid: { left: '3%', right: '3%', bottom: '3%', containLabel: true }
  })
  window.addEventListener('resize', () => chart.resize())
})
</script>

<style scoped>
.stat-card { margin-bottom: 20px; display: flex; align-items: center; padding: 16px; }
.stat-icon { width: 50px; height: 50px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #fff; margin-right: 16px; flex-shrink: 0; }
.stat-info { flex: 1; }
.stat-value { font-size: 28px; font-weight: 700; color: #333; }
.stat-label { font-size: 13px; color: #999; margin-top: 4px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
