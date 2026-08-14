package com.app.iot.ui.features.home.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.app.iot.bottom_nav.CustomBottomNavigation
import com.app.iot.ui.features.home.tab.HomeTab

@Composable
fun HomeScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    Scaffold(
        containerColor = Color(0xFFF18673),
        bottomBar = {
            CustomBottomNavigation(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> HomeTab(innerPadding)
            1 -> BaseContent("Search", innerPadding)
            2 -> BaseContent("Cart", innerPadding)
            3 -> BaseContent("Favorite", innerPadding)
            4 -> BaseContent("Profile", innerPadding)
        }
    }
}