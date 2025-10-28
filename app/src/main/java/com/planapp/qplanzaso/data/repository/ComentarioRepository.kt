package com.planapp.qplanzaso.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.planapp.qplanzaso.model.ComentarioEvento
import kotlinx.coroutines.tasks.await

class ComentarioRepository {

    private val db = FirebaseFirestore.getInstance()

    // 🔹 Crear comentario (genera id automático y agrega timestamp actual)
    suspend fun crearComentario(eventoId: String, comentario: ComentarioEvento): String {
        val colRef = db.collection("eventos").document(eventoId).collection("comentarios")
        val docRef = colRef.document()
        val id = comentario.id.ifEmpty { docRef.id }
        val toSave = comentario.copy(id = id, fecha = Timestamp.now())

        docRef.set(toSave, SetOptions.merge()).await()
        return id
    }

    // 🔹 Obtener todos los comentarios de un evento (ordenados por fecha desc)
    suspend fun obtenerComentarios(eventoId: String): List<ComentarioEvento> {
        val snapshot = db.collection("eventos")
            .document(eventoId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.toObjects(ComentarioEvento::class.java)
    }

    // 🔹 Obtener comentarios paginados (para mejorar rendimiento con muchos comentarios)
    suspend fun obtenerComentariosPaginados(
        eventoId: String,
        lastVisibleFecha: Timestamp? = null,
        limit: Long = 10
    ): Pair<List<ComentarioEvento>, Timestamp?> {
        var query = db.collection("eventos")
            .document(eventoId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(limit)

        if (lastVisibleFecha != null) {
            query = query.startAfter(lastVisibleFecha)
        }

        val snapshot = query.get().await()
        val comentarios = snapshot.toObjects(ComentarioEvento::class.java)

        // Marcador (cursor) para la siguiente página
        val nextCursor = if (comentarios.isNotEmpty()) comentarios.last().fecha else null

        return Pair(comentarios, nextCursor)
    }

    // 🔹 Editar comentario (solo texto y calificación)
    suspend fun editarComentario(eventoId: String, comentario: ComentarioEvento) {
        if (comentario.id.isBlank()) throw IllegalArgumentException("Comentario debe tener ID para editar")

        val map = mapOf(
            "texto" to comentario.texto,
            "calificacion" to comentario.calificacion,
            "fecha" to Timestamp.now() // actualiza fecha de edición
        )

        db.collection("eventos").document(eventoId)
            .collection("comentarios").document(comentario.id)
            .set(map, SetOptions.merge())
            .await()
    }

    // 🔹 Eliminar comentario por ID
    suspend fun eliminarComentario(eventoId: String, comentarioId: String) {
        db.collection("eventos").document(eventoId)
            .collection("comentarios").document(comentarioId)
            .delete()
            .await()
    }
}
