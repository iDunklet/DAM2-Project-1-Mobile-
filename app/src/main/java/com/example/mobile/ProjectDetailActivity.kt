package com.example.mobile

import android.content.Intent
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

        val user1 = User("Juan", "Pérez", "Gómez", Date(), "Clase A", "juan@mail.com", "1234", "JuanP", null, null)
        val user2 = User("Maria", "Lopez", null, Date(), "Clase B", "maria@mail.com", "abcd", "MariaL", null, null)
        val user3 = User("Carlos", "Sanchez", "Diaz", Date(), "Clase C", "carlos@mail.com", "pass123", "CarlosS", null, null)


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

        val (layoutRes, parentLayout) =
            if (task.taskStatus == "Pendiente") { R.layout.tareas_pendientes to pendientesLayout
        } else {
            R.layout.tareas_hechas to hechasLayout
        }


        val taskView = layoutInflater.inflate(layoutRes, parentLayout, false)
        val taskName = taskView.findViewById<TextView>(R.id.taskNameText)
        taskName.text = task.taskName

        taskView.setOnClickListener {
            val intent = Intent(this, TarearDetailActivity::class.java)
            intent.putExtra("taskName", task.taskName)
            intent.putExtra("taskDescription", task.taskDescription)
            intent.putExtra("taskUser", task.assignedUser?.let { "${it.firstName} ${it.lastName1}" } ?: "Sin usuario")
            intent.putExtra("taskStartDate", task.taskStartDate.time)
            intent.putExtra("taskEndDate", task.taskEndDate.time)
            startActivity(intent)
        }

        parentLayout.addView(taskView)
    }

}
