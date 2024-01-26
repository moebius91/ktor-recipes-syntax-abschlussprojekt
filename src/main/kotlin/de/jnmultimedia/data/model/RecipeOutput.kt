package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeOutput(
    val recipeId: Int? = null,
    val name: String,
    val description: String,
    val ingredients: MutableList<Ingredient?> = mutableListOf(),
    val tags: MutableList<Tag?> = mutableListOf(),
    val categories: MutableList<Category?> = mutableListOf(),
    val authorId: Int,
    val creationDate: String
)
