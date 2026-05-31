import '@tensorflow/tfjs-backend-webgl'
import '@tensorflow/tfjs-backend-cpu'
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
    console.log('[TF] Loading COCO-SSD from local...')
    model = await cocoSsd.load({
      base: 'mobilenet_v1' as any,
      modelUrl: '/models/coco-ssd/model.json',
    })
    console.log('[TF] Model loaded, warming up...')
    const dummy = document.createElement('canvas')
    dummy.width = 1
    dummy.height = 1
    await model.detect(dummy)
    dummy.remove()
    console.log('[TF] Model ready')
    return model
  } catch (e) {
    console.error('[TF] Model load failed:', e)
    throw e
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
