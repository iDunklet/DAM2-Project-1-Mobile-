package com.example.mobile.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
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
    private lateinit var cbRemember: CheckBox
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initUI()
        initHelpers()
        loadData()
        loadSavedPreferences()
        setupRememberMeListener()
        setupLoginButton()
    }

    // ---------------------------------------------------------
    // Inicialización de UI
    // ---------------------------------------------------------
    private fun initUI() {
        UIAnimations(this).startFloatingCircles()

        cbRemember = findViewById(R.id.cbRemember)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    // ---------------------------------------------------------
    // Inicialización de helper (DataManager)
    // ---------------------------------------------------------
    private fun initHelpers() {
        dataManager = DataManager(this)
    }

    // ---------------------------------------------------------
    // Cargar datos desde JSON
    // ---------------------------------------------------------
    private fun loadData() {
        projects = dataManager.loadProjects()
        users = dataManager.loadUsers()
    }

    // ---------------------------------------------------------
    // Preferencias guardadas
    // ---------------------------------------------------------
    private fun loadSavedPreferences() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        cbRemember.isChecked = prefs.getBoolean("rememberMe", false)
        etUsername.setText(prefs.getString("username", ""))
        etPassword.setText(prefs.getString("password", ""))
    }

    private fun setupRememberMeListener() {
        cbRemember.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                handleRememberMeChange(isChecked)
            }
        })
    }

    private fun handleRememberMeChange(isChecked: Boolean) {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        if (isChecked) {
            editor.putBoolean("rememberMe", true)
            editor.putString("username", etUsername.text.toString())
            editor.putString("password", etPassword.text.toString())
        } else {
            editor.putBoolean("rememberMe", false)
            editor.remove("username")
            editor.remove("password")
        }

        editor.apply()
    }


    // ---------------------------------------------------------
    // Lógica de login
    // ---------------------------------------------------------
    private fun setupLoginButton() {
        btnLogin.setOnClickListener {
            val user = checkUser()

            if (user != null) {
                saveLoginPreferences()
                navigateToProjects(user)
            } else {
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLoginPreferences() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        if (cbRemember.isChecked) {
            editor.putString("username", etUsername.text.toString())
            editor.putString("password", etPassword.text.toString())
        } else {
            editor.remove("username")
            editor.remove("password")
        }

        editor.apply()
    }

    private fun navigateToProjects(user: User) {
        val intent = Intent(this, ProjectsActivity::class.java)
        intent.putExtra("projects", ArrayList(projects))
        intent.putExtra("user", user)
        startActivity(intent)
    }

    // ---------------------------------------------------------
    // Validación de usuario
    // ---------------------------------------------------------
    fun checkUser(): User? {
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        return users.find { it.firstName == username && it.password == password }
    }

    // ---------------------------------------------------------
    // Guardado en onPause
    // ---------------------------------------------------------
    override fun onPause() {
        super.onPause()
        dataManager.saveProjects(projects)
        Log.d("MainActivity", "Datos guardados en onPause")
    }
}