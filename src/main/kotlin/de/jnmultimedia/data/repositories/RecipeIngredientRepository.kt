package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.tables.RecipeIngredientsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class RecipeIngredientRepository {

    fun addIngredientToRecipe(recipeId: Int, ingredientId: Int) {
        transaction {
            RecipeIngredientsTable.insert {
                it[RecipeIngredientsTable.recipeId] = recipeId
                it[RecipeIngredientsTable.ingredientId] = ingredientId
            }
        }
    }

    fun getIngredientsForRecipe(recipeId: Int): List<Int> {
        return transaction {
            RecipeIngredientsTable
                .select { RecipeIngredientsTable.recipeId eq recipeId }
                .map { it[RecipeIngredientsTable.ingredientId] }
        }
    }

    fun getRecipeIdsForIngredient(ingredientId: Int): List<Int> {
        return transaction {
            RecipeIngredientsTable
                .select { RecipeIngredientsTable.ingredientId eq ingredientId }
                .map { it[RecipeIngredientsTable.recipeId] }
        }
    }

    fun removeIngredientsFromRecipe(recipeId: Int) {
        transaction {
            RecipeIngredientsTable.deleteWhere {
                RecipeIngredientsTable.recipeId eq recipeId
            }
        }
    }

    fun removeIngredientFromRecipe(recipeId: Int, ingredientId: Int) {
        transaction {
            RecipeIngredientsTable.deleteWhere {
                (RecipeIngredientsTable.recipeId eq recipeId) and (RecipeIngredientsTable.ingredientId eq ingredientId)
            }
        }
    }

    fun removeIngredient(ingredientId: Int) {
        transaction {
            RecipeIngredientsTable.deleteWhere {
                RecipeIngredientsTable.ingredientId eq ingredientId
            }
        }
    }
}