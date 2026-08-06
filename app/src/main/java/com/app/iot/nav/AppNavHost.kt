package com.app.iot.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.iot.ui.features.common.screen.LauncherScreen
import com.app.iot.ui.features.home.screen.HomeScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Launcher
    ) {

        composable<Launcher> {

            LauncherScreen(
                onNavigateToMain = {
                    navController.navigate(Home) {
                        popUpTo<Launcher> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Home>(
            enterTransition = NavAnimations.enter(),
            exitTransition = NavAnimations.fadeOutOnly(),
            popExitTransition = NavAnimations.popExit()
        ) {
            HomeScreen()
        }
    }
}
