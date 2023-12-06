package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table

object RecipeTagsTable : Table("recipe_tags") {
    val recipeId = reference("recipe_id", RecipesTable.recipeId)
    val tagId = reference("tag_id", TagsTable.tagId)
    override val primaryKey = PrimaryKey(recipeId, tagId)
}