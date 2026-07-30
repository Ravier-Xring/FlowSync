<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>任务列表</h2>
        <p>支持按项目、状态和标题筛选，成员只看到自己的任务</p>
      </div>
      <div class="toolbar-actions">
        <el-select v-model="projectFilter" placeholder="项目" clearable style="width:180px"><el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" /></el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width:120px"><el-option v-for="v in ['未开始','进行中','已完成']" :key="v" :label="v" :value="v" /></el-select>
        <el-input v-model="keyword" placeholder="搜索任务" clearable style="width:190px" />
        <el-button v-if="isLeader" type="primary" @click="openCreate">新增任务</el-button>
      </div>
    </section>
    <section class="content-card">
      <el-table :data="filtered" border>
        <el-table-column prop="title" label="任务标题" min-width="180" />
        <el-table-column label="所属项目" min-width="170"><template #default="s">{{ projectName(s.row.projectId) }}</template></el-table-column>
        <el-table-column label="负责人" width="100"><template #default="s">{{ userName(s.row.assigneeId) }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column prop="dueDate" label="截止日期" width="120" />
        <el-table-column label="操作" width="145">
          <template #default="s">
            <el-button v-if="canEdit(s.row)" link type="primary" @click="openEdit(s.row)">{{ isLeader ? '编辑' : '更新状态' }}</el-button>
            <el-button v-if="isLeader" link type="danger" @click="removeRow(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" layout="prev, pager, next, sizes, total" :page-sizes="[10,20,50]" v-model:current-page="page.page" v-model:page-size="page.size" :total="page.total" @current-change="load" @size-change="load" />
    </section>
    <el-dialog v-model="dialog" :title="memberEdit ? '更新任务状态' : form.id ? '编辑任务' : '新增任务'" width="650px">
      <el-form :model="form" label-width="95px">
        <el-form-item label="所属项目"><el-select v-model="form.projectId" :disabled="memberEdit" style="width:100%"><el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" /></el-select></el-form-item>
        <el-form-item label="父任务"><el-select v-model="form.parentId" :disabled="memberEdit" clearable style="width:100%"><el-option v-for="t in tasks.filter(x=>x.id!==form.id)" :key="t.id" :label="t.title" :value="t.id" /></el-select></el-form-item>
        <el-form-item label="任务标题"><el-input v-model="form.title" :disabled="memberEdit" /></el-form-item>
        <el-form-item label="任务说明"><el-input v-model="form.description" :disabled="memberEdit" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="负责人"><el-select v-model="form.assigneeId" :disabled="memberEdit" style="width:100%"><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option v-for="v in ['未开始','进行中','已完成']" :key="v" :label="v" :value="v" /></el-select></el-form-item>
        <el-form-item label="优先级"><el-select v-model="form.priority" :disabled="memberEdit" style="width:100%"><el-option v-for="v in ['低','中','高']" :key="v" :label="v" :value="v" /></el-select></el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="form.dueDate" :disabled="memberEdit" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item v-if="isLeader" label="AI建议"><el-input v-model="form.aiSuggestion" type="textarea" :rows="3" /><el-button style="margin-top:8px" :loading="aiLoading" @click="getSuggestion">生成建议</el-button></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'
import { getCurrentUser, isLeaderUser } from '../utils/auth'
import { applyPage, pageParams, newPage } from '../utils/page'

const current = getCurrentUser()
const isLeader = isLeaderUser(current)
const loading = ref(false)
const aiLoading = ref(false)
const dialog = ref(false)
const memberEdit = ref(false)
const tasks = ref([])
const projects = ref([])
const users = ref([])
const keyword = ref('')
const statusFilter = ref('')
const projectFilter = ref(null)
const page = reactive(newPage())
const form = reactive({ id: null, projectId: null, parentId: null, title: '', description: '', assigneeId: null, creatorId: null, status: '未开始', priority: '中', dueDate: null, aiSuggestion: null })
const filtered = computed(() => tasks.value.filter(t => (!keyword.value || t.title.includes(keyword.value)) && (!statusFilter.value || t.status === statusFilter.value) && (!projectFilter.value || t.projectId === projectFilter.value)))
const projectName = id => projects.value.find(p => p.id === id)?.name || '-'
const userName = id => users.value.find(u => u.id === id)?.realName || '-'
const canEdit = r => isLeader || r.assigneeId === current.id
const readRecords = r => r.data.records || r.data || []
const loadLookups = async () => {
  const [p, u] = await Promise.all([http.get('/projects', { params: { page: 1, size: 100 } }), http.get('/users', { params: { page: 1, size: 100 } })])
  projects.value = readRecords(p)
  users.value = readRecords(u)
}
const load = async () => {
  loading.value = true
  try {
    const [t] = await Promise.all([http.get('/tasks', { params: pageParams(page) }), loadLookups()])
    applyPage(t, tasks, page)
  } finally { loading.value = false }
}
const openCreate = () => { Object.assign(form, { id: null, projectId: projects.value[0]?.id || null, parentId: null, title: '', description: '', assigneeId: null, creatorId: current.id, status: '未开始', priority: '中', dueDate: null, aiSuggestion: null }); memberEdit.value = false; dialog.value = true }
const openEdit = r => { Object.assign(form, r); memberEdit.value = !isLeader; dialog.value = true }
const save = async () => {
  if (!form.projectId || !form.title || !form.assigneeId) return ElMessage.warning('请补全项目、标题和负责人')
  await http.post('/tasks', { ...form })
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}
const removeRow = r => ElMessageBox.confirm(`确认删除任务“${r.title}”吗？删除后会同步清理相关进度和总结。`, '提示', { type: 'warning' }).then(async () => { await http.delete(`/tasks/${r.id}`); ElMessage.success('删除成功'); load() }).catch(() => {})
const getSuggestion = async () => { aiLoading.value = true; try { form.aiSuggestion = (await http.post('/ai/task-suggestion', { projectName: projectName(form.projectId), taskTitle: form.title, taskDescription: form.description })).data.suggestion } finally { aiLoading.value = false } }
onMounted(load)
</script>
