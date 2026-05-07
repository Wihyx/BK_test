<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理 <span style="color:#999;font-size:13px">共 {{ total }} 条</span></span>
          <div>
            <!-- 状态筛选下拉框：全部/待审核/已通过/已驳回 -->
            <el-select v-model="filterStatus" placeholder="筛选状态" size="small" style="width:120px" clearable @change="loadData">
              <el-option label="全部" :value="null" />
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已驳回" :value="2" />
            </el-select>
          </div>
        </div>
      </template>
      <!-- 评论表格 -->
      <el-table :data="comments" border stripe v-loading="loading" empty-text="暂无评论">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="nickname" label="昵称" width="110" />
        <el-table-column prop="content" label="评论内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="articleId" label="文章ID" width="80" align="center" />
        <el-table-column prop="createdTime" label="时间" width="165" />
        <!-- 状态列，通过statusMap映射显示 -->
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：通过、驳回、删除 -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 1" size="small" type="success" @click="approve(row.id)">通过</el-button>
            <el-button v-if="row.status !== 2" size="small" type="warning" @click="reject(row.id)">驳回</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页器 -->
      <div v-if="total > pageSize" class="pagination-wrap">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { commentApi } from '../../api'

// 评论列表数据
const comments = ref([])
// 表格加载状态
const loading = ref(false)
// 当前页码
const currentPage = ref(1)
// 每页条数
const pageSize = ref(10)
// 评论总数
const total = ref(0)
// 筛选状态（null=全部，0=待审核，1=已通过，2=已驳回）
const filterStatus = ref(null)
// 状态码到显示文字的映射
const statusMap = { 0: { label: '待审核', type: 'info' }, 1: { label: '已通过', type: 'success' }, 2: { label: '已驳回', type: 'danger' } }

// 从服务器加载评论列表（管理端接口，包含所有状态）
async function loadData() {
  loading.value = true
  try {
    const params = { page: currentPage.value, pageSize: pageSize.value }
    const res = await commentApi.adminList(params)
    comments.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

// 审核通过评论
async function approve(id) { await commentApi.updateStatus(id, 1); ElMessage.success('已通过'); loadData() }
// 驳回评论
async function reject(id) { await commentApi.updateStatus(id, 2); ElMessage.success('已驳回'); loadData() }
// 删除评论
async function handleDelete(id) { await commentApi.delete(id); ElMessage.success('已删除'); loadData() }

// 页面挂载时加载评论数据
onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-wrap { text-align: center; margin-top: 20px; }
</style>
