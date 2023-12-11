package com.example.todolist.utils.preference

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.core.content.edit

class AppPreferenceManager private constructor(
    context: Context
) {
    companion object {
        const val DEVICE_ID = "device_id"

        lateinit var instance: AppPreferenceManager

        @SuppressLint("HardwareIds")
        fun config(context: Context) {
            if (::instance.isInitialized) return
            instance = AppPreferenceManager(context)
            instance.deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ).hashCode().toString()
        }
    }

    private val sharedPreferencesName = "todo_list_app"
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    var deviceId: String
        get() = sharedPreferences.getString(DEVICE_ID, "").orEmpty()
        set(value) = sharedPreferences.edit { putString(DEVICE_ID, value) }
}