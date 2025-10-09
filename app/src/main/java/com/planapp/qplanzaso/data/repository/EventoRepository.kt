package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.planapp.qplanzaso.model.Evento
import kotlinx.coroutines.tasks.await

class EventoRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearEvento(evento: Evento): String {
        val docRef = db.collection("eventos").document()
        evento.id = docRef.id
        docRef.set(evento, SetOptions.merge()).await()
        return docRef.id
    }

    suspend fun obtenerEvento(id: String): Evento? {
        val snapshot = db.collection("eventos").document(id).get().await()
        return snapshot.toObject(Evento::class.java)
    }

    suspend fun obtenerEventos(): List<Evento> {
        val snapshot = db.collection("eventos").get().await()
        return snapshot.toObjects(Evento::class.java)
    }

    suspend fun actualizarEvento(evento: Evento) {
        db.collection("eventos").document(evento.id!!).set(evento, SetOptions.merge()).await()
    }

    suspend fun eliminarEvento(id: String) {
        db.collection("eventos").document(id).delete().await()
    }

    // Subcolecciones
    suspend fun agregarAsistente(eventoId: String, usuarioId: String) {
        db.collection("eventos").document(eventoId)
            .collection("asistentes").document(usuarioId)
            .set(mapOf("asistio" to false)).await()
    }

    suspend fun agregarFavorito(eventoId: String, usuarioId: String) {
        db.collection("eventos").document(eventoId)
            .collection("favoritos").document(usuarioId)
            .set(mapOf("fecha" to System.currentTimeMillis())).await()
    }
}
