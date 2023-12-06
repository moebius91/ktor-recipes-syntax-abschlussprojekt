package de.jnmultimedia.data.tables

import de.jnmultimedia.data.tables.RecipesTable.references
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object RecipesTable : Table("recipes") {
    val recipeId = integer("recipe_id").autoIncrement()
    override val primaryKey = PrimaryKey(recipeId)
    val name = varchar("name", 255)
    val description = text("description")
    val authorId = integer("author_id").references(UsersTable.userId)
    val creationDate = date("creation_date")
}