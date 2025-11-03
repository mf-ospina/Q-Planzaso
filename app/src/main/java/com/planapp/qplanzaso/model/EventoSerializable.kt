package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class EventoSerializable(
    val id: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val fecha: Timestamp? = null,
    val lugar: String? = null
) : Serializable
