<template>
  <div class="device-list-view">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>设备列表</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>注册设备
          </el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="keyword" placeholder="搜索设备名称/编码/位置" clearable style="width: 280px" @input="fetchData" />
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 140px" @change="fetchData">
          <el-option label="在线" value="ONLINE" />
          <el-option label="离线" value="OFFLINE" />
          <el-option label="维护中" value="MAINTENANCE" />
        </el-select>
      </div>

      <el-table :data="deviceList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="deviceName" label="设备名称" min-width="140" />
        <el-table-column prop="deviceCode" label="设备编码" width="130" />
        <el-table-column prop="ipAddress" label="IP 地址" width="140">
          <template #default="{ row }">
            {{ row.ipAddress || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="140">
          <template #default="{ row }">
            {{ row.location || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastHeartbeat" label="最后心跳" width="170">
          <template #default="{ row }">
            {{ row.lastHeartbeat || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- Add / Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑设备' : '注册设备'"
      width="480px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="如：校门摄像头 A1" />
        </el-form-item>
        <el-form-item label="设备编码" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="如：CAM-GATE-001" />
        </el-form-item>
        <el-form-item label="IP 地址">
          <el-input v-model="form.ipAddress" placeholder="如：192.168.1.101" />
        </el-form-item>
        <el-form-item label="流地址">
          <el-input v-model="form.streamUrl" placeholder="rtsp://..." />
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="form.location" placeholder="如：学校正门" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getDeviceList, createDevice, updateDevice, deleteDevice } from '@/api/device'
import type { Device } from '@/types/device'

const deviceList = ref<Device[]>([])
const loading = ref(false)
const keyword = ref('')
const filterStatus = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const saving = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  deviceName: '',
  deviceCode: '',
  ipAddress: '',
  streamUrl: '',
  location: '',
})

const rules = {
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  deviceCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
}

function statusTag(status: string) {
  const map: Record<string, string> = { ONLINE: 'success', OFFLINE: 'danger', MAINTENANCE: 'warning' }
  return map[status] || 'info'
}

function statusText(status: string) {
  const map: Record<string, string> = { ONLINE: '在线', OFFLINE: '离线', MAINTENANCE: '维护中' }
  return map[status] || status
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getDeviceList({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: filterStatus.value || undefined,
    })
    const data = res.data.data
    deviceList.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function openDialog(device?: Device) {
  if (device) {
    editingId.value = device.id
    form.deviceName = device.deviceName
    form.deviceCode = device.deviceCode
    form.ipAddress = device.ipAddress || ''
    form.streamUrl = device.streamUrl || ''
    form.location = device.location || ''
  } else {
    editingId.value = null
  }
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  form.deviceName = ''
  form.deviceCode = ''
  form.ipAddress = ''
  form.streamUrl = ''
  form.location = ''
  editingId.value = null
}

async function handleSave() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (editingId.value) {
        await updateDevice(editingId.value, { ...form })
        ElMessage.success('更新成功')
      } else {
        await createDevice({ ...form })
        ElMessage.success('注册成功')
      }
      dialogVisible.value = false
      fetchData()
    } finally {
      saving.value = false
    }
  })
}

async function handleDelete(device: Device) {
  await ElMessageBox.confirm(`确定删除设备 "${device.deviceName}" 吗？`, '确认删除', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  await deleteDevice(device.id)
  ElMessage.success('删除成功')
  fetchData()
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
