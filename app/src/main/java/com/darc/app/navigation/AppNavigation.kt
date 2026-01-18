package com.darc.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.darc.app.ui.home.HomeScreen
import com.darc.app.ui.onboarding.OnboardingScreen
import com.darc.app.ui.routines.RoutineDetailScreen
import com.darc.app.ui.routines.RoutinesScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Routines : Screen("routines")
    object RoutineDetail : Screen("routine/{routineId}") {
        fun createRoute(routineId: Long) = "routine/$routineId"
    }
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
                HomeScreen(
                    onNavigateToRoutines = {
                        navController.navigate(Screen.Routines.route)
                    }
                )
            }

            composable(Screen.Routines.route) {
                RoutinesScreen(
                    onRoutineClick = { routineId ->
                        navController.navigate(Screen.RoutineDetail.createRoute(routineId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.RoutineDetail.route,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) {
                RoutineDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
