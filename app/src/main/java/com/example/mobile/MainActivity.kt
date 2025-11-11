package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var projects: List<Project>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startAnimations()
        loadJson()
        val jsonString = File(filesDir, "data.json").readText()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // Salte a ProjectDetailActivity cuando haga clic
            val intent = Intent(this, ProjectDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadJson() {
        try {
            val jsonFile = File(filesDir, "data.json")

            if (!jsonFile.exists()) {
                Toast.makeText(this, "Archivo JSON no encontrado", Toast.LENGTH_SHORT).show()
                projects = emptyList()
                return
            }

            val jsonString = jsonFile.readText()
            val type = object : TypeToken<List<Project>>() {}.type
            projects = Gson().fromJson(jsonString, type)

            // Verificar que se cargaron los datos
            Toast.makeText(this, "Cargados ${projects.size} proyectos", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error cargando JSON: ${e.message}", Toast.LENGTH_LONG).show()
            projects = emptyList()
        }
    }


    private fun startAnimations() {
        val circle1 = findViewById<View>(R.id.circle1)
        val circle2 = findViewById<View>(R.id.circle2)
        val circle3 = findViewById<View>(R.id.circle3)

        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)

        circle1.startAnimation(floatAnimation)
        circle2.startAnimation(floatAnimation)
        circle3.startAnimation(floatAnimation)
    }

}
