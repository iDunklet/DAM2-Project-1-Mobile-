// MainActivity.kt
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
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startAnimations()
        dataManager = DataManager(this)

        projects = dataManager.loadProjects()
        users = dataManager.loadUsers()


        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        cbRemember.isChecked = prefs.getBoolean("rememberMe", false)
        etUsername.setText(prefs.getString("username", ""))
        etPassword.setText(prefs.getString("password", ""))

        cbRemember.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("rememberMe", isChecked).apply()
        }

        btnLogin.setOnClickListener {
            val loggedUser = checkUser()
            if (loggedUser != null) {
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
                intent.putExtra("user", loggedUser)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onPause() {
        super.onPause()
        dataManager.saveProjects(projects)
        Log.d("MainActivity", "Datos guardados en onPause")
    }




    private fun startAnimations() {
        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)
        findViewById<View>(R.id.circle1).startAnimation(floatAnimation)
        findViewById<View>(R.id.circle2).startAnimation(floatAnimation)
        findViewById<View>(R.id.circle3).startAnimation(floatAnimation)
    }

    fun checkUser(): User? {
        val inputUsername = findViewById<EditText>(R.id.etUsername).text.toString()
        val inputPassword = findViewById<EditText>(R.id.etPassword).text.toString()
        return users.find { it.firstName == inputUsername && it.password == inputPassword }
    }
}