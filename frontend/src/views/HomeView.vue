<template>
  <div class="layout-page">
    <aside class="side-bar">
      <div class="logo-area">
        <div class="logo-title">FlowSync</div>
        <div class="logo-subtitle">小组任务协同</div>
      </div>
      <nav class="menu-area">
        <div v-for="group in visibleMenus" :key="group.groupTitle" class="menu-group">
          <div class="group-title">{{ group.groupTitle }}</div>
          <router-link v-for="item in group.children" :key="item.path" :to="item.path" class="menu-item" active-class="active">
            <span class="menu-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </router-link>
        </div>
      </nav>
    </aside>
    <main class="main-panel">
      <header class="top-header">
        <div>
          <h1>{{ route.meta.title || 'FlowSync' }}</h1>
          <p>{{ route.meta.subtitle }}</p>
        </div>
        <div class="header-actions">
          <div class="user-info">
            <strong>{{ currentUser.realName }}</strong>
            <span>{{ currentUser.role }}</span>
          </div>
          <el-button @click="refreshKey++">刷新数据</el-button>
          <el-button type="danger" plain @click="logout">退出登录</el-button>
        </div>
      </header>
      <section class="content-wrap"><router-view :key="refreshKey" /></section>
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCurrentUser, clearCurrentUser, isLeaderUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const refreshKey = ref(0)
const currentUser = getCurrentUser() || {}
const menus = [
  { groupTitle: '工作台', children: [{ label: '总览', path: '/home/overview', icon: '⌂' }] },
  {
    groupTitle: '业务管理',
    children: [
      { label: '项目管理', path: '/home/projects', icon: '▣' },
      { label: '任务拆解', path: '/home/breakdown', icon: '✣', leaderOnly: true },
      { label: '任务管理', path: '/home/tasks', icon: '▤' },
      { label: '进度跟踪', path: '/home/progress', icon: '◴' },
      { label: '总结中心', path: '/home/summary', icon: '▧' }
    ]
  },
  {
    groupTitle: '系统信息',
    children: [
      { label: '成员列表', path: '/home/members', icon: '♙' },
      { label: '操作日志', path: '/home/operation-logs', icon: '☷', leaderOnly: true },
      { label: '个人信息', path: '/home/profile', icon: '●' }
    ]
  }
]
const visibleMenus = computed(() => menus.map(g => ({ ...g, children: g.children.filter(i => !i.leaderOnly || isLeaderUser(currentUser)) })))
const logout = () => { clearCurrentUser(); router.push('/login') }
</script>

<style scoped>
.layout-page{display:flex;width:100%;min-height:100vh;background:#f3f6fb}.side-bar{position:fixed;inset:0 auto 0 0;width:250px;background:#fff;border-right:1px solid #e6edf5;overflow-y:auto}.logo-area{height:105px;padding:24px 26px;border-bottom:1px solid #eef2f7}.logo-title{font-size:26px;font-weight:800;color:#102a43}.logo-subtitle{margin-top:9px;color:#6b778c}.menu-area{padding:18px 0}.menu-group{margin-bottom:16px}.group-title{padding:0 26px 10px;font-size:13px;color:#9aa7b8}.menu-item{display:flex;align-items:center;height:50px;padding:0 27px;gap:13px;color:#334e68;text-decoration:none;border-right:4px solid transparent}.menu-item:hover{background:#f4f8fd;color:#1f6fbd}.menu-item.active{background:#e9f3ff;color:#1f6fbd;border-right-color:#1f6fbd;font-weight:700}.menu-icon{width:18px;text-align:center}.main-panel{margin-left:250px;flex:1;min-width:0;padding:12px 18px 28px}.top-header{min-height:92px;background:#fff;border:1px solid #e8eef7;display:flex;align-items:center;justify-content:space-between;padding:0 26px}.top-header h1{margin:0 0 8px;font-size:27px;color:#102a43}.top-header p{margin:0;color:#66758a}.header-actions{display:flex;align-items:center;gap:14px}.user-info{display:flex;flex-direction:column;text-align:right}.user-info span{color:#6b778c;font-size:13px}.content-wrap{margin-top:24px}
</style>
