package com.app.iot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.HomeAutomationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            AppText.SemiBold(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = AppFont.onestSemiBold,
                    fontSize = 18.sp
                ),
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp)
                        .size(40.dp)
                        .background(
                            color = AppPalette.white.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(28.dp).padding(end = 4.dp)
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppPalette.secondary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CommonTopAppBarPreview() {
    HomeAutomationTheme {
        CommonTopAppBar(
            title = "Home Automation",
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTopAppBarNoBackPreview() {
    HomeAutomationTheme {
        CommonTopAppBar(
            title = "Settings"
        )
    }
}
