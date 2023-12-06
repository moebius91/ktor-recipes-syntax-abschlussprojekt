package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val tagId: Int? = null,
    val name: String
)