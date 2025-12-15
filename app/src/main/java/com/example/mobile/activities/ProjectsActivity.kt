package com.example.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.User
import com.example.mobile.helpers.UIAnimations

class ProjectsActivity : BaseActivity() {

    private lateinit var tabRecent: TextView
    private lateinit var tabAll: TextView
    private lateinit var indicator: View
    private lateinit var container: LinearLayout

    private var selectedTab = 1

    private lateinit var projects: List<Project>
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_projects)
        UIAnimations(this).startFloatingCircles()
        initData()
        initViews()
        initListeners()
        initAnimations()
        initTabs()
    }

    // -------------------------------
    // Inicialización
    // -------------------------------
    private fun initData() {
        projects = intent.getSerializableExtra("projects") as? ArrayList<Project> ?: emptyList()
        user = intent.getSerializableExtra("user") as User
    }

    private fun initViews() {
        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tabRecent = findViewById(R.id.tabRecent)
        tabAll = findViewById(R.id.tabAll)
        indicator = findViewById(R.id.indicator)
        container = findViewById(R.id.container)

        val IBinfoperson: ImageButton = findViewById(R.id.IBinfoperson)
        IBinfoperson.setOnClickListener { openPersonalInfo() }
    }

    private fun initListeners() {
        tabRecent.setOnClickListener {
            changeTab(1)
            filterProjects("recent")
        }

        tabAll.setOnClickListener {
            changeTab(2)
            filterProjects("all")
        }
    }

    private fun initAnimations() {
        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)
        listOf(R.id.circle1, R.id.circle2, R.id.circle3).forEach {
            findViewById<View>(it).startAnimation(floatAnimation)
        }
    }

    private fun initTabs() {
        tabRecent.post {
            moveIndicator()
            changeColors()
        }
        filterProjects("all")
    }

    // -------------------------------
    // Navegación
    // -------------------------------
    private fun openPersonalInfo() {
        val intent = Intent(this, PersonalInfoActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("projects", ArrayList(projects))
        startActivity(intent)
    }

    // -------------------------------
    // Filtrado y renderizado
    // -------------------------------
    private fun filterProjects(filter: String) {
        val userProjects = projects.filter { project ->
            project.projectMembers.any { it.userName == user.userName } ||
                    project.projectTasks.any { it.assignedUser?.userName == user.userName }
        }

        val filteredProjects = if (filter == "recent") {
            userProjects.takeLast(5)
        } else {
            userProjects
        }

        showProjects(filteredProjects)
    }

    private fun showProjects(projectList: List<Project>) {
        container.removeAllViews()

        if (projectList.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "No hay proyectos disponibles"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@ProjectsActivity, android.R.color.darker_gray))
                gravity = Gravity.CENTER
                setPadding(0, 100, 0, 0)
            }
            container.addView(emptyView)
            return
        }

        for (project in projectList) {
            val projectView = createProjectView(project)
            container.addView(projectView)
        }
    }

    private fun createProjectView(project: Project): View {
        val inflater = LayoutInflater.from(this)
        val projectView = inflater.inflate(R.layout.project_item, container, false)

        val tvTitle: TextView = projectView.findViewById(R.id.tvProjectTitle)
        val tvMembers: TextView = projectView.findViewById(R.id.tvProjectMembers)
        val tvTasks: TextView = projectView.findViewById(R.id.tvProjectTasks)

        tvTitle.text = project.title

        val membersText = if (project.projectMembers.isNotEmpty()) {
            "Miembros: " + project.projectMembers.joinToString { it.firstName }
        } else {
            "Miembros: No hay miembros"
        }
        tvMembers.text = membersText

        val tasksText = if (project.projectTasks.isNotEmpty()) {
            "Tareas: ${project.projectTasks.size} tarea(s)"
        } else {
            "Tareas: No hay tareas"
        }
        tvTasks.text = tasksText

        projectView.setOnClickListener {
            val intent = Intent(this@ProjectsActivity, ProjectDetailActivity::class.java)
            intent.putExtra("selected_project", project)
            startActivity(intent)
        }

        return projectView
    }

    // -------------------------------
    // Tabs UI
    // -------------------------------
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
        return if (selectedTab == 1) tabRecent else tabAll
    }
}