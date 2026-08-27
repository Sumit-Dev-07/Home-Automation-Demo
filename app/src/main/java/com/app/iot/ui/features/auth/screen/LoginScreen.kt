package com.app.iot.ui.features.auth.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.iot.ui.theme.AppPalette
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.compose.data.remote.model.login.LoginRequest
import com.app.iot.ui.components.core.AppTextField
import com.app.iot.ui.features.auth.viewmodel.AuthViewModel
import com.app.iot.util.UiState


@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
) {

    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(value = true) }

    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.loginState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> onLoginSuccess()
            is UiState.Error -> {
                val message = (uiState as UiState.Error).message
                snackBarHostState.showSnackbar(message)
            }

            else -> Unit
        }
    }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .background(color = AppPalette.white)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            AppTextField(
                value = username,
                onValueChange = { newValue ->
                    username = newValue
                    usernameError = viewModel.validateUsername(newValue)
                },
                label = "Enter username",
                keyboardType = KeyboardType.Text,
                title = "Username",
                isError = usernameError != null,
                errorMessage = usernameError,
                maxLength = 20
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = password,
                onValueChange = { newValue ->
                    password = newValue
                    passwordError = viewModel.validatePassword(newValue)
                },
                label = "Enter password",
                keyboardType = KeyboardType.Password,
                title = "Password",
                isError = passwordError != null,
                errorMessage = passwordError,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = {
                    passwordVisible = !passwordVisible
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            CompositionLocalProvider(LocalRippleConfiguration provides null) {
                TextButton(
                    onClick = {

                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {

                    Text("Forgot Password?")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    usernameError = viewModel.validateUsername(username)
                    passwordError = viewModel.validatePassword(password)

                    if ((usernameError == null) && (passwordError == null)) {
                        focusManager.clearFocus()
                        val request = LoginRequest(username.trim(), password.trim())
                        viewModel.login(request)
                    }
                },
                enabled = uiState !is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)

            ) {
                if (uiState is UiState.Loading) {
                    Text("Loading...", style = TextStyle(color = AppPalette.white))
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ")
                CompositionLocalProvider(LocalRippleConfiguration provides null) {
                    TextButton(
                        onClick = {

                        },
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Sign Up")
                    }
                }

            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )

    }

}
