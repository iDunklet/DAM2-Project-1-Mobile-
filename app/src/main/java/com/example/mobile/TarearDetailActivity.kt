package com.example.mobile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TarearDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarear_detail)

        val txtName = findViewById<TextView>(R.id.txtTaskName)
        val txtDescription = findViewById<TextView>(R.id.txtTaskDescription)
        val txtUser = findViewById<TextView>(R.id.txtTaskUser)
        val txtDates = findViewById<TextView>(R.id.txtTaskDates)
        val btnBack = findViewById<ImageView>(R.id.btnBack)


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
    }
}
