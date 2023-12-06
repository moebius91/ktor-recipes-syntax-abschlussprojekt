package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.extensions.toCategory
import de.jnmultimedia.data.model.Category
import de.jnmultimedia.data.tables.CategoriesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryRepository {
    // CREATE
    fun createCategory(category: Category) {
        transaction {
            CategoriesTable.insert {
                it[name] = category.name
            }
        }
    }

    // READ
    fun getAllCategories(): List<Category> {
        return transaction {
            CategoriesTable.selectAll().map { it.toCategory() }
        }
    }

    fun getCategoryById(categoryId: Int): Category? {
        return transaction {
            CategoriesTable.select { CategoriesTable.categoryId eq categoryId }.singleOrNull()?.toCategory()
        }
    }

    // UPDATE
    fun updateCategoryById(categoryId: Int, updatedCategory: Category): Boolean {
        return transaction {
            val updatedRowCount = CategoriesTable.update({ CategoriesTable.categoryId eq categoryId }) {
                it[name] = updatedCategory.name
            }
            updatedRowCount > 0
        }
    }

    //DELETE
    fun deleteCategoryById(categoryId: Int): Boolean {
        return transaction {
            val deletedRowCount = CategoriesTable.deleteWhere { CategoriesTable.categoryId eq categoryId }
            deletedRowCount > 0
        }
    }
}