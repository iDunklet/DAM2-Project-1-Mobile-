package com.example.mobile

import java.util.Calendar
import java.util.Date

data class Task(
    var taskName: String,
    var taskDescription: String,
    var taskStartDate: Date,
    var taskEndDate: Date,
    var assignedUser: User?,
    var taskStatus: String
)
val assignedUser = User(
    firstName = "Juan",
    lastName1 = "Pérez",
    lastName2 = "Gómez",
    birthDate = Date(),
    className = "Clase A",
    email = "juan@mail.com",
    password = "1234",
    userName = "JuanP",
    profileImage = null,
    miniProfileImage = null
)

val tarea = Task(
    taskName = "Tarea 1",
    taskDescription = "Descripción de la tarea",
    taskStartDate = Date(),
    taskEndDate = Date(),
    assignedUser = assignedUser,
    taskStatus = "Pendiente"
)
