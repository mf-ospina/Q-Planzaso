package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.Notificacion
import kotlinx.coroutines.tasks.await

class NotificacionRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearNotificacion(notificacion: Notificacion) {
        db.collection("notificaciones").add(notificacion).await()
    }

    suspend fun obtenerNotificacionesUsuario(usuarioId: String): List<Notificacion> {
        val snapshot = db.collection("notificaciones")
            .whereEqualTo("usuarioId", usuarioId).get().await()
        return snapshot.toObjects(Notificacion::class.java)
    }

    suspend fun marcarComoLeida(id: String) {
        db.collection("notificaciones").document(id)
            .update("leida", true).await()
    }
}
