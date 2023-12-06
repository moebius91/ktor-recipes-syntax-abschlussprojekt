package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.tables.RecipeTagsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class RecipeTagRepository {

    // CREATE
    fun addTagToRecipe(recipeId: Int, tagId: Int) {
        transaction {
            RecipeTagsTable.insert {
                it[RecipeTagsTable.recipeId] = recipeId
                it[RecipeTagsTable.tagId] = tagId
            }
        }
    }
    
    // READ
    fun getTagsForRecipe(recipeId: Int): List<Int> {
        return transaction {
            RecipeTagsTable
                .select { RecipeTagsTable.recipeId eq recipeId }
                .map { it[RecipeTagsTable.tagId] }
        }
    }

    fun getRecipeIdsForTag(tagId: Int): List<Int> {
        return transaction {
            RecipeTagsTable
                .select { RecipeTagsTable.tagId eq tagId }
                .map { it[RecipeTagsTable.recipeId] }
        }
    }

    fun removeTagsFromRecipe(recipeId: Int) {
        transaction {
            RecipeTagsTable.deleteWhere {
                RecipeTagsTable.recipeId eq recipeId
            }
        }
    }

    fun removeTagFromRecipe(recipeId: Int, tagId: Int) {
        transaction {
            RecipeTagsTable.deleteWhere {
                (RecipeTagsTable.recipeId eq recipeId) and (RecipeTagsTable.tagId eq tagId)
            }
        }
    }

    fun removeTag(tagId: Int) {
        transaction {
            RecipeTagsTable.deleteWhere {
                RecipeTagsTable.tagId eq tagId
            }
        }
    }
}