package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeOutput(
    val recipeId: Int? = null,
    val name: String,
    val description: String,
    val tags: MutableList<Tag>? = null,
    val categories: MutableList<Category>? = null,
    val authorId: Int,
    val creationDate: String
)
