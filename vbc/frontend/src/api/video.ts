import request from './request'
import type { Result, PageResult } from '@/types/common'
import type { Video } from '@/types/video'

export function getVideoList(params: {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string
  deviceId?: number
}) {
  return request.get<Result<PageResult<Video>>>('/videos', { params })
}

export function getVideo(id: number) {
  return request.get<Result<Video>>(`/videos/${id}`)
}

export function uploadVideo(formData: FormData) {
  return request.post<Result<Video>>('/videos/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000,
  })
}

export function deleteVideo(id: number) {
  return request.delete<Result<null>>(`/videos/${id}`)
}
