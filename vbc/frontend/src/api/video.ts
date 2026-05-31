import axios from 'axios'
import type { Result, PageResult } from '@/types/common'
import type { Video } from '@/types/video'

const api = axios.create({ baseURL: '/api/v1', timeout: 30000 })
const uploadApi = axios.create({ baseURL: 'http://localhost:8080/api/v1', timeout: 300000 })

export function getVideoList(params: Record<string, any>) {
  return api.get<Result<PageResult<Video>>>('/videos', { params })
}

export function getVideo(id: number) {
  return api.get<Result<Video>>(`/videos/${id}`)
}

export function uploadVideo(formData: FormData) {
  return uploadApi.post<Result<Video>>('/videos/upload', formData)
}

export function deleteVideo(id: number) {
  return api.delete<Result<null>>(`/videos/${id}`)
}
