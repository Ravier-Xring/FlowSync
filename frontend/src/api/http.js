import axios from 'axios'
import { ElMessage } from 'element-plus'

const http=axios.create({baseURL:'/api',timeout:20000})

http.interceptors.request.use(config=>{
  const cache=sessionStorage.getItem('flowSyncUser')
  const user=cache?JSON.parse(cache):null
  const token=sessionStorage.getItem('flowSyncToken')
  const excluded=config.url?.includes('/auth/login')||config.url?.includes('/ai/')
  if(token)config.headers={...(config.headers||{}),Authorization:`Bearer ${token}`}
  if(user?.id&&!excluded) config.params={...(config.params||{}),currentUserId:user.id}
  return config
})

http.interceptors.response.use(response=>{
  const payload=response.data
  if(payload&&payload.success===false){
    ElMessage.error(payload.message||'操作失败')
    return Promise.reject(new Error(payload.message||'操作失败'))
  }
  return payload
},error=>{
  ElMessage.error(error.response?.data?.message||error.message||'网络请求失败')
  return Promise.reject(error)
})
export default http
