package com.planapp.qplanzaso.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.planapp.qplanzaso.ui.screens.onboarding.SplashScreen
import com.planapp.qplanzaso.ui.screens.onboarding.LocationPermissionScreen
import com.planapp.qplanzaso.ui.screens.auth.LoginScreen
import com.planapp.qplanzaso.ui.screens.auth.AccountChoiceScreen
import com.planapp.qplanzaso.ui.screens.auth.ForgotPasswordScreen
import com.planapp.qplanzaso.ui.screens.auth.MoreInfoScreen
import com.planapp.qplanzaso.ui.screens.auth.Organizador
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.auth.RegisterScreen
import com.planapp.qplanzaso.ui.screens.auth.TipoOrganizadorScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.DetailEvent
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.EventByCategory
@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("location_permission") { LocationPermissionScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("account_choice") { AccountChoiceScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }

        composable("organizador") { Organizador(navController) }
        composable("TipoOrganizadorScreen") { TipoOrganizadorScreen(navController) }
        composable("MoreInfoScreen") { MoreInfoScreen(navController)  }

        composable("RegisterScreen") { RegisterScreen(navController) }

        // ✅ RUTA CORREGIDA (DOS ARGUMENTOS)
        composable(
            route = "EventByCategory/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryName") {
                    type = NavType.StringType
                    nullable = true // Es buena práctica si no siempre lo usas, aunque aquí sí
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName")

            // Asegúrate de que categoryId no sea nulo antes de usarlo
            EventByCategory(
                navController = navController,
                categoryId = categoryId,
                categoryName = categoryName
            )
        }

        composable(
            route = "detailEvent/{encodedJson}",
            arguments = listOf(navArgument("encodedJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            DetailEvent(navController = navController, encodedJson = encodedJson)
        }

    }
}

