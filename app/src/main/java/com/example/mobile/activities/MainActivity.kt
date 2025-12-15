package com.example.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.mobile.BaseActivity
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.User
import com.example.mobile.helpers.DataManager
import com.example.mobile.helpers.UIAnimations

class MainActivity : BaseActivity() {
    private lateinit var projects: List<Project>
    private lateinit var users: List<User>
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        UIAnimations(this).startFloatingCircles()

        dataManager = DataManager(this)

        projects = dataManager.loadProjects()
        users = dataManager.loadUsers()

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
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dataManager.saveProjects(projects)
        Log.d("MainActivity", "Datos guardados en onPause")
    }



    fun checkUser(): User? {
        val inputUsername = findViewById<EditText>(R.id.etUsername).text.toString()
        val inputPassword = findViewById<EditText>(R.id.etPassword).text.toString()
        return users.find { it.firstName == inputUsername && it.password == inputPassword }
    }
}