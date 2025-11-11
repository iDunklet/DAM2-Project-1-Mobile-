package com.example.mobile

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProjectsActivity : AppCompatActivity() {

    private lateinit var tabRecent: TextView
    private lateinit var tabFavorites: TextView
    private lateinit var tabAll: TextView
    private lateinit var indicator: View
    private var selectedTab = 1

    private lateinit var projects: List<Project>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_projects)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        setupClicks()
        startAnimations()
        setupFirstTab()


    }

    private fun findViews() {
        tabRecent = findViewById(R.id.tabRecent)
        tabFavorites = findViewById(R.id.tabFavorites)
        tabAll = findViewById(R.id.tabAll)
        indicator = findViewById(R.id.indicator)
    }

    private fun setupClicks() {
        tabRecent.setOnClickListener {
            changeTab(1)
        }

        tabFavorites.setOnClickListener {
            changeTab(2)
        }

        tabAll.setOnClickListener {
            changeTab(3)
        }
    }

    private fun startAnimations() {
        val circle1 = findViewById<View>(R.id.circle1)
        val circle2 = findViewById<View>(R.id.circle2)
        val circle3 = findViewById<View>(R.id.circle3)

        val floatAnimation = AnimationUtils.loadAnimation(this, R.anim.float_animation)

        circle1.startAnimation(floatAnimation)
        circle2.startAnimation(floatAnimation)
        circle3.startAnimation(floatAnimation)
    }

    private fun setupFirstTab() {
        tabRecent.post {
            moveIndicator()
            changeColors()
        }
    }

    private fun changeTab(tab: Int) {
        selectedTab = tab
        moveIndicator()
        changeColors()
    }

    private fun moveIndicator() {
        val selectedView = getSelectedView()

        indicator.animate()
            .translationX(selectedView.x)
            .setDuration(300)
            .start()

        val params = indicator.layoutParams
        params.width = selectedView.width
        indicator.layoutParams = params
    }

    private fun changeColors() {
        tabRecent.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        tabFavorites.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        tabAll.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        val selected = getSelectedView()
        selected.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
    }

    private fun getSelectedView(): TextView {
        return when(selectedTab) {
            1 -> tabRecent
            2 -> tabFavorites
            3 -> tabAll
            else -> tabRecent
        }
    }
}