package com.example.mobile.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.Task
import com.example.mobile.helpers.DataManager
import com.example.mobile.helpers.UIAnimations

class ProjectDetailActivity : BaseActivity() {

    private lateinit var tabTareas: TextView
    private lateinit var tabResumenes: TextView
    private lateinit var tabBar: LinearLayout
    private lateinit var indicador: View
    private lateinit var pendientesLayout: LinearLayout
    private lateinit var hechasLayout: LinearLayout
    private lateinit var progresoLayout: LinearLayout
    private lateinit var txtMembers: TextView
    private lateinit var btnBack: ImageView

    private lateinit var selectedProject: Project
    private lateinit var dataManager: DataManager
    private var originalProjects: List<Project> = emptyList()
    private var selectedTab = 0

    companion object {
        private const val REQUEST_CODE_TASK_DETAIL = 1001
        private const val STATUS_PENDIENTE = "pendiente"
        private const val STATUS_EN_PROGRESO = "en progreso"
        private const val STATUS_HECHA = "hecha"
        private const val STATUS_COMPLETADA = "completada"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_detail)

        initializeComponents()
        setupUI()
        loadData()
        setupTabNavigation()
    }

    override fun onResume() {
        super.onResume()
        refreshTasks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleTaskDetailResult(requestCode, resultCode, data)
    }

    // ---------------------------------------------------------
    //  Inicialización de componentes
    // ---------------------------------------------------------
    private fun initializeComponents() {
        dataManager = DataManager(this)
        loadViews()
        setupAnimations()
    }

    private fun loadViews() {
        tabTareas = findViewById(R.id.tabTareas)
        tabResumenes = findViewById(R.id.tabResumenes)
        tabBar = findViewById(R.id.tabBar)
        indicador = findViewById(R.id.indicator)
        pendientesLayout = findViewById(R.id.pendientesContainer)
        hechasLayout = findViewById(R.id.hechasContainer)
        progresoLayout = findViewById(R.id.progresoContainer)
        txtMembers = findViewById(R.id.txtMembers)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupAnimations() {
        UIAnimations(this).startFloatingCircles()
    }

    // ---------------------------------------------------------
    //  Configuración de UI
    // ---------------------------------------------------------
    private fun setupUI() {
        setupBackButton()
        setupIndicator()
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupIndicator() {
        tabBar.post {
            val params = indicador.layoutParams
            params.width = tabBar.width / 2
            indicador.layoutParams = params
        }
    }

    // ---------------------------------------------------------
    //  Carga de datos
    // ---------------------------------------------------------
    private fun loadData() {
        loadProjectData()
        updateMemberCount()
        refreshTasks()
    }

    private fun loadProjectData() {
        val savedProjects = dataManager.loadProjects()
        val projectFromIntent = intent.getSerializableExtra("selected_project") as? Project

        selectedProject = when {
            projectFromIntent != null && savedProjects.isNotEmpty() -> {
                savedProjects.find { it.id == projectFromIntent.id } ?: projectFromIntent
            }
            projectFromIntent != null -> projectFromIntent
            savedProjects.isNotEmpty() -> savedProjects.first()
            else -> {
                Toast.makeText(this, "No se pudo cargar el proyecto", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }

        originalProjects = savedProjects
    }

    private fun updateMemberCount() {
        txtMembers.text = "${selectedProject.projectMembers.size} miembros"
    }

    // ---------------------------------------------------------
    //  Gestión de pestañas
    // ---------------------------------------------------------
    private fun setupTabNavigation() {
        setupTasksTab()
        setupChartsTab()
        selectTab(0)
    }

    private fun setupTasksTab() {
        tabTareas.setOnClickListener { selectTab(0) }
    }

    private fun setupChartsTab() {
        tabResumenes.setOnClickListener {
            navigateToChartsActivity()
        }
    }

    private fun selectTab(position: Int) {
        selectedTab = position
        updateTabIndicator(position)
        updateTabStyles(position)
    }

    private fun updateTabIndicator(position: Int) {
        val tabWidth = tabBar.width / 2
        indicador.animate()
            .translationX((position * tabWidth).toFloat())
            .setDuration(200)
            .start()
    }

    private fun updateTabStyles(position: Int) {
        if (position == 0) {
            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabTareas.setTypeface(null, Typeface.BOLD)
            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabResumenes.setTypeface(null, Typeface.NORMAL)
        }
    }

    // ---------------------------------------------------------
    //  Navegación entre actividades
    // ---------------------------------------------------------
    private fun navigateToChartsActivity() {
        val intent = Intent(this, ProjectChartsActivity::class.java)
        intent.putExtra("selected_project", selectedProject)
        startActivity(intent)
    }

    // ---------------------------------------------------------
    //  Gestión de resultados
    // ---------------------------------------------------------
    private fun handleTaskDetailResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_TASK_DETAIL &&
            resultCode == TasksDetailActivity.RESULT_STATUS_CHANGED) {

            val taskIndex = data?.getIntExtra(TasksDetailActivity.EXTRA_TASK_INDEX, -1) ?: -1
            val newStatus = data?.getStringExtra(TasksDetailActivity.EXTRA_NEW_STATUS)
            val oldStatus = data?.getStringExtra(TasksDetailActivity.EXTRA_OLD_STATUS)

            if (isValidTaskUpdate(taskIndex, newStatus)) {
                updateTaskStatus(taskIndex, newStatus!!)
                showUpdateToast(taskIndex, oldStatus, newStatus)
                saveAndRefresh()
            }
        }
    }

    private fun isValidTaskUpdate(taskIndex: Int, newStatus: String?): Boolean {
        return taskIndex != -1 &&
                newStatus != null &&
                taskIndex < selectedProject.projectTasks.size
    }

    private fun updateTaskStatus(taskIndex: Int, newStatus: String) {
        selectedProject.projectTasks[taskIndex].taskStatus = newStatus
    }

    private fun showUpdateToast(taskIndex: Int, oldStatus: String?, newStatus: String) {
        val taskName = selectedProject.projectTasks[taskIndex].taskName
        Toast.makeText(this,
            "Tarea '$taskName' actualizada: ${oldStatus ?: "desconocido"} → $newStatus",
            Toast.LENGTH_SHORT).show()
    }

    // ---------------------------------------------------------
    //  Gestión de tareas
    // ---------------------------------------------------------
    private fun refreshTasks() {
        clearAllTaskViews()
        loadAllTasks()
    }

    private fun clearAllTaskViews() {
        clearTaskViews(pendientesLayout)
        clearTaskViews(progresoLayout)
        clearTaskViews(hechasLayout)
    }

    private fun clearTaskViews(container: LinearLayout) {
        val childCount = container.childCount
        if (childCount > 1) {
            for (i in childCount - 1 downTo 1) {
                container.removeViewAt(i)
            }
        }
    }

    private fun loadAllTasks() {
        selectedProject.projectTasks.forEach { task ->
            addTaskToLayout(task)
        }
    }

    private fun addTaskToLayout(task: Task) {
        val (layoutRes, parentLayout) = getTaskLayoutConfiguration(task)
        val taskView = layoutInflater.inflate(layoutRes, parentLayout, false)

        setupTaskView(taskView, task, parentLayout)
        parentLayout.addView(taskView)
    }

    private fun getTaskLayoutConfiguration(task: Task): Pair<Int, LinearLayout> {
        return when {
            isTaskPending(task) -> Pair(R.layout.to_do_tasks, pendientesLayout)
            isTaskInProgress(task) -> Pair(R.layout.in_progress_taks, progresoLayout)
            isTaskCompleted(task) -> Pair(R.layout.finished_tasks, hechasLayout)
            else -> Pair(R.layout.to_do_tasks, pendientesLayout)
        }
    }

    private fun isTaskPending(task: Task): Boolean {
        return task.taskStatus?.equals(STATUS_PENDIENTE, ignoreCase = true) == true ||
                task.taskStatus?.equals("pendientes", ignoreCase = true) == true ||
                task.taskStatus?.equals("sin empezar", ignoreCase = true) == true
    }

    private fun isTaskInProgress(task: Task): Boolean {
        return task.taskStatus?.equals(STATUS_EN_PROGRESO, ignoreCase = true) == true ||
                task.taskStatus?.equals("en proceso", ignoreCase = true) == true
    }

    private fun isTaskCompleted(task: Task): Boolean {
        return task.taskStatus?.equals(STATUS_HECHA, ignoreCase = true) == true ||
                task.taskStatus?.equals("hechas", ignoreCase = true) == true ||
                task.taskStatus?.equals("hecho", ignoreCase = true) == true ||
                task.taskStatus?.equals(STATUS_COMPLETADA, ignoreCase = true) == true
    }

    private fun setupTaskView(taskView: View, task: Task, parentLayout: LinearLayout) {
        val taskNameView = taskView.findViewById<TextView>(R.id.taskNameText)
        taskNameView.text = task.taskName

        val btnEmpezar = taskView.findViewById<Button?>(R.id.btnEmpezar)
        val btnReiniciar = taskView.findViewById<Button?>(R.id.btnReiniciar)

        setupTaskButtons(btnEmpezar, btnReiniciar, task)
        setupTaskClickListener(taskView, task)
    }

    private fun setupTaskButtons(btnEmpezar: Button?, btnReiniciar: Button?, task: Task) {
        when {
            isTaskCompleted(task) -> setupCompletedTaskButtons(btnEmpezar, btnReiniciar, task)
            isTaskInProgress(task) -> setupInProgressTaskButtons(btnEmpezar, btnReiniciar, task)
            else -> setupPendingTaskButtons(btnEmpezar, btnReiniciar, task)
        }
    }

    private fun setupCompletedTaskButtons(btnEmpezar: Button?, btnReiniciar: Button?, task: Task) {
        btnEmpezar?.visibility = View.GONE
        btnReiniciar?.let {
            it.visibility = View.VISIBLE
            it.text = "Reiniciar"
            it.setOnClickListener {
                task.taskStatus = "Pendiente"
                saveAndRefresh()
            }
        }
    }

    private fun setupInProgressTaskButtons(btnEmpezar: Button?, btnReiniciar: Button?, task: Task) {
        btnEmpezar?.let {
            it.text = "Terminar"
            it.visibility = View.VISIBLE
            it.setOnClickListener {
                task.taskStatus = STATUS_HECHA
                saveAndRefresh()
            }
        }

        btnReiniciar?.let {
            it.visibility = View.VISIBLE
            it.text = "Volver a Pendiente"
            it.setOnClickListener {
                task.taskStatus = "Pendiente"
                saveAndRefresh()
            }
        }
    }

    private fun setupPendingTaskButtons(btnEmpezar: Button?, btnReiniciar: Button?, task: Task) {
        btnEmpezar?.let {
            it.text = "Empezar"
            it.visibility = View.VISIBLE
            it.setOnClickListener {
                task.taskStatus = STATUS_EN_PROGRESO
                saveAndRefresh()
            }
        }

        btnReiniciar?.visibility = View.GONE
    }

    private fun setupTaskClickListener(taskView: View, task: Task) {
        taskView.setOnClickListener {
            navigateToTaskDetail(task)
        }
    }

    private fun navigateToTaskDetail(task: Task) {
        val intent = Intent(this, TasksDetailActivity::class.java)
        intent.putExtra("project", selectedProject)
        val taskIndex = selectedProject.projectTasks.indexOf(task)
        intent.putExtra("taskIndex", taskIndex)
        startActivityForResult(intent, REQUEST_CODE_TASK_DETAIL)
    }

    // ---------------------------------------------------------
    //  Persistencia de datos
    // ---------------------------------------------------------
    private fun saveAndRefresh() {
        saveProjectChanges()
        refreshTasks()
    }

    private fun saveProjectChanges() {
        dataManager.saveProjectChanges(selectedProject)
        Toast.makeText(this, "Cambios guardados localmente", Toast.LENGTH_SHORT).show()
        logSavedData()
    }

    private fun logSavedData() {
        val sharedPref = getSharedPreferences("project_data", MODE_PRIVATE)
        val json = sharedPref.getString("saved_projects", "{}")
        println("✅✅✅ Datos guardados correctamente!")
        println("📊 Estado actual: $json")
    }
}