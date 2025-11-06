// ui/screens/profile/NotificationSettingsEntry.kt
package com.planapp.qplanzaso.ui.screens.profile
import com.planapp.qplanzaso.work.DailySummaryScheduler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.planapp.qplanzaso.ui.screens.profile.NotificationSettingsViewModel
import com.planapp.qplanzaso.utils.NotificationHelper

@Composable
fun NotificationSettingsEntry(navController: NavController) {
    val ctx = LocalContext.current
    val vm: NotificationSettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationSettingsViewModel(
                    _root_ide_package_.com.planapp.qplanzaso.ui.screens.profile.NotificationPrefsRepository(
                        ctx
                    )
                ) as T
            }
        }
    )
    val s by vm.state.collectAsState()

    NotificationSettingsScreen(
        navController = navController,
        state = s,
        onChange = vm::update,                    // <- clave
        onSave = { vm.save {
            // 🔹 Programar / cancelar el resumen diario según las prefs guardadas
            NotificationHelper.createNotificationChannel(ctx)
            DailySummaryScheduler.scheduleFromPrefs(ctx)
            navController.popBackStack() }
    })
}
