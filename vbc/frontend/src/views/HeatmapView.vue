<template>
  <div class="heatmap-view">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>热力图分析</span>
          <div style="display:flex;gap:12px;align-items:center">
            <el-select v-model="selectedVideoId" placeholder="选择视频" @change="loadHeatmap" style="width:180px">
              <el-option v-for="v in videos" :key="v.id" :label="v.title" :value="v.id" />
            </el-select>
            <el-button type="primary" size="small" @click="loadHeatmap" :loading="loading">加载</el-button>
          </div>
        </div>
      </template>
      <CrowdHeatmapChart v-if="heatmapData.length" :data="heatmapData" />
      <el-empty v-else description="请选择视频并加载热力图数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getVideoList } from '@/api/video'
import type { Video } from '@/types/video'
import CrowdHeatmapChart from '@/components/charts/CrowdHeatmapChart.vue'
import request from '@/api/request'
import type { Result } from '@/types/common'

const videos = ref<Video[]>([])
const selectedVideoId = ref<number | null>(null)
const heatmapData = ref<any[]>([])
const loading = ref(false)

onMounted(async () => {
  const res = await getVideoList({ page: 1, pageSize: 50, status: 'READY' })
  videos.value = res.data.data.records
})

async function loadHeatmap() {
  if (!selectedVideoId.value) return
  loading.value = true
  try {
    const res = await request.get<Result<any[]>>(`/dashboard/heatmap-data/${selectedVideoId.value}`, {
      params: { startTime: 0, endTime: 999999 },
    })
    heatmapData.value = res.data.data
  } finally { loading.value = false }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
