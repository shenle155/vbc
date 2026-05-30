<template>
  <div class="video-list-view">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索视频" clearable style="width: 240px" />
      <el-button type="primary" @click="showUpload = true">
        <el-icon><Upload /></el-icon>上传视频
      </el-button>
    </div>
    <div class="video-grid">
      <el-empty description="暂无视频，请上传" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const keyword = ref('')
const showUpload = ref(false)
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.video-grid {
  min-height: 400px;
}
</style>
