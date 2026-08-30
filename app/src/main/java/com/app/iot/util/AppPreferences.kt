package com.app.iot.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("home_iot_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SELECTED_IP = "selected_ip"
        private const val KEY_SELECTED_NAME = "selected_name"
    }

    var selectedIp: String
        get() = prefs.getString(KEY_SELECTED_IP, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SELECTED_IP, value).apply()

    var selectedName: String
        get() = prefs.getString(KEY_SELECTED_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SELECTED_NAME, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
