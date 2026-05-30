<template>
  <div class="video-detail-view">
    <div class="page-header">
      <el-button @click="$router.push('/videos')" link>
        <el-icon><ArrowLeft /></el-icon>返回视频列表
      </el-button>
      <span class="title">{{ video?.title || '加载中...' }}</span>
      <el-tag v-if="video" :type="statusTagType" size="small">{{ statusText }}</el-tag>
    </div>

    <el-row :gutter="20" v-if="video">
      <!-- Left: Video Player + Detection Canvas -->
      <el-col :span="16">
        <el-card shadow="hover">
          <div class="video-area" ref="videoContainerRef">
            <video
              v-if="video.status === 'READY'"
              ref="videoRef"
              :src="videoUrl"
              controls
              class="video-player"
              @loadedmetadata="onVideoLoaded"
            />
            <div v-else class="video-placeholder">
              <el-icon :size="48"><VideoCamera /></el-icon>
              <p v-if="video.status === 'PROCESSING'">视频处理中，请稍候...</p>
              <p v-else-if="video.status === 'UPLOADING'">视频上传中...</p>
              <p v-else-if="video.status === 'ERROR'">视频处理异常</p>
              <p v-else>视频未就绪</p>
            </div>
            <canvas
              ref="canvasRef"
              class="detection-canvas"
              :style="{ display: detecting ? 'block' : 'none' }"
            />
          </div>
          <div class="video-controls">
            <el-button
              type="primary"
              :disabled="video.status !== 'READY'"
              @click="triggerExtract"
              :loading="extracting"
            >
              <el-icon><Operation /></el-icon>{{ extracting ? '提取中...' : '提取帧' }}
            </el-button>
            <el-button
              :type="detecting ? 'danger' : 'success'"
              :disabled="video.status !== 'READY' || frames.length === 0"
              @click="toggleDetection"
            >
              <el-icon><VideoPlay /></el-icon>{{ detecting ? '停止检测' : '开始检测' }}
            </el-button>
            <span v-if="extractStatusMsg" class="status-msg">{{ extractStatusMsg }}</span>
            <span v-if="detecting" class="fps-counter">FPS: {{ fps }}</span>
          </div>
        </el-card>

        <!-- Frame thumbnails -->
        <el-card shadow="hover" style="margin-top: 16px" v-if="frames.length > 0">
          <template #header>
            <span>帧列表（{{ frames.length }} 帧）</span>
          </template>
          <div class="frame-strip">
            <img
              v-for="frame in frames.slice(0, 20)"
              :key="frame.id"
              :src="frame.fileUrl"
              class="frame-thumb"
              :class="{ active: currentFrameIndex === frame.frameIndex }"
              @click="jumpToFrame(frame)"
            />
          </div>
        </el-card>
      </el-col>

      <!-- Right: Info Panel -->
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>视频信息</template>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="文件名">{{ video.fileName }}</el-descriptions-item>
            <el-descriptions-item label="文件大小">{{ formatSize(video.fileSize) }}</el-descriptions-item>
            <el-descriptions-item label="时长">{{ formatDuration(video.durationSeconds) }}</el-descriptions-item>
            <el-descriptions-item label="分辨率">{{ video.width }} x {{ video.height }}</el-descriptions-item>
            <el-descriptions-item label="帧率">{{ video.fps?.toFixed(2) }} fps</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="hover" style="margin-top: 16px" v-if="detecting">
          <template #header>实时检测结果</template>
          <div v-if="detectionResults.length > 0">
            <div
              v-for="(obj, i) in detectionResults"
              :key="i"
              class="detection-item"
            >
              <el-tag :type="obj.class === 'person' ? 'success' : 'warning'" size="small">
                {{ obj.class }}
              </el-tag>
              <span class="confidence">{{ (obj.confidence * 100).toFixed(0) }}%</span>
            </div>
          </div>
          <el-empty v-else description="等待检测结果..." :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- Loading state -->
    <div v-if="!video && !loadError" class="loading-area">
      <el-skeleton :rows="8" animated />
    </div>
    <el-empty v-if="loadError" description="视频不存在或加载失败" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVideo, getVideoList } from '@/api/video'
import { useWebSocket } from '@/composables/useWebSocket'
import type { Video } from '@/types/video'
import type { FrameVO } from '@/types/websocket'
import request from '@/api/request'
import type { Result } from '@/types/common'

