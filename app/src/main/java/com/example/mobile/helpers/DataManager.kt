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

    // ---------------------------------------------------------
    //  Configuración inicial
    // ---------------------------------------------------------
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .create()

    private val projectsFile = "projects.json"
    private val usersFile = "users.json"

    // ---------------------------------------------------------
    //  Carga de datos
    // ---------------------------------------------------------
    fun loadProjects(): List<Project> {
        return loadData(projectsFile, R.raw.projects)
    }

    fun loadUsers(): List<User> {
        return loadData(usersFile, R.raw.users)
    }

    // ---------------------------------------------------------
    //  Guardado de datos
    // ---------------------------------------------------------
    fun saveProjectChanges(project: Project) {
        updateProjectList(project)
    }

    fun saveProjects(projects: List<Project>) {
        saveData(projectsFile, projects)
    }

    // ---------------------------------------------------------
    //  Métodos privados de carga
    // ---------------------------------------------------------
    private inline fun <reified T> loadData(fileName: String, rawResId: Int): List<T> {
        val file = getFile(fileName)

        ensureFileExists(file, rawResId)

        //no hace falta el try pero lo puse pq si
        try {
            return readFromFile<T>(file)
        }catch (e: com.google.gson.JsonSyntaxException){
            return emptyList()
            Log.e("DataManager", "Algo se petó jajajajaj: ${e.message}")
        }


    }

    private fun getFile(fileName: String): File {
        return File(context.filesDir, fileName)
    }

    //lo hice antes de saber que se puede importar a FilesDir
    private fun ensureFileExists(file: File, rawResId: Int) {
        if (!file.exists()) {
            copyFromRaw(rawResId, file)
        }
    }

    private inline fun <reified T> readFromFile(file: File): List<T> {
        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type)
    }

    // ---------------------------------------------------------
    //  Métodos privados de guardado
    // ---------------------------------------------------------
    private fun <T> saveData(fileName: String, data: List<T>) {
        val file = getFile(fileName)
        val json = gson.toJson(data)
        file.writeText(json)
    }

    private fun updateProjectList(updatedProject: Project) {
        val allProjects = loadProjects().toMutableList()
        val index = allProjects.indexOfFirst { it.id == updatedProject.id }

        if (index != -1) {
            allProjects[index] = updatedProject
        } else {
            allProjects.add(updatedProject)
        }

        saveProjects(allProjects)
    }

    // ---------------------------------------------------------
    //  Métodos privados de utilidad
    // ---------------------------------------------------------
    private fun copyFromRaw(rawResId: Int, targetFile: File) {
        try {
            val inputStream = context.resources.openRawResource(rawResId)
            val outputStream = targetFile.outputStream()

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("DataManager", "Error copiando archivo desde raw: ${e.message}")
        }
    }
}