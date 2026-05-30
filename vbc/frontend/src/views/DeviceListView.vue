<template>
  <div class="device-list-view">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>设备列表</span>
          <el-button type="primary" size="small">
            <el-icon><Plus /></el-icon>注册设备
          </el-button>
        </div>
      </template>
      <el-empty description="暂无设备" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
