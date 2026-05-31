<template>
  <v-chart :option="option" style="height: 400px" autoresize />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { HeatmapChart } from 'echarts/charts'
import { TooltipComponent, VisualMapComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, HeatmapChart, TooltipComponent, VisualMapComponent, GridComponent])

const props = defineProps<{
  data: { gridX: number; gridY: number; densityValue: number }[]
}>()

const option = computed(() => ({
  tooltip: {},
  grid: { top: 20, bottom: 40, left: 50, right: 20 },
  xAxis: { type: 'category', data: [...Array(10).keys()], splitArea: { show: true } },
  yAxis: { type: 'category', data: [...Array(10).keys()], splitArea: { show: true } },
  visualMap: { min: 0, max: 1, calculable: true, orient: 'horizontal', bottom: 0,
    inRange: { color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#fee090', '#fdae61', '#f46d43', '#d73027'] }
  },
  series: [{
    type: 'heatmap',
    data: props.data.map((d) => [d.gridX, d.gridY, d.densityValue]),
    label: { show: false },
    emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.5)' } },
  }],
}))
</script>
