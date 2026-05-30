export interface Video {
  id: number
  title: string
  fileName: string
  filePath: string
  fileSize: number
  durationSeconds: number
  width: number
  height: number
  fps: number
  status: 'UPLOADING' | 'PROCESSING' | 'READY' | 'ERROR'
  thumbnailPath: string
  deviceId: number | null
  createdAt: string
  updatedAt: string
}
