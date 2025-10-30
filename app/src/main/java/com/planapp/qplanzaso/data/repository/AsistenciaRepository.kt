package com.planapp.qplanzaso.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.tasks.await

class AsistenciaRepository {

    private val db = Firebase.firestore

    // Registrar asistencia real (check-in)
    suspend fun registrarAsistencia(eventoId: String, usuarioId: String, lat: Double?, lon: Double?) {
        val data = mutableMapOf<String, Any>(
            "usuarioId" to usuarioId,
            "fechaAsistencia" to Timestamp.now(),
            "verificada" to true
        )

        if (lat != null && lon != null) {
            data["latitud"] = lat
            data["longitud"] = lon
        }

        db.collection("evento").document(eventoId)
            .collection("asistencias").document(usuarioId)
            .set(data, SetOptions.merge()).await()

        // Actualiza contador en el documento del evento
        db.collection("evento").document(eventoId)
            .update("asistentesCount", FieldValue.increment(1)).await()
    }

    // Verificar si un usuario ya asistió
    suspend fun verificarAsistencia(eventoId: String, usuarioId: String): Boolean {
        val doc = db.collection("evento").document(eventoId)
            .collection("asistencias").document(usuarioId)
            .get().await()
        return doc.exists()
    }

    // Obtener todos los asistentes
    suspend fun obtenerAsistentes(eventoId: String): List<String> {
        val snapshot = db.collection("evento").document(eventoId)
            .collection("asistencias").get().await()
        return snapshot.documents.mapNotNull { it.getString("usuarioId") }
    }
}
