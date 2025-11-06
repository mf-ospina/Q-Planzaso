package com.planapp.qplanzaso.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.planapp.qplanzaso.ui.screens.profile.NotificationPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object DailySummaryScheduler {

    fun scheduleFromPrefs(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val prefsRepo = NotificationPrefsRepository(context)
            val prefs = prefsRepo.data.first()

            // Si no quiere resumen → cancelamos
            if (!prefs.dailySummary || !prefs.push || !prefs.reminders) {
                WorkManager.getInstance(context)
                    .cancelUniqueWork(DailySummaryWorker.UNIQUE_NAME)
                return@launch
            }

            val delayMillis = calculateDelayMillis(prefs.summaryTime)

            val request = OneTimeWorkRequestBuilder<DailySummaryWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                DailySummaryWorker.UNIQUE_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    private fun calculateDelayMillis(summaryTime: String): Long {
        val parts = summaryTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30

        val now = LocalDateTime.now()
        val targetToday = now.toLocalDate().atTime(LocalTime.of(hour, minute))

        val next = if (targetToday.isAfter(now)) {
            targetToday
        } else {
            targetToday.plusDays(1)
        }

        val zone = ZoneId.systemDefault()
        val nowMillis = now.atZone(zone).toInstant().toEpochMilli()
        val targetMillis = next.atZone(zone).toInstant().toEpochMilli()

        return targetMillis - nowMillis
    }
}
