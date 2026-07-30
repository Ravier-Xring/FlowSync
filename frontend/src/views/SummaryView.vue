<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>总结列表</h2>
        <p>阶段总结和最终总结按权限展示</p>
      </div>
      <el-button type="primary" @click="openDialog">新增总结</el-button>
    </section>
    <section class="content-card">
      <el-table :data="summaries" border>
        <el-table-column label="项目" min-width="180"><template #default="s">{{ projectName(s.row.projectId) }}</template></el-table-column>
        <el-table-column label="关联任务" min-width="170"><template #default="s">{{ taskName(s.row.taskId) }}</template></el-table-column>
        <el-table-column prop="summaryType" label="类型" width="110" />
        <el-table-column prop="content" label="总结内容" min-width="300" show-overflow-tooltip />
        <el-table-column label="创建人" width="110"><template #default="s">{{ userName(s.row.createdBy) }}</template></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
      <el-pagination class="table-pagination" layout="prev, pager, next, sizes, total" :page-sizes="[10,20,50]" v-model:current-page="page.page" v-model:page-size="page.size" :total="page.total" @current-change="load" @size-change="load" />
    </section>
    <el-dialog v-model="dialog" title="新增总结" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="项目"><el-select v-model="form.projectId" style="width:100%"><el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" /></el-select></el-form-item>
        <el-form-item label="关联任务"><el-select v-model="form.taskId" clearable style="width:100%"><el-option v-for="t in availableTasks" :key="t.id" :label="t.title" :value="t.id" /></el-select></el-form-item>
        <el-form-item label="总结类型"><el-select v-model="form.summaryType" style="width:100%"><el-option label="阶段总结" value="阶段总结" /><el-option label="最终总结" value="最终总结" /></el-select></el-form-item>
        <el-form-item label="总结内容"><el-input v-model="form.content" type="textarea" :rows="5" /></el-form-item>
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
const summaries = ref([])
const projects = ref([])
const tasks = ref([])
const users = ref([])
const page = reactive(newPage())
const form = reactive({ projectId: null, taskId: null, summaryType: '阶段总结', content: '', createdBy: current.id })
const availableTasks = computed(() => tasks.value.filter(t => !form.projectId || t.projectId === form.projectId))
const projectName = id => projects.value.find(p => p.id === id)?.name || '-'
const taskName = id => id ? (tasks.value.find(t => t.id === id)?.title || '-') : '未关联'
const userName = id => users.value.find(u => u.id === id)?.realName || '-'
const readRecords = r => r.data.records || r.data || []
const loadLookups = async () => {
  const [p, t, u] = await Promise.all([http.get('/projects', { params: { page: 1, size: 100 } }), http.get('/tasks', { params: { page: 1, size: 100 } }), http.get('/users', { params: { page: 1, size: 100 } })])
  projects.value = readRecords(p)
  tasks.value = readRecords(t)
  users.value = readRecords(u)
}
const load = async () => {
  loading.value = true
  try {
    const [s] = await Promise.all([http.get('/summaries', { params: pageParams(page) }), loadLookups()])
    applyPage(s, summaries, page)
  } finally { loading.value = false }
}
const openDialog = () => { Object.assign(form, { projectId: projects.value[0]?.id || null, taskId: null, summaryType: '阶段总结', content: '', createdBy: current.id }); dialog.value = true }
const save = async () => {
  if (!form.projectId || !form.content) return ElMessage.warning('请选择项目并填写总结')
  await http.post('/summaries', form)
  ElMessage.success('新增成功')
  dialog.value = false
  load()
}
onMounted(load)
</script>
