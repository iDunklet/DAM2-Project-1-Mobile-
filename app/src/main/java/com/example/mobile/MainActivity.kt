package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var projects: List<Project>
    private lateinit var users: List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startAnimations()
        loadJson()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this, ProjectDetailActivity::class.java)

            startActivity(intent)
        }
    }

    private fun loadJson() {
        try {
            // Abrimos el archivo JSON desde res/raw
            val inputStream = resources.openRawResource(R.raw.project_data)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()

            val type = object : TypeToken<List<Project>>() {}.type
            projects = gson.fromJson(jsonString, type)


        } catch (e: Exception) {
            Toast.makeText(this, "Error cargando JSON: ${e.message}", Toast.LENGTH_LONG).show()
            projects = emptyList()
        }
        try {
            // Abrimos el archivo JSON desde res/raw
            val inputStream = resources.openRawResource(R.raw.user_data)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()

            val type = object : TypeToken<List<Project>>() {}.type
            users = gson.fromJson(jsonString, type)

            Log.d("USER_LOAD", "Usuarios cargados: ${users.size}")

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
