package de.jnmultimedia.data.extensions

import de.jnmultimedia.data.model.*
import de.jnmultimedia.data.tables.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
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

fun ResultRow.toRecipeOutput(): RecipeOutput {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val creationDate = this[RecipesTable.creationDate].format(formatter)

    val recipeId = this[RecipesTable.recipeId]

    // Extrahiere Ingredients
    val ingredients = RecipeIngredientsTable.select {
        RecipeIngredientsTable.recipeId eq recipeId
    }.map { ingredientRow ->
        val ingredientId = ingredientRow[RecipeIngredientsTable.ingredientId]
        val ingredient = IngredientsTable.select { IngredientsTable.ingredientId eq ingredientId }
            .singleOrNull()
            ?.let { ingredientResultRow ->
                Ingredient(
                    ingredientId = ingredientResultRow[IngredientsTable.ingredientId],
                    name = ingredientResultRow[IngredientsTable.name],
                    // Hier fehlte die Angabe des Ingredients im Pair
                    count = ingredientRow[RecipeIngredientsTable.count],
                    unit = ingredientRow[RecipeIngredientsTable.unit]
                )
            }
        ingredient
    }.toMutableList()

    // Extrahiere Categories
    val categories = RecipeCategoriesTable.select {
        RecipeCategoriesTable.recipeId eq recipeId
    }.map { categoryRow ->
        val categoryId = categoryRow[RecipeCategoriesTable.categoryId]
        val category = CategoriesTable.select { CategoriesTable.categoryId eq categoryId }
            .singleOrNull()
            ?.let { categoryResultRow ->
                Category(
                    categoryId = categoryResultRow[CategoriesTable.categoryId],
                    name = categoryResultRow[CategoriesTable.name]
                )
            }
        category
    }.toMutableList()

    // Extrahiere Tags
    val tags = RecipeTagsTable.select {
        RecipeTagsTable.recipeId eq recipeId
    }.map { tagRow ->
        val tagId = tagRow[RecipeTagsTable.tagId]
        val tag = TagsTable.select { TagsTable.tagId eq tagId }
            .singleOrNull()
            ?.let { tagResultRow ->
                Tag(
                    tagId = tagResultRow[TagsTable.tagId],
                    name = tagResultRow[TagsTable.name]
                )
            }
        tag
    }.toMutableList()

    return RecipeOutput(
        recipeId = recipeId,
        name = this[RecipesTable.name],
        description = this[RecipesTable.description],
        ingredients = ingredients,
        tags = tags,
        categories = categories,
        authorId = this[RecipesTable.authorId],
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
    )
}