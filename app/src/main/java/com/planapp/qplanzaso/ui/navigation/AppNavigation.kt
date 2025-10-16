package com.planapp.qplanzaso.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.auth.AccountChoiceScreen
import com.planapp.qplanzaso.ui.screens.auth.ForgotPasswordScreen
import com.planapp.qplanzaso.ui.screens.auth.LoginScreen
import com.planapp.qplanzaso.ui.screens.auth.MoreInfoScreen
import com.planapp.qplanzaso.ui.screens.auth.Organizador
import com.planapp.qplanzaso.ui.screens.auth.RegisterScreen
import com.planapp.qplanzaso.ui.screens.auth.TipoOrganizadorScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.DetailEvent
import com.planapp.qplanzaso.ui.screens.onboarding.LocationPermissionScreen
import com.planapp.qplanzaso.ui.screens.onboarding.SplashScreen
import com.planapp.qplanzaso.ui.screens.profile.EditProfileEntry
import com.planapp.qplanzaso.ui.screens.profile.NotificationSettingsEntry
import com.planapp.qplanzaso.ui.screens.profile.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
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
        composable("splash") { SplashScreen(navController) }
        composable("location_permission") { LocationPermissionScreen(navController) }

        composable("home") { HomeScreen(navController = navController) }
        composable("home?tab={tab}") { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab")
            HomeScreen(navController = navController, startTab = tab)
        }

        composable("account_choice") { AccountChoiceScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("organizador") { Organizador(navController) }
        composable("TipoOrganizadorScreen") { TipoOrganizadorScreen(navController) }
        composable("MoreInfoScreen") { MoreInfoScreen(navController) }
        composable("RegisterScreen") { RegisterScreen(navController) }

        composable("DetailEvent/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            DetailEvent(navController, id)
        }

        // Editar perfil con VM + Firestore
        composable("edit_profile") { EditProfileEntry(navController) }

        composable("settings") { SettingsScreen(navController = navController) }
        composable("notif_settings") { NotificationSettingsEntry(navController) }
    }
}
