package com.example.mobile

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.mobile.helpers.LanguageHelper

open class BaseActivity : AppCompatActivity() {

    lateinit var personalizedFont: Typeface

    override fun attachBaseContext(newBase: Context) {
        val lang = LanguageHelper.loadLanguagePref(newBase)
        val wrapped = LanguageHelper.wrapContext(newBase, lang)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        personalizedFont = ResourcesCompat.getFont(this, R.font.montserrat_regular)!!
    }

    fun applyFont(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            when (view) {
                is TextView -> view.typeface = personalizedFont
                is ViewGroup -> applyFont(view)
            }
        }
    }
}