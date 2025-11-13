package com.example.mobile
import java.util.Date

data class Task(
    var taskName: String,
    var taskDescription: String,
    var taskStartDate: Date,
    var taskEndDate: Date,
    var assignedUser: User?,
    var taskStatus: String
)

