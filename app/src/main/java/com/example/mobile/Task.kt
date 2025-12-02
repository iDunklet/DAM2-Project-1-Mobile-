package com.example.mobile

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class Task(
    @SerializedName("nombreTarea") var taskName: String,
    @SerializedName("descripcionTarea") var taskDescription: String?,
    @SerializedName("fechaInicioTarea") var taskStartDate: Date?,
    @SerializedName("fechaFinTarea") var taskEndDate: Date?,
    @SerializedName("responsableAsignado") var assignedUser: User?,
    @SerializedName("statusTarea") var taskStatus: String,
    @SerializedName("horas") var taskTime: Int? = null,

) : Serializable