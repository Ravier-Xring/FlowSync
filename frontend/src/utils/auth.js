export const getCurrentUser=()=>{
  const cache=sessionStorage.getItem('flowSyncUser')
  return cache?JSON.parse(cache):null
}
export const setCurrentUser=user=>sessionStorage.setItem('flowSyncUser',JSON.stringify(user))
export const clearCurrentUser=()=>{
  sessionStorage.removeItem('flowSyncUser')
  sessionStorage.removeItem('flowSyncToken')
}
export const isLeaderUser=user=>user&&(user.role==='负责人'||user.role==='管理员')
