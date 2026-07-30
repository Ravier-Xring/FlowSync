<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>AI 任务拆解</h2>
        <p>AI 仅提供建议，导入前可调整负责人</p>
      </div>
      <el-button type="primary" :loading="generating" @click="generate">生成拆解方案</el-button>
    </section>
    <section class="content-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择项目"><el-select v-model="form.projectId" style="width:100%" @change="syncProject"><el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" /></el-select></el-form-item>
        <el-form-item label="任务目标"><el-input v-model="form.goal" /></el-form-item>
        <el-form-item label="补充说明"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
    </section>
    <section v-if="plan.items.length" class="content-card">
      <el-alert :title="plan.summary" type="info" :closable="false" />
      <el-table ref="tableRef" :data="plan.items" border style="margin-top:18px" @selection-change="selected=$event">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="title" label="任务标题" min-width="170" />
        <el-table-column prop="description" label="任务说明" min-width="230" />
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column prop="suggestedDays" label="建议天数" width="100" />
        <el-table-column label="负责人" width="160"><template #default="s"><el-select v-model="s.row.assigneeId"><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></template></el-table-column>
      </el-table>
      <div style="margin-top:18px;text-align:right"><el-button type="success" :disabled="!selected.length" @click="importTasks">导入选中任务</el-button></div>
    </section>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { getCurrentUser } from '../utils/auth'

const current = getCurrentUser()
const loading = ref(false)
const generating = ref(false)
const projects = ref([])
const users = ref([])
const selected = ref([])
const tableRef = ref()
const form = reactive({ projectId: null, operatorId: current.id, projectName: '', goal: '', description: '' })
const plan = reactive({ summary: '', items: [] })
const readRecords = r => r.data.records || r.data || []
const load = async () => {
  loading.value = true
  try {
    const [p, u] = await Promise.all([http.get('/projects', { params: { page: 1, size: 100 } }), http.get('/users', { params: { page: 1, size: 100 } })])
    projects.value = readRecords(p)
    users.value = readRecords(u)
    if (projects.value.length) { form.projectId = projects.value[0].id; syncProject() }
  } finally { loading.value = false }
}
const syncProject = () => { form.projectName = projects.value.find(p => p.id === form.projectId)?.name || '' }
const generate = async () => {
  if (!form.projectId || !form.goal) return ElMessage.warning('请选择项目并填写任务目标')
  generating.value = true
  try {
    syncProject()
    const r = await http.post('/ai/task-plan', form)
    plan.summary = r.data.summary
    plan.items = r.data.items || []
    await nextTick()
    plan.items.forEach(x => tableRef.value?.toggleRowSelection(x, true))
  } finally { generating.value = false }
}
const importTasks = async () => {
  if (selected.value.some(i => !i.assigneeId)) return ElMessage.warning('每个选中任务都必须选择负责人')
  await http.post('/ai/task-plan/import', { projectId: form.projectId, creatorId: current.id, items: selected.value })
  ElMessage.success('任务导入成功')
  plan.items = []
  selected.value = []
}
onMounted(load)
</script>
