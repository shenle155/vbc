import request from './request'
import type { Result, PageResult } from '@/types/common'
import type { Device } from '@/types/device'

export function getDeviceList(params: {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string
}) {
  return request.get<Result<PageResult<Device>>>('/devices', { params })
}

export function getDevice(id: number) {
  return request.get<Result<Device>>(`/devices/${id}`)
}

export function createDevice(data: {
  deviceName: string
  deviceCode: string
  ipAddress?: string
  streamUrl?: string
  location?: string
}) {
  return request.post<Result<Device>>('/devices', data)
}

export function updateDevice(id: number, data: {
  deviceName: string
  deviceCode: string
  ipAddress?: string
  streamUrl?: string
  location?: string
}) {
  return request.put<Result<Device>>(`/devices/${id}`, data)
}

export function deleteDevice(id: number) {
  return request.delete<Result<null>>(`/devices/${id}`)
}

export function deviceHeartbeat(id: number, status: string) {
  return request.post<Result<null>>(`/devices/${id}/heartbeat`, { status })
}
