package com.app.iot.ui.features.home.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.features.home.tab.components.HorizontalOnOffToggle
import com.app.iot.ui.theme.AppFont
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

data class Schedule(
    val id: String,
    val relativeTime: String,
    val absoluteTime: String,
    val amPm: String,
    val message: String,
    val turnOffAfter: String,
    val isOn: Boolean
)

@Composable
fun ScheduleTab(innerPadding: PaddingValues) {
    var schedules by remember {
        mutableStateOf(
            listOf(
                Schedule(
                    "1",
                    "2 Min's ago",
                    "11:30",
                    "PM",
                    "Nice.! Hall's all Lights are now off",
                    "2 Min",
                    false
                ),
                Schedule(
                    "2",
                    "2 Min's ago",
                    "11:30",
                    "PM",
                    "Would you like to Turn off hall's lights",
                    "2 Min",
                    true
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        items(schedules) { schedule ->
            ScheduleItem(
                schedule = schedule,
                onToggle = { newValue ->
                    schedules = schedules.map {
                        if (it.id == schedule.id) it.copy(isOn = newValue) else it
                    }
                }
            )
        }
    }
}

@Composable
fun ScheduleItem(
    schedule: Schedule,
    onToggle: (Boolean) -> Unit
) {
    val outerCornerRadius = 28.dp
    val innerCornerRadius = 24.dp
    val gap = 6.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow/border layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    AppPalette.black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gap)
                .clip(RoundedCornerShape(innerCornerRadius)),
            color = AppPalette.white
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lamp Icon Placeholder (Since actual asset isn't identified)
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(AppPalette.lightGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = if (schedule.isOn) AppPalette.primary else AppPalette.gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        AppText.Medium(
                            text = schedule.relativeTime,
                            fontSize = 13.sp,
                            color = AppPalette.gray
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            AppText.Bold(
                                text = schedule.absoluteTime,
                                fontSize = 28.sp,
                                color = AppPalette.black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            AppText.Medium(
                                text = schedule.amPm,
                                fontSize = 16.sp,
                                color = AppPalette.black,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        AppText.Normal(
                            text = schedule.message,
                            fontSize = 13.sp,
                            color = AppPalette.black.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppText.SemiBold(
                            text = "Turn Off After",
                            fontSize = 14.sp,
                            color = AppPalette.black
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .background(AppPalette.lightGray, RoundedCornerShape(8.dp))
                                .border(1.dp, AppPalette.black.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            AppText.Medium(
                                text = schedule.turnOffAfter,
                                fontSize = 14.sp,
                                color = AppPalette.black
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            thickness = 1.dp,
                            color = AppPalette.black.copy(alpha = 0.05f)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        HorizontalOnOffToggle(
                            isOn = schedule.isOn,
                            onStatusChange = onToggle,
                            showIcon = true
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleTabPreview() {
    AppPreview(padding = 0.dp) {
        ScheduleTab(PaddingValues(0.dp))
    }
}
