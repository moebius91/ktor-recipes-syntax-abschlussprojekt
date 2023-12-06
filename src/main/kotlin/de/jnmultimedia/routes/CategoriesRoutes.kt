package de.jnmultimedia.routes

import de.jnmultimedia.data.model.Category
import de.jnmultimedia.data.model.Recipe
import de.jnmultimedia.data.model.UserRole
import de.jnmultimedia.data.repositories.CategoryRepository
import de.jnmultimedia.data.repositories.Repositories
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoriesRoutes(repositories: Repositories) {
    val categoryRepository = repositories.categoryRepository
    val recipeCategoryRepository = repositories.recipeCategoryRepository
    val recipeRepository = repositories.recipeRepository

    route("/categories") {
        get {
            val categoryInfo = categoryRepository.getAllCategories()
            call.respond(categoryInfo)
        }
    }

    route("/category") {
        get("{categoryId}") {
            val categoryId = call.parameters["categoryId"]?.toIntOrNull()
            if (categoryId != null) {
                val category = categoryRepository.getCategoryById(categoryId)
                if (category != null) {
                    val recipesIds = recipeCategoryRepository.getRecipeIdsForCategory(categoryId)
                    val recipes = mutableListOf<Recipe>()

                    recipesIds.forEach { recipeId ->
                        val recipe = recipeRepository.getRecipeById(recipeId)

                        if (recipe != null) {
                            recipes.add(recipe)
                        }
                    }

                    call.respond(recipes)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid category ID")
            }
        }

        authenticate {
            // CREATE
            post {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Author || role == UserRole.Admin) {
                    val category = call.receive<Category>()
                    categoryRepository.createCategory(category)
                    call.respond(HttpStatusCode.OK, category)
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to create category")
                }
            }

            // UPDATE
            put("{categoryId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Author || role == UserRole.Admin) {
                    val categoryId = call.parameters["categoryId"]?.toIntOrNull()
                    if (categoryId != null) {
                        val updatedCategory = call.receive<Category>()
                        try {
                            val isUpdated = categoryRepository.updateCategoryById(categoryId, updatedCategory)
                            if (isUpdated) {
                                call.respond(HttpStatusCode.OK, updatedCategory)
                            } else {
                                call.respond(HttpStatusCode.NotFound, "Category with ID $categoryId not found.")
                            }
                        } catch (e: Throwable) {
                            application.log.error("Failed to update category", e)
                            call.respond(HttpStatusCode.InternalServerError, "Internal server error occurred")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid category ID")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to update category.")
                }
            }

            // DELETE
            delete("{categoryId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Admin) {
                    val categoryId = call.parameters["categoryId"]?.toIntOrNull()
                    if (categoryId != null) {
                        val isDeleted = categoryRepository.deleteCategoryById(categoryId)
                        if (isDeleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Category with ID $categoryId not found.")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid category ID")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "Only admins can delete category.")
                }
            }
        }
    }
}