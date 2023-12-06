package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table

object RecipeIngredientsTable : Table("recipe_ingredients") {
    val recipeId = reference("recipe_id", RecipesTable.recipeId)
    val ingredientId = reference("ingredient_id", IngredientsTable.ingredientId)
    override val primaryKey = PrimaryKey(recipeId, ingredientId)
}