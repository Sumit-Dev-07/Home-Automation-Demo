package com.app.iot.ui.features.auth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.iot.ui.components.CommonTopAppBar
import com.app.iot.ui.components.MobileNumberTextField
import com.app.iot.ui.components.NormalButton
import com.app.iot.ui.components.PasswordTextField
import com.app.iot.ui.features.auth.state.LoginUiEvent
import com.app.iot.ui.features.auth.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {

    val loginState by remember {
        loginViewModel.loginState
    }

    LaunchedEffect(key1 = loginState.isLoginSuccessful) {
        if (loginState.isLoginSuccessful) {
            navigateToHome()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = "Login"
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MobileNumberTextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginState.emailOrMobile,
                onValueChange = { inputString ->
                    loginViewModel.onUiEvent(
                        loginUiEvent = LoginUiEvent.EmailOrMobileChanged(
                            inputString
                        )
                    )
                },
                label = "Mobile Number",
                isError = loginState.errorState.emailOrMobileErrorState.hasError,
                errorText = stringResource(id = loginState.errorState.emailOrMobileErrorState.errorMessageStringResource)
            )

            Spacer(Modifier.height(16.dp))

            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginState.password,
                onValueChange = { inputString ->
                    loginViewModel.onUiEvent(
                        loginUiEvent = LoginUiEvent.PasswordChanged(
                            inputString
                        )
                    )
                },
                label = "Password",
                isError = loginState.errorState.passwordErrorState.hasError,
                errorText = stringResource(id = loginState.errorState.passwordErrorState.errorMessageStringResource)
            )

            Spacer(Modifier.height(24.dp))

            NormalButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Login",
                enabled = !loginState.isLoading,
                onClick = { loginViewModel.onUiEvent(loginUiEvent = LoginUiEvent.Submit) }
            )

            if (loginState.isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}
