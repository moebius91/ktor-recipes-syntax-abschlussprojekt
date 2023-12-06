package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table

object TagsTable : Table("tags") {
    val tagId = integer("tag_id").autoIncrement()
    override val primaryKey = PrimaryKey(tagId)
    val name = varchar("name", 255).uniqueIndex()
}