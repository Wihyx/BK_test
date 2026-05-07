import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例，统一基础配置
const request = axios.create({
  baseURL: '/api',      // API 基础路径
  timeout: 8000         // 请求超时 8s
})

// 请求拦截器：自动附带登录 token
request.interceptors.request.use(config => {
  const user = localStorage.getItem('user')
  if (user) {
    config.headers.Authorization = JSON.parse(user).token
  }
  return config
})

// 响应拦截器：统一处理错误码与异常提示
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// ===== 文章 API =====
export const articleApi = {
  list: params => request.get('/articles', { params }),          // 文章列表
  detail: id => request.get(`/articles/${id}`),                  // 文章详情
  like: id => request.put(`/articles/${id}/like`),               // 点赞
  save: data => request.post('/admin/articles', data),           // 新建文章
  update: data => request.put('/admin/articles', data),          // 编辑文章
  delete: id => request.delete(`/admin/articles/${id}`)          // 删除文章
}

// ===== 分类 API =====
export const categoryApi = {
  list: () => request.get('/categories'),                        // 分类列表
  save: data => request.post('/admin/categories', data),         // 新建分类
  update: data => request.put('/admin/categories', data),        // 编辑分类
  delete: id => request.delete(`/admin/categories/${id}`)        // 删除分类
}

// ===== 标签 API =====
export const tagApi = {
  list: () => request.get('/tags'),                              // 标签列表
  save: data => request.post('/admin/tags', data),               // 新建标签
  delete: id => request.delete(`/admin/tags/${id}`)              // 删除标签
}

// ===== 评论 API =====
export const commentApi = {
  list: params => request.get('/comments', { params }),          // 前台评论列表
  adminList: params => request.get('/admin/comments', { params }),// 后台评论管理列表
  save: data => request.post('/comments', data),                 // 提交评论
  updateStatus: (id, status) => request.put(`/admin/comments/${id}/status`, null, { params: { status } }),// 审核评论
  delete: id => request.delete(`/admin/comments/${id}`)          // 删除评论
}

// ===== 管理员 API =====
export const adminApi = {
  login: data => request.post('/admin/login', data),             // 管理员登录
  dashboard: () => request.get('/admin/dashboard')               // 仪表盘数据
}

// ===== 名言/语录 API =====
export const quoteApi = {
  random: () => request.get('/quotes/random'),                   // 随机名言
  list: () => request.get('/admin/quotes'),                      // 名言列表
  save: data => request.post('/admin/quotes', data),             // 添加名言
  delete: id => request.delete(`/admin/quotes/${id}`)            // 删除名言
}

// ===== 个人主页 API =====
export const profileApi = {
  get: () => request.get('/profile'),                            // 获取主页信息
  update: data => request.put('/admin/profile', data)            // 更新主页信息
}

// ===== RSS 订阅 API =====
export const feedApi = {
  list: () => request.get('/admin/feeds'),                       // 订阅源列表
  save: data => request.post('/admin/feeds', data),              // 添加订阅源
  update: data => request.put('/admin/feeds', data),             // 更新订阅源
  delete: id => request.delete(`/admin/feeds/${id}`),            // 删除订阅源
  sync: () => request.post('/admin/feeds/sync'),                 // 手动同步所有订阅源
  clear: () => request.post('/admin/feeds/clear'),               // 清空已抓取文章
  test: url => request.post('/admin/feeds/test', { url }),       // 测试订阅链接有效性
  importOpml: file => {
    const fd = new FormData()
    fd.append('file', file)
    return request.post('/admin/feeds/import', fd)               // 导入 OPML 订阅文件
  },
  articles: () => request.get('/feed-articles')                  // 获取所有订阅文章
}

// ===== 图片/音乐资源 API =====
export const imageApi = {
  list: type => request.get('/images', { params: { type } }),    // 资源列表（images/music）
  listMusic: () => request.get('/music'),                        // 音乐列表
  upload: (file, type = 'images') => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    return request.post('/upload', formData)                     // 上传文件
  },
  delete: (type, name) => request.delete(`/images/${type}/${name}`)// 删除资源
}

// ===== 文件上传 API =====
export const uploadApi = {
  upload: (file, type = 'images') => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    return request.post('/upload', formData)                     // 上传文件
  }
}

export default request
