// DataManager.kt
package com.example.mobile

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataManager(private val context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("project_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProjects(projects: List<Project>) {
        val json = gson.toJson(projects)
        sharedPref.edit().putString("saved_projects", json).apply()
    }

    fun loadProjects(): List<Project> {
        val json = sharedPref.getString("saved_projects", null)
        return if (json != null) {
            val type = object : TypeToken<List<Project>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveProjectChanges(project: Project) {
        val allProjects = loadProjects().toMutableList()

        val index = allProjects.indexOfFirst { it.id == project.id }
        if (index != -1) {
            allProjects[index] = project
        } else {
            allProjects.add(project)
        }

        saveProjects(allProjects)
    }

    fun saveTaskStatus(projectId: Int, taskIndex: Int, newStatus: String) {
        val allProjects = loadProjects().toMutableList()
        val project = allProjects.find { it.id == projectId }

        project?.let {
            if (taskIndex < it.projectTasks.size) {
                it.projectTasks[taskIndex].taskStatus = newStatus
                saveProjects(allProjects)
            }
        }
    }
}