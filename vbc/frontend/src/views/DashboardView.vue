<template>
  <div class="dashboard-view">
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="6">
        <StatCard label="在线设备" :value="overview.onlineDevices" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatCard label="今日告警" :value="overview.todayAlarms" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatCard label="今日人数" :value="overview.todayPersonCount" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatCard label="今日车辆" :value="overview.todayVehicleCount" />
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>人数趋势 (24h)</template>
          <PersonTrendChart :data="trendData" v-if="trendData.length" />
          <div v-else class="chart-placeholder">暂无数据</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>告警类型分布</template>
          <AlarmTypePieChart :data="alarmStats.byType" v-if="alarmStats.byType?.length" />
          <div v-else class="chart-placeholder">暂无数据</div>
        </el-card>
      </el-col>
    </el-row>

    <div class="ws-indicator">
      <span class="dot" :class="{ online: wsConnected }" />
      {{ wsConnected ? '实时' : '离线' }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useWebSocket } from '@/composables/useWebSocket'
import { getOverview, getPersonTrend, getAlarmStats } from '@/api/dashboard'
import { useAlarmStore } from '@/store/alarm'
import StatCard from '@/components/common/StatCard.vue'
import PersonTrendChart from '@/components/charts/PersonTrendChart.vue'
import AlarmTypePieChart from '@/components/charts/AlarmTypePieChart.vue'
import type { PersonTrend } from '@/types/dashboard'

const overview = reactive({
  onlineDevices: 0, todayAlarms: 0, todayPersonCount: 0, todayVehicleCount: 0,
})
const trendData = ref<PersonTrend[]>([])
const alarmStats = reactive({ byType: [] as { type: string; count: number }[] })
const wsConnected = ref(false)
const alarmStore = useAlarmStore()
const { connected, connect, subscribe, disconnect } = useWebSocket()

onMounted(async () => {
  await loadData()
  await connect()
  wsConnected.value = connected.value

  subscribe('/topic/dashboard', (msg: any) => {
    if (msg.type === 'STATS_UPDATE') {
      if (msg.onlineDevices != null) overview.onlineDevices = msg.onlineDevices
      if (msg.todayAlarms != null) overview.todayAlarms = msg.todayAlarms
      if (msg.todayPersonCount != null) overview.todayPersonCount = msg.todayPersonCount
      if (msg.todayVehicleCount != null) overview.todayVehicleCount = msg.todayVehicleCount
    }
  })
  subscribe('/topic/alarm/0', (msg: any) => {
    if (msg.type === 'ALARM_TRIGGERED') alarmStore.incrementUnread()
  })
})

onUnmounted(() => disconnect())

async function loadData() {
  try {
    const [ov, tr, as] = await Promise.all([getOverview(), getPersonTrend(), getAlarmStats()])
    Object.assign(overview, ov.data.data)
    trendData.value = tr.data.data
    alarmStats.byType = as.data.data.byType
  } catch { /* ignore */ }
}
</script>

<style scoped lang="scss">
.stat-row { .el-col { margin-bottom: 16px; } }
.chart-placeholder {
  height: 300px; display: flex; align-items: center; justify-content: center;
  background: #f5f7fa; color: #909399; border-radius: 4px;
}
.ws-indicator {
  margin-top: 16px; display: flex; align-items: center; justify-content: flex-end;
  gap: 6px; font-size: 12px; color: #909399;
}
.dot { width: 8px; height: 8px; border-radius: 50%; background: #f56c6c; &.online { background: #67c23a; } }
</style>
