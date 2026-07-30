<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>操作日志</h2>
        <p>记录登录、项目、任务、进度、总结等关键操作</p>
      </div>
    </section>
    <section class="content-card">
      <el-table :data="logs" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="action" label="操作" width="110" />
        <el-table-column prop="operatorId" label="操作人ID" width="100" />
        <el-table-column prop="targetId" label="对象ID" width="100" />
        <el-table-column prop="detail" label="详情" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
      <el-pagination
        class="table-pagination"
        layout="prev, pager, next, sizes, total"
        :page-sizes="[10, 20, 50]"
        v-model:current-page="page.page"
        v-model:page-size="page.size"
        :total="page.total"
        @current-change="load"
        @size-change="load"
      />
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import http from '../api/http'
import { applyPage, pageParams, newPage } from '../utils/page'

const loading = ref(false)
const logs = ref([])
const page = reactive(newPage())

const load = async () => {
  loading.value = true
  try {
    applyPage(await http.get('/operation-logs', { params: pageParams(page) }), logs, page)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
