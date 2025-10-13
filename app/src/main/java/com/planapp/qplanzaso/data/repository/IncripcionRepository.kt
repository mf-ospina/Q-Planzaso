package com.planapp.qplanzaso.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.planapp.qplanzaso.model.Evento
import kotlinx.coroutines.tasks.await

class InscripcionRepository {

    private val db = Firebase.firestore

    // 🔹 Inscribirse en un evento
    suspend fun inscribirseEnEvento(eventoId: String, usuarioId: String) {
        val data = mapOf(
            "usuarioId" to usuarioId,
            "fechaInscripcion" to Timestamp.now()
        )

        // Subcolección dentro del evento
        db.collection("eventos").document(eventoId)
            .collection("inscritos").document(usuarioId)
            .set(data, SetOptions.merge()).await()

        // Añadir usuario al campo inscritosIds del evento
        db.collection("eventos").document(eventoId)
            .update("inscritosIds", FieldValue.arrayUnion(usuarioId)).await()
    }

    // 🔹 Cancelar inscripción
    suspend fun cancelarInscripcion(eventoId: String, usuarioId: String) {
        db.collection("eventos").document(eventoId)
            .collection("inscritos").document(usuarioId)
            .delete().await()

        db.collection("eventos").document(eventoId)
            .update("inscritosIds", FieldValue.arrayRemove(usuarioId)).await()
    }

    // 🔹 Obtener todos los eventos donde el usuario está inscrito
    suspend fun obtenerEventosInscritos(usuarioId: String): List<Evento> {
        val snapshot = db.collection("eventos")
            .whereArrayContains("inscritosIds", usuarioId)
            .get().await()
        return snapshot.toObjects(Evento::class.java)
    }
}
