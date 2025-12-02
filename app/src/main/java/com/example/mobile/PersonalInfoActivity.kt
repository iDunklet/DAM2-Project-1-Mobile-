package com.example.mobile

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView

    private lateinit var tvFullName: TextView

    private lateinit var tvTasksDone: TextView

    private lateinit var tvScore: TextView

    private lateinit var tvTasksPending: TextView

    private lateinit var user: User

    private lateinit var projects: List<Project>

    private lateinit var userTasks: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadView()
        user = intent.getSerializableExtra("user") as User
        projects = intent.getSerializableExtra("projects") as? ArrayList<Project> ?: emptyList()

        filterProjects()

        var userTasks = filterTasks()
        var tasksDone = filterTasksDone(userTasks)
        var tasksUndone = filterTasksUndone(userTasks)

        tvTasksDone.text = tasksDone.toString()
        tvTasksPending.text = tasksUndone.toString()
        var score = calcularPuntuacion(tasksDone, tasksUndone)

        tvScore.text = score.toString()
        var greeting: String

        greeting = "¡Bienvenido" + user.firstName + "!"
        tvGreeting.text = greeting



    }

    private fun calcularPuntuacion(done: Int, undone: Int): Int {
        return (done * 10) - (undone * 5)
    }

    private fun filterTasksUndone(userTasks: List<Task>): Int {
        var i = 0
        for (task in userTasks) {
            if (task.taskStatus.contains("Pendiente")) {
                i++
            }
        }
        return i
    }

    private fun filterTasksDone(userTasks: List<Task>): Int {
        var i = 0
        for (task in userTasks) {
            if (task.taskStatus.contains("Completada")) {
                i++
            }
        }
        return i
    }


    private fun filterTasks(): List<Task> {
        for (project in projects) {
            for (task in project.projectTasks) {
                if (task.assignedUser == user) {
                    userTasks.add(task)
                }
            }
        }
        return userTasks
    }

    private fun filterProjects() {
        projects = projects.filter { it.projectMembers.contains(user) }
    }

    private fun loadView() {
        findViewById<TextView>(R.id.tvGreeting)
        findViewById<TextView>(R.id.tvFullName)
        findViewById<TextView>(R.id.tvTasksDone)
        findViewById<TextView>(R.id.tvScore)
        findViewById<TextView>(R.id.tvTasksPending)
    }
}