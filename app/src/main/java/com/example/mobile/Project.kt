package com.example.mobile

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.Date


data class Project(
    @SerializedName("ID") val id: Int,
    @SerializedName("titulo") var title: String,
    @SerializedName("fechaEntrega") var deliveryDate: Date,
    @SerializedName("miembrosProyecto") var projectMembers: MutableList<User>,
    @SerializedName("tareasProyecto") var projectTasks: MutableList<Task>,
    @Transient val projectImage: Bitmap? = null
)

