<template>
  <div class="alarm-list-view">
    <el-card shadow="hover">
      <template #header>告警记录</template>
      <div class="filter-bar">
        <el-select v-model="filterType" placeholder="告警类型" clearable @change="fetchData" style="width: 160px">
          <el-option label="区域入侵" value="ZONE_INTRUSION" />
          <el-option label="人群聚集" value="CROWD_GATHERING" />
          <el-option label="人数超限" value="PEOPLE_EXCEED" />
        </el-select>
        <el-select v-model="filterLevel" placeholder="告警级别" clearable @change="fetchData" style="width: 120px">
          <el-option label="提示" value="INFO" />
          <el-option label="警告" value="WARNING" />
          <el-option label="严重" value="CRITICAL" />
        </el-select>
        <el-switch v-model="filterHandled" active-text="已处理" inactive-text="未处理" @change="fetchData" />
      </div>
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="alarmType" label="类型" width="110">
          <template #default="{ row }">{{ typeText(row.alarmType) }}</template>
        </el-table-column>
        <el-table-column prop="alarmLevel" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.alarmLevel)" size="small">{{ levelText(row.alarmLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmMessage" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="170" />
        <el-table-column prop="handled" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.handled ? 'success' : 'danger'" size="small">{{ row.handled ? '已处理' : '未处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="!row.handled" link type="primary" size="small" @click="doHandle(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="prev, pager, next, total" @current-change="fetchData" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAlarmRecords, handleAlarm } from '@/api/alarm'
import type { AlarmRecord } from '@/types/alarm'

const records = ref<AlarmRecord[]>([])
const loading = ref(false)
const filterType = ref('')
const filterLevel = ref('')
const filterHandled = ref<boolean | undefined>(undefined)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

function typeText(t: string) {
  const m: Record<string, string> = { ZONE_INTRUSION: '区域入侵', CROWD_GATHERING: '人群聚集', PEOPLE_EXCEED: '人数超限' }
  return m[t] || t
}
function levelText(l: string) {
  const m: Record<string, string> = { INFO: '提示', WARNING: '警告', CRITICAL: '严重' }
  return m[l] || l
}
function levelTag(l: string) {
  const m: Record<string, string> = { INFO: 'info', WARNING: 'warning', CRITICAL: 'danger' }
  return m[l] || 'info'
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getAlarmRecords({
      page: page.value, pageSize: pageSize.value,
      alarmType: filterType.value || undefined,
      alarmLevel: filterLevel.value || undefined,
      handled: filterHandled.value,
    })
    const d = res.data.data
    records.value = d.records
    total.value = d.total
  } finally { loading.value = false }
}

async function doHandle(row: AlarmRecord) {
  await handleAlarm(row.id)
  ElMessage.success('已处理')
  fetchData()
}
</script>

<style scoped lang="scss">
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.pagination { margin-top: 16px; display: flex; justify-content: center; }
</style>
