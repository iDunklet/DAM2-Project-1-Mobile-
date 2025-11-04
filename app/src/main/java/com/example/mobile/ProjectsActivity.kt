package com.example.mobile

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProjectsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_projects)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        iniciarAnimaciones()
    }
    private fun iniciarAnimaciones() {

        val circle1 = findViewById<View>(R.id.circle1)
        val circle2 = findViewById<View>(R.id.circle2)
        val circle3 = findViewById<View>(R.id.circle3)
        val circle4 = findViewById<View>(R.id.circle4)


        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)


        circle1.startAnimation(floatAnimation)
        circle2.startAnimation(floatAnimation)
        circle3.startAnimation(floatAnimation)
        circle4.startAnimation(floatAnimation)
    }
}