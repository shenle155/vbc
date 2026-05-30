import { ref } from 'vue'
import { useTensorFlow } from './useTensorFlow'
import { filterByConfidence, scaleBboxToCanvas, type DetectionBox } from '@/utils/tf/postProcess'
import { useWebSocket } from './useWebSocket'

interface FrameItem {
  id: number
  frameIndex: number
  timestampSeconds: number
  fileUrl: string
}

export function useDetection() {
  const { modelReady, initModel, detect, dispose } = useTensorFlow()

  const running = ref(false)
  const confidenceThreshold = ref(0.5)
  const currentDetections = ref<DetectionBox[]>([])
  const fps = ref(0)
  const frameCount = ref(0)

  let animationId = 0
  let lastFpsTime = 0
  let fpsCounter = 0

  const { connected, connect: wsConnect, send } = useWebSocket()

  async function start(
    frames: FrameItem[],
    videoElement: HTMLVideoElement,
    canvasElement: HTMLCanvasElement,
    videoId: number
  ) {
    if (running.value) return

    // Ensure model is loaded
    if (!modelReady.value) {
      await initModel()
    }
    if (!modelReady.value) return

    running.value = true
    runDetectionLoop(frames, videoElement, canvasElement, videoId)
  }

  function stop() {
    running.value = false
    if (animationId) {
      cancelAnimationFrame(animationId)
      animationId = 0
    }
    currentDetections.value = []
  }

  function disposeAll() {
    stop()
    dispose()
  }

  async function runDetectionLoop(
    frames: FrameItem[],
    video: HTMLVideoElement,
    canvas: HTMLCanvasElement,
    videoId: number
  ) {
    const ctx = canvas.getContext('2d')
    if (!ctx) return

    fpsCounter = 0
    lastFpsTime = performance.now()

    async function processFrame(index: number) {
      if (!running.value || index >= frames.length) {
        if (index >= frames.length) running.value = false
        return
      }

      const frame = frames[index]
      const img = new Image()
      img.crossOrigin = 'anonymous'
      img.src = frame.fileUrl

      await new Promise<void>((resolve) => {
        img.onload = () => resolve()
        img.onerror = () => resolve()
      })

      if (!running.value) return

      // Match canvas size to video display size
      const videoRect = video.getBoundingClientRect()
      canvas.width = videoRect.width
      canvas.height = videoRect.height
      ctx.clearRect(0, 0, canvas.width, canvas.height)

      try {
        const detections = await detect(img)
        const filtered = filterByConfidence(detections, confidenceThreshold.value)
        currentDetections.value = filtered

        // Draw bounding boxes
        for (const d of filtered) {
          const color = d.class === 'person' ? '#00ff00' : '#0088ff'
          const [x, y, w, h] = scaleBboxToCanvas(
            d.bbox, img.naturalWidth, img.naturalHeight, canvas.width, canvas.height
          )
          ctx.strokeStyle = color
          ctx.lineWidth = 2
          ctx.strokeRect(x, y, w, h)
          ctx.fillStyle = color
          ctx.font = '14px sans-serif'
          ctx.fillText(`${d.class} ${(d.score * 100).toFixed(0)}%`, x, y > 18 ? y - 4 : y + 14)
        }

        // Send detection report via WebSocket
        if (connected.value && filtered.length > 0) {
          send('/app/detection/report', {
            videoId,
            frameIndex: frame.frameIndex,
            timestampSeconds: frame.timestampSeconds,
            detections: filtered.map((d) => ({
              className: d.class,
              confidence: d.score,
              bbox: { x: d.bbox[0], y: d.bbox[1], w: d.bbox[2], h: d.bbox[3] },
            })),
            personCount: filtered.filter((d) => d.class === 'person').length,
            vehicleCount: filtered.filter((d) => ['car', 'truck', 'bus', 'motorcycle'].includes(d.class)).length,
            totalCount: filtered.length,
          })
        }

        // FPS counter
        fpsCounter++
        const now = performance.now()
        if (now - lastFpsTime >= 1000) {
          fps.value = Math.round(fpsCounter / ((now - lastFpsTime) / 1000))
          fpsCounter = 0
          lastFpsTime = now
        }
        frameCount.value = index + 1
      } catch {
        // Skip frame on error
      }

      img.remove()

      if (running.value) {
        animationId = requestAnimationFrame(() => processFrame(index + 1))
      }
    }

    await wsConnect()
    processFrame(0)
  }

  return {
    running,
    confidenceThreshold,
    currentDetections,
    fps,
    frameCount,
    modelReady,
    initModel,
    start,
    stop,
    dispose: disposeAll,
  }
}
