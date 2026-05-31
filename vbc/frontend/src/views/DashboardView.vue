<template>
  <div class="dashboard-view">
    <el-row :gutter="20">
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
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { getOverview } from '@/api/dashboard'
import StatCard from '@/components/common/StatCard.vue'

const overview = reactive({ onlineDevices: 0, todayAlarms: 0, todayPersonCount: 0, todayVehicleCount: 0 })

onMounted(async () => {
  try {
    const res = await getOverview()
    Object.assign(overview, res.data.data)
  } catch { /* ignore */ }
})
</script>
