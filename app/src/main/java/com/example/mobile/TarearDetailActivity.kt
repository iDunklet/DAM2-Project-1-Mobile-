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





        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val taskName = intent.getStringExtra("taskName")
        val taskDescription = intent.getStringExtra("taskDescription")
        val taskUser = intent.getStringExtra("taskUser")
        val startDateMillis = intent.getLongExtra("taskStartDate", 0L)
        val endDateMillis = intent.getLongExtra("taskEndDate", 0L)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = dateFormat.format(Date(startDateMillis))
        val endDate = dateFormat.format(Date(endDateMillis))

        txtName.text = taskName
        txtDescription.text = taskDescription
        txtUser.text = "Asignado a: $taskUser"
        txtDates.text = "Desde $startDate hasta $endDate"

        btnEmpezar.setOnClickListener {
            startTime = System.currentTimeMillis()
            isTiming = true
            txtTime.text = "Empezado"
            txtSinEmpezar.setTextColor(resources.getColor(R.color.Turquesa))

        }

        btnTerminar.setOnClickListener {
            val endTime = System.currentTimeMillis()
            val elapsed = endTime - startTime
            val totalSeconds = (elapsed / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            val timeString = String.format("%d:%02d", minutes, seconds)
            txtTime.text = "Terminado: $timeString"
        }


        }
    }

