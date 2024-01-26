package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeCreationRequest(
    val recipe: Recipe,
    val categoryIds: List<Int>,
    val tagIds: List<Int>,
    val ingredients: List<Ingredient>
)