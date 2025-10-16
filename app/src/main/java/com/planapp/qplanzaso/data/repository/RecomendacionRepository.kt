package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.RecomendacionIA
import kotlinx.coroutines.tasks.await

class RecomendacionRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun guardarRecomendacion(recomendacion: RecomendacionIA) {
        db.collection("recomendacionesIA").add(recomendacion).await()
    }

    suspend fun obtenerRecomendacionesUsuario(usuarioId: String): List<RecomendacionIA> {
        val snapshot = db.collection("recomendacionesIA")
            .whereEqualTo("usuarioId", usuarioId).get().await()
        return snapshot.toObjects(RecomendacionIA::class.java)
    }
}
