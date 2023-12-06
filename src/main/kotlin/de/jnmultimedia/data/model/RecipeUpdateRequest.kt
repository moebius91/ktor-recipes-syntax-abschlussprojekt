package de.jnmultimedia.data.model

data class RecipeUpdateRequest(
    val recipe: Recipe,
    val categoryIds: List<Int>,
    val tagIds: List<Int>
)
