package com.example.mobile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.Date


class ProjectChartsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_charts)

        val listaTareas = generarDatosDePrueba()

        // Intent
        // val proyecto = intent.getSerializableExtra("extra_project") as? Project
        // val listaTareas = proyecto?.taskList ?: generarDatosDePrueba()

        // Gráfico 1: Barras
        val barChart = findViewById<BarChart>(R.id.barChartHoras)
        setupBarChart(barChart, listaTareas) // <--- ¡Mira! Le pasamos la lista

        // Gráfico 2: Pastel
        val pieChart = findViewById<PieChart>(R.id.pieChartEstado)
        setupPieChart(pieChart, listaTareas) // <--- ¡Mira! Le pasamos la lista

        // Gráfico 3: Línea (Este se queda simulado visualmente)
        val lineChart = findViewById<LineChart>(R.id.lineChartProgreso)
        setupLineChart(lineChart)

        // Sincronizar Scroll
        setupCustomScrollbar()
    }

    // Datos Fantasmas

    private fun generarDatosDePrueba(): ArrayList<Task> {
        val lista = ArrayList<Task>()
        // Aquí inventamos tareas con TU estructura 'Task' real
        lista.add(Task("Diseño UI", "Figma", null, null, null, "Hecha", 5))
        lista.add(Task("Base Datos", "SQL", null, null, null, "Hecha", 8))
        lista.add(Task("Login", "Auth", null, null, null, "En Progreso", 12))
        lista.add(Task("API Rest", "Backend", null, null, null, "En Progreso", 6))
        lista.add(Task("Testing", "Unitarios", null, null, null, "Pendiente", 4))
        lista.add(Task("Deploy", "Play Store", null, null, null, "Pendiente", 2))
        return lista
    }

    private fun setupBarChart(chart: BarChart, lista: ArrayList<Task>) {
        val entries = ArrayList<BarEntry>()
        val nombres = ArrayList<String>()

        // BUCLE: Transforma tu lista de tareas en barras
        for (i in lista.indices) {
            val tarea = lista[i]
            // Sacamos las horas (si es null ponemos 0)
            val horas = tarea.taskTime?.toFloat() ?: 0f

            entries.add(BarEntry(i.toFloat(), horas))
            nombres.add(tarea.taskName)
        }

        val dataSet = BarDataSet(entries, "Horas invertidas")
        dataSet.color = Color.parseColor("#1A4349") // Verde Oscuro
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        chart.data = barData

        // Estilos visuales
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.animateY(1500)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(nombres) // Ponemos nombres reales
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        chart.axisRight.isEnabled = false
        chart.invalidate()
    }

    private fun setupPieChart(chart: PieChart, lista: ArrayList<Task>) {
        // Contadores
        var pendientes = 0f
        var enProgreso = 0f
        var hechas = 0f

        // BUCLE: Cuenta cuántas hay de cada tipo
        for (tarea in lista) {
            when (tarea.taskStatus) {
                "Pendiente" -> pendientes++
                "En Progreso" -> enProgreso++
                "Hecha" -> hechas++
                // Ajusta estos textos si tu compañero usa otros (ej: "TODO")
            }
        }

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        if (pendientes > 0) {
            entries.add(PieEntry(pendientes, "Pendientes"))
            colors.add(Color.parseColor("#FF7043")) // Naranja
        }
        if (enProgreso > 0) {
            entries.add(PieEntry(enProgreso, "En Progreso"))
            colors.add(Color.parseColor("#1A4349")) // Verde
        }
        if (hechas > 0) {
            entries.add(PieEntry(hechas, "Hechas"))
            colors.add(Color.LTGRAY) // Gris
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val pieData = PieData(dataSet)
        chart.data = pieData

        chart.description.isEnabled = false
        chart.centerText = "Estado"
        chart.setCenterTextSize(16f)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun setupLineChart(chart: LineChart) {
        // Mantenemos datos fijos aquí porque 'Task' no tiene histórico diario
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 1f))
        entries.add(Entry(1f, 3f))
        entries.add(Entry(2f, 0f))
        entries.add(Entry(3f, 5f))
        entries.add(Entry(4f, 2f))

        val dataSet = LineDataSet(entries, "Progreso Semanal")
        dataSet.color = Color.parseColor("#1A4349")
        dataSet.setCircleColor(Color.parseColor("#FF7043"))
        dataSet.lineWidth = 2f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#1A4349")
        dataSet.fillAlpha = 50
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Lun", "Mar", "Mié", "Jue", "Vie"))
        chart.invalidate()
    }

    // Barra de scroll
    private fun setupCustomScrollbar() {
        val scrollView = findViewById<ScrollView>(R.id.mainScrollView)
        val indicador = findViewById<View>(R.id.customScrollIndicator)

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val alturaContenido = scrollView.getChildAt(0).height
            val alturaVentana = scrollView.height
            val maximoScroll = alturaContenido - alturaVentana
            val alturaRiel = (indicador.parent as View).height
            val alturaIndicador = indicador.height
            val maximoMovimientoIndicador = alturaRiel - alturaIndicador

            if (maximoScroll > 0) {
                val porcentajeScroll = scrollView.scrollY.toFloat() / maximoScroll
                indicador.translationY = porcentajeScroll * maximoMovimientoIndicador
            }
        }
    }
}