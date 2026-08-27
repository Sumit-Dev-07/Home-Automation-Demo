package com.app.iot.bottom_nav

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.iot.R
import com.app.iot.ui.theme.AppPalette

@Composable
fun CustomBottomNavigation(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItem("Home", R.drawable.home_active, R.drawable.home_inactive),
        NavigationItem("Schedule", R.drawable.ic_active_clock, R.drawable.ic_clock),
        NavigationItem("Cart", R.drawable.cart_active, R.drawable.cart_inactive),
        NavigationItem("Favorite", R.drawable.fav_active, R.drawable.fav_inactive),
        NavigationItem("Profile", R.drawable.user_active, R.drawable.user_inactive)
    )

    val orangeColor = AppPalette.primary
    val grayColor = AppPalette.gray

    // Layout dimensions calculation:
    // Outer Height = Inner Bar Height + (Gap * 2) -> 76 + (8 * 2) = 92dp
    // Outer Radius ≈ Inner Radius + Gap -> 28 + 8 = 36dp (using 32dp for a tighter look)
    val outerBoxHeight = 92.dp
    val innerBarHeight = 76.dp
    val outerCornerRadius = 24.dp
    val innerCornerRadius = 20.dp
    val gapBetweenBars = 8.dp // Space between the outer glow and inner white bar
    
    val horizontalPadding = 16.dp // Margin from screen sides
    val bottomMargin = 24.dp      // Distance from the bottom of the screen

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = bottomMargin, start = horizontalPadding, end = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow/border layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(outerBoxHeight)
                .clip(RoundedCornerShape(outerCornerRadius))
                .background(AppPalette.white.copy(alpha = 0.2f))
                .border(
                    1.dp,
                    AppPalette.white.copy(alpha = 0.3f),
                    RoundedCornerShape(outerCornerRadius),
                )
        )

        // Main white bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(gapBetweenBars) // Gap between outer and inner
                .height(innerBarHeight)
                .clip(RoundedCornerShape(innerCornerRadius))
                .background(AppPalette.white),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == index
                CustomBottomNavItem(
                    icon = painterResource(id = if (isSelected) item.selectedIcon else item.unselectedIcon),
                    label = item.label,
                    isSelected = isSelected,
                    selectedColor = orangeColor,
                    unselectedColor = grayColor,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }
}