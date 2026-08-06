package com.app.iot.ui.features.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.iot.ui.features.auth.state.LoginErrorState
import com.app.iot.ui.features.auth.state.LoginState
import com.app.iot.ui.features.auth.state.LoginUiEvent
import com.app.iot.ui.features.auth.state.emailOrMobileEmptyErrorState
import com.app.iot.ui.features.auth.state.passwordEmptyErrorState
import com.app.iot.ui.features.common.state.ErrorState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for Login Screen
 */
class LoginViewModel : ViewModel() {

    var loginState = mutableStateOf(LoginState())
        private set

    /**
     * Function called on any login event [LoginUiEvent]
     */
    fun onUiEvent(loginUiEvent: LoginUiEvent) {
        when (loginUiEvent) {

            // Email/Mobile changed
            is LoginUiEvent.EmailOrMobileChanged -> {
                loginState.value = loginState.value.copy(
                    emailOrMobile = loginUiEvent.inputValue,
                    errorState = loginState.value.errorState.copy(
                        emailOrMobileErrorState = if (loginUiEvent.inputValue.trim().isNotEmpty())
                            ErrorState()
                        else
                            emailOrMobileEmptyErrorState
                    )
                )
            }

            // Password changed
            is LoginUiEvent.PasswordChanged -> {
                loginState.value = loginState.value.copy(
                    password = loginUiEvent.inputValue,
                    errorState = loginState.value.errorState.copy(
                        passwordErrorState = if (loginUiEvent.inputValue.trim().isNotEmpty())
                            ErrorState()
                        else
                            passwordEmptyErrorState
                    )
                )
            }

            // Submit Login
            is LoginUiEvent.Submit -> {
                val inputsValidated = validateInputs()
                if (inputsValidated) {
                    viewModelScope.launch {
                        loginState.value = loginState.value.copy(isLoading = true)
                        delay(1000) // Simulate network delay
                        loginState.value = loginState.value.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Function to validate inputs
     * Ideally it should be on domain layer (usecase)
     * @return true -> inputs are valid
     * @return false -> inputs are invalid
     */
    private fun validateInputs(): Boolean {
        val emailOrMobileString = loginState.value.emailOrMobile.trim()
        val passwordString = loginState.value.password
        return when {

            // Email/Mobile empty
            emailOrMobileString.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        emailOrMobileErrorState = emailOrMobileEmptyErrorState
                    )
                )
                false
            }

            //Password Empty
            passwordString.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        passwordErrorState = passwordEmptyErrorState
                    )
                )
                false
            }

            // No errors
            else -> {
                // Set default error state
                loginState.value = loginState.value.copy(errorState = LoginErrorState())
                true
            }
        }
    }

}