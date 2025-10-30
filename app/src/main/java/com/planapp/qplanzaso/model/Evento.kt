package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

/**
 * Representa un evento dentro de la aplicación.
 * Compatible con las funcionalidades de búsqueda, filtros y gestión del organizador.
 */
data class Evento(
    var id: String? = null,
    var nombre: String = "",
    var descripcion: String = "",
    var categoriasIds: List<String> = emptyList(),
    var vibras: List<String> = emptyList(),
    var precio: Double = 0.0,
    // 🔹 Patrocinadores
    var patrocinadores: List<String> = emptyList(),
    var fechaInicio: Timestamp? = null,
    var fechaFin: Timestamp? = null,
    var organizadorId: String = "",
    var verificado: Boolean = false,
    var estado: String = "proximo",
    var calificacionPromedio: Double = 0.0,
    var calificacionesCount: Int = 0,
    var asistentesCount: Int = 0,
    var favoritosCount: Int = 0,
    var imagen: String = "",

    // 🔹 ubicación:
    var ubicacion: GeoPoint? = null, // coordenadas (lat, lon)
    var direccion: String? = null, // texto legible como parque simon bolivar
    var ciudad: String? = null,
    var pais: String? = null,

    //Inscripcion
    val inscritosIds: List<String> = emptyList(), // lista de usuarios inscritos

    //Storage
    val imagenUrl: String? = null

)
