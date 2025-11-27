package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
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

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        setupContainer()
        setupClicks()
        startAnimations()
        setupFirstTab()

        val IBinfoperson: ImageButton = findViewById(R.id.IBinfoperson)
        IBinfoperson.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@ProjectsActivity, PersonalInfoActivity::class.java)
                intent.putExtra("users", ArrayList(users))
                startActivity(intent)
            }
        })
    }

    private fun findViews() {
        tabRecent = findViewById(R.id.tabRecent)
        tabAll = findViewById(R.id.tabAll)
        indicator = findViewById(R.id.indicator)
        container = findViewById(R.id.container)
    }

    private fun setupContainer() {
        showProjects(projects)
    }

    private fun showProjects(projectList: List<Project>) {
        container.removeAllViews()

        if (projectList.isEmpty()) {
            val emptyView = TextView(this)
            emptyView.text = "No hay proyectos disponibles"
            emptyView.textSize = 16f
            emptyView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            emptyView.gravity = android.view.Gravity.CENTER
            emptyView.setPadding(0, 100, 0, 0)
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

        val membersText: String
        if (project.projectMembers.isNotEmpty()) {
            membersText = "Miembros: " + project.projectMembers.joinToString { it.firstName ?: "Sin nombre" }
        } else {
            membersText = "Miembros: No hay miembros"
        }
        tvMembers.text = membersText

        val tasksText: String
        if (project.projectTasks.isNotEmpty()) {
            tasksText = "Tareas: " + project.projectTasks.size + " tarea(s)"
        } else {
            tasksText = "Tareas: No hay tareas"
        }
        tvTasks.text = tasksText

        projectView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@ProjectsActivity, ProjectDetailActivity::class.java)
                intent.putExtra("selected_project", project)
                intent.putExtra("users", ArrayList(users))
                startActivity(intent)
            }
        })

        return projectView
    }

    private fun setupClicks() {
        tabRecent.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                changeTab(1)
                filterProjects("recent")
            }
        })

        tabAll.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                changeTab(2)
                filterProjects("all")
            }
        })
    }

    private fun filterProjects(filter: String) {
        val filteredProjects: List<Project>
        if (filter == "recent") {
            filteredProjects = projects.takeLast(5)
        } else {
            filteredProjects = projects
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
        tabRecent.post(object : Runnable {
            override fun run() {
                moveIndicator()
                changeColors()
            }
        })
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
        return if (selectedTab == 1) {
            tabRecent
        } else if (selectedTab == 2) {
            tabAll
        } else {
            tabRecent
        }
    }
}