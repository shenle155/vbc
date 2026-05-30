<template>
  <div class="main-layout">
    <el-container>
      <el-aside :width="sidebarWidth">
        <AppSidebar />
      </el-aside>
      <el-container>
        <el-header height="60px">
          <AppHeader />
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/store/app'
import AppSidebar from './AppSidebar.vue'
import AppHeader from './AppHeader.vue'

const appStore = useAppStore()
const sidebarWidth = computed(() =>
  appStore.sidebarCollapsed ? '64px' : '220px'
)
</script>

<style scoped lang="scss">
.main-layout {
  height: 100vh;
}
.el-container {
  height: 100%;
}
.el-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.el-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}
.el-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
