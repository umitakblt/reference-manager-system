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
  labels: ['Türk Hava Yolları', 'Lufthansa', 'British Airways', 'Air France', 'KLM'],
  datasets: [{
    label: 'Zamanında Uçuş Oranı (%)',
    data: [92, 88, 85, 87, 90],
    backgroundColor: [
      '#409eff',
      '#67c23a',
      '#e6a23c',
      '#f56c6c',
      '#909399'
    ],
    borderWidth: 0
  }]
}

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      max: 100,
      grid: {
        color: 'rgba(0, 0, 0, 0.1)'
      }
    },
    x: {
      grid: {
        display: false
      }
    }
  }
}

onMounted(() => {
  const ctx = chartRef.value.getContext('2d')
  chart = new Chart(ctx, {
    type: 'bar',
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