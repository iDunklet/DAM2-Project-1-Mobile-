package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.Button

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var tabTareas: TextView
    private lateinit var tabResumenes: TextView
    private lateinit var tabBar: LinearLayout
    private lateinit var indicador: View

    private lateinit var pendientesLayout: LinearLayout
    private lateinit var hechasLayout: LinearLayout

    private var selectedTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_detail)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        tabTareas = findViewById(R.id.tabTareas)
        tabResumenes = findViewById(R.id.tabResumenes)
        tabBar = findViewById(R.id.tabBar)
        indicador = findViewById(R.id.indicator)

        pendientesLayout = findViewById(R.id.pendientesContainer)
        hechasLayout = findViewById(R.id.hechasContainer)

        val selectedProject = intent.getSerializableExtra("selected_project") as? Project
            ?: return

        val txtMembers = findViewById<TextView>(R.id.txtMembers)
        txtMembers.text = "${selectedProject.projectMembers.size} miembros"

        val tareas = selectedProject.projectTasks ?: emptyList()

        tabBar.post {
            val params = indicador.layoutParams
            params.width = tabBar.width / 2
            indicador.layoutParams = params
        }

        tabTareas.setOnClickListener { selectTab(0) }
        tabResumenes.setOnClickListener { selectTab(1) }

        selectTab(0)

        tareas.forEach { task ->
            addTaskToLayout(task)
        }
    }

    private fun selectTab(position: Int) {
        selectedTab = position
        val tabWidth = tabBar.width / 2

        indicador.animate()
            .translationX((position * tabWidth).toFloat())
            .setDuration(200)
            .start()

        if (position == 0) {

            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabTareas.setTypeface(null, android.graphics.Typeface.BOLD)

            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabResumenes.setTypeface(null, android.graphics.Typeface.NORMAL)


        } else {

            tabResumenes.setTextColor(ContextCompat.getColor(this, R.color.naranja))
            tabResumenes.setTypeface(null, android.graphics.Typeface.BOLD)

            tabTareas.setTextColor(ContextCompat.getColor(this, R.color.oscuro))
            tabTareas.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun addTaskToLayout(task: Task) {
        val (layoutRes, parentLayout) = if (task.taskStatus.equals("Pendiente", ignoreCase = true)) {
            R.layout.tareas_pendientes to pendientesLayout
        } else {
            R.layout.tareas_hechas to hechasLayout
        }

        val taskView = layoutInflater.inflate(layoutRes, parentLayout, false)
        val taskName = taskView.findViewById<TextView>(R.id.taskNameText)
        taskName.text = task.taskName


        val btnEmpezar = taskView.findViewById<Button>(R.id.btnEmpezar)
        if (btnEmpezar !=null){
            btnEmpezar.visibility = if (task.taskStatus.equals("Pendientes", ignoreCase = true)){
               View.VISIBLE
            }else{
                View.GONE
            }
            btnEmpezar.setOnClickListener{
                task.taskStatus = "Hecha"
                parentLayout.removeView(taskView)
                addTaskToLayout(task)
            }

        }
        taskView.setOnClickListener {
            val intent = Intent(this, TarearDetailActivity::class.java)
            intent.putExtra("task", task)
            startActivity(intent)
        }

        parentLayout.addView(taskView)

    }
}
