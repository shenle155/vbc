<template>
  <v-chart :option="option" style="height: 300px" autoresize />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent])

const props = defineProps<{
  data: { time: string; personCount: number; vehicleCount: number }[]
}>()

const option = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['人数', '车辆'], bottom: 0 },
  xAxis: { type: 'category', data: props.data.map((d) => d.time) },
  yAxis: { type: 'value' },
  series: [
    { name: '人数', type: 'line', data: props.data.map((d) => d.personCount), smooth: true, color: '#67c23a' },
    { name: '车辆', type: 'line', data: props.data.map((d) => d.vehicleCount), smooth: true, color: '#409eff' },
  ],
}))
</script>
