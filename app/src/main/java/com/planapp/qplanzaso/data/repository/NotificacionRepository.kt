package com.planapp.qplanzaso.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.Notificacion
import com.planapp.qplanzaso.model.TipoNotificacion
import kotlinx.coroutines.tasks.await

class NotificacionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val coleccion = db.collection("notificaciones")

    /**
     * Agregar o crear una notificación con ID generado automáticamente si está vacío
     */
    suspend fun agregarNotificacion(notificacion: Notificacion) {
        try {
            val docRef = if (notificacion.id.isBlank()) coleccion.document() else coleccion.document(notificacion.id)
            notificacion.id = docRef.id
            docRef.set(notificacion).await()
        } catch (e: Exception) {
            println("❌ Error agregando notificación: ${e.message}")
            throw e
        }
    }

    /**
     * Obtener notificaciones de un usuario, ordenadas por fecha descendente
     */
    suspend fun obtenerNotificacionesUsuario(usuarioId: String): List<Notificacion> {
        return try {
            val snapshot = coleccion
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .await()
            snapshot.toObjects(Notificacion::class.java)
                .sortedByDescending { it.fecha?.toDate() } // orden descendente seguro
        } catch (e: Exception) {
            println("❌ Error obteniendo notificaciones: ${e.message}")
            emptyList()
        }
    }

    /**
     * Marcar como leída
     */
    suspend fun marcarComoLeida(id: String) {
        try {
            coleccion.document(id)
                .update("leida", true)
                .await()
        } catch (e: Exception) {
            println("❌ Error al marcar notificación como leída: ${e.message}")
            throw e
        }
    }

    /**
     * Eliminar notificación
     */
    suspend fun eliminarNotificacion(id: String) {
        try {
            coleccion.document(id).delete().await()
        } catch (e: Exception) {
            println("❌ Error al eliminar notificación: ${e.message}")
            throw e
        }
    }

    /**
     * Enviar una notificación individual
     */
    suspend fun enviarNotificacion(
        usuarioId: String,
        titulo: String,
        mensaje: String,
        tipo: TipoNotificacion
    ) {
        try {
            val notificacion = Notificacion(
                id = "",
                usuarioId = usuarioId,
                titulo = titulo,
                mensaje = mensaje,
                fecha = Timestamp.now(),
                tipo = tipo,
                leida = false
            )
            agregarNotificacion(notificacion)
        } catch (e: Exception) {
            println("❌ Error enviando notificación: ${e.message}")
            throw e
        }
    }

    /**
     * Enviar notificaciones masivas
     */
    suspend fun enviarNotificacionMasiva(
        usuariosIds: List<String>,
        titulo: String,
        mensaje: String,
        tipo: TipoNotificacion
    ) {
        val batch = db.batch()
        val ahora = Timestamp.now()

        usuariosIds.forEach { userId ->
            val docRef = coleccion.document()
            val notificacion = Notificacion(
                id = docRef.id,
                usuarioId = userId,
                titulo = titulo,
                mensaje = mensaje,
                fecha = ahora,
                tipo = tipo,
                leida = false
            )
            batch.set(docRef, notificacion)
        }

        try {
            batch.commit().await()
            println("✅ Notificaciones enviadas correctamente a ${usuariosIds.size} usuarios.")
        } catch (e: Exception) {
            println("❌ Error enviando notificaciones masivas: ${e.message}")
            throw e
        }
    }
}
