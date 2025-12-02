package com.example.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class TarearDetailActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarear_detail)

        txtTime = findViewById(R.id.txtTiempo)
        txtEnProceso = findViewById(R.id.txtEnProgreso)
        txtSinEmpezar = findViewById(R.id.txtSinEmpezar)
        txtHecho = findViewById(R.id.txtHecho)
        btnEmpezar = findViewById(R.id.btnEmpezar)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnTerminar = findViewById(R.id.btnTerminal)

        val txtName = findViewById<TextView>(R.id.txtTaskName)
        val txtDescription = findViewById<TextView>(R.id.txtTaskDescription)
        val txtUser = findViewById<TextView>(R.id.txtTaskUser)
        val txtDates = findViewById<TextView>(R.id.txtTaskDates)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val txtTime = findViewById<TextView>(R.id.txtTiempo)

        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        project = intent.getSerializableExtra("project") as? Project ?: run { finish(); return }
        taskIndex = intent.getIntExtra("taskIndex", -1)
        if (taskIndex < 0 || taskIndex >= project.projectTasks.size) {
            finish(); return
        }
        task = project.projectTasks[taskIndex]

        val hours = task.taskTime ?: 0  // horas Pasar desde json

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

        val hasTime = initialElapsedMillis > 0

        btnEmpezar.isEnabled = !hasTime
        btnEmpezar.setTextColor(
            ContextCompat.getColor(
                this,
                if (!hasTime) R.color.white else R.color.gris
            )
        )
        btnReanudar.isEnabled = hasTime
        btnReanudar.setTextColor(
            ContextCompat.getColor(
                this,
                if (hasTime) R.color.white else R.color.gris
            )
        )
        txtSinEmpezar.setTextColor(resources.getColor(R.color.Turquesa))
        txtEnProceso.setTextColor(resources.getColor(R.color.oscuro))
        txtHecho.setTextColor(resources.getColor(R.color.oscuro))

        btnEmpezar.setOnClickListener {
            updateUIStart()
            btnEmpezar.isEnabled = false
            btnEmpezar.setTextColor(ContextCompat.getColor(this, R.color.gris))

        }
        btnReanudar.setOnClickListener {
            updateUIStart()
            btnReanudar.isEnabled = false
            btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.gris))
        }

        btnTerminar.setOnClickListener {

            txtEnProceso.setTextColor(resources.getColor(R.color.oscuro))
            txtSinEmpezar.setTextColor(resources.getColor(R.color.oscuro))
            txtHecho.setTextColor(resources.getColor(R.color.Turquesa))
            txtTime.text = "La tarea está terminada!"
            btnTerminar.isEnabled = false
            btnEmpezar.isEnabled = false
            btnReanudar.isEnabled = true
            btnTerminar.setTextColor(ContextCompat.getColor(this, R.color.gris))
            btnReanudar.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }
    private fun updateUIStart() {
        txtTime.text = "En proceso..."
        txtEnProceso.setTextColor(resources.getColor(R.color.Turquesa))
        txtSinEmpezar.setTextColor(resources.getColor(R.color.oscuro))
        txtHecho.setTextColor(resources.getColor(R.color.oscuro))
        btnTerminar.isEnabled = true
        btnTerminar.setTextColor(ContextCompat.getColor(this, R.color.white))
    }








}
