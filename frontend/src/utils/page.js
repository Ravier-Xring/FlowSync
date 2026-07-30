export const pageParams = page => ({ page: page.page, size: page.size })

export const applyPage = (response, target, page) => {
  const payload = response?.data
  if (payload?.records) {
    target.value = payload.records
    page.total = payload.total || 0
    page.page = payload.page || page.page
    page.size = payload.size || page.size
    return
  }
  target.value = Array.isArray(payload) ? payload : []
  page.total = target.value.length
}

export const newPage = (size = 10) => ({ page: 1, size, total: 0 })
