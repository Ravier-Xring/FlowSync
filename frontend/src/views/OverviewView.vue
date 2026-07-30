<template>
  <div class="page-shell" v-loading="loading">
    <section class="overview-hero">
      <div>
        <h2>工作台</h2>
        <p>查看项目、任务、进度和总结的整体执行情况</p>
      </div>
      <div class="health-panel">
        <span>任务完成率</span>
        <strong>{{ data.taskDoneRate || 0 }}%</strong>
        <el-progress :percentage="data.taskDoneRate || 0" :stroke-width="8" :show-text="false" />
      </div>
    </section>

    <div class="stats-grid">
      <div v-for="x in cards" :key="x.label" class="stat-card">
        <span>{{ x.label }}</span>
        <strong>{{ x.value }}</strong>
        <small>{{ x.note }}</small>
      </div>
    </div>

    <div class="alert-grid">
      <section class="content-card compact-card">
        <h3>执行预警</h3>
        <div class="warning-list">
          <div>
            <span>逾期未完成任务</span>
            <strong>{{ data.overdueTaskCount || 0 }}</strong>
          </div>
          <div>
            <span>7 天内到期任务</span>
            <strong>{{ data.dueSoonTaskCount || 0 }}</strong>
          </div>
          <div>
            <span>我的待处理任务</span>
            <strong>{{ data.myOpenTaskCount || 0 }}</strong>
          </div>
        </div>
      </section>
      <section class="content-card compact-card">
        <h3>个人任务</h3>
        <div class="personal-grid">
          <div><span>全部</span><strong>{{ data.myTaskCount || 0 }}</strong></div>
          <div><span>未完成</span><strong>{{ data.myOpenTaskCount || 0 }}</strong></div>
          <div><span>已完成</span><strong>{{ data.myFinishedTaskCount || 0 }}</strong></div>
          <div><span>即将到期</span><strong>{{ data.myDueSoonTaskCount || 0 }}</strong></div>
        </div>
      </section>
    </div>

    <div class="panel-grid">
      <section class="content-card">
        <h3>任务状态</h3>
        <div class="bar-list">
          <div v-for="item in data.taskStatusStats || []" :key="item.name" class="bar-row">
            <span>{{ item.name }}</span>
            <div class="bar-track"><i :style="barStyle(item, data.taskStatusStats)" /></div>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </section>
      <section class="content-card">
        <h3>任务优先级</h3>
        <div class="bar-list">
          <div v-for="item in data.taskPriorityStats || []" :key="item.name" class="bar-row">
            <span>{{ item.name }}</span>
            <div class="bar-track"><i :style="barStyle(item, data.taskPriorityStats)" /></div>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </section>
    </div>

    <div class="panel-grid">
      <section class="content-card">
        <div class="section-title">
          <h3>最近项目</h3>
          <span>项目完成率 {{ data.projectDoneRate || 0 }}%</span>
        </div>
        <el-table :data="data.recentProjects || []" border>
          <el-table-column prop="name" label="项目名称" min-width="180" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="priority" label="优先级" width="90" />
          <el-table-column prop="endDate" label="结束日期" width="120" />
        </el-table>
      </section>
      <section class="content-card">
        <div class="section-title">
          <h3>最近任务</h3>
          <span>未完成 {{ data.openTaskCount || 0 }}</span>
        </div>
        <el-table :data="data.recentTasks || []" border>
          <el-table-column prop="title" label="任务标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="priority" label="优先级" width="90" />
          <el-table-column prop="dueDate" label="截止日期" width="120" />
        </el-table>
      </section>
    </div>

    <div class="panel-grid">
      <section class="content-card">
        <h3>我的最近任务</h3>
        <el-table :data="data.myRecentTasks || []" border empty-text="暂无分配给你的任务">
          <el-table-column prop="title" label="任务标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="priority" label="优先级" width="90" />
          <el-table-column prop="dueDate" label="截止日期" width="120" />
        </el-table>
      </section>
      <section class="content-card">
        <h3>最近总结</h3>
        <el-table :data="data.recentSummaries || []" border>
          <el-table-column prop="summaryType" label="类型" width="110" />
          <el-table-column prop="content" label="总结内容" min-width="220" show-overflow-tooltip />
          <el-table-column label="创建时间" width="160">
            <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
          </el-table-column>
        </el-table>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import http from '../api/http'

