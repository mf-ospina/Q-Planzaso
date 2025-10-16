// ui/screens/profile/NotificationSettingsEntry.kt
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
import androidx.compose.ui.platform.LocalContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationSettingsEntry(navController: NavController) {
    val ctx = LocalContext.current
    val vm: NotificationSettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationSettingsViewModel(NotificationPrefsRepository(ctx)) as T
            }
        }
    )
    val s by vm.state.collectAsState()

    NotificationSettingsScreen(
        navController = navController,
        state = s,
        onChange = vm::update,                    // <- clave
        onSave = { vm.save { navController.popBackStack() } }
    )
}
