<template>
  <v-chart :option="option" style="height: 300px" autoresize />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'

use([CanvasRenderer, PieChart, TooltipComponent, LegendComponent])

const props = defineProps<{ data: { type: string; count: number }[] }>()

const typeNames: Record<string, string> = {
  ZONE_INTRUSION: '区域入侵', CROWD_GATHERING: '人群聚集', PEOPLE_EXCEED: '人数超限',
}

const option = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [{
    type: 'pie', radius: ['40%', '70%'],
    data: props.data.map((d) => ({ name: typeNames[d.type] || d.type, value: d.count })),
    label: { show: false },
  }],
}))
</script>
