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
      <el-col :span="16">
        <el-card shadow="hover">
          <div class="video-area" ref="videoContainerRef">
            <video
              v-if="video.status === 'READY'"
              ref="videoRef"
              :src="videoUrl"
              controls
              class="video-player"
            />
            <div v-else class="video-placeholder">
              <el-icon :size="48"><VideoCamera /></el-icon>
              <p v-if="video.status === 'PROCESSING'">视频处理中，请稍候...</p>
              <p v-else-if="video.status === 'UPLOADING'">视频上传中...</p>
              <p v-else-if="video.status === 'ERROR'">视频处理异常</p>
              <p v-else>视频未就绪</p>
            </div>
            <DetectionCanvas ref="detectionCanvasRef" v-if="video.status === 'READY'" />
          </div>

          <!-- Controls -->
          <DetectionControls
            :running="detection.running.value"
            :fps="detection.fps.value"
            :frame-count="detection.frameCount.value"
            :can-start="video.status === 'READY' && frames.length > 0 && detection.modelReady.value"
            @start="startDetection"
            @stop="detection.stop()"
            @update:threshold="detection.confidenceThreshold.value = $event"
          />

          <!-- Extract button -->
          <div style="margin-top: 8px">
            <el-button
              size="small"
              :disabled="video.status !== 'READY'"
              @click="triggerExtract"
              :loading="extracting"
            >
              {{ extracting ? '提取中...' : '提取帧' }}
            </el-button>
            <el-button size="small" :disabled="!detection.modelReady.value" @click="detection.initModel()" v-if="!detection.modelReady.value">
              加载检测模型
            </el-button>
            <span v-if="extractStatusMsg" class="status-msg">{{ extractStatusMsg }}</span>
            <span v-if="detection.modelLoading.value" class="status-msg">模型加载中...</span>
          </div>
        </el-card>

        <!-- Frame thumbnails -->
        <el-card shadow="hover" style="margin-top: 16px" v-if="frames.length > 0">
          <template #header>帧列表（{{ frames.length }} 帧）</template>
          <div class="frame-strip">
            <img
              v-for="frame in frames.slice(0, 30)"
              :key="frame.id"
              :src="frame.fileUrl"
              class="frame-thumb"
              @click="jumpToFrame(frame)"
            />
          </div>
        </el-card>
      </el-col>

      <!-- Right panel -->
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

        <!-- Current detections -->
        <el-card shadow="hover" style="margin-top: 16px" v-if="detection.running.value">
          <template #header>检测结果</template>
          <div v-if="detection.currentDetections.value.length > 0">
            <div v-for="(d, i) in detection.currentDetections.value" :key="i" class="detection-item">
              <el-tag :type="d.class === 'person' ? 'success' : 'warning'" size="small">{{ d.class }}</el-tag>
              <span>{{ (d.score * 100).toFixed(0) }}%</span>
            </div>
          </div>
          <el-empty v-else description="未检测到目标" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <div v-if="!video && !loadError" class="loading-area">
      <el-skeleton :rows="8" animated />
    </div>
    <el-empty v-if="loadError" description="视频不存在或加载失败" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVideo } from '@/api/video'
import { useDetection } from '@/composables/useDetection'
import { useWebSocket } from '@/composables/useWebSocket'
import type { Video } from '@/types/video'
import type { Result } from '@/types/common'
import DetectionCanvas from '@/components/video/DetectionCanvas.vue'
import DetectionControls from '@/components/video/DetectionControls.vue'
import request from '@/api/request'

interface FrameData {
  id: number
  videoId: number
  frameIndex: number
  timestampSeconds: number
  fileUrl: string
  processed: boolean
}

const route = useRoute()
const video = ref<Video | null>(null)
const loadError = ref(false)
const videoRef = ref<HTMLVideoElement | null>(null)
const detectionCanvasRef = ref<InstanceType<typeof DetectionCanvas> | null>(null)
const videoContainerRef = ref<HTMLDivElement | null>(null)
const extracting = ref(false)
const extractStatusMsg = ref('')
const frames = ref<FrameData[]>([])

const videoId = computed(() => Number(route.params.id))
const videoUrl = computed(() => video.value ? `/api/v1/files/videos/${getFileName(video.value.filePath)}` : '')

const detection = useDetection()
const { connected, connect, subscribe, disconnect } = useWebSocket()

const statusTagType = computed(() => {
  const m: Record<string, string> = { UPLOADING: 'info', PROCESSING: 'warning', READY: 'success', ERROR: 'danger' }
  return m[video.value?.status || ''] || 'info'
})
const statusText = computed(() => {
  const m: Record<string, string> = { UPLOADING: '上传中', PROCESSING: '处理中', READY: '就绪', ERROR: '异常' }
  return m[video.value?.status || ''] || video.value?.status || ''
})

onMounted(async () => {
  await loadVideo()
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
  // Preload model in background
  detection.initModel()
})

onUnmounted(() => {
  detection.dispose()
  disconnect()
})

async function loadVideo() {
  try {
    const res = await getVideo(videoId.value)
    video.value = res.data.data
    if (video.value?.status === 'READY') loadFrames()
  } catch {
    loadError.value = true
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
    await request.post(`/videos/${videoId.value}/extract`, { intervalSeconds: 1.0, maxFrames: 300 })
    ElMessage.success('开始提取帧，请稍候...')
  } catch {
    extracting.value = false
  }
}

function startDetection() {
  const canvas = detectionCanvasRef.value?.getCanvas()
  const videoEl = videoRef.value
  if (!canvas || !videoEl || !video.value) return
  detection.start(frames.value, videoEl, canvas, video.value.id)
}

function jumpToFrame(frame: FrameData) {
  if (videoRef.value) videoRef.value.currentTime = frame.timestampSeconds
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
.video-placeholder {
  color: #999;
  text-align: center;
  p { margin-top: 12px; }
}
.status-msg {
  color: #909399;
  font-size: 13px;
  margin-left: 8px;
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
  &:hover { opacity: 0.8; border-color: #409eff; }
}
.detection-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #909399;
}
.loading-area {
  padding: 40px;
}
</style>
