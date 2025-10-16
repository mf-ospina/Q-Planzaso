package com.planapp.qplanzaso.model

data class FirebaseUserData(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val token: String?,
    val issuedAt: Long?,
    val expiresAt: Long?
)
