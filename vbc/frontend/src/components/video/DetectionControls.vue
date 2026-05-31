<template>
  <div class="detection-controls">
    <el-button
      :type="running ? 'danger' : 'success'"
      size="small"
      @click="toggle"
      :disabled="!canStart"
    >
      {{ running ? '停止检测' : '开始检测' }}
    </el-button>
    <span class="threshold-label">置信度: {{ confidence }}</span>
    <el-slider
      v-model="confidence"
      :min="0.1"
      :max="0.9"
      :step="0.05"
      style="width: 120px"
      @input="$emit('update:threshold', confidence)"
    />
    <span v-if="running" class="fps">FPS: {{ fps }}</span>
    <span v-if="running" class="frame-count">帧: {{ frameCount }}</span>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  running: boolean
  fps: number
  frameCount: number
  canStart: boolean
}>()

const emit = defineEmits<{
  start: []
  stop: []
  'update:threshold': [value: number]
}>()

const confidence = ref(0.3)

function toggle() {
  emit(props.running ? 'stop' : 'start')
}
</script>

<style scoped>
.detection-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}
.threshold-label {
  font-size: 13px;
  color: #606266;
}
.fps, .frame-count {
  font-size: 13px;
  color: #67c23a;
}
</style>
