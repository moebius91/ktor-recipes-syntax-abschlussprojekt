package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.extensions.toTag
import de.jnmultimedia.data.model.Tag
import de.jnmultimedia.data.tables.TagsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TagRepository {

    // CREATE
    fun createTag(tag: Tag): Tag? {
        val name = tag.name

        return transaction {
            val tagId = TagsTable.insert {
                it[TagsTable.name] = name
            }.resultedValues
                ?.firstOrNull()
                ?.get(TagsTable.tagId)

            tagId?.let {
                Tag(
                    it,
                    name
                )
            }
        }
    }

    // READ
    fun getAllTags(): List<Tag> {
        return transaction {
            TagsTable.selectAll().map { it.toTag() }
        }
    }

    fun getTagById(tagId: Int): Tag? {
        return transaction {
            TagsTable.select { TagsTable.tagId eq tagId }.singleOrNull()?.toTag()
        }
    }

    // UPDATE
    fun updateTagById(tagId: Int, updatedTag: Tag): Boolean {
        return transaction {
            val updatedRowCount = TagsTable.update({ TagsTable.tagId eq tagId }) {
                it[name] = updatedTag.name
            }
            updatedRowCount > 0
        }
    }

    // DELETE
    fun deleteTagById(tagId: Int): Boolean {
        return transaction {
            val deletedRowCount = TagsTable.deleteWhere { TagsTable.tagId eq tagId }
            deletedRowCount > 0
        }
    }
}