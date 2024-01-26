package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table

object IngredientsTable : Table("ingredients") {
    val ingredientId = integer("ingredient_id").autoIncrement()
    override val primaryKey = PrimaryKey(ingredientId)
    val name = varchar("name", 255)
}