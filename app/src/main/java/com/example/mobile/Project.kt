package com.example.mobile

import android.graphics.Bitmap
import java.time.LocalDate

data class Project(
    val id: Int,
    var title: String,
    var deliveryDate: LocalDate,
    var projectMembers: MutableList<User>,
    var projectTasks: MutableList<Task>,
    val projectImage: Bitmap?
)
