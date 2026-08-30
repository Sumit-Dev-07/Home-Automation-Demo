package com.app.iot.ui.features.home.tab.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.iot.ui.theme.AppPalette
import com.app.iot.ui.theme.AppPreview

@Composable
fun DevicePagerIndicator(
	pagerState: PagerState,
	pageCount: Int,
	modifier: Modifier = Modifier,
	activeColor: Color = AppPalette.black,
	inactiveColor: Color = AppPalette.black.copy(alpha = 0.6f)
) {
	if (pageCount <= 1) return
	
	Surface(
		modifier = modifier
			.clip(RoundedCornerShape(20.dp))
			.border(
				width = 0.6.dp,
				color = AppPalette.white,
				shape = RoundedCornerShape(20.dp)
			),
		color = AppPalette.transparent,
	) {
		Row(
			modifier = Modifier
				.padding(horizontal = 12.dp, vertical = 6.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			repeat(pageCount) { iteration ->
				val isSelected = pagerState.currentPage == iteration
				val width by animateDpAsState(
					targetValue = if (isSelected) 24.dp else 8.dp,
					label = "width"
				)
				
				Box(
					modifier = Modifier
						.height(8.dp)
						.width(width)
						.clip(CircleShape)
						.background(if (isSelected) activeColor else inactiveColor)
				)
			}
		}
	}
}

// Reusing Surface for consistency if needed, but standard Surface works
@Composable
private fun Surface(
	modifier: Modifier = Modifier,
	color: Color,
	content: @Composable () -> Unit
) {
	Box(
		modifier = modifier.background(color),
		contentAlignment = Alignment.Center
	) {
		content()
	}
}

@Preview(showBackground = true)
@Composable
private fun DevicePagerIndicatorPreview() {
	val pagerState = rememberPagerState(pageCount = { 3 })
	AppPreview {
		DevicePagerIndicator(
			pagerState = pagerState,
			pageCount = 3
		)
	}
}
