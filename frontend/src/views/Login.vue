<template>
<div class="login-page"><div class="login-panel">
  <div class="brand-name">FlowSync</div><div class="brand-desc">小组任务协同管理系统</div>
  <h1>项目、任务、进度与总结一体化管理</h1><p class="subtitle">请输入测试账号登录系统</p>
  <el-form :model="form" label-position="top" @keyup.enter="login">
    <el-form-item label="用户名"><el-input v-model="form.username" size="large"/></el-form-item>
    <el-form-item label="密码"><el-input v-model="form.password" size="large" show-password/></el-form-item>
    <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="login">登录系统</el-button>
  </el-form>
  <div class="test-tip">测试账号：leader / 123456、member1 / 123456、member2 / 123456</div>
</div></div>
</template>
<script setup>
import{reactive,ref}from'vue';import{useRouter}from'vue-router';import{ElMessage}from'element-plus'
import http from'../api/http';import{setCurrentUser}from'../utils/auth'
const router=useRouter(),loading=ref(false),form=reactive({username:'leader',password:'123456'})
const login=async()=>{if(!form.username||!form.password)return ElMessage.warning('请输入用户名和密码');loading.value=true;try{const r=await http.post('/auth/login',form);setCurrentUser(r.data.user);sessionStorage.setItem('flowSyncToken',r.data.token);ElMessage.success('登录成功');router.push('/home/overview')}finally{loading.value=false}}
</script>
<style scoped>
.login-page{width:100vw;height:100vh;display:flex;align-items:center;justify-content:center;background:radial-gradient(circle at 20% 20%,rgba(64,158,255,.16),transparent 28%),linear-gradient(135deg,#edf5ff,#fff)}
.login-panel{width:520px;padding:42px 40px 36px;border-radius:20px;background:rgba(255,255,255,.96);box-shadow:0 22px 60px rgba(22,52,86,.13);border:1px solid #e8eef7}
.brand-name{font-size:32px;font-weight:800;color:#0f2942}.brand-desc{margin-top:8px;color:#66758a}h1{margin:28px 0 8px;font-size:23px;color:#172b4d}.subtitle{margin:0 0 26px;color:#7a869a}.login-btn{width:100%}.test-tip{margin-top:18px;padding:12px 14px;border-radius:10px;background:#f5f8fc;color:#6b778c;font-size:12px}
</style>
