package com.app.iot.nav

import androidx.navigation.toRoute
import com.app.iot.ui.features.common.screen.LauncherScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.iot.ui.features.auth.screen.LoginScreen
import com.app.iot.ui.features.home.screen.HomeScreen
import com.app.iot.ui.features.home.search.SearchDeviceScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Launcher
    ) {
        // Launcher
        composable<Launcher> {
            LauncherScreen(
                onNavigateToMain = {
                    navController.navigate(Home) {
                        popUpTo<Launcher> {
                            inclusive = true
                        }
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Search(showBackButton = false)) {
                        popUpTo<Launcher> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Login
        composable<Login>(
            exitTransition = NavAnimations.exit(),
            popEnterTransition = NavAnimations.popEnter(),
            popExitTransition = NavAnimations.popExit()
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Home){
                        popUpTo<Login> {
                            inclusive = true
                        }
                    }
                },
            )
        }

        // Home
        composable<Home>(
            /*enterTransition = NavAnimations.enter(),
            exitTransition = NavAnimations.fadeOutOnly(),
            popExitTransition = NavAnimations.popExit()*/
        ) {
            HomeScreen()
        }

        // Search
        composable<Search> { backStackEntry ->
            val search: Search = backStackEntry.toRoute()
            SearchDeviceScreen(
                showBackButton = search.showBackButton,
                onClose = {
                    navController.navigate(Home) {
                        popUpTo<Search> {
                            inclusive = true
                        }
                    }
                },
                onDeviceSelected = { _, _ ->
                    navController.navigate(Home) {
                        popUpTo<Search> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
