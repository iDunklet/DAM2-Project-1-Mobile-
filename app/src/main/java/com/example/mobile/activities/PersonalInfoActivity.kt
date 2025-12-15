package com.example.mobile.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.Task
import com.example.mobile.classes.User
import com.example.mobile.helpers.LanguageHelper
import com.example.mobile.helpers.UIAnimations

class PersonalInfoActivity : BaseActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvFullName: TextView
    private lateinit var tvTasksDone: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvTasksPending: TextView

    private lateinit var user: User
    private lateinit var projects: List<Project>
    private var userTasks: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_info)
        UIAnimations(this).startFloatingCircles()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadView()
        user = intent.getSerializableExtra("user") as User
        projects = intent.getSerializableExtra("projects") as? ArrayList<Project> ?: emptyList()

        filterProjects()
        userTasks = filterTasks().toMutableList()

        val tasksDone = filterTasksDone(userTasks)
        val tasksUndone = filterTasksUndone(userTasks)

        tvTasksDone.text = tasksDone.toString()
        tvTasksPending.text = tasksUndone.toString()

        val score = calcularPuntuacion(tasksDone, tasksUndone)
        tvScore.text = score.toString()

        val greeting = getString(R.string.greeting).replace("%s", user.firstName)
        tvGreeting.text = greeting

        tvFullName.text = "${user.firstName} ${user.lastName1} ${user.lastName2 ?: ""}"

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val ivLanguage = findViewById<ImageView>(R.id.ivLanguage)
        ivLanguage.setOnClickListener {
            val idiomas = arrayOf("Español", "Català", "English")
            val icons = intArrayOf(
                R.drawable.bandera_es,
                R.drawable.bandera_cat,
                R.drawable.bandera_eua
            )

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_language))
                .setItems(idiomas) { _, which ->
                    val newLang = when (which) {
                        0 -> "es"
                        1 -> "cat"
                        2 -> "en"
                        else -> "es"
                    }
                    // 🔹 Solo guardamos idioma, BaseActivity lo aplicará al recrear
                    LanguageHelper.saveLanguagePref(this, newLang)

                    ivLanguage.setImageResource(icons[which])

                    recreate()
                }
                .show()
        }
    }

    private fun calcularPuntuacion(done: Int, undone: Int): Int {
        return (done * 10) - (undone * 5)
    }

    private fun filterTasksUndone(userTasks: List<Task>): Int {
        return userTasks.count { it.taskStatus.equals("Pendiente", ignoreCase = true) }
    }

    private fun filterTasksDone(userTasks: List<Task>): Int {
        return userTasks.count { it.taskStatus.equals("Completada", ignoreCase = true) }
    }

    private fun filterTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        for (project in projects) {
            for (task in project.projectTasks) {
                if (task.assignedUser?.userName == user.userName) {
                    tasks.add(task)
                }
            }
        }
        return tasks
    }

    private fun filterProjects() {
        projects = projects.filter { project ->
            project.projectMembers.any { it.userName == user.userName }
        }
    }

    private fun loadView() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvFullName = findViewById(R.id.tvFullName)
        tvTasksDone = findViewById(R.id.tvTasksDone)
        tvScore = findViewById(R.id.tvScore)
        tvTasksPending = findViewById(R.id.tvTasksPending)
    }
}