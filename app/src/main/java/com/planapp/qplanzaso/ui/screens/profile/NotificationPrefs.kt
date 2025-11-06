package com.planapp.qplanzaso.ui.screens.profile

data class NotificationPrefs(
    val push: Boolean = true,
    val email: Boolean = false,
    val vibrate: Boolean = true,
    val sound: Boolean = true,
    val priorityHigh: Boolean = true,
    val dailySummary: Boolean = false,
    val summaryTime: String = "08:30",
    val nearEvents: Boolean = true,
    val reminders: Boolean = true
)
