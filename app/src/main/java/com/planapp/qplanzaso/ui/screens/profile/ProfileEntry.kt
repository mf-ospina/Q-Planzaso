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
        ?: run { navController.navigate("login"); return }

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
        onBack = { navController.navigate("home?tab=home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
        onEdit = { form ->
            navController.currentBackStackEntry?.savedStateHandle?.set("profile_form", form)
            navController.navigate("edit_profile")
        },
        onOpenSettings = { navController.navigate("settings") }
    )
}
