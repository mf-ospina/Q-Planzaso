// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/NotificationPrefsRepository.kt
package com.planapp.qplanzaso.ui.screens.profile

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(name = "notification_prefs")

private object Keys {
    val PUSH = booleanPreferencesKey("push")
    val EMAIL = booleanPreferencesKey("email")
    val VIBRATE = booleanPreferencesKey("vibrate")
    val SOUND = booleanPreferencesKey("sound")
    val PRIORITY_HIGH = booleanPreferencesKey("priority_high")
    val DAILY_SUMMARY = booleanPreferencesKey("daily_summary")
    val SUMMARY_TIME = stringPreferencesKey("summary_time")
    val NEAR_EVENTS = booleanPreferencesKey("near_events")
    val REMINDERS = booleanPreferencesKey("reminders")
}

class NotificationPrefsRepository(private val context: Context) {

    // Mantén el nombre 'data' para que coincida con tu ViewModel
    val data: Flow<NotificationPrefs> =
        context.notificationDataStore.data.map { p: Preferences ->
            NotificationPrefs(
                push         = p[Keys.PUSH] ?: true,
                email        = p[Keys.EMAIL] ?: false,
                vibrate      = p[Keys.VIBRATE] ?: true,
                sound        = p[Keys.SOUND] ?: true,
                priorityHigh = p[Keys.PRIORITY_HIGH] ?: true,
                dailySummary = p[Keys.DAILY_SUMMARY] ?: false,
                summaryTime  = p[Keys.SUMMARY_TIME] ?: "08:30",
                nearEvents   = p[Keys.NEAR_EVENTS] ?: true,
                reminders    = p[Keys.REMINDERS] ?: true
            )
        }

    suspend fun save(prefs: NotificationPrefs) {
        context.notificationDataStore.edit { e ->
            e[Keys.PUSH]          = prefs.push
            e[Keys.EMAIL]         = prefs.email
            e[Keys.VIBRATE]       = prefs.vibrate
            e[Keys.SOUND]         = prefs.sound
            e[Keys.PRIORITY_HIGH] = prefs.priorityHigh
            e[Keys.DAILY_SUMMARY] = prefs.dailySummary
            e[Keys.SUMMARY_TIME]  = prefs.summaryTime
            e[Keys.NEAR_EVENTS]   = prefs.nearEvents
            e[Keys.REMINDERS]     = prefs.reminders
        }
    }

    suspend fun set(transform: (NotificationPrefs) -> NotificationPrefs) {
        val current = data.first()
        save(transform(current))
    }
}
