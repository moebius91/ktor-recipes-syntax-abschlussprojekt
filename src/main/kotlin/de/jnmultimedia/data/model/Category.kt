package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val categoryId: Int? = null,
    val name: String
)
