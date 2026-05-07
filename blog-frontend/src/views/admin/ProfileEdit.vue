<template>
  <div>
    <el-card>
      <template #header><span>个人信息</span></template>
      <!-- 个人信息编辑表单 -->
      <el-form :model="form" label-width="80px" v-loading="loading">
        <!-- 昵称/名称 -->
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="你的名称" />
        </el-form-item>
        <!-- 头像：预览 + 输入链接 + 上传按钮 -->
        <el-form-item label="头像">
          <div class="avatar-row">
            <el-avatar :size="64" :src="form.avatar" />
            <el-input v-model="form.avatar" placeholder="头像链接或上传" style="width:300px" />
            <el-upload :show-file-list="false" :http-request="uploadAvatar" accept="image/*">
              <el-button size="small">上传头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <!-- 个人简介 -->
        <el-form-item label="简介">
          <el-input v-model="form.bio" type="textarea" :rows="3" placeholder="个人简介" />
        </el-form-item>
        <!-- 技术栈，逗号分隔 -->
        <el-form-item label="技术栈">
          <el-input v-model="form.techStack" placeholder="Java, SpringBoot, Vue, MySQL..." />
        </el-form-item>
        <!-- 邮箱 -->
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="your@email.com" />
        </el-form-item>
        <!-- GitHub主页 -->
        <el-form-item label="GitHub">
          <el-input v-model="form.github" placeholder="https://github.com/yourname" />
        </el-form-item>
        <!-- 保存按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { profileApi, uploadApi } from '../../api'

// 表单数据：名称、头像、简介、技术栈、邮箱、GitHub
const form = ref({ name: '', avatar: '', bio: '', techStack: '', email: '', github: '' })
// 页面加载状态
const loading = ref(false)
// 保存按钮加载状态
const saving = ref(false)

// 从服务器加载当前个人信息
async function loadProfile() {
  loading.value = true
  try {
    const res = await profileApi.get()
    if (res.data) form.value = res.data
  } finally { loading.value = false }
}

// 保存个人信息到服务器
async function handleSave() {
  saving.value = true
  try {
    await profileApi.update(form.value)
    ElMessage.success('保存成功')
  } finally { saving.value = false }
}

// 上传头像图片，将返回的URL填到表单中
async function uploadAvatar(options) {
  const res = await uploadApi.upload(options.file)
  form.value.avatar = res.data
}

// 页面挂载时加载个人信息
onMounted(loadProfile)
</script>

<style scoped>
.avatar-row { display: flex; align-items: center; gap: 12px; }
</style>
