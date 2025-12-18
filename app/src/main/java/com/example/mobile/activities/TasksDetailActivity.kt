package com.example.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.Task
import com.example.mobile.helpers.UIAnimations
import java.text.SimpleDateFormat
import java.util.Locale

class TasksDetailActivity : BaseActivity() {

    private lateinit var txtTime: TextView
    private lateinit var txtEnProceso: TextView
    private lateinit var txtSinEmpezar: TextView
    private lateinit var txtHecho: TextView
    private lateinit var txtName: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtUser: TextView
    private lateinit var txtDates: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnEmpezar: Button
    private lateinit var btnReanudar: Button
    private lateinit var btnTerminar: Button
    private lateinit var btnReiniciar: Button
    private lateinit var project: Project
    private lateinit var task: Task
    private var taskIndex: Int = -1
    private var originalStatus: String = ""
    private var statusChanged = false
    private var initialElapsedMillis: Long = 0L

    companion object {
        const val RESULT_STATUS_CHANGED = 1001
        const val EXTRA_TASK_INDEX = "task_index"
        const val EXTRA_OLD_STATUS = "old_status"
        const val EXTRA_NEW_STATUS = "new_status"

        private const val STATUS_PENDIENTE = "Pendiente"
        private const val STATUS_EN_PROGRESO = "En progreso"
        private const val STATUS_HECHA = "Hecha"
        private const val STATUS_COMPLETADA = "Completada"
        private const val STATUS_SIN_EMPEZAR = "Sin empezar"
        private const val STATUS_EN_PROCESO = "En proceso"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_detail)
        applyFont(findViewById(android.R.id.content))


