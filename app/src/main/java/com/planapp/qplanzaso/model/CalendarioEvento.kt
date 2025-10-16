package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp

data class CalendarioEvento(
    val id: String = "",
    val titulo: String = "",
    val fecha: Timestamp? = null,
    val tipo: String = "favorito", // "favorito", "inscrito", "recordatorio"
    val notificado: Boolean = false
)
