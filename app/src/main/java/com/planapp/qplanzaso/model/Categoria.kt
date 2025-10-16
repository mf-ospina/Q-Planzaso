package com.planapp.qplanzaso.model

/**
 * Modelo que define una categoría de evento.
 * Se usa para filtros, menús y recomendaciones IA.
 */
data class Categoria(
    val id: String = "",
    val nombre: String = "",
    val icono: String = "",                // Nombre o URL del ícono
    val colorHex: String = "#FFA726",      // Color representativo (chips UI)
    val descripcion: String = "",
    val vibrasAsociadas: List<String> = emptyList(), // "Festivo", "Cultural", etc.
    val popularidad: Int = 0,
    val activa: Boolean = true
)
