package com.example.mobile

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Date

class ProjectDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_detail)


        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val pendientesLayout = findViewById<LinearLayout>(R.id.pendientesContainer)
        val hechasLayout = findViewById<LinearLayout>(R.id.hechasContainer)

        val user1 = User(
            firstName = "Juan",
            lastName1 = "Pérez",
            lastName2 = "Gómez",
            birthDate = Date(),
            className = "Clase A",
            email = "juan@mail.com",
            password = "1234",
            userName = "JuanP",
            profileImage = null,
            miniProfileImage = null
        )

        val user2 = User(
            firstName = "Maria",
            lastName1 = "Lopez",
            lastName2 = null,
            birthDate = Date(),
            className = "Clase B",
            email = "maria@mail.com",
            password = "abcd",
            userName = "MariaL",
            profileImage = null,
            miniProfileImage = null
        )

        val user3 = User(
            firstName = "Carlos",
            lastName1 = "Sanchez",
            lastName2 = "Diaz",
            birthDate = Date(),
            className = "Clase C",
            email = "carlos@mail.com",
            password = "pass123",
            userName = "CarlosS",
            profileImage = null,
            miniProfileImage = null
        )

        val tareas = listOf(
            Task("Tarea 1", "Revisar documentos del proyecto", Date(), Date(), user1, "Pendiente"),
            Task("Tarea 2", "Enviar correo al cliente", Date(), Date(), user2, "Hecha"),
            Task("Tarea 3", "Actualizar la base de datos", Date(), Date(), user3, "Pendiente"),
            Task("Tarea 4", "Preparar presentación", Date(), Date(), null, "Hecha"),
            Task("Tarea 5", "Reunión de seguimiento", Date(), Date(), user1, "Pendiente"),
            Task("Tarea 6", "Analizar resultados", Date(), Date(), user2, "Hecha"),
            Task("Tarea 7", "Probar la aplicación", Date(), Date(), null, "Pendiente"),
            Task("Tarea 8", "Documentar cambios", Date(), Date(), user3, "Hecha")
        )


        tareas.forEach { task ->
            addTaskToLayout(task, pendientesLayout, hechasLayout)
        }
    }

    private fun addTaskToLayout(
        task: Task,
        pendientesLayout: LinearLayout,
        hechasLayout: LinearLayout
    ) {
        val layoutRes: Int
        val parentLayout: LinearLayout

        if (task.taskStatus == "Pendiente") {
            layoutRes = R.layout.tareas_pendientes
            parentLayout = pendientesLayout
        } else {
            layoutRes = R.layout.tareas_hechas
            parentLayout = hechasLayout
        }

        val taskView = layoutInflater.inflate(layoutRes, parentLayout, false)

        val taskName = taskView.findViewById<TextView>(R.id.taskNameText)


        taskName.text = task.taskName

        parentLayout.addView(taskView)
    }

}


