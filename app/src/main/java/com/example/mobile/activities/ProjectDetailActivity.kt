package com.example.mobile.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.ScrollView
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.Task
import com.example.mobile.helpers.DataManager
import com.example.mobile.helpers.UIAnimations
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ProjectDetailActivity : BaseActivity() {

    // --- UI General ---
    private lateinit var tabTareas: TextView
    private lateinit var tabResumenes: TextView
    private lateinit var tabBar: LinearLayout
    private lateinit var indicador: View

    // --- Contenedores de Vistas ---
    private lateinit var scrollTasks: ScrollView
    private lateinit var scrollResumen: ScrollView

    // --- UI Tareas ---
    private lateinit var pendientesLayout: LinearLayout
    private lateinit var hechasLayout: LinearLayout
    private lateinit var progresoLayout: LinearLayout

    // --- UI Resumen / Gráficos ---
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    // --- Datos ---
    private lateinit var selectedProject: Project
    private lateinit var dataManager: DataManager
    private var originalProjects: List<Project> = emptyList()

    private var selectedTab = 0

    companion object {
        private const val REQUEST_CODE_TASK_DETAIL = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_detail)
        UIAnimations(this).startFloatingCircles()

        dataManager = DataManager(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        tabTareas = findViewById(R.id.tabTareas)
        tabResumenes = findViewById(R.id.tabResumenes)
        tabBar = findViewById(R.id.tabBar)
        indicador = findViewById(R.id.indicator)

        // Inicializar Vistas Principales (Contenedores)
        scrollTasks = findViewById(R.id.scrollTasks)
        scrollResumen = findViewById(R.id.scrollResumen)

        pendientesLayout = findViewById(R.id.pendientesContainer)
        hechasLayout = findViewById(R.id.hechasContainer)
        progresoLayout = findViewById(R.id.progresoContainer)

        // Inicializar Referencias UI Gráficos
        barChart = findViewById(R.id.barChartHoras)
        pieChart = findViewById(R.id.pieChartEstado)

        loadProjectData()

        val txtMembers = findViewById<TextView>(R.id.txtMembers)
        // CAMBIO: Uso de getString para "miembros"
        txtMembers.text = "${selectedProject.projectMembers.size} ${getString(R.string.pd_members)}"

        tabBar.post {
            val params = indicador.layoutParams
            params.width = tabBar.width / 2
            indicador.layoutParams = params
        }

        tabTareas.setOnClickListener { selectTab(0) }
        tabResumenes.setOnClickListener {
            selectTab(1)
        }

        selectTab(0)
        loadAllTasks()
    }


    private fun loadProjectData() {
        val savedProjects = dataManager.loadProjects()

        val projectFromIntent = intent.getSerializableExtra("selected_project") as? Project
        projectFromIntent?.let { intentProject ->
            if (savedProjects.isNotEmpty()) {
                val savedProject = savedProjects.find { it.id == intentProject.id }
                selectedProject = savedProject ?: intentProject
            } else {
                selectedProject = intentProject
            }
        } ?: run {
            if (savedProjects.isNotEmpty()) {
                selectedProject = savedProjects.first()
            } else {
                Toast.makeText(this, "No se pudo cargar el proyecto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        originalProjects = savedProjects
    }

    override fun onResume() {
        super.onResume()
        loadAllTasks()
        // Si estamos en la pestaña de resumen, actualizar gráficos al volver
        if (selectedTab == 1) {
            refreshCharts()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TASK_DETAIL) {
            if (resultCode == TasksDetailActivity.RESULT_STATUS_CHANGED) {
                val taskIndex = data?.getIntExtra(TasksDetailActivity.EXTRA_TASK_INDEX, -1) ?: -1
                val newStatus = data?.getStringExtra(TasksDetailActivity.EXTRA_NEW_STATUS)

                if (taskIndex != -1 && newStatus != null && taskIndex < selectedProject.projectTasks.size) {
                    selectedProject.projectTasks[taskIndex].taskStatus = newStatus

                    val taskName = selectedProject.projectTasks[taskIndex].taskName
                    Toast.makeText(this, "Tarea '$taskName' actualizada", Toast.LENGTH_SHORT).show()

                    saveProjectChanges()
                    loadAllTasks()
                }
            }
        }
    }

    private fun loadAllTasks() {
        clearTaskViews(pendientesLayout)
        clearTaskViews(progresoLayout)
        clearTaskViews(hechasLayout)

        selectedProject.projectTasks.forEach { task ->
            addTaskToLayout(task)
        }
    }

    private fun clearTaskViews(container: LinearLayout) {
        val childCount = container.childCount
        if (childCount > 1) {
            for (i in childCount - 1 downTo 1) {
                container.removeViewAt(i)
            }
        }
    }

    private fun selectTab(position: Int) {
        selectedTab = position
        val tabWidth = tabBar.width / 2

        indicador.animate()
            .translationX((position * tabWidth).toFloat())
            .setDuration(200)
            .start()

        if (position == 0) {
            // --- MODO TAREAS ---
            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabTareas.setTypeface(null, Typeface.BOLD)
            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabResumenes.setTypeface(null, Typeface.NORMAL)

            scrollTasks.visibility = View.VISIBLE
            scrollResumen.visibility = View.GONE

        } else {
            // --- MODO RESUMEN ---
            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabTareas.setTypeface(null, Typeface.NORMAL)
            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabResumenes.setTypeface(null, Typeface.BOLD)

            scrollTasks.visibility = View.GONE
            scrollResumen.visibility = View.VISIBLE

            refreshCharts()
        }
    }

    private fun saveProjectChanges() {
        dataManager.saveProjectChanges(selectedProject)
    }

    private fun addTaskToLayout(task: Task) {
        val (layoutRes, parentLayout) = when {
            task.taskStatus?.equals("pendiente", ignoreCase = true) == true ||
                    task.taskStatus?.equals("pendientes", ignoreCase = true) == true ||
                    task.taskStatus?.equals("sin empezar", ignoreCase = true) == true -> {
                R.layout.to_do_tasks to pendientesLayout
            }
            task.taskStatus?.equals("en progreso", ignoreCase = true) == true ||
                    task.taskStatus?.equals("en proceso", ignoreCase = true) == true -> {
                R.layout.in_progress_taks to progresoLayout
            }
            task.taskStatus?.equals("hecha", ignoreCase = true) == true ||
                    task.taskStatus?.equals("hechas", ignoreCase = true) == true ||
                    task.taskStatus?.equals("hecho", ignoreCase = true) == true ||
                    task.taskStatus?.equals("completada", ignoreCase = true) == true -> {
                R.layout.finished_tasks to hechasLayout
            }
            else -> {
                R.layout.to_do_tasks to pendientesLayout
            }
        }

        val taskView = layoutInflater.inflate(layoutRes, parentLayout, false)
        val taskName = taskView.findViewById<TextView>(R.id.taskNameText)
        taskName.text = task.taskName

        val btnEmpezar = taskView.findViewById<Button>(R.id.btnEmpezar)
        val btnReiniciar = taskView.findViewById<Button>(R.id.btnReiniciar)

        if (btnEmpezar != null) {
            when {
                task.taskStatus?.equals("hecha", ignoreCase = true) == true ||
                        task.taskStatus?.equals("hechas", ignoreCase = true) == true ||
                        task.taskStatus?.equals("hecho", ignoreCase = true) == true ||
                        task.taskStatus?.equals("completada", ignoreCase = true) == true -> {
                    btnEmpezar.visibility = View.GONE
                    if (btnReiniciar != null) {
                        btnReiniciar.visibility = View.VISIBLE
                        // CAMBIO: Traducción de botones
                        btnReiniciar.text = getString(R.string.td_restart) // "Reiniciar"
                        btnReiniciar.setOnClickListener {
                            task.taskStatus = "Pendiente"
                            saveProjectChanges()
                            loadAllTasks()
                        }
                    }
                }
                task.taskStatus?.equals("en progreso", ignoreCase = true) == true ||
                        task.taskStatus?.equals("en proceso", ignoreCase = true) == true -> {
                    // CAMBIO: Traducción de botones
                    btnEmpezar.text = getString(R.string.btn_finish) // No había un string exacto para "Terminar", lo dejo para evitar crash, o podrías usar getString(R.string.td_status_done)
                    btnEmpezar.visibility = View.VISIBLE
                    if (btnReiniciar != null) {
                        btnReiniciar.visibility = View.VISIBLE
                        btnReiniciar.text = getString(R.string.btn_back_to_pending) // Tampoco hay string exacto.
                        btnReiniciar.setOnClickListener {
                            task.taskStatus = "Pendiente"
                            saveProjectChanges()
                            loadAllTasks()
                        }
                    }

                    btnEmpezar.setOnClickListener {
                        task.taskStatus = "hecha"
                        saveProjectChanges()
                        loadAllTasks()
                    }
                }
                else -> {
                    // CAMBIO: Traducción de botones
                    btnEmpezar.text = getString(R.string.td_start) // "Empezar"
                    btnEmpezar.visibility = View.VISIBLE
                    if (btnReiniciar != null) {
                        btnReiniciar.visibility = View.GONE
                    }
                    btnEmpezar.setOnClickListener {
                        task.taskStatus = "en progreso"
                        saveProjectChanges()
                        loadAllTasks()
                    }
                }
            }
        }

        taskView.setOnClickListener {
            val intent = Intent(this, TasksDetailActivity::class.java)
            intent.putExtra("project", selectedProject)
            val taskIndex = selectedProject.projectTasks.indexOf(task)
            intent.putExtra("taskIndex", taskIndex)

            startActivityForResult(intent, REQUEST_CODE_TASK_DETAIL)
        }

        parentLayout.addView(taskView)
    }

    //Logica de gráficos
    private fun refreshCharts() {
        val listaTareas = ArrayList(selectedProject.projectTasks)
        setupBarChart(barChart, listaTareas)
        setupPieChart(pieChart, listaTareas)
    }

    private fun setupBarChart(chart: BarChart, lista: ArrayList<Task>) {
        val entries = ArrayList<BarEntry>()
        val nombres = ArrayList<String>()

        for (i in lista.indices) {
            val tarea = lista[i]
            val horas = tarea.taskTime?.toFloat() ?: 0f
            entries.add(BarEntry(i.toFloat(), horas))
            nombres.add(tarea.taskName)
        }

        // CAMBIO: Usamos strings.xml para la leyenda (Horas por Tareas)
        val dataSet = BarDataSet(entries, getString(R.string.graphics_zone_tittle_tasksperhour))
        dataSet.color = Color.parseColor("#1A4349")
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        chart.data = barData

        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.extraBottomOffset = 30f
        chart.animateY(1500)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        if (nombres.isNotEmpty()) {
            xAxis.valueFormatter = IndexAxisValueFormatter(nombres)
        }
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        chart.axisRight.isEnabled = false

        val legend = chart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.yEntrySpace = 5f
        legend.textSize = 12f
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

        // CAMBIO IMPORTANTE: Ahora usamos getString(...) para que cambie de idioma
        if (pendientes > 0) {
            // "Sin Empezar" (matches better than "Pendientes" with ---)
            entries.add(PieEntry(pendientes, getString(R.string.td_status_not_started)))
            colors.add(Color.parseColor("#FF7043"))
        }
        if (enProgreso > 0) {
            // "En Progreso"
            entries.add(PieEntry(enProgreso, getString(R.string.td_status_in_progress)))
            colors.add(Color.parseColor("#1A4349"))
        }
        if (hechas > 0) {
            // "Hecho" / "Done"
            entries.add(PieEntry(hechas, getString(R.string.td_status_done)))
            colors.add(Color.LTGRAY)
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 20f
        dataSet.valueTypeface = Typeface.DEFAULT

        val pieData = PieData(dataSet)
        chart.data = pieData

        chart.description.isEnabled = false

        chart.isDrawHoleEnabled = true
        chart.holeRadius = 20f
        chart.transparentCircleRadius = 50f

        chart.centerText = ""
        chart.setCenterTextSize(22f)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setCenterTextColor(Color.parseColor("#1A4349"))

        val legend = chart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.yEntrySpace = 5f
        legend.textSize = 12f
        legend.textColor = Color.DKGRAY

        chart.animateY(1000)
        chart.invalidate()
    }
}