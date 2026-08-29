package com.app.iot.ui.features.home.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

@Composable
fun SettingTab(innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Settings",
                    fontFamily = AppFont.onestBold,
                    fontSize = 28.sp,
                    color = AppPalette.white
                )
                Text(
                    text = "Manage your account and app preferences",
                    fontFamily = AppFont.onestRegular,
                    fontSize = 14.sp,
                    color = AppPalette.white
                )
            }
        }

        item {
            SettingSection(
                title = "Connectivity & Devices",
                items = listOf(
                    SettingItemData("Wifi setting", Icons.Default.Wifi),
                    SettingItemData("Manage devices", Icons.Default.Devices),
                    SettingItemData("Search Devices", Icons.Default.Search)
                )
            )
        }

        item {
            SettingSection(
                title = "App Settings",
                items = listOf(
                    SettingItemData("Theme color", Icons.Default.Palette, trailingText = "Coral"),
                    SettingItemData("Notification", Icons.Default.Notifications),
                )
            )
        }

        item {
            SettingSection(
                title = "Support",
                items = listOf(
                    SettingItemData("Privacy", Icons.Default.Lock),
                    SettingItemData("About us", Icons.Default.Info)
                )
            )
        }
    }
}

@Composable
fun LastSyncCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppPalette.white)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = AppPalette.gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Last Sync",
                fontFamily = AppFont.onestMedium,
                fontSize = 16.sp,
                color = AppPalette.black
            )
        }
        Text(
            text = "August 19, 2026 at 12:07 PM",
            fontFamily = AppFont.onestRegular,
            fontSize = 14.sp,
            color = AppPalette.primary
        )
    }
}

@Composable
fun SettingSection(
    title: String,
    items: List<SettingItemData>
) {
    val outerCornerRadius = 28.dp
    val innerCornerRadius = 24.dp
    val gap = 6.dp
    
    Column {
        AppText(
            text = title,
            fontFamily = AppFont.onestBold,
            fontSize = 16.sp,
            color = AppPalette.white,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    AppPalette.black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCornerRadius),
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(gap)
                    .clip(RoundedCornerShape(innerCornerRadius))
                    .background(AppPalette.white)
            ) {
                items.forEachIndexed { index, item ->
                    SettingRow(
                        item = item,
                        onClick = { /* TODO: Handle click */ }
                    )
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = AppPalette.lightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(
    item: SettingItemData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = AppPalette.gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            fontFamily = AppFont.onestMedium,
            fontSize = 16.sp,
            color = AppPalette.black,
            modifier = Modifier.weight(1f)
        )
        if (item.trailingText != null) {
            Text(
                text = item.trailingText,
                fontFamily = AppFont.onestRegular,
                fontSize = 14.sp,
                color = AppPalette.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = AppPalette.gray
        )
    }
}

data class SettingItemData(
    val title: String,
    val icon: ImageVector,
    val trailingText: String? = null
)

@Preview
@Composable
fun SettingTabPreview() {
    AppPreview(padding = 0.dp) {
        SettingTab(PaddingValues(0.dp))
    }
}
