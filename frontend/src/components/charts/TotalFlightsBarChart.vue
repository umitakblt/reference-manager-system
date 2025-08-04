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
  labels: ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran'],
  datasets: [{
    label: 'Toplam Uçuş',
    data: [1250, 1180, 1320, 1450, 1580, 1420],
    backgroundColor: '#409eff',
    borderColor: '#2563eb',
    borderWidth: 1
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