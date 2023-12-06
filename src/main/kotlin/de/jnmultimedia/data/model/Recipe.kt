package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val recipeId: Int? = null,
    val name: String,
    val description: String,
    val authorId: Int,
    val creationDate: String
)
