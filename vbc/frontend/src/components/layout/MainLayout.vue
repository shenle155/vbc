<template>
  <div style="display:flex;height:100vh">
    <div style="width:200px;background:#304156;color:#bfcbd9;flex-shrink:0;overflow-y:auto">
      <h3 style="color:#fff;padding:16px;text-align:center;margin:0">智慧园区</h3>
      <div v-for="item in navItems" :key="item.path"
        :style="{ background: route.path === item.path ? '#409eff' : 'transparent', padding:'12px 20px', cursor:'pointer' }"
        @click="$router.push(item.path)">
        {{ item.label }}
      </div>
    </div>
    <div style="flex:1;display:flex;flex-direction:column">
      <div style="height:50px;line-height:50px;padding:0 20px;background:#fff;border-bottom:1px solid #e6e6e6;font-weight:600">
        智慧园区 AI 视觉分析平台
      </div>
      <div style="flex:1;padding:20px;overflow-y:auto;background:#f5f7fa">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
const navItems = [
  { path: '/dashboard', label: '数据大屏' },
  { path: '/videos', label: '视频管理' },
  { path: '/alarms', label: '告警记录' },
  { path: '/alarms/config', label: '告警规则' },
  { path: '/devices', label: '设备管理' },
  { path: '/heatmap', label: '热力图' },
]
</script>
