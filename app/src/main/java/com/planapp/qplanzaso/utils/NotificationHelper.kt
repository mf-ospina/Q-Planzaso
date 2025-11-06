package com.planapp.qplanzaso.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.planapp.qplanzaso.MainActivity
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.screens.profile.NotificationPrefsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object NotificationHelper {

    const val CHANNEL_ID = "event_reminders_channel"
    private const val CHANNEL_NAME = "Recordatorios de eventos"
    private const val CHANNEL_DESC = "Notificaciones cuando te inscribes a eventos o tienes recordatorios"

    // Llamar al menos una vez (MainActivity.onCreate)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 👇 leemos las preferencias de notificación
            val prefs = runBlocking {
                NotificationPrefsRepository(context).data.first()
            }

            val importance = if (prefs.priorityHigh) {
                NotificationManager.IMPORTANCE_HIGH
            } else {
                NotificationManager.IMPORTANCE_DEFAULT
            }

            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC

                // 🔹 Permitimos vibración en el canal
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 200, 250)

                // 🔔 sonido (opcional, si quieres que respete el toggle)
                if (!prefs.sound) {
                    setSound(null, null)
                }

                // 📳 vibración
                enableVibration(prefs.vibrate)
                vibrationPattern = if (prefs.vibrate) {
                    longArrayOf(0, 250, 200, 250)
                } else {
                    longArrayOf(0L)
                }
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun showInscripcionNotification(
        context: Context,
        tituloEvento: String,
        direccion: String?
    ) {
        // 🔐 Android 13+: comprobar permiso antes de notificar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                // No hay permiso → salimos en silencio para no crashear
                return
            }
        }

        // 1️⃣ Leer preferencias de notificación
        val prefs = runBlocking {
            NotificationPrefsRepository(context).data.first()
        }

        // Si el usuario desactivó push o recordatorios, no mostramos nada
        if (!prefs.push || !prefs.reminders) {
            return
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val contentText = if (!direccion.isNullOrBlank()) {
            "Te inscribiste a $tituloEvento en $direccion"
        } else {
            "Te inscribiste a $tituloEvento"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)   // 👈 icono existente
            .setContentTitle("Inscripción confirmada")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 2️⃣ Aplicar sonido
        if (!prefs.sound) {
            builder.setSound(null)   // sin sonido
        }

        // 3️⃣ Aplicar vibración
        if (prefs.vibrate) {
            builder.setVibrate(longArrayOf(0, 250, 200, 250))
        } else {
            builder.setVibrate(longArrayOf(0L))
        }

        // 4️⃣ Prioridad
        builder.priority = if (prefs.priorityHigh) {
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
    fun showDailySummary(
        context: Context,
        body: String
    ) {
        // 🔐 Android 13+: comprobar permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return
        }

        // 1️⃣ Leer preferencias de notificación
        val prefs = runBlocking {
            NotificationPrefsRepository(context).data.first()
        }

        // Abrir la app al tocar la noti
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Resumen diario de tus eventos")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 2️⃣ Aplicar sonido
        if (!prefs.sound) {
            builder.setSound(null)   // sin sonido
        }

        // 3️⃣ Aplicar vibración
        if (prefs.vibrate) {
            builder.setVibrate(longArrayOf(0, 250, 200, 250))
        } else {
            builder.setVibrate(longArrayOf(0L))
        }

        // 4️⃣ Prioridad
        builder.priority = if (prefs.priorityHigh) {
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        with(NotificationManagerCompat.from(context)) {
            notify(2001, builder.build())
        }
    }
}

