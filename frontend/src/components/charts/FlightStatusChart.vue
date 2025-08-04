<template>
  <div class="chart-container">
    <canvas ref="chartRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const chartRef = ref(null)
let chart = null

const chartData = {
  labels: ['Zamanında', 'Gecikmeli', 'İptal Edildi'],
  datasets: [{
    data: [75, 15, 10],
    backgroundColor: [
      '#67c23a',
      '#e6a23c',
      '#f56c6c'
    ],
    borderWidth: 0
  }]
}

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: {
        usePointStyle: true,
        padding: 20
      }
    }
  }
}

onMounted(() => {
  const ctx = chartRef.value.getContext('2d')
  chart = new Chart(ctx, {
    type: 'doughnut',
    data: chartData,
    options: chartOptions
  })
})

onUnmounted(() => {
  if (chart) {
    chart.destroy()
  }
})
</script>

<style scoped>
.chart-container {
  height: 300px;
  position: relative;
}
</style> 