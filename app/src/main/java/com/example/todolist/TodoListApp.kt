package com.example.todolist

import android.app.Application
import android.provider.Settings
import android.provider.Settings.Secure
import com.example.todolist.utils.preference.AppPreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TodoListApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferenceManager.config(this)
    }
}