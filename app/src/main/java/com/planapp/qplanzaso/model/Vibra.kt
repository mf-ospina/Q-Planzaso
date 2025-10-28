package com.planapp.qplanzaso.model

/**
 * Define las vibras o ambientes emocionales de los eventos.
 * Por ejemplo: Chill, Festivo, Cultural, Tranquilo.
 */
data class Vibra(
    val id: String = "",
    val nombre: String = "",
    val colorHex: String = "#90CAF9",
    val icono: String = "",
    val descripcion: String = "",
    val activa: Boolean = true
)
