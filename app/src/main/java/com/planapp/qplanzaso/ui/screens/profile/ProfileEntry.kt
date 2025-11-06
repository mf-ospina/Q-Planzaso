// ui/screens/profile/ProfileEntry.kt
package com.planapp.qplanzaso.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileEntry(
    navController: NavController
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
        ?: run {
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

    // ✅ Enlaza con tu perfil principal ya funcional
    com.planapp.qplanzaso.ui.screens.bottomNavigationMod.Profile(
        navController = navController
    )
}
