<template>
  <canvas ref="canvasRef" class="detection-canvas" />
</template>

<script setup lang="ts">
import { ref } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)

function getCanvas(): HTMLCanvasElement | null {
  return canvasRef.value
}

defineExpose({ getCanvas })
</script>

<style>
.detection-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 10;
}
</style>
