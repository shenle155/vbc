import * as cocoSsd from '@tensorflow-models/coco-ssd'
import type { ObjectDetection } from '@tensorflow-models/coco-ssd'

let model: ObjectDetection | null = null
let loading = false

export async function loadModel(): Promise<ObjectDetection> {
  if (model) return model
  if (loading) {
    // Wait for concurrent load to finish
    let retries = 0
    while (loading && retries < 200) {
      await new Promise((r) => setTimeout(r, 100))
      retries++
    }
    if (model) return model
  }
  loading = true
  try {
    model = await cocoSsd.load({ base: 'lite_mobilenet_v2' })
    // Warm up with a dummy tensor to initialize WebGL
    const dummy = document.createElement('canvas')
    dummy.width = 1
    dummy.height = 1
    await model.detect(dummy)
    dummy.remove()
    return model
  } finally {
    loading = false
  }
}

export function getModel(): ObjectDetection | null {
  return model
}

export function disposeModel() {
  model?.dispose()
  model = null
}
