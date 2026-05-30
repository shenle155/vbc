<template>
  <div class="dashboard-view">
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">{{ stat.value }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>人数趋势</template>
          <div class="chart-placeholder">人数趋势图</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>告警类型分布</template>
          <div class="chart-placeholder">告警饼图</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- WebSocket connection indicator -->
    <div class="ws-indicator">
      <span class="dot" :class="{ online: wsConnected }" />
      {{ wsConnected ? '实时连接' : '实时连接断开' }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useWebSocket } from '@/composables/useWebSocket'
import { useAlarmStore } from '@/store/alarm'

const stats = reactive([
  { label: '在线设备', value: '0' },
  { label: '今日告警', value: '0' },
  { label: '今日人数', value: '0' },
  { label: '今日车辆', value: '0' },
])

const wsConnected = ref(false)
const alarmStore = useAlarmStore()
const { connected, connect, subscribe, disconnect } = useWebSocket()

onMounted(async () => {
  await connect()
  wsConnected.value = connected.value

  // Subscribe to dashboard stats updates
  subscribe('/topic/dashboard', (msg: any) => {
    if (msg.type === 'STATS_UPDATE') {
      if (msg.onlineDevices !== undefined) stats[0].value = String(msg.onlineDevices)
      if (msg.todayAlarms !== undefined) stats[1].value = String(msg.todayAlarms)
      if (msg.todayPersonCount !== undefined) stats[2].value = String(msg.todayPersonCount)
      if (msg.todayVehicleCount !== undefined) stats[3].value = String(msg.todayVehicleCount)
    }
  })

  // Subscribe to all alarm notifications for badge update
  subscribe('/topic/alarm/0', (msg: any) => {
    if (msg.type === 'ALARM_TRIGGERED') {
      alarmStore.incrementUnread()
    }
  })
})

onUnmounted(() => {
  disconnect()
})
</script>

<style scoped lang="scss">
.stat-card { text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 32px; font-weight: 700; color: #303133; }
.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  border-radius: 4px;
}
.ws-indicator {
  margin-top: 16px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f56c6c;
  &.online { background: #67c23a; }
}
</style>
