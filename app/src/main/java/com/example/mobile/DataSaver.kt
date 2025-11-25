package com.example.mobile

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

fun saveProjects(context: Context, projects: List<Project>) {
    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    val json = gson.toJson(projects)
    val file = File(context.filesDir, "projects_autosave.json")
    file.writeText(json)
    Log.d("AutoSave", "Projects guardados en ${file.absolutePath}")
}
