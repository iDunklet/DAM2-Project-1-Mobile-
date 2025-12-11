package com.example.mobile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.helpers.LanguageHelper

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val lang = LanguageHelper.loadLanguagePref(newBase)
        val wrapped = LanguageHelper.wrapContext(newBase, lang)
        super.attachBaseContext(wrapped)
    }
}