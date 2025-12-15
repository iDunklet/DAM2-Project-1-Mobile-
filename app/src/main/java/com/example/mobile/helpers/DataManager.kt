package com.example.mobile.helpers

import android.content.Context
import android.util.Log
import com.example.mobile.R
import com.example.mobile.classes.Project
import com.example.mobile.classes.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Date

class DataManager(private val context: Context) {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .create()

    private val projectsFile = "projects.json"
    private val usersFile = "users.json"



    fun loadProjects(): List<Project> {
        val file = File(context.filesDir, projectsFile)
        if (!file.exists()) {
            Log.d("DataManager", "No existe projects.json, copiando desde raw")
            copyFromRaw(R.raw.projects, file)
        }
        return if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<Project>>() {}.type
            val projects: List<Project> = gson.fromJson(json, type)
            Log.d("DataManager", "Proyectos cargados: ${projects.size}")
            projects
        } else emptyList()
    }

    fun loadUsers(): List<User> {
        val file = File(context.filesDir, usersFile)
        if (!file.exists()) {
            Log.d("DataManager", "No existe users.json, copiando desde raw")
            copyFromRaw(R.raw.users, file)
        }
        return if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<User>>() {}.type
            val users: List<User> = gson.fromJson(json, type)
            Log.d("DataManager", "Usuarios cargados: ${users.size}")
            users
        } else emptyList()
    }

    //save at projectDetail
    fun saveProjectChanges(project: Project) {
        val allProjects = loadProjects().toMutableList()
        val index = allProjects.indexOfFirst { it.id == project.id }
        if (index != -1) allProjects[index] = project else allProjects.add(project)
        saveProjects(allProjects)
        Log.d("DataManager", "Proyecto actualizado: ${project.id}")
    }
    //save at pause
    fun saveProjects(projects: List<Project>) {
        val json = gson.toJson(projects)
        File(context.filesDir, projectsFile).writeText(json)
        Log.d("DataManager", "Proyectos guardados: ${projects.size}")
    }

    private fun copyFromRaw(rawResId: Int, targetFile: File) {
        try {
            context.resources.openRawResource(rawResId).use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("DataManager", "Archivo copiado desde raw a ${targetFile.name}")
        } catch (e: Exception) {
            Log.e("DataManager", "Error copiando archivo desde raw: ${e.message}")
        }
    }
}