const loading = ref(false)
const data = reactive({})

const cards = computed(() => [
  { label: '成员总数', value: data.userCount || 0, note: '系统成员' },
  { label: '项目总数', value: data.projectCount || 0, note: `进行中 ${data.activeProjectCount || 0}` },
  { label: '任务总数', value: data.taskCount || 0, note: `已完成 ${data.finishedTaskCount || 0}` },
  { label: '总结总数', value: data.summaryCount || 0, note: `进度记录 ${data.logCount || 0}` }
])

const maxValue = list => Math.max(1, ...(list || []).map(item => Number(item.value) || 0))
const barStyle = (item, list) => ({ width: `${Math.round(((Number(item.value) || 0) / maxValue(list)) * 100)}%` })
const formatDate = value => (value ? String(value).replace('T', ' ').slice(0, 16) : '-')

const load = async () => {
  loading.value = true
  try {
    Object.assign(data, (await http.get('/overview')).data)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.overview-hero {
  min-height: 128px;
  padding: 26px 28px;
  border: 1px solid #dfe8f5;
  border-radius: 14px;
  background: linear-gradient(135deg, #ffffff 0%, #eef6ff 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}
.overview-hero h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #102a43;
}
.overview-hero p {
  margin: 0;
  color: #62748a;
}
.health-panel {
  width: 220px;
  padding: 16px;
  border: 1px solid #e4edf8;
  border-radius: 10px;
  background: rgba(255,255,255,.8);
}
.health-panel span {
  display: block;
  margin-bottom: 8px;
  color: #62748a;
  font-size: 13px;
}
.health-panel strong {
  display: block;
  margin-bottom: 10px;
  font-size: 30px;
  color: #102a43;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
  margin-top: 20px;
}
.stat-card {
  min-height: 124px;
  padding: 22px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e8eef7;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.stat-card span,
.stat-card small {
  color: #66758a;
}
.stat-card strong {
  font-size: 36px;
  color: #102a43;
}
.alert-grid,
.panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}
.compact-card {
  min-height: 150px;
}
.content-card h3 {
  margin: 0 0 18px;
  color: #102a43;
}
.warning-list,
.personal-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.personal-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
.warning-list div,
.personal-grid div {
  min-height: 76px;
  padding: 14px;
  border-radius: 10px;
  background: #f8fafd;
  border: 1px solid #e8eef7;
}
.warning-list span,
.personal-grid span {
  display: block;
  margin-bottom: 10px;
  color: #66758a;
  font-size: 13px;
}
.warning-list strong,
.personal-grid strong {
  font-size: 26px;
  color: #102a43;
}
.bar-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.bar-row {
  display: grid;
  grid-template-columns: 72px 1fr 44px;
  align-items: center;
  gap: 12px;
}
.bar-row span {
  color: #42526e;
}
.bar-row strong {
  text-align: right;
  color: #102a43;
}
.bar-track {
  height: 10px;
  border-radius: 999px;
  background: #edf2f7;
  overflow: hidden;
}
.bar-track i {
  display: block;
  height: 100%;
  min-width: 4px;
  border-radius: inherit;
  background: #2f80ed;
}
.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}
.section-title h3 {
  margin: 0;
}
.section-title span {
  color: #66758a;
  font-size: 13px;
}
@media (max-width: 1180px) {
  .stats-grid,
  .alert-grid,
  .panel-grid {
    grid-template-columns: 1fr 1fr;
  }
  .overview-hero {
    align-items: flex-start;
    flex-direction: column;
  }
}
@media (max-width: 760px) {
  .stats-grid,
  .alert-grid,
  .panel-grid,
  .warning-list,
  .personal-grid {
    grid-template-columns: 1fr;
  }
  .health-panel {
    width: 100%;
  }
}
</style>
