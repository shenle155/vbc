import request from './request'
import type { Result } from '@/types/common'
import type { DashboardOverview, PersonTrend, AlarmStats, DeviceStatus } from '@/types/dashboard'

export function getOverview() {
  return request.get<Result<DashboardOverview>>('/dashboard/overview')
}

export function getPersonTrend(period = 'HOURLY') {
  return request.get<Result<PersonTrend[]>>('/dashboard/person-trend', { params: { period } })
}

export function getAlarmStats() {
  return request.get<Result<AlarmStats>>('/dashboard/alarm-stats')
}

export function getDeviceStatus() {
  return request.get<Result<DeviceStatus[]>>('/dashboard/device-status')
}
