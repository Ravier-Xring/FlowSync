<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>项目列表</h2>
        <p>按 ID 倒序展示你有权限查看的项目</p>
      </div>
      <div class="toolbar-actions">
        <el-input v-model="keyword" placeholder="搜索项目名称" clearable style="width:210px" />
        <el-button v-if="isLeader" type="primary" @click="openCreate">新建项目</el-button>
      </div>
    </section>
    <section class="content-card">
      <el-table :data="filtered" border>
        <el-table-column prop="name" label="项目名称" min-width="180" />
        <el-table-column prop="description" label="项目说明" min-width="240" show-overflow-tooltip />
        <el-table-column label="负责人" width="120"><template #default="s">{{ userName(s.row.ownerId) }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column v-if="isLeader" label="操作" width="130">
          <template #default="s">
            <el-button link type="primary" @click="openEdit(s.row)">编辑</el-button>
            <el-button link type="danger" @click="removeRow(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" layout="prev, pager, next, sizes, total" :page-sizes="[10,20,50]" v-model:current-page="page.page" v-model:page-size="page.size" :total="page.total" @current-change="load" @size-change="load" />
    </section>

    <el-dialog v-model="dialog" :title="form.id ? '编辑项目' : '新建项目'" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="项目名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="项目说明"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="负责人"><el-select v-model="form.ownerId" style="width:100%"><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option v-for="v in ['未开始','进行中','已完成']" :key="v" :label="v" :value="v" /></el-select></el-form-item>
        <el-form-item label="优先级"><el-select v-model="form.priority" style="width:100%"><el-option v-for="v in ['低','中','高']" :key="v" :label="v" :value="v" /></el-select></el-form-item>
        <el-form-item label="时间范围"><el-date-picker v-model="dates" type="daterange" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
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
const dialog = ref(false)
const keyword = ref('')
const projects = ref([])
const users = ref([])
const dates = ref([])
const page = reactive(newPage())
const form = reactive({ id: null, name: '', description: '', status: '未开始', priority: '中', ownerId: null, startDate: null, endDate: null })
const filtered = computed(() => projects.value.filter(p => !keyword.value || p.name.includes(keyword.value)))
const userName = id => users.value.find(u => u.id === id)?.realName || '-'
const loadUsers = async () => { users.value = (await http.get('/users', { params: { page: 1, size: 100 } })).data.records || [] }
const load = async () => {
  loading.value = true
  try {
    const [p] = await Promise.all([http.get('/projects', { params: pageParams(page) }), loadUsers()])
    applyPage(p, projects, page)
  } finally { loading.value = false }
}
const openCreate = () => { Object.assign(form, { id: null, name: '', description: '', status: '未开始', priority: '中', ownerId: current.id, startDate: null, endDate: null }); dates.value = []; dialog.value = true }
const openEdit = r => { Object.assign(form, r); dates.value = r.startDate && r.endDate ? [r.startDate, r.endDate] : []; dialog.value = true }
const save = async () => {
  if (!form.name) return ElMessage.warning('请输入项目名称')
  form.startDate = dates.value?.[0] || null
  form.endDate = dates.value?.[1] || null
  await http.post('/projects', { ...form })
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}
const removeRow = r => ElMessageBox.confirm(`确认删除项目“${r.name}”吗？删除后会同步清理项目下任务、进度和总结。`, '提示', { type: 'warning' }).then(async () => { await http.delete(`/projects/${r.id}`); ElMessage.success('删除成功'); load() }).catch(() => {})

onMounted(load)
</script>
