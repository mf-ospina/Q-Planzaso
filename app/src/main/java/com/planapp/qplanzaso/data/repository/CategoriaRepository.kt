package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.planapp.qplanzaso.model.Categoria
import kotlinx.coroutines.tasks.await

class CategoriaRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearCategoria(categoria: Categoria): String {
        val docRef = db.collection("categorias").document()
        val id = categoria.id.ifEmpty { docRef.id }
        docRef.set(categoria.copy(id = id), SetOptions.merge()).await()
        return id
    }

    suspend fun obtenerCategoriasActivas(): List<Categoria> {
        val snapshot = db.collection("categorias")
            .whereEqualTo("activa", true)
            .get().await()
        return snapshot.toObjects(Categoria::class.java)
    }

    suspend fun obtenerCategoriaPorId(id: String): Categoria? {
        val doc = db.collection("categorias").document(id).get().await()
        return doc.toObject(Categoria::class.java)
    }

    suspend fun incrementarPopularidad(idCategoria: String) {
        db.collection("categorias").document(idCategoria)
            .update("popularidad", com.google.firebase.firestore.FieldValue.increment(1))
            .await()
    }
}
