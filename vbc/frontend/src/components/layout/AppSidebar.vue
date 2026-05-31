<template>
  <div class="app-sidebar">
    <div class="logo">
      <el-icon :size="24"><VideoCamera /></el-icon>
      <span v-show="!appStore.sidebarCollapsed" class="logo-text">智慧园区</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="appStore.sidebarCollapsed"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      router
      @select="handleMenuSelect"
    >
      <el-menu-item index="/dashboard">
        <el-icon><DataAnalysis /></el-icon>
        <span>数据大屏</span>
      </el-menu-item>
      <el-menu-item index="/videos">
        <el-icon><VideoCamera /></el-icon>
        <span>视频管理</span>
      </el-menu-item>
      <el-sub-menu index="alarm-group">
        <template #title>
          <el-icon><Bell /></el-icon>
          <span>告警管理</span>
        </template>
        <el-menu-item index="/alarms">
          <el-icon><List /></el-icon>
          <span>告警记录</span>
        </el-menu-item>
        <el-menu-item index="/alarms/config">
          <el-icon><Setting /></el-icon>
          <span>告警规则</span>
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/devices">
        <el-icon><Monitor /></el-icon>
        <span>设备管理</span>
      </el-menu-item>
      <el-menu-item index="/heatmap">
        <el-icon><Grid /></el-icon>
        <span>热力图分析</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { VideoCamera, DataAnalysis, Bell, List, Setting, Monitor, Grid } from '@element-plus/icons-vue'
import { useAppStore } from '@/store/app'

const route = useRoute()
const appStore = useAppStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/alarms')) return path
  return path
})

function handleMenuSelect(index: string) {
  appStore.setActiveMenu(index)
}
</script>

<style scoped lang="scss">
.app-sidebar {
  height: 100%;
  overflow-y: auto;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  gap: 8px;
}
.logo-text {
  white-space: nowrap;
}
.el-menu {
  border-right: none;
}
</style>
