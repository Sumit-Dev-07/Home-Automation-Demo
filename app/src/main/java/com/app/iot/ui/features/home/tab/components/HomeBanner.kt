package com.app.iot.ui.features.home.tab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

data class BannerData(
    val title: String,
    val subtitle: String,
    val colors: List<Color>,
    val ip: String? = null,
    val isActive: Boolean = false,
    val onEditClick: (() -> Unit)? = null
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
    val outerCorner = 20.dp
    val innerCorner = 18.dp
    val gap = 4.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(outerCorner))
                .background(AppPalette.black.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    AppPalette.black.copy(alpha = 0.1f),
                    RoundedCornerShape(outerCorner),
                )
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(gap),
            shape = RoundedCornerShape(innerCorner),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                    tint = AppPalette.primary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 30.dp, y = 30.dp)
                )

                // Edit Button
                if (banner.onEditClick != null) {
                    IconButton(
                        onClick = banner.onEditClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppText.Bold(
                                text = banner.title,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                            
                            if (banner.isActive) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppPalette.green.copy(alpha = 0.15f))
                                        .border(1.dp, AppPalette.green.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    AppText.SemiBold(
                                        text = "Active",
                                        fontSize = 10.sp,
                                        color = AppPalette.darkGreen
                                    )
                                }
                            }
                        }
                        
                        if (!banner.ip.isNullOrEmpty()) {
                            AppText.Medium(
                                text = "IP: ${banner.ip}",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        AppText.Normal(
                            text = banner.subtitle,
                            fontSize = 13.sp,
                            color = Color.Black.copy(alpha = 0.8f),
                            maxLines = 2
                        )
                    }
                }
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
            listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))
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
