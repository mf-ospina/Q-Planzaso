package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.planapp.qplanzaso.model.Vibra
import kotlinx.coroutines.tasks.await

class VibraRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearVibra(vibra: Vibra): String {
        val docRef = db.collection("vibras").document()
        val id = vibra.id.ifEmpty { docRef.id }
        docRef.set(vibra.copy(id = id), SetOptions.merge()).await()
        return id
    }

    suspend fun obtenerVibrasActivas(): List<Vibra> {
        val snapshot = db.collection("vibras")
            .whereEqualTo("activa", true)
            .get().await()
        return snapshot.toObjects(Vibra::class.java)
    }
}
