<template>
  <el-dialog
    v-model="visible"
    title="上传视频"
    width="520px"
    :close-on-click-modal="false"
    @close="resetForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="视频标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入视频标题" />
      </el-form-item>
      <el-form-item label="关联设备" prop="deviceId">
        <el-select v-model="form.deviceId" placeholder="可选" clearable style="width: 100%">
          <el-option
            v-for="d in devices"
            :key="d.id"
            :label="d.deviceName"
            :value="d.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="视频文件" prop="file">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          accept=".mp4,.avi,.mov,.mkv,.wmv"
          drag
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽视频文件到此处或点击上传</div>
          <template #tip>
            <div class="el-upload__tip">支持 MP4 / AVI / MOV / MKV / WMV，最大 200MB</div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item v-if="uploading">
        <el-progress :percentage="uploadPercent" :status="uploadPercent === 100 ? 'success' : ''" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false" :disabled="uploading">取消</el-button>
      <el-button type="primary" @click="submitUpload" :loading="uploading">
        {{ uploading ? '上传中...' : '开始上传' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage, type FormInstance, type UploadInstance } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { uploadVideo } from '@/api/video'
import type { Device } from '@/types/device'

const props = defineProps<{ modelValue: boolean; devices: Device[] }>()
const emit = defineEmits<{ 'update:modelValue': (v: boolean) => void; uploaded: [] }>()

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => { visible.value = v })
watch(visible, (v) => { emit('update:modelValue', v) })

const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const uploading = ref(false)
const uploadPercent = ref(0)
const selectedFile = ref<File | null>(null)

const form = reactive({
  title: '',
  deviceId: null as number | null,
})

const rules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
}

function handleFileChange(file: UploadFile) {
  const raw = file.raw
  if (raw && raw.size > 200 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 200MB')
    uploadRef.value?.clearFiles()
    return
  }
  selectedFile.value = raw || null
}

function handleFileRemove() {
  selectedFile.value = null
}

async function submitUpload() {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (!valid) return
  })
  if (!selectedFile.value) {
    ElMessage.warning('请选择视频文件')
    return
  }

  uploading.value = true
  uploadPercent.value = 0

  const fd = new FormData()
  fd.append('file', selectedFile.value)
  fd.append('title', form.title)
  if (form.deviceId) {
    fd.append('deviceId', String(form.deviceId))
  }

  try {
    // Simulate progress since axios doesn't track upload progress by default
    const progressTimer = setInterval(() => {
      if (uploadPercent.value < 90) {
        uploadPercent.value += 10
      }
    }, 300)

    await uploadVideo(fd)
    clearInterval(progressTimer)
    uploadPercent.value = 100
    ElMessage.success('上传成功')
    visible.value = false
    emit('uploaded')
  } catch {
    // Error handled by interceptor
  } finally {
    uploading.value = false
    uploadPercent.value = 0
  }
}

function resetForm() {
  formRef.value?.resetFields()
  uploadRef.value?.clearFiles()
  selectedFile.value = null
  uploading.value = false
  uploadPercent.value = 0
}
</script>
