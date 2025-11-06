package com.example.mobile

import java.time.LocalDate

data class Task(
    var taskName: String,
    var taskDescription: String,
    var taskStartDate: LocalDate,
    var taskEndDate: LocalDate,
    var assignedUser: User?,
    var taskStatus: String
)

