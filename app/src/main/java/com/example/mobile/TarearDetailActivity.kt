package com.example.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TarearDetailActivity : AppCompatActivity() {

    private var startTime: Long = 0L
    private var isTiming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarear_detail)

        val txtName = findViewById<TextView>(R.id.txtTaskName)
        val txtDescription = findViewById<TextView>(R.id.txtTaskDescription)
        val txtTime = findViewById<TextView>(R.id.txtTiempo)
        val txtUser = findViewById<TextView>(R.id.txtTaskUser)
        val txtDates = findViewById<TextView>(R.id.txtTaskDates)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnEmpezar = findViewById<Button>(R.id.btnEmpezar)
        val btnTerminar = findViewById<Button>(R.id.btnTerminal)

        val txtSinEmpezar = findViewById<TextView>(R.id.txtSinEmpezar)
        val txtEnProceso = findViewById<TextView>(R.id.txtEnProceso)
        val txtHecho = findViewById<TextView>(R.id.txtHecho)

        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val task = intent.getSerializableExtra("task") as? Task
        if (task == null) {
            txtName.text = "Error: no se encontró la tarea"
            return
        }

        // Mostrar datos del Task
        txtName.text = task.taskName
        txtDescription.text = task.taskDescription

        // Usuario asignado
        val assignedUserName =
            task.assignedUser?.let { "${it.firstName} ${it.lastName1 ?: ""}" } ?: "Sin usuario"
        txtUser.text = "Asignado a: $assignedUserName"

        // Fechas
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = dateFormat.format(task.taskStartDate)
        val endDate = dateFormat.format(task.taskEndDate)

        txtDates.text = "Desde $startDate hasta $endDate"

        // Estado inicial → Sin empezar
        txtSinEmpezar.setTextColor(resources.getColor(R.color.Turquesa))
        txtEnProceso.setTextColor(resources.getColor(R.color.oscuro))
        txtHecho.setTextColor(resources.getColor(R.color.oscuro))

        // ▶️ Botón Empezar → cambia estado
        btnEmpezar.setOnClickListener {
            startTime = System.currentTimeMillis()
            isTiming = true

            txtTime.text = "En proceso..."
            txtEnProceso.setTextColor(resources.getColor(R.color.Turquesa))
            txtSinEmpezar.setTextColor(resources.getColor(R.color.oscuro))
            txtHecho.setTextColor(resources.getColor(R.color.oscuro))
        }

        // ✔️ Botón Terminar → calcula tiempo
        btnTerminar.setOnClickListener {
            if (!isTiming) return@setOnClickListener

            val endTime = System.currentTimeMillis()
            val elapsed = endTime - startTime

            val minutes = (elapsed / 1000 / 60).toInt()
            val seconds = (elapsed / 1000 % 60).toInt()

            val timeString = String.format("%d:%02d", minutes, seconds)
            txtTime.text = "Terminado en: $timeString"

            txtHecho.setTextColor(resources.getColor(R.color.Turquesa))
            txtSinEmpezar.setTextColor(resources.getColor(R.color.oscuro))
            txtEnProceso.setTextColor(resources.getColor(R.color.oscuro))
        }
    }
}
