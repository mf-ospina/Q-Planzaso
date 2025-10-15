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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.auth.RegisterScreen
import com.planapp.qplanzaso.ui.screens.auth.TipoOrganizadorScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.DetailEvent


@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier // 👈 aquí aplicas el padding del Scaffold
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

        composable("DetailEvent/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            DetailEvent(navController, id)
        }
    }
}

