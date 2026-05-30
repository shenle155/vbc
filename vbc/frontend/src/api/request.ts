import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '@/types/common'

const instance: AxiosInstance = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
})

instance.interceptors.response.use(
  (response: AxiosResponse<Result<any>>) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return response
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default instance
