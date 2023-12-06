package de.jnmultimedia.data.repositories

import com.zaxxer.hikari.HikariDataSource

class Repositories(
    dataSource: HikariDataSource
) {
    val tagRepository = TagRepository()
    val categoryRepository = CategoryRepository()
    val ingredientRepository = IngredientRepository()
    val userRepository = UserRepository()
    val tokenRepository = TokenRepository(dataSource)
    val recipeRepository = RecipeRepository()
    val recipeTagRepository = RecipeTagRepository()
    val recipeCategoryRepository = RecipeCategoryRepository()
    val recipeIngredientRepository = RecipeIngredientRepository()
}