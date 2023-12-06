package de.jnmultimedia.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val ingredientId: Int? = null,
    val name: String,
    val count: Int,
    val unit: IngredientUnit
)
