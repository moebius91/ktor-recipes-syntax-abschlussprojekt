package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.extensions.toIngredient
import de.jnmultimedia.data.model.Ingredient
import de.jnmultimedia.data.tables.IngredientsTable
import de.jnmultimedia.data.tables.RecipeIngredientsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class IngredientRepository {
    // CREATE
    fun createIngredient(ingredient: Ingredient) {
        transaction {
            IngredientsTable.insert {
                it[name] = ingredient.name
            }
        }
    }

    // READ
    fun getAllIngredients(): List<Ingredient> {
        return transaction {
            IngredientsTable.selectAll().map { it.toIngredient() }
        }
    }

    fun getIngredientById(ingredientId: Int): Ingredient? {
        return transaction {
            IngredientsTable.select { IngredientsTable.ingredientId eq ingredientId }.singleOrNull()?.toIngredient()
        }
    }

    // UPDATE
    fun updateIngredientById(ingredientId: Int, updatedIngredient: Ingredient): Boolean {
        return transaction {
            val updatedRowCount = IngredientsTable.update({ IngredientsTable.ingredientId eq ingredientId }) {
                it[name] = updatedIngredient.name
            }
            updatedRowCount > 0
        }
    }

    //DELETE
    fun deleteIngredientById(ingredientId: Int): Boolean {
        return transaction {
            val deletedRowCount = IngredientsTable.deleteWhere { IngredientsTable.ingredientId eq ingredientId }
            deletedRowCount > 0
        }
    }
}