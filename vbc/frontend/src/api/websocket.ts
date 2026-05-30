import request from './request'
import type { Result } from '@/types/common'

export function getWsToken() {
  return request.get<Result<{ token: string; brokerUrl: string }>>('/ws/token')
}
