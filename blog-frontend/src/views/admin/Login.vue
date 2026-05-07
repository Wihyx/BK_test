<template>
  <!-- 后台登录页面：左右分栏布局 -->
  <div class="login-page">
    <!-- 左侧品牌展示区 -->
    <div class="login-left">
      <div class="login-brand">
        <h1>个人博客</h1>
        <p>用文字记录思考</p>
      </div>
    </div>
    <!-- 右侧登录表单区 -->
    <div class="login-right">
      <div class="login-card">
        <!-- 登录卡片顶部封面图 -->
        <div class="login-cover" :style="{ backgroundImage: `url(${coverUrl})` }">
          <h2 class="login-title">欢迎回来</h2>
        </div>
        <!-- 登录表单：用户名、密码 -->
        <div class="login-body">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item>
              <!-- 登录按钮，加载中时禁用 -->
              <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '../../api'

// 路由对象，用于登录后跳转
const router = useRouter()
// 表单引用，用于触发表单校验
const formRef = ref(null)
// 登录请求进行中状态
const loading = ref(false)
// 登录封面图URL
const coverUrl = '/uploads/materials/login.jpg'
// 登录表单数据（用户名、密码）
const form = reactive({ username: '', password: '' })
// 表单校验规则：用户名和密码均为必填
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 处理登录：先校验表单，再发送登录请求，成功后将用户信息存入localStorage并跳转后台首页
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  loading.value = true
  try {
    const res = await adminApi.login(form)
    localStorage.setItem('user', JSON.stringify(res.data))
    router.push('/admin/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #2d2d2d 50%, #3a3a3a 50%);
}
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-brand {
  text-align: center;
  color: #e0e0e0;
}
.login-brand h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #409eff;
}
.login-brand p {
  font-size: 16px;
  color: #aaa;
}
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 440px;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0,0,0,.3);
  display: flex;
  flex-direction: column;
}
.login-cover {
  flex: 2;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 240px;
}
.login-title {
  color: #fff;
  font-size: 28px;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0,0,0,.5);
}
.login-body {
  flex: 1;
  padding: 36px 32px;
}
.login-btn { width: 100%; }
</style>
