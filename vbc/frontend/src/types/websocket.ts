export interface WsDetectionResult {
  type: 'DETECTION_RESULT'
  videoId: number
  frameIndex: number
  timestampSeconds: number
  detections: {
    class: string
    confidence: number
    bbox: { x: number; y: number; w: number; h: number }
  }[]
  personCount: number
  vehicleCount: number
  totalCount: number
  createdAt: string
}

export interface WsAlarmTriggered {
  type: 'ALARM_TRIGGERED'
  alarmId: number
  videoId: number
  alarmType: string
  alarmLevel: string
  alarmMessage: string
  ruleId: number
  ruleName: string
  frameIndex: number
  timestampSeconds: number
  snapshotUrl: string
  createdAt: string
}

export interface WsStatsUpdate {
  type: 'STATS_UPDATE'
  onlineDevices: number
  totalDevices: number
  todayAlarms: number
  unhandledAlarms: number
  todayPersonCount: number
  todayVehicleCount: number
  timestamp: number
}

export type WsMessage = WsDetectionResult | WsAlarmTriggered | WsStatsUpdate
