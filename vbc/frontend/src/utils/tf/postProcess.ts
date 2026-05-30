export interface DetectionBox {
  class: string
  score: number
  bbox: [number, number, number, number]
}

export function filterByConfidence(
  detections: DetectionBox[],
  threshold: number
): DetectionBox[] {
  return detections.filter((d) => d.score >= threshold)
}

export function scaleBboxToCanvas(
  bbox: [number, number, number, number],
  videoW: number,
  videoH: number,
  canvasW: number,
  canvasH: number
): [number, number, number, number] {
  const scaleX = canvasW / videoW
  const scaleY = canvasH / videoH
  return [
    bbox[0] * scaleX,
    bbox[1] * scaleY,
    bbox[2] * scaleX,
    bbox[3] * scaleY,
  ]
}
