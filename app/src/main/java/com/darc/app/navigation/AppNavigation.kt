package com.darc.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darc.app.ui.home.HomeScreen
import com.darc.app.ui.onboarding.OnboardingScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
}

@Composable
fun AppNavigation(
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val hasPlayer by navigationViewModel.hasPlayer.collectAsState(initial = null)

    // Determine start destination based on whether player exists
    val startDestination = when (hasPlayer) {
        true -> Screen.Home.route
        false -> Screen.Onboarding.route
        null -> null // Still loading
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen()
            }
        }
    }
}
