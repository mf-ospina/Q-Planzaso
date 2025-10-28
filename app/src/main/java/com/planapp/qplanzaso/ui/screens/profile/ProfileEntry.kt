// ui/screens/profile/ProfileEntry.kt
package com.planapp.qplanzaso.ui.screens.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileEntry(
    navController: NavController
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
        ?: run {
            // Si no hay sesión, manda a login y corta la composición
            navController.navigate("login") {
                launchSingleTop = true
            }
            return
        }

    val vm: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = ProfileRepository(uid, FirebaseFirestore.getInstance())
                return ProfileViewModel(repo) as T
            }
        }
    )

    val state by vm.state.collectAsState()

    Profile(
        state = state,

        // Volver: si tu Home soporta tab param, puedes ir directo al tab de perfil
        onBack = {
            navController.navigate("home?tab=profile") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        },

        // Editar perfil
        onEdit = { form ->
            // Pasamos el form al entry de edición por SavedStateHandle
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("profile_form", form)

            navController.navigate("edit_profile") {
                launchSingleTop = true
            }
        },

        // Abrir configuración
        onOpenSettings = {
            navController.navigate("settings") {
                launchSingleTop = true
            }
        },

        // ✅ Clave: navegación a NewEventScreen (la ruta debe existir en tu NavHost)
        onCreateEvent = {
            navController.navigate("NewEventScreen") {
                launchSingleTop = true
            }
        }
    )
}
