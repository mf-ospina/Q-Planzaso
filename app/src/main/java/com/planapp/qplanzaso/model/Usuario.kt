package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: String = "asistente", // "asistente" o "organizador"
    val tipoUsuario: String = "particular", // "particular" o "empresarial"
    val fechaRegistro: Timestamp? = null,
    val preferencias: List<String> = listOf(),
    val fotoPerfil: String = "",
    val verified: Boolean = false,
    val eventosPublicados: Int = 0
)
