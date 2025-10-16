// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/EditProfileEntry.kt
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
fun EditProfileEntry(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
        ?: run { navController.popBackStack(); return }

    val vm: EditProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = ProfileRepository(uid, FirebaseFirestore.getInstance())
                return EditProfileViewModel(repo) as T
            }
        }
    )

    val state by vm.state.collectAsState()

    EditProfileScreen(
        state = state,
        onChange = vm::update,                  // recibe ProfileFormState y lo pone en VM
        onSave = { vm.save { navController.popBackStack() } },
        onBack = { navController.popBackStack() },
        onOpenSettings = { navController.navigate("settings") }
    )
}
