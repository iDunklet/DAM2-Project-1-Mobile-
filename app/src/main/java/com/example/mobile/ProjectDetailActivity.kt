package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.Button

class ProjectDetailActivity : BaseActivity() {

    private lateinit var tabTareas: TextView
    private lateinit var tabResumenes: TextView
    private lateinit var tabBar: LinearLayout
    private lateinit var indicador: View

    private lateinit var pendientesLayout: LinearLayout
    private lateinit var hechasLayout: LinearLayout
    private lateinit var progresoLayout: LinearLayout
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


        dataManager = DataManager(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        tabTareas = findViewById(R.id.tabTareas)
        tabResumenes = findViewById(R.id.tabResumenes)
        tabBar = findViewById(R.id.tabBar)
        indicador = findViewById(R.id.indicator)

        pendientesLayout = findViewById(R.id.pendientesContainer)
        hechasLayout = findViewById(R.id.hechasContainer)
        progresoLayout = findViewById(R.id.progresoContainer)

        loadProjectData()

        val txtMembers = findViewById<TextView>(R.id.txtMembers)
        txtMembers.text = "${selectedProject.projectMembers.size} miembros"

        tabBar.post {
            val params = indicador.layoutParams
            params.width = tabBar.width / 2
            indicador.layoutParams = params
        }

        tabTareas.setOnClickListener { selectTab(0) }
        tabResumenes.setOnClickListener { selectTab(1) }

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("DEBUG: onActivityResult called")
        println("DEBUG: requestCode: $requestCode, resultCode: $resultCode")

        if (requestCode == REQUEST_CODE_TASK_DETAIL) {
            if (resultCode == TasksDetailActivity.RESULT_STATUS_CHANGED) {

                val taskIndex = data?.getIntExtra(TasksDetailActivity.EXTRA_TASK_INDEX, -1) ?: -1
                val newStatus = data?.getStringExtra(TasksDetailActivity.EXTRA_NEW_STATUS)
                val oldStatus = data?.getStringExtra(TasksDetailActivity.EXTRA_OLD_STATUS)

                println("DEBUG: Task index from result: $taskIndex")
                println("DEBUG: Old status: $oldStatus")
                println("DEBUG: New status: $newStatus")
                println("DEBUG: Project tasks size: ${selectedProject.projectTasks.size}")


                if (taskIndex != -1 && newStatus != null && taskIndex < selectedProject.projectTasks.size) {

                    selectedProject.projectTasks[taskIndex].taskStatus = newStatus


                    println("DEBUG: Updated task status in project: ${selectedProject.projectTasks[taskIndex].taskStatus}")
                    println("DEBUG: Task name: ${selectedProject.projectTasks[taskIndex].taskName}")

                    val taskName = selectedProject.projectTasks[taskIndex].taskName

                    Toast.makeText(this,
                        "Tarea '$taskName' actualizada: ${oldStatus ?: "desconocido"} → $newStatus",
                        Toast.LENGTH_SHORT).show()

                    saveProjectChanges()

                    loadAllTasks()
                } else {
                    println("DEBUG: Invalid task index or new status")
                    println("DEBUG: taskIndex: $taskIndex, newStatus: $newStatus")
                }
            } else {
                println("DEBUG: Result code not RESULT_STATUS_CHANGED: $resultCode")

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
            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabTareas.setTypeface(null, android.graphics.Typeface.BOLD)
            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabResumenes.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabResumenes.setTypeface(null, android.graphics.Typeface.BOLD)
            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabTareas.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun saveProjectChanges() {

        dataManager.saveProjectChanges(selectedProject)

        val sharedPref = getSharedPreferences("project_data", MODE_PRIVATE)
        val json = sharedPref.getString("saved_projects", "{}")
        println("✅✅✅ Datos guardados correctamente!")
        println("📊 Estado actual: $json")

        Toast.makeText(this, "Cambios guardados localmente", Toast.LENGTH_SHORT).show()
    }

    private fun addTaskToLayout(task: Task) {

        val (layoutRes, parentLayout) = when {
            task.taskStatus.equals("pendiente", ignoreCase = true) ||
                    task.taskStatus.equals("pendientes", ignoreCase = true) ||
                    task.taskStatus.equals("sin empezar", ignoreCase = true) -> {
                R.layout.to_do_tasks to pendientesLayout
            }
            task.taskStatus.equals("en progreso", ignoreCase = true) ||
                    task.taskStatus.equals("en proceso", ignoreCase = true) -> {
                R.layout.in_progress_taks to progresoLayout
            }
            task.taskStatus.equals("hecha", ignoreCase = true) ||
                    task.taskStatus.equals("hechas", ignoreCase = true) ||
                    task.taskStatus.equals("hecho", ignoreCase = true) ||
                    task.taskStatus.equals("completada", ignoreCase = true) -> {
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
                task.taskStatus.equals("hecha", ignoreCase = true) ||
                        task.taskStatus.equals("hechas", ignoreCase = true) ||
                        task.taskStatus.equals("hecho", ignoreCase = true) ||
                        task.taskStatus.equals("completada", ignoreCase = true) -> {
                    btnEmpezar.visibility = View.GONE
                    if (btnReiniciar != null) {
                        btnReiniciar.visibility = View.VISIBLE
                        btnReiniciar.text = "Reiniciar"
                        btnReiniciar.setOnClickListener {

                            task.taskStatus = "Pendiente"
                            saveProjectChanges()
                            loadAllTasks()
                        }
                    }
                }
                task.taskStatus.equals("en progreso", ignoreCase = true) ||
                        task.taskStatus.equals("en proceso", ignoreCase = true) -> {
                    btnEmpezar.text = "Terminar"
                    btnEmpezar.visibility = View.VISIBLE
                    if (btnReiniciar != null) {
                        btnReiniciar.visibility = View.VISIBLE
                        btnReiniciar.text = "Volver a Pendiente"
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
                    btnEmpezar.text = "Empezar"
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
}
//