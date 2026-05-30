export interface DetectionObject {
  class: string
  confidence: number
  bbox: [number, number, number, number]
}

export interface DetectionRecord {
  id: number
  videoId: number
  frameIndex: number
  timestampSeconds: number
  detectedObjects: DetectionObject[]
  personCount: number
  vehicleCount: number
  totalCount: number
  createdAt: string
}

export interface DetectionStats {
  totalFrames: number
  framesWithPerson: number
  framesWithVehicle: number
  totalPersonDetections: number
  totalVehicleDetections: number
  avgPersonPerFrame: number
  avgVehiclePerFrame: number
}
