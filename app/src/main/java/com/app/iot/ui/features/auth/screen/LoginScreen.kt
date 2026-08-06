package com.app.iot.ui.features.auth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.iot.ui.components.CommonTopAppBar
import com.app.iot.ui.components.MobileNumberTextField
import com.app.iot.ui.components.NormalButton
import com.app.iot.ui.components.PasswordTextField
import com.app.iot.ui.features.auth.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {

    val mobileNumber = viewModel.mobileNumber
    val password = viewModel.password

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
                value = mobileNumber,
                onValueChange = { viewModel.onMobileNumberChange(it) },
                label = "Mobile Number"
            )

            Spacer(Modifier.height(16.dp))

            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Password"
            )

            Spacer(Modifier.height(24.dp))

            NormalButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Login",
                onClick = { navigateToHome() }
            )
        }
    }
}
