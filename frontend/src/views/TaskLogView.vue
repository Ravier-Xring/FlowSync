<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>进度记录</h2>
        <p>为可见任务新增进度记录</p>
      </div>
      <div class="toolbar-actions">
        <el-select v-model="taskFilter" placeholder="按任务筛选" clearable style="width:230px"><el-option v-for="t in tasks" :key="t.id" :label="t.title" :value="t.id" /></el-select>
        <el-button type="primary" @click="openDialog">新增进度</el-button>
      </div>
    </section>
    <section class="content-card">
      <el-table :data="filtered" border>
        <el-table-column label="任务" min-width="190"><template #default="s">{{ taskName(s.row.taskId) }}</template></el-table-column>
        <el-table-column prop="progressPercent" label="进度" width="180"><template #default="s"><el-progress :percentage="s.row.progressPercent" /></template></el-table-column>
        <el-table-column prop="content" label="进度说明" min-width="260" />
        <el-table-column label="记录人" width="110"><template #default="s">{{ userName(s.row.operatorId) }}</template></el-table-column>
        <el-table-column prop="createTime" label="记录时间" width="180" />
      </el-table>
      <el-pagination class="table-pagination" layout="prev, pager, next, sizes, total" :page-sizes="[10,20,50]" v-model:current-page="page.page" v-model:page-size="page.size" :total="page.total" @current-change="load" @size-change="load" />
    </section>
    <el-dialog v-model="dialog" title="新增进度记录" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="任务"><el-select v-model="form.taskId" style="width:100%"><el-option v-for="t in tasks" :key="t.id" :label="t.title" :value="t.id" /></el-select></el-form-item>
        <el-form-item label="进度百分比"><el-slider v-model="form.progressPercent" show-input /></el-form-item>
        <el-form-item label="进度说明"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { getCurrentUser } from '../utils/auth'
import { applyPage, pageParams, newPage } from '../utils/page'

const current = getCurrentUser()
const loading = ref(false)
const dialog = ref(false)
const logs = ref([])
const tasks = ref([])
const users = ref([])
const taskFilter = ref(null)
const page = reactive(newPage())
const form = reactive({ taskId: null, progressPercent: 0, content: '', operatorId: current.id })
const filtered = computed(() => logs.value.filter(l => !taskFilter.value || l.taskId === taskFilter.value))
const taskName = id => tasks.value.find(t => t.id === id)?.title || '-'
const userName = id => users.value.find(u => u.id === id)?.realName || '-'
const readRecords = r => r.data.records || r.data || []
const loadLookups = async () => {
  const [t, u] = await Promise.all([http.get('/tasks', { params: { page: 1, size: 100 } }), http.get('/users', { params: { page: 1, size: 100 } })])
  tasks.value = readRecords(t)
  users.value = readRecords(u)
}
const load = async () => {
  loading.value = true
  try {
    const [l] = await Promise.all([http.get('/task-logs', { params: pageParams(page) }), loadLookups()])
    applyPage(l, logs, page)
  } finally { loading.value = false }
}
const openDialog = () => { Object.assign(form, { taskId: tasks.value[0]?.id || null, progressPercent: 0, content: '', operatorId: current.id }); dialog.value = true }
const save = async () => {
  if (!form.taskId || !form.content) return ElMessage.warning('请选择任务并填写说明')
  await http.post('/task-logs', form)
  ElMessage.success('新增成功')
  dialog.value = false
  load()
}
onMounted(load)
</script>
