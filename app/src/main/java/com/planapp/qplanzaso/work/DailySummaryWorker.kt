package com.planapp.qplanzaso.work

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.data.repository.InscripcionRepository
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.screens.profile.NotificationPrefsRepository
import com.planapp.qplanzaso.utils.NotificationHelper
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Locale

class DailySummaryWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val UNIQUE_NAME = "daily_summary_work"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val context = applicationContext

        // 1) Usuario logueado
        val user = FirebaseAuth.getInstance().currentUser ?: return Result.success()
        val userId = user.uid

        // 2) Preferencias
        val prefsRepo = NotificationPrefsRepository(context)
        val prefs = prefsRepo.data.first()

        // Si no quiere resumen / push / recordatorios → nada
        if (!prefs.dailySummary || !prefs.push || !prefs.reminders) {
            return Result.success()
        }

        // 3) Eventos inscritos
        val inscripcionRepo = InscripcionRepository()
        val eventos = try {
            inscripcionRepo.obtenerEventosInscritos(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }

        val ahora = Timestamp.now()
        val futuros = eventos.filter { it.fechaInicio != null && it.fechaInicio!! > ahora }

        if (futuros.isEmpty()) {
            NotificationHelper.showDailySummary(
                context = context,
                body = "No tienes eventos próximos. ¡Únete a uno!"
            )
            return Result.success()
        }

        // 4) Armar resumen
        val body = buildResumen(futuros)

        // 5) Notificación usando el helper “bueno”
        NotificationHelper.showDailySummary(
            context = context,
            body = body
        )

        return Result.success()
    }

    private fun buildResumen(eventos: List<Evento>): String {
        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

        return if (eventos.size == 1) {
            val e = eventos.first()
            val fecha = e.fechaInicio?.toDate()?.let { sdf.format(it) } ?: ""
            "Tienes 1 evento pendiente: ${e.nombre} $fecha"
        } else {
            val destacados = eventos
                .sortedBy { it.fechaInicio?.seconds ?: Long.MAX_VALUE }
                .take(3)
                .joinToString("\n") { e ->
                    val fecha = e.fechaInicio?.toDate()?.let { sdf.format(it) } ?: ""
                    "• ${e.nombre} $fecha"
                }

            "Tienes ${eventos.size} eventos próximos:\n$destacados"
        }
    }
}
