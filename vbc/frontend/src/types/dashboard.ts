export interface DashboardOverview {
  onlineDevices: number
  totalDevices: number
  todayAlarms: number
  unhandledAlarms: number
  todayPersonCount: number
  todayVehicleCount: number
  activeVideos: number
}

export interface PersonTrend {
  time: string
  personCount: number
  vehicleCount: number
}

export interface AlarmStats {
  byType: { type: string; count: number }[]
  byLevel: { level: string; count: number }[]
  trend: { date: string; count: number }[]
}

export interface DeviceStatus {
  deviceId: number
  deviceName: string
  status: string
  lastHeartbeat: string
}

export interface HeatmapData {
  timestampSeconds: number
  gridX: number
  gridY: number
  densityValue: number
}