        initializeActivity()
        setupBackButton()
        loadTaskData()
        setupTaskDetails()
        initializeUIByStatus()
        setupButtonListeners()
    }

    // ---------------------------------------------------------
    //  Inicialización de actividad
    // ---------------------------------------------------------
    private fun initializeActivity() {
        loadAllViews()
        setupAnimations()
    }

    private fun loadAllViews() {
        txtTime = findViewById(R.id.txtTiempo)
        txtEnProceso = findViewById(R.id.txtEnProgreso)
        txtSinEmpezar = findViewById(R.id.txtSinEmpezar)
        txtHecho = findViewById(R.id.txtHecho)
        btnEmpezar = findViewById(R.id.btnEmpezar)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnTerminar = findViewById(R.id.btnTerminal)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        btnBack = findViewById(R.id.btnBack)
        txtName = findViewById(R.id.txtTaskName)
        txtDescription = findViewById(R.id.txtTaskDescription)
        txtUser = findViewById(R.id.txtTaskUser)
        txtDates = findViewById(R.id.txtTaskDates)
    }

    private fun setupAnimations() {
        UIAnimations(this).startFloatingCircles()
    }

    // ---------------------------------------------------------
    //  Configuración del botón de retroceso
    // ---------------------------------------------------------
    private fun setupBackButton() {
        btnBack.setOnClickListener {
            handleBackButtonClick()
        }
    }

    private fun handleBackButtonClick() {
        logBackButtonDebugInfo()

        val resultIntent = if (statusChanged) {
            createStatusChangedResult()
        } else {
            createCanceledResult()
        }

        setResult(if (statusChanged) RESULT_STATUS_CHANGED else RESULT_CANCELED, resultIntent)
        finish()
    }

    private fun logBackButtonDebugInfo() {
        println("DEBUG: Back button clicked")
        println("DEBUG: Status changed: $statusChanged")
        println("DEBUG: Original status: $originalStatus")
        println("DEBUG: Current status: ${task.taskStatus}")
    }

    private fun createStatusChangedResult(): Intent {
        return Intent().apply {
            putExtra(EXTRA_TASK_INDEX, taskIndex)
            putExtra(EXTRA_OLD_STATUS, originalStatus)
            putExtra(EXTRA_NEW_STATUS, task.taskStatus)
        }
    }

    private fun createCanceledResult(): Intent? {
        return null
    }

    // ---------------------------------------------------------
    //  Carga de datos de tarea
    // ---------------------------------------------------------
    private fun loadTaskData() {
        loadProjectFromIntent()
        validateTaskIndex()
        loadTaskFromProject()
        saveOriginalStatus()
        logTaskDebugInfo()
    }

    private fun loadProjectFromIntent() {
        project = intent.getSerializableExtra("project") as? Project ?: run {
            finish()
            return
        }
    }

    private fun validateTaskIndex() {
        taskIndex = intent.getIntExtra("taskIndex", -1)
        if (taskIndex < 0 || taskIndex >= project.projectTasks.size) {
            finish()
            return
        }
    }

    private fun loadTaskFromProject() {
        task = project.projectTasks[taskIndex]
    }

    private fun saveOriginalStatus() {
        originalStatus = task.taskStatus
    }

    private fun logTaskDebugInfo() {
        println("DEBUG: Task status from project: ${task.taskStatus}")
        println("DEBUG: Original status saved: $originalStatus")
        println("DEBUG: Task name: ${task.taskName}")
    }

    // ---------------------------------------------------------
    //  Configuración de detalles de tarea
    // ---------------------------------------------------------
    private fun setupTaskDetails() {
        displayTaskTime()
        displayTaskBasicInfo()
        displayTaskAssignedUser()
        displayTaskDates()
    }

    private fun displayTaskTime() {
        val hours = task.taskTime ?: 0
        txtTime.text = "Tiempo Actual: $hours:00:00 h"
    }

    private fun displayTaskBasicInfo() {
        txtName.text = task.taskName
        txtDescription.text = task.taskDescription ?: ""
    }

    private fun displayTaskAssignedUser() {
        val assignedUserName = task.assignedUser?.let { user ->
            "${user.firstName} ${user.lastName1 ?: ""}"
        } ?: "Sin usuario"

        txtUser.text = "Asignado a: $assignedUserName"
    }

    private fun displayTaskDates() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = task.taskStartDate?.let { dateFormat.format(it) } ?: "Sin fecha"
        val endDate = task.taskEndDate?.let { dateFormat.format(it) } ?: "Sin fecha"
        txtDates.text = "Desde $startDate hasta $endDate"
    }

    // ---------------------------------------------------------
    //  Configuración de listeners de botones
    // ---------------------------------------------------------
    private fun setupButtonListeners() {
        setupEmpezarButton()
        setupReanudarButton()
        setupTerminarButton()
        setupReiniciarButton()
    }

    private fun setupEmpezarButton() {
        btnEmpezar.setOnClickListener {
            handleEmpezarButtonClick()
        }
    }

    private fun handleEmpezarButtonClick() {
        println("DEBUG: btnEmpezar clicked, current status: ${task.taskStatus}")

        updateTaskStatus(STATUS_EN_PROGRESO)
        updateUIForStatus(STATUS_EN_PROGRESO)
        disableButtonWithColor(btnEmpezar, R.color.gris)
    }

    private fun setupReanudarButton() {
        btnReanudar.setOnClickListener {
            handleReanudarButtonClick()
        }
    }

    private fun handleReanudarButtonClick() {
        if (isTaskCompleted(task.taskStatus)) {
            updateTaskStatus(STATUS_EN_PROGRESO)
        }

        updateUIForStatus(STATUS_EN_PROGRESO)
        disableButtonWithColor(btnReanudar, R.color.gris)
    }

    private fun setupTerminarButton() {
        btnTerminar.setOnClickListener {
            handleTerminarButtonClick()
        }
    }

    private fun handleTerminarButtonClick() {
        updateTaskStatus(STATUS_HECHA)
        updateUIForStatus(STATUS_HECHA)
    }

    private fun setupReiniciarButton() {
        btnReiniciar.setOnClickListener {
            handleReiniciarButtonClick()
        }
    }

    private fun handleReiniciarButtonClick() {
        updateTaskStatus(STATUS_PENDIENTE)
        updateUIForStatus(STATUS_PENDIENTE)
    }

    // ---------------------------------------------------------
    //  Gestión de estados de tarea
    // ---------------------------------------------------------
    private fun initializeUIByStatus() {
        updateUIForStatus(task.taskStatus)
    }

    private fun updateTaskStatus(newStatus: String) {
        task.taskStatus = newStatus
        statusChanged = true

        println("DEBUG: Status after update: ${task.taskStatus}")
        println("DEBUG: Task in project array: ${project.projectTasks[taskIndex].taskStatus}")
    }

    private fun isTaskPending(status: String): Boolean {
        return status.equals(STATUS_PENDIENTE, ignoreCase = true) ||
                status.equals("Pendientes", ignoreCase = true) ||
                status.equals(STATUS_SIN_EMPEZAR, ignoreCase = true)
    }

    private fun isTaskInProgress(status: String): Boolean {
        return status.equals(STATUS_EN_PROGRESO, ignoreCase = true) ||
                status.equals(STATUS_EN_PROCESO, ignoreCase = true)
    }

    private fun isTaskCompleted(status: String): Boolean {
        return status.equals(STATUS_HECHA, ignoreCase = true) ||
                status.equals("Hechas", ignoreCase = true) ||
                status.equals("Hecho", ignoreCase = true) ||
                status.equals(STATUS_COMPLETADA, ignoreCase = true)
    }

    // ---------------------------------------------------------
    //  Actualización de UI según estado
    // ---------------------------------------------------------
    private fun updateUIForStatus(status: String) {
        println("DEBUG: updateUIForStatus called with: $status")

        resetStatusTextColors()
        resetButtons()

        when {
            isTaskPending(status) -> setupUIPending()
            isTaskInProgress(status) -> setupUIInProgress()
            isTaskCompleted(status) -> setupUICompleted()
            else -> setupUIDefault()
        }
    }

    private fun resetStatusTextColors() {
        val darkColor = resources.getColor(R.color.oscuro)
        txtSinEmpezar.setTextColor(darkColor)
        txtEnProceso.setTextColor(darkColor)
        txtHecho.setTextColor(darkColor)
    }

    private fun resetButtons() {
        val buttons = listOf(btnEmpezar, btnReanudar, btnTerminar)
        buttons.forEach { button ->
            button.isEnabled = false
            button.setTextColor(ContextCompat.getColor(this, R.color.gris))
        }
    }

    private fun setupUIPending() {
        val turquesaColor = resources.getColor(R.color.Turquesa)
        txtSinEmpezar.setTextColor(turquesaColor)

        btnEmpezar.isEnabled = true
        btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.white))

        val hours = task.taskTime ?: 0
        txtTime.text = "Tiempo Actual: $hours:00:00 h"
    }

    private fun setupUIInProgress() {
        val turquesaColor = resources.getColor(R.color.Turquesa)
        txtEnProceso.setTextColor(turquesaColor)

        btnTerminar.isEnabled = true
        btnTerminar.setTextColor(ContextCompat.getColor(this, R.color.white))

        txtTime.text = "En proceso..."
    }

    private fun setupUICompleted() {
        val turquesaColor = resources.getColor(R.color.Turquesa)
        txtHecho.setTextColor(turquesaColor)

        btnReanudar.isEnabled = true
        btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.white))

        txtTime.text = "La tarea está terminada!"
    }

    private fun setupUIDefault() {
        val turquesaColor = resources.getColor(R.color.Turquesa)
        txtSinEmpezar.setTextColor(turquesaColor)

        btnEmpezar.isEnabled = true
        btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.white))

        val hours = task.taskTime ?: 0
        txtTime.text = "Tiempo Actual: $hours:00:00 h"
    }

    // ---------------------------------------------------------
    //  Utilidades
    // ---------------------------------------------------------
    private fun disableButtonWithColor(button: Button, colorResId: Int) {
        button.isEnabled = false
        button.setTextColor(ContextCompat.getColor(this, colorResId))
    }
}