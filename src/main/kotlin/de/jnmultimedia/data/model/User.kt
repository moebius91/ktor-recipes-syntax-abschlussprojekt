package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int? = null,
    val username: String,
    val password: String,
    val role: UserRole,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
