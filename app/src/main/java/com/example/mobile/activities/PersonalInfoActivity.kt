package com.example.mobile.activities

import android.app.AlertDialog
import android.content.Intent
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
    private lateinit var ivLanguage: ImageView

    private lateinit var user: User
    private lateinit var projects: List<Project>
    private var userTasks: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_info)
        applyFont(findViewById(android.R.id.content))


        setupInsets()
        UIAnimations(this).startFloatingCircles()

        loadView()
        loadSavedIcon()
        loadIntentData()

        filterProjects()
        userTasks = filterTasks().toMutableList()

        updateTaskStats()
        updateUserInfo()
        setupBackButton()
        setupLanguageSelector()
    }

    // ---------------------------------------------------------
    //  Configuración de insets
    // ---------------------------------------------------------
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // ---------------------------------------------------------
    //  Cargar vistas
    // ---------------------------------------------------------
    private fun loadView() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvFullName = findViewById(R.id.tvFullName)
        tvTasksDone = findViewById(R.id.tvTasksDone)
        tvScore = findViewById(R.id.tvScore)
        tvTasksPending = findViewById(R.id.tvTasksPending)
        ivLanguage = findViewById(R.id.ivLanguage)
    }

    // ---------------------------------------------------------
    //  Cargar datos del intent
    // ---------------------------------------------------------
    private fun loadIntentData() {
        user = intent.getSerializableExtra("user") as User
        projects = intent.getSerializableExtra("projects") as? ArrayList<Project> ?: emptyList()
    }

    // ---------------------------------------------------------
    //  Actualizar estadísticas de tareas
    // ---------------------------------------------------------
    private fun updateTaskStats() {
        val tasksDone = filterTasksDone(userTasks)
        val tasksUndone = filterTasksUndone(userTasks)

        tvTasksDone.text = tasksDone.toString()
        tvTasksPending.text = tasksUndone.toString()

        val score = calcularPuntuacion(tasksDone, tasksUndone)
        tvScore.text = score.toString()
    }

    // ---------------------------------------------------------
    //  Actualizar información del usuario
    // ---------------------------------------------------------
    private fun updateUserInfo() {
        val greeting = getString(R.string.greeting).replace("%s", user.firstName)
        tvGreeting.text = greeting

        tvFullName.text = "${user.firstName} ${user.lastName1} ${user.lastName2 ?: ""}"
    }

    // ---------------------------------------------------------
    //  Botón atrás
    // ---------------------------------------------------------
    private fun setupBackButton() {
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    // ---------------------------------------------------------
    //  Selector de idioma
    // ---------------------------------------------------------
    private fun setupLanguageSelector() {
        ivLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showLanguageDialog() {
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

                changeIcon(newLang)
                LanguageHelper.saveLanguagePref(this, newLang)
                restartApp()
                ivLanguage.setImageResource(icons[which])
            }
            .show()
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // ---------------------------------------------------------
    //  Lógica de tareas
    // ---------------------------------------------------------
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

    private fun filterTasksDone(userTasks: List<Task>): Int =
        userTasks.count { it.taskStatus.equals("Completada", ignoreCase = true) }

    private fun filterTasksUndone(userTasks: List<Task>): Int =
        userTasks.count { it.taskStatus.equals("Pendiente", ignoreCase = true) }

    private fun calcularPuntuacion(done: Int, undone: Int): Int =
        (done * 10) - (undone * 5)

    // ---------------------------------------------------------
    //  Icono de idioma
    // ---------------------------------------------------------
    private fun changeIcon(lang: String) {
        when (lang) {
            "es" -> ivLanguage.setImageResource(R.drawable.bandera_es)
            "cat" -> ivLanguage.setImageResource(R.drawable.bandera_cat)
            "en" -> ivLanguage.setImageResource(R.drawable.bandera_eua)
        }
        saveIconPref(lang)
    }

    private fun saveIconPref(lang: String) {
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        prefs.edit().putString("icon_lang", lang).apply()
    }

    private fun loadSavedIcon() {
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val lang = prefs.getString("icon_lang", "es") ?: "es"
        changeIcon(lang)
    }
}