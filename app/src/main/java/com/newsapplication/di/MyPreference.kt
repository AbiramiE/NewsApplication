package com.newsapplication.di

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPreference @Inject constructor(@ApplicationContext context : Context){
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getStoredTag(id: String): Boolean {
        return prefs.getBoolean(id, false)
    }

    fun setStoredTag(id: String, query: Boolean) {
        prefs.edit().putBoolean(id, query).apply()
    }
}