<template>
  <div class="page-shell" v-loading="loading">
    <section class="toolbar-card">
      <div>
        <h2>成员列表</h2>
        <p>查看系统成员基础信息，密码字段不会返回前端</p>
      </div>
    </section>
    <section class="content-card">
      <el-table :data="users" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="role" label="角色" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="电子邮箱" min-width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
const users = ref([])
const page = reactive(newPage())

const load = async () => {
  loading.value = true
  try {
    applyPage(await http.get('/users', { params: pageParams(page) }), users, page)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
