package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table

object CategoriesTable : Table("categories") {
    val categoryId = integer("category_id").autoIncrement()
    override val primaryKey = PrimaryKey(categoryId)
    val name = varchar("name", 255).uniqueIndex()
}