interface FrameData {
  id: number
  videoId: number
  frameIndex: number
  timestampSeconds: number
  fileUrl: string
  processed: boolean
}

interface DetectionObj {
  class: string
  confidence: number
  bbox: { x: number; y: number; w: number; h: number }
}

const route = useRoute()
const video = ref<Video | null>(null)
const loadError = ref(false)
const videoRef = ref<HTMLVideoElement | null>(null)
const canvasRef = ref<HTMLCanvasElement | null>(null)
const videoContainerRef = ref<HTMLDivElement | null>(null)
const extracting = ref(false)
const extractStatusMsg = ref('')
const frames = ref<FrameData[]>([])
const detecting = ref(false)
const detectionResults = ref<DetectionObj[]>([])
const currentFrameIndex = ref(-1)
const fps = ref(0)

const videoId = computed(() => Number(route.params.id))
const videoUrl = computed(() => video.value ? `/api/v1/files/videos/${getFileName(video.value.filePath)}` : '')

const statusTagType = computed(() => {
  const map: Record<string, string> = { UPLOADING: 'info', PROCESSING: 'warning', READY: 'success', ERROR: 'danger' }
  return map[video.value?.status || ''] || 'info'
})
const statusText = computed(() => {
  const map: Record<string, string> = { UPLOADING: '上传中', PROCESSING: '处理中', READY: '就绪', ERROR: '异常' }
  return map[video.value?.status || ''] || video.value?.status || ''
})

const { connected, connect, subscribe, disconnect } = useWebSocket()

onMounted(async () => {
  await loadVideo()
  await connectWebSocket()
})

onUnmounted(() => {
  disconnect()
})

async function loadVideo() {
  try {
    const res = await getVideo(videoId.value)
    video.value = res.data.data
    if (video.value?.status === 'READY') {
      loadFrames()
    }
  } catch {
    loadError.value = true
  }
}

async function connectWebSocket() {
  await connect()
  if (connected.value) {
    subscribe(`/topic/video/${videoId.value}/status`, (msg: any) => {
      if (msg.type === 'STATUS_CHANGE') {
        extractStatusMsg.value = msg.message
        if (msg.status === 'READY') {
          extracting.value = false
          loadVideo()
        } else if (msg.status === 'ERROR') {
          extracting.value = false
          ElMessage.error('帧提取失败')
        }
      }
    })
  }
}

async function loadFrames() {
  try {
    const res = await request.get<Result<FrameData[]>>(`/videos/${videoId.value}/frames`, {
      params: { startIndex: 0, endIndex: 100 },
    })
    frames.value = res.data.data
  } catch { /* ignore */ }
}

async function triggerExtract() {
  extracting.value = true
  extractStatusMsg.value = '正在提取帧...'
  try {
    await request.post(`/videos/${videoId.value}/extract`, {
      intervalSeconds: 1.0,
      maxFrames: 300,
    })
    ElMessage.success('开始提取帧，请稍候...')
  } catch {
    extracting.value = false
  }
}

function toggleDetection() {
  detecting.value = !detecting.value
  if (!detecting.value) {
    detectionResults.value = []
  }
}

function jumpToFrame(frame: FrameData) {
  currentFrameIndex.value = frame.frameIndex
  if (videoRef.value) {
    videoRef.value.currentTime = frame.timestampSeconds
  }
}

function onVideoLoaded() {
  // Video metadata loaded
}

function getFileName(path: string): string {
  return path.split(/[/\\]/).pop() || ''
}

function formatSize(bytes?: number): string {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function formatDuration(seconds?: number): string {
  if (!seconds) return '-'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  .title { font-size: 18px; font-weight: 600; }
}
.video-area {
  position: relative;
  background: #000;
  border-radius: 4px;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.video-player {
  width: 100%;
  max-height: 500px;
}
.detection-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.video-placeholder {
  color: #999;
  text-align: center;
  p { margin-top: 12px; }
}
.video-controls {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.status-msg {
  color: #909399;
  font-size: 13px;
}
.fps-counter {
  color: #67c23a;
  font-size: 13px;
  margin-left: auto;
}
.frame-strip {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  padding-bottom: 8px;
}
.frame-thumb {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
  &.active { border-color: #409eff; }
  &:hover { opacity: 0.8; }
}
.detection-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}
.confidence {
  font-size: 13px;
  color: #909399;
}
.loading-area {
  padding: 40px;
}
</style>
