package com.app.iot.ui.features.home.tab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.iot.R
import com.app.iot.ui.components.core.AppText
import com.app.iot.ui.theme.AppPreview

data class BannerData(
    val title: String,
    val subtitle: String,
    val colors: List<Color>
)

@Composable
fun HomeBannerList(banners: List<BannerData>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        HomeBannerItem(banners[page])
    }
}

@Composable
fun HomeBannerItem(banner: BannerData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = banner.colors
                )
            )
    ) {
        // Background Decorative Icon
        Icon(
            painter = painterResource(id = R.drawable.ic_devices),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = 30.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                AppText.Bold(
                    text = banner.title,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                AppText.Normal(
                    text = "2 Rooms - 4 Devices",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeBannerPreview() {
    val banners = listOf(
        BannerData(
            "Home",
            "Optimize your energy consumption with AI.",
            listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
        ),
        BannerData(
            "Test",
            "Physical therapy for body function improvement.",
            listOf(Color(0xFF8E24AA), Color(0xFF64B5F6))
        ),
    )
    AppPreview(padding = 0.dp) {
        HomeBannerList(banners)
    }
}
