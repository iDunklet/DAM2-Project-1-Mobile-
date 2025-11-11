package com.example.mobile

import android.graphics.Bitmap
import java.util.Calendar
import java.util.Date

data class User(
    val firstName: String,
    val lastName1: String,
    val lastName2: String?,
    val birthDate: Date,
    val className: String,
    val email: String,
    var password: String,
    val userName: String,
    var profileImage: Bitmap?,
    var miniProfileImage: Bitmap?
)
val user = User(
    firstName = "Maria",
    lastName1 = "Lopez",
    lastName2 = null,
    birthDate = Date(),
    className = "Clase B",
    email = "maria@mail.com",
    password = "abcd",
    userName = "MariaL",
    profileImage = null,
    miniProfileImage = null
)
