package com.app.iot.ui.features.common.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.iot.ui.theme.HomeAutomationTheme
import kotlinx.coroutines.delay

@Composable
fun LauncherScreen(onNavigateToMain: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToMain()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LauncherPreview() {
    HomeAutomationTheme() {
        LauncherScreen(onNavigateToMain = {})
    }
}