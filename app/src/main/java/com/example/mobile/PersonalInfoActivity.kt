package com.example.mobile

import android.os.Bundle
import android.util.Log
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

    private var userTasks: MutableList<Task> = mutableListOf()

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
        userTasks = filterTasks().toMutableList()

        val tasksDone = filterTasksDone(userTasks)
        val tasksUndone = filterTasksUndone(userTasks)

        tvTasksDone.text = tasksDone.toString()
        tvTasksPending.text = tasksUndone.toString()

        val score = calcularPuntuacion(tasksDone, tasksUndone)
        tvScore.text = score.toString()

        val greeting = "¡Bienvenido ${user.firstName}!"
        tvGreeting.text = greeting

        tvFullName.text = "${user.firstName} ${user.lastName1} ${user.lastName2 ?: ""}"
        Log.d("PersonalInfoActivity", "Tareas del usuario: ${userTasks.size}")
        for (task in userTasks) {
            Log.d(
                "PersonalInfoActivity",
                "Tarea: ${task.taskName}, Estado: ${task.taskStatus}, Responsable: ${task.assignedUser?.userName}"
            )
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
        for (project in projects) {
            for (task in project.projectTasks) {
                if (task.assignedUser?.userName == user.userName) {
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
        tvGreeting = findViewById(R.id.tvGreeting)
        tvFullName = findViewById(R.id.tvFullName)
        tvTasksDone = findViewById(R.id.tvTasksDone)
        tvScore = findViewById(R.id.tvScore)
        tvTasksPending = findViewById(R.id.tvTasksPending)

    }
}