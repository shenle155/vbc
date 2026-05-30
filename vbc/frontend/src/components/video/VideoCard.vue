<template>
  <el-card shadow="hover" class="video-card" @click="goDetail">
    <div class="thumbnail">
      <img v-if="video.thumbnailUrl" :src="video.thumbnailUrl" alt="thumbnail" />
      <div v-else class="thumbnail-placeholder">
        <el-icon :size="40"><VideoCamera /></el-icon>
      </div>
      <el-tag
        :type="statusType"
        size="small"
        class="status-tag"
      >
        {{ statusText }}
      </el-tag>
    </div>
    <div class="info">
      <div class="title" :title="video.title">{{ video.title }}</div>
      <div class="meta">
        <span>{{ video.fileName }}</span>
        <span v-if="video.fileSize">{{ formatSize(video.fileSize) }}</span>
      </div>
      <div class="meta" v-if="video.durationSeconds">
        <el-icon :size="14"><VideoPlay /></el-icon>
        <span>{{ formatDuration(video.durationSeconds) }}</span>
        <span v-if="video.width && video.height">{{ video.width }}x{{ video.height }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { Video } from '@/types/video'

const props = defineProps<{ video: Video }>()
const router = useRouter()

const statusMap: Record<string, { text: string; type: any }> = {
  UPLOADING: { text: '上传中', type: 'info' },
  PROCESSING: { text: '处理中', type: 'warning' },
  READY: { text: '就绪', type: 'success' },
  ERROR: { text: '异常', type: 'danger' },
}

const statusText = computed(() => statusMap[props.video.status]?.text || props.video.status)
const statusType = computed(() => statusMap[props.video.status]?.type || 'info')

function goDetail() {
  if (props.video.status === 'READY') {
    router.push(`/videos/${props.video.id}`)
  }
}

function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function formatDuration(seconds: number): string {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}
</script>

<style scoped lang="scss">
.video-card {
  cursor: pointer;
  transition: transform 0.2s;
  &:hover {
    transform: translateY(-2px);
  }
}
.thumbnail {
  position: relative;
  width: 100%;
  height: 140px;
  background: #000;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
.thumbnail-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #555;
}
.status-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}
.title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}
</style>
