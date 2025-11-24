package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

// Recuperar estado guardado
        cbRemember.isChecked = prefs.getBoolean("rememberMe", false)
        etUsername.setText(prefs.getString("username", ""))
        etPassword.setText(prefs.getString("password", ""))

// Guardar estado del checkbox
        cbRemember.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("rememberMe", isChecked).apply()
        }

// Login
        btnLogin.setOnClickListener {
            if (checkUser()) {
                val editor = prefs.edit()
                if (cbRemember.isChecked) {
                    editor.putString("username", etUsername.text.toString())
                    editor.putString("password", etPassword.text.toString())
                } else {
                    editor.remove("username")
                    editor.remove("password")
                }
                editor.apply()

                val intent = Intent(this, ProjectsActivity::class.java)
                intent.putExtra("projects", ArrayList(projects))
                intent.putExtra("users", ArrayList(users))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }





    }

    private fun loadJson() {
        try {
            // Cargar projects
            val projectsInput = resources.openRawResource(R.raw.projects)
            val projectJsonString = projectsInput.bufferedReader().use { it.readText() }

            val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()

            val projectType = object : TypeToken<List<Project>>() {}.type
            projects = gson.fromJson(projectJsonString, projectType)

        } catch (e: Exception) {
            Toast.makeText(this, "Error cargando PROJECTS JSON: ${e.message}", Toast.LENGTH_LONG).show()
            projects = emptyList()
        }

        try {
            // Cargar users
            val usersInput = resources.openRawResource(R.raw.users) // Usa el archivo correcto
            val usersJsonString = usersInput.bufferedReader().use { it.readText() }

            val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()

            val userType = object : TypeToken<List<User>>() {}.type
            users = gson.fromJson(usersJsonString, userType)

            Log.d("USER_LOAD", "Usuarios cargados: ${users.size}")

        } catch (e: Exception) {
            Toast.makeText(this, "Error cargando USERS JSON: ${e.message}", Toast.LENGTH_LONG).show()
            users = emptyList()
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
    fun checkUser(): Boolean {
        val inputUsername = findViewById<EditText>(R.id.etUsername).text.toString()
        val inputPassword = findViewById<EditText>(R.id.etPassword).text.toString()

        for (user in users) {
            if (user.firstName == inputUsername && user.password == inputPassword) {
                return true
            }
        }
        return false
    }


}


