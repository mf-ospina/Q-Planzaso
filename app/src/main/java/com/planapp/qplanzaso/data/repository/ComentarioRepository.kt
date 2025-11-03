package com.planapp.qplanzaso.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.planapp.qplanzaso.model.ComentarioEvento
import kotlinx.coroutines.tasks.await

class ComentarioRepository {

    private val db = FirebaseFirestore.getInstance()

    // Crear comentario
    // Archivo: ComentarioRepository.kt

    // Crear comentario
    suspend fun crearComentario(eventoId: String, comentario: ComentarioEvento): String {
        val colRef = db.collection("evento").document(eventoId).collection("comentarios")

        // Si el comentario ya tiene un ID, lo usamos (para casos de edición/reintento)
        val docRef = if (comentario.id.isNullOrEmpty()) colRef.document() else colRef.document(comentario.id!!)

        // Aseguramos el ID y la fecha
        val id = docRef.id
        val toSave = comentario.copy(id = id, fecha = Timestamp.now())

        // ⚠️ CRÍTICO: Usamos SetOptions.merge para asegurar que si ya existía (por ID), se actualice.
        docRef.set(toSave, SetOptions.merge()).await()
        return id
    }

    // Obtener todos los comentarios
    suspend fun obtenerComentarios(eventoId: String): List<ComentarioEvento> {
        val snapshot = db.collection("evento")
            .document(eventoId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .await()

        // Mapeo para incluir el ID
        return snapshot.documents.mapNotNull { document ->
            val comentario = document.toObject(ComentarioEvento::class.java)
            comentario?.copy(id = document.id)
        }
    }

    // Obtener comentarios paginados
    suspend fun obtenerComentariosPaginados(
        eventoId: String,
        lastVisibleFecha: Timestamp? = null,
        limit: Long = 10
    ): Pair<List<ComentarioEvento>, Timestamp?> {
        var query = db.collection("evento")
            .document(eventoId)
            .collection("comentarios")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(limit)

        if (lastVisibleFecha != null) {
            query = query.startAfter(lastVisibleFecha)
        }

        val snapshot = query.get().await()
        val comentarios = snapshot.toObjects(ComentarioEvento::class.java)

        val nextCursor = if (comentarios.isNotEmpty()) comentarios.last().fecha else null
        return Pair(comentarios, nextCursor)
    }

    // Editar comentario
    suspend fun editarComentario(eventoId: String, comentario: ComentarioEvento) {
        if (comentario.id.isBlank()) throw IllegalArgumentException("Comentario debe tener ID para editar")

        val map = mapOf(
            "texto" to comentario.texto,
            "calificacion" to comentario.calificacion,
            "fecha" to Timestamp.now()
        )

        db.collection("evento").document(eventoId)
            .collection("comentarios").document(comentario.id) // ⬅️ Usa el ID para dirigirse
            .set(map, SetOptions.merge()) // ⬅️ set(map) con ID existente actualiza
            .await()
    }

    // Eliminar comentario
    suspend fun eliminarComentario(eventoId: String, comentarioId: String) {
        db.collection("evento").document(eventoId)
            .collection("comentarios").document(comentarioId)
            .delete()
            .await()
    }

    suspend fun obtenerComentarioPorUsuario(eventoId: String, usuarioId: String): ComentarioEvento? {
        val snapshot = db.collection("evento")
            .document(eventoId)
            .collection("comentarios")
            .whereEqualTo("usuarioId", usuarioId) // ⬅️ Filtramos por el ID del usuario
            .limit(1) // Solo necesitamos un resultado
            .get()
            .await()

        // Si se encuentra un documento, lo mapeamos al objeto ComentarioEvento
        return snapshot.documents.firstOrNull()?.let { document ->
            val comentario = document.toObject(ComentarioEvento::class.java)
            // Aseguramos que el ID del documento esté en el objeto
            comentario?.copy(id = document.id)
        }
    }
}







