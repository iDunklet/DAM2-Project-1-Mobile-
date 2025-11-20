package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProjectsActivity : AppCompatActivity() {

    private lateinit var tabRecent: TextView
    private lateinit var tabAll: TextView
    private lateinit var indicator: View
    private lateinit var container: LinearLayout
    private var selectedTab = 1

    private lateinit var projects: List<Project>
    private lateinit var users: List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_projects)

        // Obtener datos del intent
        projects = intent.getSerializableExtra("projects") as? ArrayList<Project> ?: emptyList()
        users = intent.getSerializableExtra("users") as? ArrayList<User> ?: emptyList()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        setupContainer()
        setupClicks()
        startAnimations()
        setupFirstTab()
    }

    private fun findViews() {
        tabRecent = findViewById(R.id.tabRecent)
        tabAll = findViewById(R.id.tabAll)
        indicator = findViewById(R.id.indicator)
        container = findViewById(R.id.container)
    }

    private fun setupContainer() {
        // Limpiar contenedor y agregar todos los proyectos
        showProjects(projects)
    }

    private fun showProjects(projectList: List<Project>) {
        // Limpiar contenedor
        container.removeAllViews()

        if (projectList.isEmpty()) {
            // Mostrar mensaje si no hay proyectos
            val emptyView = TextView(this).apply {
                text = "No hay proyectos disponibles"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                gravity = android.view.Gravity.CENTER
                setPadding(0, 100, 0, 0)
            }
            container.addView(emptyView)
            return
        }

        // Crear y agregar vistas para cada proyecto
        projectList.forEach { project ->
            val projectView = createProjectView(project)
            container.addView(projectView)
        }
    }

    private fun createProjectView(project: Project): View {
        // Inflar el layout del item
        val inflater = LayoutInflater.from(this)
        val projectView = inflater.inflate(R.layout.project_item, container, false)

        // Configurar los datos
        val tvTitle: TextView = projectView.findViewById(R.id.tvProjectTitle)
        val tvMembers: TextView = projectView.findViewById(R.id.tvProjectMembers)
        val tvTasks: TextView = projectView.findViewById(R.id.tvProjectTasks)

        tvTitle.text = project.title

        // Miembros
        val membersText = if (project.projectMembers.isNotEmpty()) {
            "Miembros: ${project.projectMembers.joinToString { it.firstName ?: "Sin nombre" }}"
        } else {
            "Miembros: No hay miembros"
        }
        tvMembers.text = membersText

        // Tareas
        val tasksText = if (project.projectTasks.isNotEmpty()) {
            "Tareas: ${project.projectTasks.size} tarea(s)"
        } else {
            "Tareas: No hay tareas"
        }
        tvTasks.text = tasksText

        // Click listener para cada proyecto - ABRE PROJECT DETAIL
        projectView.setOnClickListener {
            val intent = Intent(this, ProjectDetailActivity::class.java)
            intent.putExtra("selected_project", project)
            intent.putExtra("users", ArrayList(users))
            startActivity(intent)
        }

        return projectView
    }

    private fun setupClicks() {
        tabRecent.setOnClickListener {
            changeTab(1)
            filterProjects("recent")
        }

        tabAll.setOnClickListener {
            changeTab(2) // Changed from 3 to 2
            filterProjects("all")
        }
    }

    private fun filterProjects(filter: String) {
        val filteredProjects = when (filter) {
            "recent" -> projects.takeLast(5)
            // Removed "favorites" filter since we removed the favorites tab
            else -> projects
        }
        showProjects(filteredProjects)
    }

    private fun startAnimations() {
        val circle1 = findViewById<View>(R.id.circle1)
        val circle2 = findViewById<View>(R.id.circle2)
        val circle3 = findViewById<View>(R.id.circle3)

        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)

        circle1.startAnimation(floatAnimation)
        circle2.startAnimation(floatAnimation)
        circle3.startAnimation(floatAnimation)
    }

    private fun setupFirstTab() {
        tabRecent.post {
            moveIndicator()
            changeColors()
        }
        filterProjects("all")
    }

    private fun changeTab(tab: Int) {
        selectedTab = tab
        moveIndicator()
        changeColors()
    }

    private fun moveIndicator() {
        val selectedView = getSelectedView()

        indicator.animate()
            .translationX(selectedView.x)
            .setDuration(300)
            .start()

        val params = indicator.layoutParams
        params.width = selectedView.width
        indicator.layoutParams = params
    }

    private fun changeColors() {
        tabRecent.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        tabAll.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        val selected = getSelectedView()
        selected.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
    }

    private fun getSelectedView(): TextView {
        return when(selectedTab) {
            1 -> tabRecent
            2 -> tabAll // Changed from 3 to 2
            else -> tabRecent
        }
    }
}