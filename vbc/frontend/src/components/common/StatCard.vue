<template>
  <el-card shadow="hover" class="stat-card">
    <div class="stat-label">{{ label }}</div>
    <div class="stat-value">{{ displayValue }}</div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{ label: string; value: number }>()
const displayValue = ref(0)

watch(() => props.value, (newVal) => {
  const start = displayValue.value
  const diff = newVal - start
  const duration = 500
  const startTime = performance.now()
  function animate(now: number) {
    const elapsed = now - startTime
    const progress = Math.min(elapsed / duration, 1)
    displayValue.value = Math.round(start + diff * progress)
    if (progress < 1) requestAnimationFrame(animate)
  }
  requestAnimationFrame(animate)
}, { immediate: true })
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 32px; font-weight: 700; color: #303133; }
</style>
