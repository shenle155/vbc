import request from './request'
import type { Result, PageResult } from '@/types/common'
import type { AlarmRule, AlarmRecord } from '@/types/alarm'

export function getAlarmRecords(params: Record<string, any>) {
  return request.get<Result<PageResult<AlarmRecord>>>('/alarms', { params })
}

export function handleAlarm(id: number) {
  return request.put<Result<null>>(`/alarms/${id}/handle`, { handledBy: 'admin' })
}

export function getAlarmRules(params: Record<string, any>) {
  return request.get<Result<PageResult<AlarmRule>>>('/alarms/rules', { params })
}

export function createAlarmRule(data: Record<string, any>) {
  return request.post<Result<AlarmRule>>('/alarms/rules', data)
}

export function updateAlarmRule(id: number, data: Record<string, any>) {
  return request.put<Result<AlarmRule>>(`/alarms/rules/${id}`, data)
}

export function deleteAlarmRule(id: number) {
  return request.delete<Result<null>>(`/alarms/rules/${id}`)
}
