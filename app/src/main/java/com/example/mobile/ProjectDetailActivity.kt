package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var pendientesLayout: LinearLayout
    private lateinit var hechasLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_detail)

        // Volver atrás
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        pendientesLayout = findViewById(R.id.pendientesContainer)
        hechasLayout = findViewById(R.id.hechasContainer)

        // Recibir proyecto desde ProjectsActivity
        val selectedProject = intent.getSerializableExtra("selected_project") as? Project
            ?: return

        // Actualizar número de miembros
        val txtMembers = findViewById<TextView>(R.id.txtMembers)
        txtMembers.text = "${selectedProject.projectMembers.size} miembros"

        // Si no hay tareas, no hacemos nada
        val tareas = selectedProject.projectTasks ?: emptyList()

        // Renderizar tareas
        tareas.forEach { task ->
            addTaskToLayout(task)
        }
    }

    private fun addTaskToLayout(task: Task) {

        // Decide layout según estado
        val (layoutRes, parentLayout) =
            if (task.taskStatus.equals("Pendiente", ignoreCase = true)) {
                R.layout.tareas_pendientes to pendientesLayout
            } else {
                R.layout.tareas_hechas to hechasLayout
            }

        // Inflamos vista
        val taskView = layoutInflater.inflate(layoutRes, parentLayout, false)

        // Mostrar nombre de tarea
        val taskName = taskView.findViewById<TextView>(R.id.taskNameText)
        taskName.text = task.taskName

        // Click → abrir detalle de tarea
        taskView.setOnClickListener {
            val intent = Intent(this, TarearDetailActivity::class.java)
            intent.putExtra("task", task)
            startActivity(intent)
        }

        // Añadir a su contenedor
        parentLayout.addView(taskView)
    }
}
