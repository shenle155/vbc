import { ref } from 'vue'
import * as cocoSsd from '@tensorflow-models/coco-ssd'
import { loadModel, disposeModel, getModel } from '@/utils/tf/modelLoader'
import type { DetectionBox } from '@/utils/tf/postProcess'

export function useTensorFlow() {
  const modelReady = ref(false)
  const modelLoading = ref(false)
  const loadError = ref('')

  async function initModel() {
    if (modelReady.value) return
    modelLoading.value = true
    loadError.value = ''
    try {
      await loadModel()
      modelReady.value = true
    } catch (e: any) {
      loadError.value = e.message || '模型加载失败'
    } finally {
      modelLoading.value = false
    }
  }

  async function detect(
    source: HTMLImageElement | HTMLVideoElement | HTMLCanvasElement
  ): Promise<DetectionBox[]> {
    const m = getModel()
    if (!m) throw new Error('Model not loaded')

    const results = await m.detect(source)
    return results.map((r) => ({
      class: r.class,
      score: r.score,
      bbox: r.bbox as [number, number, number, number],
    }))
  }

  function dispose() {
    disposeModel()
    modelReady.value = false
  }

  return { modelReady, modelLoading, loadError, initModel, detect, dispose }
}
