package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.CalendarioEvento
import kotlinx.coroutines.tasks.await

class CalendarioRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun guardarEventoCalendario(usuarioId: String, evento: CalendarioEvento) {
        db.collection("usuarios").document(usuarioId)
            .collection("calendario").document(evento.id!!)
            .set(evento).await()
    }

    suspend fun obtenerEventosCalendario(usuarioId: String): List<CalendarioEvento> {
        val snapshot = db.collection("usuarios").document(usuarioId)
            .collection("calendario").get().await()
        return snapshot.toObjects(CalendarioEvento::class.java)
    }
}
