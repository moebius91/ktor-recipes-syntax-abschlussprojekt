package de.jnmultimedia.data.extensions

import de.jnmultimedia.data.model.*
import de.jnmultimedia.data.tables.*
import org.jetbrains.exposed.sql.ResultRow
import java.time.format.DateTimeFormatter

fun ResultRow.toUser(): User {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    val createdAt = this[UsersTable.createdAt].format(formatter)
    val updatedAt = this[UsersTable.updatedAt].format(formatter)

    return User(
        userId = this[UsersTable.userId],
        username = this[UsersTable.username],
        password = "Entengrütze",
        role = this[UsersTable.role],
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ResultRow.toRecipe(): Recipe {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val creationDate = this[RecipesTable.creationDate].format(formatter)

    return Recipe(
        recipeId = this[RecipesTable.recipeId],
        name = this[RecipesTable.name],
        description = this[RecipesTable.description],
        authorId = this [RecipesTable.authorId],
        creationDate = creationDate
    )
}

fun ResultRow.toTag(): Tag {
    return Tag(
        tagId = this[TagsTable.tagId],
        name = this[TagsTable.name]
    )
}

fun ResultRow.toCategory(): Category {
    return Category(
        categoryId = this[CategoriesTable.categoryId],
        name = this[CategoriesTable.name]
    )
}

fun ResultRow.toIngredient(): Ingredient {
    return Ingredient(
        ingredientId = this[IngredientsTable.ingredientId],
        name = this[IngredientsTable.name],
        count = this[IngredientsTable.count],
        unit = IngredientUnit.valueOf(this[IngredientsTable.unit])
    )
}