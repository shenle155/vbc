<template>
  <div class="alarm-config-view">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>规则列表</template>
          <el-empty description="暂无告警规则" />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>规则编辑器</template>
          <el-empty description="请选择规则或新建" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
</script>
