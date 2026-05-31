<template>
  <div class="alarm-config-view">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>规则列表</span>
              <el-button size="small" type="primary" @click="openDialog()">新增</el-button>
            </div>
          </template>
          <el-table :data="rules" v-loading="loading" size="small" highlight-current-row
            @row-click="selectRule" :row-class-name="(r: AlarmRule) => r.id === editingId ? 'selected-row' : ''">
            <el-table-column prop="ruleName" label="名称" show-overflow-tooltip />
            <el-table-column prop="ruleType" label="类型" width="90">
              <template #default="{ row }">{{ typeText(row.ruleType) }}</template>
            </el-table-column>
            <el-table-column prop="enabled" label="状态" width="60">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'" size="small">{{ row.enabled ? '开' : '关' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>规则编辑</template>
          <el-form v-if="editingId || isNew" :model="form" label-width="90px" size="small">
            <el-form-item label="规则名称" required>
              <el-input v-model="form.ruleName" />
            </el-form-item>
            <el-form-item label="规则类型" required>
              <el-select v-model="form.ruleType" style="width: 100%">
                <el-option label="区域入侵" value="ZONE_INTRUSION" />
                <el-option label="人群聚集" value="CROWD_GATHERING" />
                <el-option label="人数超限" value="PEOPLE_EXCEED" />
              </el-select>
            </el-form-item>
            <el-form-item label="阈值">
              <el-input-number v-model="form.thresholdValue" :min="1" />
            </el-form-item>
            <el-form-item label="告警级别">
              <el-radio-group v-model="form.alarmLevel">
                <el-radio value="INFO">提示</el-radio>
                <el-radio value="WARNING">警告</el-radio>
                <el-radio value="CRITICAL">严重</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="启用">
              <el-switch v-model="form.enabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="save" :loading="saving">保存</el-button>
              <el-button v-if="editingId" type="danger" @click="del">删除</el-button>
            </el-form-item>
          </el-form>
          <el-empty v-else description="请选择一条规则或点击新增" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAlarmRules, createAlarmRule, updateAlarmRule, deleteAlarmRule } from '@/api/alarm'
import type { AlarmRule } from '@/types/alarm'

const rules = ref<AlarmRule[]>([])
const loading = ref(false)
const saving = ref(false)
const editingId = ref<number | null>(null)
const isNew = ref(false)

const form = reactive({
  ruleName: '', ruleType: 'ZONE_INTRUSION',
  thresholdValue: 1, alarmLevel: 'WARNING', enabled: true,
})

function typeText(t: string) {
  const m: Record<string, string> = { ZONE_INTRUSION: '区域入侵', CROWD_GATHERING: '人群聚集', PEOPLE_EXCEED: '人数超限' }
  return m[t] || t
}

onMounted(() => fetchRules())

async function fetchRules() {
  loading.value = true
  try {
    const res = await getAlarmRules({ page: 1, pageSize: 50 })
    rules.value = res.data.data.records
  } finally { loading.value = false }
}

function selectRule(r: AlarmRule) {
  editingId.value = r.id
  isNew.value = false
  form.ruleName = r.ruleName
  form.ruleType = r.ruleType
  form.thresholdValue = r.thresholdValue || 1
  form.alarmLevel = r.alarmLevel
  form.enabled = r.enabled
}

function openDialog() {
  editingId.value = null
  isNew.value = true
  form.ruleName = ''
  form.ruleType = 'ZONE_INTRUSION'
  form.thresholdValue = 1
  form.alarmLevel = 'WARNING'
  form.enabled = true
}

async function save() {
  if (!form.ruleName) { ElMessage.warning('请输入规则名称'); return }
  saving.value = true
  try {
    if (isNew.value) {
      await createAlarmRule({ ...form })
      ElMessage.success('创建成功')
    } else {
      await updateAlarmRule(editingId.value!, { ...form })
      ElMessage.success('更新成功')
    }
    isNew.value = false; editingId.value = null
    fetchRules()
  } finally { saving.value = false }
}

async function del() {
  await ElMessageBox.confirm('确定删除？', '确认', { type: 'warning' })
  await deleteAlarmRule(editingId.value!)
  ElMessage.success('已删除')
  editingId.value = null
  fetchRules()
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
:deep(.selected-row) { background-color: #ecf5ff; }
</style>
