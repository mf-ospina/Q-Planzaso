package com.planapp.qplanzaso.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.model.EventoStats
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class EventoRepository {

    private val db = FirebaseFirestore.getInstance()

    // 🔹 Crear evento
    suspend fun crearEvento(evento: Evento): String {
        val docRef = db.collection("evento").document()
        val id = evento.id ?: docRef.id
        docRef.set(evento.copy(id = id), SetOptions.merge()).await()
        return id
    }

    // 🔹 Editar evento
    suspend fun editarEvento(evento: Evento) {
        if (evento.id != null) {
            db.collection("evento").document(evento.id!!)
                .set(evento, SetOptions.merge()).await()
        }
    }

    // 🔹 Eliminar evento
    suspend fun eliminarEvento(eventoId: String) {
        db.collection("evento").document(eventoId).delete().await()
    }

    // 🔹 Obtener un solo evento por ID

    /*
    suspend fun obtenerEvento(eventoId: String): Evento? {
        val doc = db.collection("evento").document(eventoId).get().await()
        return if (doc.exists()) doc.toObject(Evento::class.java)?.copy(id = doc.id) else null
    }*/

    suspend fun obtenerEvento(eventoId: String): Evento? {
        //                                  👇
        val doc = db.collection("evento").document(eventoId).get().await() // <-- CORREGIDO
        return if (doc.exists()) doc.toObject(Evento::class.java)?.copy(id = doc.id) else null
    }

    // 🔹 Obtener todos los eventos
    suspend fun obtenerEventos(): List<Evento> {
        val snapshot = db.collection("evento").get().await()
        return snapshot.toObjects(Evento::class.java)
    }

    // 🔹 Buscar eventos por texto
    suspend fun buscarEventosPorTexto(texto: String): List<Evento> {
        val snapshot = db.collection("evento")
            .orderBy("nombre")
            .startAt(texto)
            .endAt(texto + "\uf8ff")
            .get().await()
        return snapshot.toObjects(Evento::class.java)
    }

    // 🔹 Filtro avanzado por múltiples criterios
    suspend fun filtrarEventos(
        categoriasIds: List<String>? = null,
        fechaInicioParam: Timestamp? = null,
        fechaFinParam: Timestamp? = null,
        precioMax: Double? = null,
        ubicacionActual: GeoPoint? = null,
        maxDistanciaKm: Double? = null,
        soloVerificados: Boolean = false
    ): List<Evento> {
        var ref: Query = db.collection("evento")

        if (soloVerificados) ref = ref.whereEqualTo("verificado", true)
        if (precioMax != null) ref = ref.whereLessThanOrEqualTo("precio", precioMax)

        val snapshot = ref.get().await()
        var eventos = snapshot.toObjects(Evento::class.java)

        // 🔸 Filtrar manualmente por categorías
        if (!categoriasIds.isNullOrEmpty()) {
            eventos = eventos.filter { evento ->
                evento.categoriasIds.any { id -> categoriasIds.contains(id) }
            }
        }

        // 🔸 Filtrar por fecha
        if (fechaInicioParam != null && fechaFinParam != null) {
            eventos = eventos.filter { evento ->
                val inicio = evento.fechaInicio
                val fin = evento.fechaFin
                inicio != null && fin != null &&
                        inicio >= fechaInicioParam && fin <= fechaFinParam
            }
        }

        // 🔸 Filtrar por distancia (Haversine)
        if (ubicacionActual != null && maxDistanciaKm != null) {
            eventos = eventos.filter { evento ->
                val ubic = evento.ubicacion
                if (ubic != null) {
                    val d = calcularDistanciaKm(
                        ubicacionActual.latitude,
                        ubicacionActual.longitude,
                        ubic.latitude,
                        ubic.longitude
                    )
                    d <= maxDistanciaKm
                } else false
            }
        }

        return eventos
    }

    // 🔹 Obtener eventos creados por un organizador
    suspend fun obtenerEventosPorOrganizador(organizadorId: String): List<Evento> {
        val snapshot = db.collection("evento")
            .whereEqualTo("organizadorId", organizadorId)
            .get().await()
        return snapshot.toObjects(Evento::class.java)
    }

    // 🔹 Obtener eventos relacionados (por categorías)
    suspend fun obtenerEventosRelacionados(categoriasIds: List<String>, eventoId: String): List<Evento> {
        val snapshot = db.collection("evento").get().await()
        val eventos = snapshot.toObjects(Evento::class.java)
        return eventos.filter {
            it.id != eventoId && it.categoriasIds.any { id -> categoriasIds.contains(id) }
        }
    }

    // 🔹 Obtener eventos por categoría
    suspend fun obtenerEventosPorCategoriaN(categoryId: String): List<Evento> {
        Log.d("QPLANZASO_DEBUG", "Repositorio va a buscar eventos con 'categoria' = '$categoryId'")

        val snapshot = db.collection("evento")
            .whereEqualTo("categoria", categoryId)
            .get().await()

        Log.d("QPLANZASO_DEBUG", "Consulta completada. Documentos encontrados: ${snapshot.size()}")
        return snapshot.toObjects(Evento::class.java)
    }

    // 🔹 Actualizar estadísticas (visualizaciones, favoritos, asistentes)
    suspend fun actualizarEstadisticas(eventoId: String, stats: EventoStats) {
        val statsMap = mapOf(
            "asistentesCount" to stats.asistentesCount,
            "favoritosCount" to stats.favoritosCount,
            "calificacionPromedio" to stats.calificacionPromedio
        )
        db.collection("evento").document(eventoId)
            .set(statsMap, SetOptions.merge()).await()
    }

    // 🔹 Actualizar estado de eventos (pasado/próximo)
    suspend fun actualizarEstadoEventos() {
        val snapshot = db.collection("evento").get().await()
        for (doc in snapshot.documents) {
            val evento = doc.toObject(Evento::class.java)
            val fechaFin = evento?.fechaFin
            if (fechaFin != null && fechaFin < Timestamp.now()) {
                evento?.id?.let { id ->
                    db.collection("evento").document(id)
                        .update("estado", "pasado").await()
                }
            }
        }
    }

    // 🔹 Favoritos
    suspend fun agregarFavorito(eventoId: String, usuarioId: String) {
        db.collection("evento").document(eventoId)
            .collection("favoritos").document(usuarioId)
            .set(mapOf("fecha" to System.currentTimeMillis())).await()
    }

    suspend fun eliminarFavorito(eventoId: String, usuarioId: String) {
        db.collection("evento").document(eventoId)
            .collection("favoritos").document(usuarioId).delete().await()
    }

    suspend fun obtenerEventosFavoritosPorUsuario(usuarioId: String): List<Evento> {
        val eventosSnapshot = db.collection("evento").get().await()
        val eventosFavoritos = mutableListOf<Evento>()

        for (eventoDoc in eventosSnapshot.documents) {
            val favoritoDoc = eventoDoc.reference
                .collection("favoritos")
                .document(usuarioId)
                .get()
                .await()

            if (favoritoDoc.exists()) {
                eventoDoc.toObject(Evento::class.java)?.let { evento ->
                    eventosFavoritos.add(evento.copy(id = eventoDoc.id))
                }
            }
        }

        return eventosFavoritos
    }

    suspend fun esEventoFavorito(eventoId: String, usuarioId: String): Boolean {
        val doc = db.collection("evento")
            .document(eventoId)
            .collection("favoritos")
            .document(usuarioId)
            .get()
            .await()
        return doc.exists()
    }



    // 🔹 Calcular distancia entre coordenadas
    fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    // 🔹 Calificaciones
    suspend fun registrarCalificacion(eventoId: String, usuarioId: String, valor: Double) {
        // Guardar la calificación en la subcolección "calificaciones" con doc = usuarioId
        val calRef = db.collection("evento").document(eventoId)
            .collection("calificaciones").document(usuarioId)
        val data = mapOf(
            "usuarioId" to usuarioId,
            "valor" to valor,
            "fecha" to com.google.firebase.Timestamp.now()
        )
        calRef.set(data, SetOptions.merge()).await()

        // Recalcular promedio
        recalcularPromedioCalificaciones(eventoId)
    }

    suspend fun eliminarCalificacion(eventoId: String, usuarioId: String) {
        db.collection("evento").document(eventoId)
            .collection("calificaciones").document(usuarioId)
            .delete().await()
        recalcularPromedioCalificaciones(eventoId)
    }

    private suspend fun recalcularPromedioCalificaciones(eventoId: String) {
        val snapshot = db.collection("evento").document(eventoId)
            .collection("calificaciones").get().await()

        val valores = snapshot.documents.mapNotNull { it.getDouble("valor") }
        val promedio = if (valores.isNotEmpty()) valores.average() else 0.0
        val count = valores.size

        // Actualizar campos en documento evento
        db.collection("evento").document(eventoId)
            .set(mapOf("calificacionPromedio" to promedio, "calificacionesCount" to count), SetOptions.merge())
            .await()
    }
    suspend fun actualizarCampoEvento(eventoId: String, campo: String, valor: Any) {
        db.collection("evento").document(eventoId)
            .update(campo, valor)
            .await()
    }

    suspend fun obtenerCalificacionUsuario(eventoId: String, usuarioId: String): Double? {
        return try {
            // 1. Apunta al documento exacto que quieres leer
            val docRef = db.collection("evento").document(eventoId)
                .collection("calificaciones").document(usuarioId)

            // 2. Intenta obtenerlo
            val snapshot = docRef.get().await()

            // 3. Si existe, devuelve el campo "valor". Si no, devuelve null.
            if (snapshot.exists()) {
                snapshot.getDouble("valor")
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error al obtener calificación de usuario: ${e.message}")
            null
        }
    }
}
