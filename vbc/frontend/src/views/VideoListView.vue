<template>
  <div class="video-list-view">
    <div class="toolbar">
      <div class="left">
        <el-input v-model="keyword" placeholder="搜索视频" clearable style="width: 240px" @input="fetchData" />
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 140px" @change="fetchData">
          <el-option label="上传中" value="UPLOADING" />
          <el-option label="处理中" value="PROCESSING" />
          <el-option label="就绪" value="READY" />
          <el-option label="异常" value="ERROR" />
        </el-select>
      </div>
      <el-button type="primary" @click="showUpload = true">
        <el-icon><Upload /></el-icon>上传视频
      </el-button>
    </div>

    <el-row :gutter="16" v-loading="loading">
      <el-col
        v-for="video in videoList"
        :key="video.id"
        :xs="24" :sm="12" :md="8" :lg="6"
        style="margin-bottom: 16px"
      >
        <VideoCard :video="video" />
      </el-col>
      <el-col :span="24" v-if="!loading && videoList.length === 0">
        <el-empty description="暂无视频，请上传" />
      </el-col>
    </el-row>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="fetchData"
      />
    </div>

    <VideoUploadDialog
      v-model="showUpload"
      :devices="[]"
      @uploaded="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { getVideoList } from '@/api/video'
import type { Video } from '@/types/video'
import VideoCard from '@/components/video/VideoCard.vue'
import VideoUploadDialog from '@/components/video/VideoUploadDialog.vue'

const videoList = ref<Video[]>([])
const loading = ref(false)
const keyword = ref('')
const filterStatus = ref('')
const showUpload = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getVideoList({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: filterStatus.value || undefined,
    })
    const data = res.data.data
    videoList.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  .left {
    display: flex;
    gap: 12px;
  }
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
