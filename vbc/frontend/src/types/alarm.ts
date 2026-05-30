export type RuleType = 'ZONE_INTRUSION' | 'CROWD_GATHERING' | 'PEOPLE_EXCEED' | 'LOITERING'
export type AlarmLevel = 'INFO' | 'WARNING' | 'CRITICAL'

export interface AlarmRule {
  id: number
  ruleName: string
  ruleType: RuleType
  videoId: number | null
  zoneId: number | null
  thresholdValue: number
  durationSeconds: number
  enabled: boolean
  alarmLevel: AlarmLevel
  notifyMethods: string[]
  createdAt: string
  updatedAt: string
}

export interface AlarmRecord {
  id: number
  ruleId: number
  videoId: number
  alarmType: RuleType
  alarmLevel: AlarmLevel
  alarmMessage: string
  snapshotPath: string
  frameIndex: number
  timestampSeconds: number
  handled: boolean
  handledBy: string
  handledAt: string
  createdAt: string
}
