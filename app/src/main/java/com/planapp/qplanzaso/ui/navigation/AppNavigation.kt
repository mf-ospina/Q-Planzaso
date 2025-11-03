package com.planapp.qplanzaso.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.planapp.qplanzaso.model.EventFormData
import com.planapp.qplanzaso.model.Evento
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.ui.screens.onboarding.SplashScreen
import com.planapp.qplanzaso.ui.screens.onboarding.LocationPermissionScreen
import com.planapp.qplanzaso.ui.screens.auth.*
import com.planapp.qplanzaso.ui.screens.HomeScreen
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.*
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.favorites.FavoritosScreen
import com.planapp.qplanzaso.ui.viewModel.CalendarioViewModel
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val eventoViewModel: EventoViewModel = viewModel()
    val calendarioViewModel: CalendarioViewModel = viewModel()


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
        composable("home") {
            HomeScreen(navController = navController, calendarioViewModel = calendarioViewModel)
        }



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

        composable("selector_ubicacion") {
            SelectorUbicacionMapa(navController)
        }

        composable(
            route = "EditEventScreen/{encodedJson}",
            arguments = listOf(navArgument("encodedJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("encodedJson")
            EditEventScreen(navController = navController, encodedJson = encodedJson)
        }



        composable("favoritos") {
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            FavoritosScreen(navController = navController, viewModel = eventoViewModel, usuarioId = usuarioId)
        }


        // Asumiendo que tienes una función para definir tus rutas

        composable(route = "all_comments_screen/{eventoId}",
            arguments = listOf(navArgument("eventoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getString("eventoId") ?: return@composable
            AllCommentsScreen(navController = navController, eventoId = eventoId)
        }


    }
}
