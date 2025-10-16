package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp

data class RecomendacionIA(
    val usuarioId: String = "",
    val eventosRecomendados: List<String> = listOf(),
    val categoriasSugeridas: List<String> = listOf(),
    val fechaGeneracion: Timestamp? = null,
    val precisionModelo: Double = 0.0
)
