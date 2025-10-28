package com.planapp.qplanzaso.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.auth.AccountChoiceScreen
import com.planapp.qplanzaso.ui.screens.auth.ForgotPasswordScreen
import com.planapp.qplanzaso.ui.screens.auth.LoginScreen
import com.planapp.qplanzaso.ui.screens.auth.MoreInfoScreen
import com.planapp.qplanzaso.ui.screens.auth.Organizador
import com.planapp.qplanzaso.ui.screens.auth.RegisterScreen
import com.planapp.qplanzaso.ui.screens.auth.TipoOrganizadorScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.DetailEvent
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.EventByCategory
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.EventSummaryScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.NewEventScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.SelectorUbicacionMapa
import com.planapp.qplanzaso.ui.screens.onboarding.LocationPermissionScreen
import com.planapp.qplanzaso.ui.screens.onboarding.SplashScreen
import com.planapp.qplanzaso.ui.screens.profile.EditProfileEntry
import com.planapp.qplanzaso.ui.screens.profile.NotificationSettingsEntry
import com.planapp.qplanzaso.ui.screens.profile.SettingsScreen
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // VM compartido para creación de eventos, como en tu versión original
    val eventoViewModel: EventoViewModel = viewModel()

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

        // Home con soporte de tab inicial (calendar/home/profile)
        composable("home") { HomeScreen(navController = navController) }
        composable("home?tab={tab}") { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab")
            HomeScreen(navController = navController, startTab = tab)
        }

        // Eventos por categoría (2 argumentos)
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

        // Detail Event (versión con ID simple)
        composable(
            route = "detailEvent/{encodedJson}",
            arguments = listOf(navArgument("encodedJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            DetailEvent(navController = navController, encodedJson = encodedJson)
        }

        // Detail Event (versión con JSON codificado, por compatibilidad)
        composable(
            route = "detailEvent/{encodedJson}",
            arguments = listOf(navArgument("encodedJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            DetailEvent(navController = navController, encodedJson = encodedJson)
        }

        // Crear y resumen de eventos
        composable("NewEventScreen") {
            NewEventScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }
        composable("EventSummaryScreen") {
            EventSummaryScreen(
                navController = navController,
                viewModel = eventoViewModel
            )
        }

        // Selector de ubicación en mapa
        composable("selector_ubicacion") {
            SelectorUbicacionMapa(navController)
        }

        // Perfil / Ajustes
        composable("edit_profile") { EditProfileEntry(navController) }
        composable("settings") { SettingsScreen(navController = navController) }
        composable("notif_settings") { NotificationSettingsEntry(navController) }
    }
}
