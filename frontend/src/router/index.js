import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import HomeView from '../views/HomeView.vue'
import OverviewView from '../views/OverviewView.vue'
import ProjectManageView from '../views/ProjectManageView.vue'
import TaskPlanView from '../views/TaskPlanView.vue'
import TaskManageView from '../views/TaskManageView.vue'
import TaskLogView from '../views/TaskLogView.vue'
import SummaryView from '../views/SummaryView.vue'
import MemberView from '../views/MemberView.vue'
import ProfileView from '../views/ProfileView.vue'
import OperationLogView from '../views/OperationLogView.vue'
import { getCurrentUser, isLeaderUser } from '../utils/auth'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  {
    path: '/home',
    component: HomeView,
    redirect: '/home/overview',
    children: [
      { path: 'overview', component: OverviewView, meta: { title: '总览', subtitle: '查看用户、项目、任务与总结统计' } },
      { path: 'projects', component: ProjectManageView, meta: { title: '项目管理', subtitle: '维护项目基础信息、负责人和项目状态' } },
      { path: 'breakdown', component: TaskPlanView, meta: { title: '任务拆解', subtitle: '使用千问辅助拆分任务并推荐负责人', leaderOnly: true } },
      { path: 'tasks', component: TaskManageView, meta: { title: '任务管理', subtitle: '查看、分配和维护任务执行状态' } },
      { path: 'progress', component: TaskLogView, meta: { title: '进度跟踪', subtitle: '记录任务进度百分比与执行说明' } },
      { path: 'summary', component: SummaryView, meta: { title: '总结中心', subtitle: '沉淀阶段总结和最终总结' } },
      { path: 'members', component: MemberView, meta: { title: '成员列表', subtitle: '查看系统中的全部成员信息' } },
      { path: 'operation-logs', component: OperationLogView, meta: { title: '操作日志', subtitle: '查看关键业务操作记录', leaderOnly: true } },
      { path: 'profile', component: ProfileView, meta: { title: '个人信息', subtitle: '查看个人资料并修改登录密码' } }
    ]
  }
]

const router = createRouter({ history: createWebHistory(process.env.BASE_URL), routes })
router.beforeEach(to => {
  const user = getCurrentUser()
  if (to.path !== '/login' && !user) return '/login'
  if (to.path === '/login' && user) return '/home/overview'
  if (to.meta.leaderOnly && !isLeaderUser(user)) return '/home/overview'
  return true
})

export default router
