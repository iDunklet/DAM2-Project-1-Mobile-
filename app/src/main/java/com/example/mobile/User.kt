package com.example.mobile

import android.graphics.Bitmap
import java.time.LocalDate

data class User(
    val firstName: String,
    val lastName1: String,
    val lastName2: String?,
    val birthDate: LocalDate,
    val className: String,
    val email: String,
    var password: String,
    val userName: String,
    var profileImage: Bitmap?,
    var miniProfileImage: Bitmap?
) {
    val fullLastName: String
        get() = if (!lastName2.isNullOrEmpty()) "$lastName1 $lastName2" else lastName1
}
