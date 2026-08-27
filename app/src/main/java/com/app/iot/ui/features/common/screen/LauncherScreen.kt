import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.iot.BuildConfig
import com.app.iot.R
import com.app.iot.ui.components.core.AppImage
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.HomeAutomationTheme
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LauncherScreen(onNavigateToMain: () -> Unit) {
	
	LaunchedEffect(Unit) {
		delay(2.seconds)
		onNavigateToMain()
	}
	
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background),
		contentAlignment = Alignment.Center
	) {
		AppImage(imageRes = R.drawable.app_logo)
		AppText(
			"Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
			fontFamily = AppFont.onestSemiBold,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(bottom = 32.dp),
			color = AppPalette.gray
		)
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LauncherPreview() {
	HomeAutomationTheme() {
		LauncherScreen(onNavigateToMain = {})
	}
}