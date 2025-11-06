/*package com.planapp.qplanzaso.work

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.utils.NotificationHelper

class TestWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val context = applicationContext

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // asegúrate que existe
            .setContentTitle("TestWorker ejecutado")
            .setContentText("Si lees esto, WorkManager está funcionando ✅")
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(7777, notification)

        return Result.success()
    }
}
*/