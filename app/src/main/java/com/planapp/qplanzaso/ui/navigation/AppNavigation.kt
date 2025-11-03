package com.planapp.qplanzaso.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.auth.AuthViewModel
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.auth.*
import com.planapp.qplanzaso.ui.screens.onboarding.LocationPermissionScreen
import com.planapp.qplanzaso.ui.screens.onboarding.SplashScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.*
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.favorites.FavoritosScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.notifications.NotificacionesScreen
import com.planapp.qplanzaso.ui.viewModel.CalendarioViewModel
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import com.planapp.qplanzaso.ui.viewModel.NotificacionViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    screenToOpen: String? = null
) {
    val eventoViewModel: EventoViewModel = viewModel()
    val calendarioViewModel: CalendarioViewModel = viewModel()
    val notificacionViewModel: NotificacionViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val startDestination = when (screenToOpen) {
        "notificaciones" -> "notificaciones"
        else -> "splash"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // Splash & Onboarding
        composable("splash") {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate("location_permission") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("location_permission") { LocationPermissionScreen(navController) }
        composable("account_choice") { AccountChoiceScreen(navController) }
        composable("login") { LoginScreen(navController = navController, authViewModel = authViewModel) }
        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("organizador") { Organizador(navController) }
        composable("TipoOrganizadorScreen") { TipoOrganizadorScreen(navController) }
        composable("MoreInfoScreen") { MoreInfoScreen(navController) }
        composable("RegisterScreen") { RegisterScreen(navController) }

        // Home con bottom navigation
        composable("home") {
            HomeScreen(
                navController = navController,
                calendarioViewModel = calendarioViewModel
            )
        }

        // Eventos por categoría
        composable(
            "EventByCategory/{categoryId}/{categoryName}",
            arguments = listOf(
                androidx.navigation.navArgument("categoryId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("categoryName") { type = androidx.navigation.NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            EventByCategory(navController = navController, categoryId = categoryId, categoryName = categoryName)
        }

        // Detalle de evento
        composable(
            "detailEvent/{encodedJson}",
            arguments = listOf(androidx.navigation.navArgument("encodedJson") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            DetailEvent(navController = navController, encodedJson = encodedJson)
        }

        // Crear y editar eventos
        composable("NewEventScreen") { NewEventScreen(navController = navController, viewModel = eventoViewModel) }
        composable("EventSummaryScreen") { EventSummaryScreen(navController = navController, viewModel = eventoViewModel) }
        composable("selector_ubicacion") { SelectorUbicacionMapa(navController) }
        composable(
            "EditEventScreen/{encodedJson}",
            arguments = listOf(androidx.navigation.navArgument("encodedJson") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            EditEventScreen(navController = navController, encodedJson = encodedJson)
        }

        // Favoritos
        composable("favoritos") {
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            FavoritosScreen(navController, viewModel = eventoViewModel, usuarioId = usuarioId)
        }

        // Notificaciones
        composable("notificaciones") {
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
            if (usuarioId != null) {
                NotificacionesScreen(navController = navController, viewModel = notificacionViewModel)
            } else {
                // Si no hay usuario logueado, redirigimos a login
                navController.navigate("login") { popUpTo("notificaciones") { inclusive = true } }
            }
        }
    }
}
