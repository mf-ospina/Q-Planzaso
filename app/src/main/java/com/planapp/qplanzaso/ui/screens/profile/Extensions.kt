// ui/common/Extensions.kt
package com.planapp.qplanzaso.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StateFlow<NotificationPrefs>.rememberPrefs(): NotificationPrefs {
    val s by collectAsState(initial = NotificationPrefs())
    return s
}

fun NotificationSettingsViewModel.saveAndPop(navController: NavController) {
    save { navController.popBackStack() }
}
