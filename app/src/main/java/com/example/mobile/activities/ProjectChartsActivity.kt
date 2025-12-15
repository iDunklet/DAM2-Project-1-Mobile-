package com.example.mobile.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.Task
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ProjectChartsActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_charts)

        // Recibir el proyecto del intent
        val proyecto = intent.getSerializableExtra("selected_project") as? Project

        // *** LISTENER PARA TAB TAREAS ***
        val tabTareas = findViewById<TextView>(R.id.tabTareas)
        tabTareas.setOnClickListener {
            // Crear intent para volver a ProjectDetailActivity
            val intent = Intent(this@ProjectChartsActivity, ProjectDetailActivity::class.java)
            // Pasar el proyecto de vuelta
            if (proyecto != null) {
                intent.putExtra("selected_project", proyecto)
            }
            // Iniciar la actividad
            startActivity(intent)
            // Opcional: cerrar esta actividad
            finish()
        }

        // Usar las tareas reales del proyecto o lista vacía
        val listaTareas = if (proyecto != null) {
            ArrayList(proyecto.projectTasks)
        } else {
            ArrayList<Task>() // Lista vacía si no hay proyecto
        }

        // Gráfico 1: Barras
        val barChart = findViewById<BarChart>(R.id.barChartHoras)
        setupBarChart(barChart, listaTareas)

        // Gráfico 2: Pastel
        val pieChart = findViewById<PieChart>(R.id.pieChartEstado)
        setupPieChart(pieChart, listaTareas)

        // Gráfico 3: Línea (Este se queda simulado visualmente)
        val lineChart = findViewById<LineChart>(R.id.lineChartProgreso)
        setupLineChart(lineChart)

        // Sincronizar Scroll
        setupCustomScrollbar()
    }

    // ... el resto del código se mantiene igual ...
    private fun setupBarChart(chart: BarChart, lista: ArrayList<Task>) {
        val entries = ArrayList<BarEntry>()
        val nombres = ArrayList<String>()

        for (i in lista.indices) {
            val tarea = lista[i]
            val horas = tarea.taskTime?.toFloat() ?: 0f
            entries.add(BarEntry(i.toFloat(), horas))
            nombres.add(tarea.taskName)
        }

        val dataSet = BarDataSet(entries, "Horas invertidas")
        dataSet.color = Color.parseColor("#1A4349")
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        chart.data = barData

        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.animateY(1500)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(nombres)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        chart.axisRight.isEnabled = false
        chart.invalidate()
    }

    private fun setupPieChart(chart: PieChart, lista: ArrayList<Task>) {
        var pendientes = 0f
        var enProgreso = 0f
        var hechas = 0f

        for (tarea in lista) {
            when (tarea.taskStatus?.lowercase()) {
                "pendiente", "pendientes", "sin empezar" -> pendientes++
                "en progreso", "en proceso" -> enProgreso++
                "hecha", "hechas", "hecho", "completada" -> hechas++
            }
        }

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        if (pendientes > 0) {
            entries.add(PieEntry(pendientes, "Pendientes"))
            colors.add(Color.parseColor("#FF7043"))
        }
        if (enProgreso > 0) {
            entries.add(PieEntry(enProgreso, "En Progreso"))
            colors.add(Color.parseColor("#1A4349"))
        }
        if (hechas > 0) {
            entries.add(PieEntry(hechas, "Hechas"))
            colors.add(Color.LTGRAY)
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