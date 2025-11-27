package com.example.mobile

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class User(
    @SerializedName("nombre") val firstName: String,
    @SerializedName("apellido1") val lastName1: String,
    @SerializedName("apellido2") val lastName2: String?,
    @SerializedName("fechaNacimiento") val birthDate: Date?,
    @SerializedName("classe") val className: String,
    @SerializedName("email") val email: String,
    var password: String,
    @SerializedName("userName") val userName: String,
    @Transient var profileImage: Bitmap? = null,
    @Transient var miniProfileImage: Bitmap? = null
               ) : Serializable