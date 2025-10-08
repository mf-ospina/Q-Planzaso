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
@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier // 👈 aquí aplicas el padding del Scaffold
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("location_permission") { LocationPermissionScreen(navController) }
        //composable("home") { HomeScreen(navController) }
        composable("account_choice") { AccountChoiceScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }
    }
}
