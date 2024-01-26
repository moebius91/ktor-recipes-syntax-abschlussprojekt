package de.jnmultimedia.data.tables

import de.jnmultimedia.data.model.IngredientUnit
import org.jetbrains.exposed.sql.Table

object RecipeIngredientsTable : Table("recipe_ingredients") {
    val recipeId = reference("recipe_id", RecipesTable.recipeId)
    val ingredientId = reference("ingredient_id", IngredientsTable.ingredientId)
    val count = integer("count").check { it greaterEq 0 }
    val unit = enumerationByName("unit", 20, IngredientUnit::class)
    override val primaryKey = PrimaryKey(recipeId, ingredientId)
}