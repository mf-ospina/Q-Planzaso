package com.planapp.qplanzaso.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.planapp.qplanzaso.ui.screens.onboarding.SplashScreen
import com.planapp.qplanzaso.ui.screens.onboarding.LocationPermissionScreen
import com.planapp.qplanzaso.ui.screens.auth.*
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.*

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        // Onboarding
        composable("splash") { SplashScreen(navController) }
        composable("location_permission") { LocationPermissionScreen(navController) }

        // Auth
        composable("account_choice") { AccountChoiceScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("organizador") { Organizador(navController) }
        composable("TipoOrganizadorScreen") { TipoOrganizadorScreen(navController) }
        composable("MoreInfoScreen") { MoreInfoScreen(navController) }
        composable("RegisterScreen") { RegisterScreen(navController) }

        // Home
        composable("home") { HomeScreen(navController) }

        // Event by category (2 argumentos)
        composable(
            route = "EventByCategory/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryName") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName")

            EventByCategory(
                navController = navController,
                categoryId = categoryId,
                categoryName = categoryName
            )
        }

        // Detail Event (1 argumento JSON)
        composable(
            route = "detailEvent/{encodedJson}",
            arguments = listOf(navArgument("encodedJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            DetailEvent(navController = navController, encodedJson = encodedJson)
        }

        composable("NewEventScreen") { NewEventScreen(navController) }
        composable("EventSummaryScreen") { EventSummaryScreen(navController = navController)}
    }
}