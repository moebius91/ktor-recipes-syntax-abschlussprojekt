package de.jnmultimedia.data.dto

import de.jnmultimedia.data.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateDTO(
    val username: String? = null,
    val password: String? = null,
    val role: UserRole? = null
)