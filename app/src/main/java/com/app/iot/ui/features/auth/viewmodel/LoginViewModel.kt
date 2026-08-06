package com.app.iot.ui.features.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var mobileNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun onMobileNumberChange(newValue: String) {
        val filtered = newValue
            .filter { it.isDigit() }
            .take(10)

        mobileNumber = when {
            filtered.isEmpty() -> ""
            filtered.first() in '6'..'9' -> filtered
            else -> mobileNumber
        }
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }
}
