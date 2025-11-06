package com.example.mobile

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startAnimations()

        //Cargar JSON desde res/raw
        //val jsonString = loadJsonFromRaw(R.raw.import0)
        /*if (jsonString != null) {
            // Ejemplo: parsear a objeto User
            val user = Gson().fromJson(jsonString, User::class.java)
            // Aquí ya puedes usar 'user'
        }*/

    }

    // Función para cargar JSON desde res/raw
    /*private fun loadJsonFromRaw(resId: Int): String? {
        return try {
            resources.openRawResource(resId).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }*/

    private fun startAnimations() {
        val circle1 = findViewById<View>(R.id.circle1)
        val circle2 = findViewById<View>(R.id.circle2)
        val circle3 = findViewById<View>(R.id.circle3)

        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)

        circle1.startAnimation(floatAnimation)
        circle2.startAnimation(floatAnimation)
        circle3.startAnimation(floatAnimation)
    }
}
