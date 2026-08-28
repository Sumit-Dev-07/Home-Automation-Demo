package com.app.iot.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
	primary = AppPalette.primary,
	secondary = AppPalette.secondary,
	tertiary = AppPalette.darkGray
)

private val LightColorScheme = lightColorScheme(
	primary = AppPalette.primary,
	secondary = AppPalette.secondary,
	tertiary = AppPalette.gray,
	background = AppPalette.white,
	surface = AppPalette.white,
	onPrimary = AppPalette.white,
	onSecondary = AppPalette.white,
	onBackground = AppPalette.black,
	onSurface = AppPalette.black,
)

@Composable
fun HomeAutomationTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		
		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}
	
	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}

@Composable
fun AppPreview(padding: Dp = 16.dp, color: Color = AppPalette.secondary, content: @Composable () -> Unit) {
	HomeAutomationTheme {
		Box(
			modifier = Modifier
				.background(color)
				.padding(padding)
		) {
			content()
		}
	}
}
