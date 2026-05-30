<template>
  <div class="video-detail-view">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <div class="video-area">
            <div class="video-placeholder">视频播放区域</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>视频信息</template>
          <el-empty description="请先选择视频" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.video-area {
  background: #000;
  border-radius: 4px;
  min-height: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.video-placeholder {
  color: #666;
  font-size: 16px;
}
</style>
