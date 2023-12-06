package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.model.Category
import de.jnmultimedia.data.model.Recipe
import de.jnmultimedia.data.tables.RecipeCategoriesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class RecipeCategoryRepository {

    // CREATE
    fun addCategoryToRecipe(recipeId: Int, categoryId: Int) {
        transaction {
            RecipeCategoriesTable.insert {
                it[RecipeCategoriesTable.recipeId] = recipeId
                it[RecipeCategoriesTable.categoryId] = categoryId
            }
        }
    }

    // READ
    fun getCategoriesForRecipe(recipeId: Int): List<Int> {
        return transaction {
            RecipeCategoriesTable
                .select { RecipeCategoriesTable.recipeId eq recipeId }
                .map { it[RecipeCategoriesTable.categoryId] }
        }
    }

    fun getRecipeIdsForCategory(categoryId: Int): List<Int> {
        return transaction {
            RecipeCategoriesTable
                .select { RecipeCategoriesTable.categoryId eq categoryId }
                .map {it[RecipeCategoriesTable.recipeId]}
        }
    }

    // DELETE
    fun removeCategoriesFromRecipe(recipeId: Int) {
        transaction {
            RecipeCategoriesTable.deleteWhere {
                RecipeCategoriesTable.recipeId eq recipeId
            }
        }
    }

    fun removeCategoryFromRecipe(recipeId: Int, categoryId: Int) {
        transaction {
            RecipeCategoriesTable.deleteWhere {
                (RecipeCategoriesTable.recipeId eq recipeId) and (RecipeCategoriesTable.categoryId eq categoryId)
            }
        }
    }

    fun removeCategory(categoryId: Int) {
        transaction {
            RecipeCategoriesTable.deleteWhere {
                RecipeCategoriesTable.categoryId eq categoryId
            }
        }
    }
}