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
import java.text.SimpleDateFormat
import java.util.*

class TasksDetailActivity : BaseActivity() {

    private var initialElapsedMillis: Long = 0L
    private lateinit var project: Project
    private lateinit var task: Task
    private var taskIndex: Int = -1
    private lateinit var txtTime: TextView
    private lateinit var txtEnProceso: TextView
    private lateinit var txtSinEmpezar: TextView
    private lateinit var txtHecho: TextView
    private lateinit var btnEmpezar: Button
    private lateinit var btnReanudar: Button
    private lateinit var btnTerminar: Button

    private lateinit var btnReiniciar: Button
    private var originalStatus: String = ""
    private var statusChanged = false

    companion object {
        const val RESULT_STATUS_CHANGED = 1001
        const val EXTRA_TASK_INDEX = "task_index"
        const val EXTRA_OLD_STATUS = "old_status"
        const val EXTRA_NEW_STATUS = "new_status"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_detail)

        txtTime = findViewById(R.id.txtTiempo)
        txtEnProceso = findViewById(R.id.txtEnProgreso)
        txtSinEmpezar = findViewById(R.id.txtSinEmpezar)
        txtHecho = findViewById(R.id.txtHecho)
        btnEmpezar = findViewById(R.id.btnEmpezar)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnTerminar = findViewById(R.id.btnTerminal)
        btnReiniciar = findViewById(R.id.btnReiniciar)

        val txtName = findViewById<TextView>(R.id.txtTaskName)
        val txtDescription = findViewById<TextView>(R.id.txtTaskDescription)
        val txtUser = findViewById<TextView>(R.id.txtTaskUser)
        val txtDates = findViewById<TextView>(R.id.txtTaskDates)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {

            println("DEBUG: Back button clicked")
            println("DEBUG: Status changed: $statusChanged")
            println("DEBUG: Original status: $originalStatus")
            println("DEBUG: Current status: ${task.taskStatus}")

            if (statusChanged) {
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_TASK_INDEX, taskIndex)
                    putExtra(EXTRA_OLD_STATUS, originalStatus)
                    putExtra(EXTRA_NEW_STATUS, task.taskStatus)
                }
                setResult(RESULT_STATUS_CHANGED, resultIntent)
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
        }

        project = intent.getSerializableExtra("project") as? Project ?: run { finish(); return }
        taskIndex = intent.getIntExtra("taskIndex", -1)
        if (taskIndex < 0 || taskIndex >= project.projectTasks.size) {
            finish(); return
        }
        task = project.projectTasks[taskIndex]


        originalStatus = task.taskStatus

        println("DEBUG: Task status from project: ${task.taskStatus}")
        println("DEBUG: Original status saved: $originalStatus")
        println("DEBUG: Task name: ${task.taskName}")

        val hours = task.taskTime ?: 0
        txtTime.text = "Tiempo Actual: $hours:00:00 h"

        txtName.text = task.taskName
        txtDescription.text = task.taskDescription ?: ""
        val assignedUserName =
            task.assignedUser?.let { "${it.firstName} ${it.lastName1 ?: ""}" } ?: "Sin usuario"
        txtUser.text = "Asignado a: $assignedUserName"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = task.taskStartDate?.let { dateFormat.format(it) } ?: "Sin fecha"
        val endDate = task.taskEndDate?.let { dateFormat.format(it) } ?: "Sin fecha"
        txtDates.text = "Desde $startDate hasta $endDate"


        initializeUIByStatus()

        btnEmpezar.setOnClickListener {
            println("DEBUG: btnEmpezar clicked, current status: ${task.taskStatus}")

            task.taskStatus = "En progreso"
            statusChanged = true

            println("DEBUG: Status after update: ${task.taskStatus}")
            println("DEBUG: Task in project array: ${project.projectTasks[taskIndex].taskStatus}")

            updateUIForStatus("En progreso")
            btnEmpezar.isEnabled = false
            btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.gris))
        }

        btnReanudar.setOnClickListener {
            if (task.taskStatus.equals("Hecha", ignoreCase = true) ||
                task.taskStatus.equals("Completada", ignoreCase = true)
            )
            {
                task.taskStatus = "En progreso"
                statusChanged = true
                updateUIForStatus("En progreso")
            } else {

                updateUIForStatus("En progreso")
            }
            btnReanudar.isEnabled = false
            btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.gris))
        }

        btnTerminar.setOnClickListener {

            task.taskStatus = "Hecha"
            statusChanged = true
            updateUIForStatus("Hecha")
        }
        btnReiniciar.setOnClickListener {

            task.taskStatus = "Pendiente"
            statusChanged = true
            updateUIForStatus("Pendiente")
        }
    }


    private fun initializeUIByStatus() {
        updateUIForStatus(task.taskStatus)
    }

    private fun updateUIForStatus(status: String) {

        println("DEBUG: updateUIForStatus called with: $status")

        txtSinEmpezar.setTextColor(resources.getColor(R.color.oscuro))
        txtEnProceso.setTextColor(resources.getColor(R.color.oscuro))
        txtHecho.setTextColor(resources.getColor(R.color.oscuro))

        resetButtons()

        when {
            status.equals("Pendiente", ignoreCase = true) ||
                    status.equals("Pendientes", ignoreCase = true) ||
                    status.equals("Sin empezar", ignoreCase = true) -> {

                txtSinEmpezar.setTextColor(resources.getColor(R.color.Turquesa))
                btnEmpezar.isEnabled = true
                btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtTime.text = "Tiempo Actual: ${task.taskTime ?: 0}:00:00 h"
            }

            status.equals("En progreso", ignoreCase = true) ||
                    status.equals("En proceso", ignoreCase = true) -> {

                txtEnProceso.setTextColor(resources.getColor(R.color.Turquesa))
                btnReanudar.isEnabled = false
                btnTerminar.isEnabled = true
                btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.white))
                btnTerminar.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtTime.text = "En proceso..."
            }

            status.equals("Hecha", ignoreCase = true) ||
                    status.equals("Hechas", ignoreCase = true) ||
                    status.equals("Hecho", ignoreCase = true) ||
                    status.equals("Completada", ignoreCase = true) -> {

                txtHecho.setTextColor(resources.getColor(R.color.Turquesa))
                btnReanudar.isEnabled = true
                btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtTime.text = "La tarea está terminada!"
            }

            else -> {

                txtSinEmpezar.setTextColor(resources.getColor(R.color.Turquesa))
                btnEmpezar.isEnabled = true
                btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.white))
                txtTime.text = "Tiempo Actual: ${task.taskTime ?: 0}:00:00 h"
            }
        }
    }

    private fun resetButtons() {
        btnEmpezar.isEnabled = false
        btnReanudar.isEnabled = false
        btnTerminar.isEnabled = false


        btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.gris))
        btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.gris))
        btnTerminar.setTextColor(ContextCompat.getColor(this, R.color.gris))

    }
}
//
