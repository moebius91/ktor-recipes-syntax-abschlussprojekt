package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table

object RecipeCategoriesTable : Table("recipe_categories") {
    val recipeId = reference("recipe_id", RecipesTable.recipeId)
    val categoryId = reference("category_id", CategoriesTable.categoryId)
    override val primaryKey = PrimaryKey(recipeId, categoryId)
}