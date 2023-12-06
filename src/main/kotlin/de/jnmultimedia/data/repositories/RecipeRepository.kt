package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.extensions.toRecipe
import de.jnmultimedia.data.model.Recipe
import de.jnmultimedia.data.tables.RecipesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecipeRepository {

    // CREATE
    fun createRecipe(recipe: Recipe): Recipe? {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val parsedDate = LocalDate.parse(recipe.creationDate, formatter)

        val name = recipe.name
        val description = recipe.description
        val authorId = recipe.authorId
        val creationDate = parsedDate

        return transaction {
            val recipeId = RecipesTable.insert {
                it[RecipesTable.name] = name
                it[RecipesTable.description] = description
                it[RecipesTable.authorId] = authorId
                it[RecipesTable.creationDate] = creationDate
            }.resultedValues
                ?.firstOrNull()
                ?.get(RecipesTable.recipeId)

            recipeId?.let {
                Recipe(
                    it,
                    name,
                    description,
                    authorId,
                    creationDate.toString()
                )
            }
        }
    }

    // READ
    fun getAllRecipes(): List<Recipe> {
        return transaction {
            RecipesTable.selectAll().map { it.toRecipe() }
        }
    }

    fun getRecipeById(recipeId: Int): Recipe? {
        return transaction {
            RecipesTable.select { RecipesTable.recipeId eq recipeId }.singleOrNull()?.toRecipe()
        }
    }
    // UPDATE
    fun updateRecipeById(recipeId: Int, updatedRecipe: Recipe): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val parsedDate = LocalDate.parse(updatedRecipe.creationDate, formatter)

        return transaction {
            val updatedRowCount = RecipesTable.update({ RecipesTable.recipeId eq recipeId }) {
                it[name] = updatedRecipe.name
                it[description] = updatedRecipe.description
                it[authorId] = updatedRecipe.authorId
                it[creationDate] = parsedDate
            }
            updatedRowCount > 0
        }
    }

    // DELETE
    fun deleteRecipeById(recipeId: Int): Boolean {
        return transaction {
            val deletedRowCount = RecipesTable.deleteWhere { RecipesTable.recipeId eq recipeId }
            deletedRowCount > 0
        }
    }
}