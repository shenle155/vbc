export interface Device {
  id: number
  deviceName: string
  deviceCode: string
  ipAddress: string
  streamUrl: string
  location: string
  status: 'ONLINE' | 'OFFLINE' | 'MAINTENANCE'
  lastHeartbeat: string
  createdAt: string
  updatedAt: string
}
