package com.example.mobile

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ProjectChartsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_charts)

        // 1. Buscamos el gráfico que acabas de poner en el XML
        // Asegúrate que el ID 'barChartHoras' coincida con el que pusiste en el XML
        val barChart = findViewById<BarChart>(R.id.barChartHoras)

        // 2. Llamamos a la función que lo prepara
        setupBarChart(barChart)
    }

    private fun setupBarChart(chart: BarChart) {
        // --- A. CREAR LOS DATOS (Simulados) ---
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 1f)) // Tarea 1: 1 hora
        entries.add(BarEntry(1f, 3f)) // Tarea 2: 3 horas
        entries.add(BarEntry(2f, 6f)) // Tarea 3: 6 horas
        entries.add(BarEntry(3f, 5f)) // Tarea 4: 5 horas
        entries.add(BarEntry(4f, 3f)) // Tarea 5: 3 horas
        entries.add(BarEntry(5f, 1.5f)) // Tarea 6
        entries.add(BarEntry(6f, 2f))   // Tarea 7

        // --- B. ESTILO DE LAS BARRAS (Tu color verde oscuro) ---
        val dataSet = BarDataSet(entries, "Horas invertidas")
        dataSet.color = Color.parseColor("#1A4349") // <--- TU COLOR EXACTO
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        // --- C. CARGAR DATOS ---
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f // Hace las barras más finas y elegantes
        chart.data = barData

        // --- D. LIMPIEZA VISUAL (Para que parezca tu diseño) ---
        chart.description.isEnabled = false // Borra la frase "Description Label"
        chart.setFitBars(true) // Centra las barras
        chart.animateY(1500) // Animación suave al subir

        // Configuración Eje X (Abajo)
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false) // Quita la rejilla vertical fea
        val taskNames = listOf("Tarea 1", "Tarea 2", "Tarea 3", "Tarea 4", "Tarea 5", "Tarea 6", "Tarea 7")
        xAxis.valueFormatter = IndexAxisValueFormatter(taskNames)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f // Rota el texto como en tu imagen

        // Configuración Eje Y (Izquierda/Derecha)
        chart.axisRight.isEnabled = false // Quita los números de la derecha
        chart.axisLeft.setDrawGridLines(true) // Deja las líneas guía horizontales
        chart.axisLeft.gridColor = Color.parseColor("#CFD8DC") // Color gris suave para las líneas

        // Refrescar para ver los cambios
        chart.invalidate()
    }
}