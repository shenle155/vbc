<template>
  <div class="app-header">
    <div class="left">
      <el-icon class="collapse-btn" @click="appStore.toggleSidebar" :size="20">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="right">
      <el-badge :value="alarmStore.unreadCount" :hidden="alarmStore.unreadCount === 0" :max="99">
        <el-icon :size="20" class="bell-icon"><Bell /></el-icon>
      </el-badge>
      <el-avatar :size="32" icon="UserFilled" />
      <span class="username">管理员</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useAlarmStore } from '@/store/alarm'

const route = useRoute()
const appStore = useAppStore()
const alarmStore = useAlarmStore()

const currentTitle = computed(() => route.meta?.title as string | undefined)
</script>

<style scoped lang="scss">
.app-header {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.collapse-btn {
  cursor: pointer;
  &:hover { color: #409eff; }
}
.right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.bell-icon {
  cursor: pointer;
  &:hover { color: #409eff; }
}
.username {
  font-size: 14px;
  color: #666;
}
</style>
