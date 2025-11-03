package com.planapp.qplanzaso.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.planapp.qplanzaso.model.Evento
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Repositorio encargado de manejar la lógica de inscripción del usuario en eventos.
 * - Gestiona subcolección "inscritos" dentro de cada evento.
 * - Actualiza campo array `inscritosIds` del evento.
 * - Devuelve eventos inscritos listos para el módulo calendario.
 */
class InscripcionRepository {

    private val db = Firebase.firestore

    // 🔹 Inscribirse en un evento
    suspend fun inscribirseEnEvento(eventoId: String, usuarioId: String) = withContext(Dispatchers.IO) {
        try {
            val data = mapOf(
                "usuarioId" to usuarioId,
                "fechaInscripcion" to Timestamp.now()
            )

            val eventoRef = db.collection("evento").document(eventoId)

            // Subcolección: inscrito
            eventoRef.collection("inscritos").document(usuarioId)
                .set(data, SetOptions.merge()).await()

            // Actualizar lista de inscritosIds
            eventoRef.update("inscritosIds", FieldValue.arrayUnion(usuarioId)).await()

        } catch (e: Exception) {
            throw Exception("Error al inscribirse: ${e.localizedMessage}", e)
        }
    }

    // 🔹 Cancelar inscripción
    suspend fun cancelarInscripcion(eventoId: String, usuarioId: String) = withContext(Dispatchers.IO) {
        try {
            val eventoRef = db.collection("evento").document(eventoId)

            // Eliminar de la subcolección
            eventoRef.collection("inscritos").document(usuarioId).delete().await()

            // Remover del array principal
            eventoRef.update("inscritosIds", FieldValue.arrayRemove(usuarioId)).await()

        } catch (e: Exception) {
            throw Exception("Error al cancelar inscripción: ${e.localizedMessage}", e)
        }
    }

    // 🔹 Obtener todos los eventos donde el usuario está inscrito
    suspend fun obtenerEventosInscritos(usuarioId: String): List<Evento> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("evento")
                .whereArrayContains("inscritosIds", usuarioId)
                .get()
                .await()

            // Convertir documentos a objetos Evento, incluyendo el id del documento
            val eventos = snapshot.documents.mapNotNull { doc ->
                val evento = doc.toObject(Evento::class.java)
                evento?.copy(
                    id = doc.id,
                    fechaInicio = evento.fechaInicio ?: Timestamp(Date()), // evita null
                    fechaFin = evento.fechaFin ?: evento.fechaInicio ?: Timestamp(Date())
                )
            }

            // Ordenar eventos por fecha de inicio ascendente
            return@withContext eventos.sortedBy { it.fechaInicio?.toDate()?.time ?: 0L }

        } catch (e: Exception) {
            throw Exception("Error al obtener eventos inscritos: ${e.localizedMessage}", e)
        }
    }

    suspend fun obtenerUsuariosInscritos(eventoId: String): List<String> {
        val snapshot = db.collection("evento")
            .document(eventoId)
            .collection("inscritos")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.id }
    }

}
