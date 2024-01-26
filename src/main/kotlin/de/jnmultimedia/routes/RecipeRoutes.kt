package de.jnmultimedia.routes

import de.jnmultimedia.data.model.*
import de.jnmultimedia.data.repositories.Repositories
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.recipeRoutes(repositories: Repositories) {
    val recipeRepository = repositories.recipeRepository
    val recipeCategoryRepository = repositories.recipeCategoryRepository
    val recipeTagRepository = repositories.recipeTagRepository
    val recipeIngredientRepository = repositories.recipeIngredientRepository
    val ingredientRepository = repositories.ingredientRepository
    val tagRepository = repositories.tagRepository
    val categoryRepository = repositories.categoryRepository

    // READ
    route("/recipes") {
        get {
            val recipeInfo = recipeRepository.getAllRecipes()
            call.respond(recipeInfo)
        }
    }

    route("/recipe") {
        get("{recipeId}") {
            val recipeId = call.parameters["recipeId"]?.toIntOrNull()
            if (recipeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                return@get
            }

            val recipe = recipeRepository.getRecipeById(recipeId)
            if (recipe == null) {
                call.respond(HttpStatusCode.NotFound, "Recipe not found")
                return@get
            }

            val ingredientsIds = recipeIngredientRepository.getIngredientsForRecipe(recipeId)

            val tagsIds = recipeTagRepository.getTagsForRecipe(recipeId)
            val categoryIds = recipeCategoryRepository.getCategoriesForRecipe(recipeId)

            val ingredients = mutableListOf<Ingredient?>()
            val tags = mutableListOf<Tag?>()
            val categories = mutableListOf<Category?>()

            ingredientsIds.forEach { ingredientId ->
                val ingredient = ingredientRepository.getIngredientById(ingredientId)
                if (ingredient != null) {
                    ingredients.add(ingredient)
                }
            }

            tagsIds.forEach { tagId ->
                val tag = tagRepository.getTagById(tagId)
                if (tag != null) {
                    tags.add(tag)
                }
            }

            categoryIds.forEach { categoryId ->
                val category = categoryRepository.getCategoryById(categoryId)
                if (category != null) {
                    categories.add(category)
                }
            }

            val recipeOutput = RecipeOutput(
                recipe.recipeId,
                recipe.name,
                recipe.description,
                ingredients,
                tags,
                categories,
                recipe.authorId ?: 0,
                recipe.creationDate!!
            )

            call.respond(recipeOutput)
        }

        authenticate {
            // CREATE
            post {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val role = UserRole.valueOf(roleName ?: "None")

                if (role != UserRole.Author && role != UserRole.Admin) {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to create recipe")
                    return@post
                }

                val recipeCreationRequest = try {
                    call.receive<RecipeCreationRequest>()
                } catch (e: Exception) {
                    application.log.error("Error while receiving RecipeCreationRequest", e)
                    call.respond(HttpStatusCode.BadRequest, "Invalid request format")
                    return@post
                }

                val recipeToCreate = recipeCreationRequest.recipe.copy(authorId = userId ?: 0)
                val createdRecipe = recipeRepository.createRecipe(recipeToCreate)

                if (createdRecipe?.recipeId == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create recipe")
                    return@post
                }

                try {

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Ingredients not found in RecipeCreateRequest.")
                }

                try {

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Ingredients not found in RecipeCreateRequest.")
                }

                try {

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Ingredients not found in RecipeCreateRequest.")
                }

                try {
                    recipeCreationRequest.categoryIds.forEach { categoryId ->
                        recipeCategoryRepository.addCategoryToRecipe(createdRecipe.recipeId, categoryId)
                    }

                    recipeCreationRequest.tagIds.forEach { tagId ->
                        recipeTagRepository.addTagToRecipe(createdRecipe.recipeId, tagId)
                    }

                    recipeCreationRequest.ingredients.forEach { ingredient ->
                        recipeIngredientRepository.addIngredientToRecipe(createdRecipe.recipeId, ingredient)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add categories or tags.")
                }

                call.respond(HttpStatusCode.Created, createdRecipe)
            }

            // UPDATE
            put("{recipeId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")

                if (role != UserRole.Author && role != UserRole.Admin) {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to update recipe")
                    return@put
                }

                val recipeId = call.parameters["recipeId"]?.toIntOrNull()
                if (recipeId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                    return@put
                }

                val updateRequest = try {
                    call.receive<RecipeUpdateRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid recipe data")
                    return@put
                }

                try {
                    val isUpdated = recipeRepository.updateRecipeById(recipeId, updateRequest.recipe)
                    if (!isUpdated) {
                        call.respond(HttpStatusCode.NotFound, "Recipe with ID $recipeId not found.")
                        return@put
                    }

                    recipeCategoryRepository.removeCategoriesFromRecipe(recipeId)
                    updateRequest.categoryIds.forEach { categoryId ->
                        recipeCategoryRepository.addCategoryToRecipe(recipeId, categoryId)
                    }

                    recipeTagRepository.removeTagsFromRecipe(recipeId)
                    updateRequest.tagIds.forEach { tagId ->
                        recipeTagRepository.addTagToRecipe(recipeId, tagId)
                    }

                    call.respond(HttpStatusCode.OK, updateRequest.recipe)
                } catch (e: Throwable) {
                    application.log.error("Failed to update recipe", e)
                    call.respond(HttpStatusCode.InternalServerError, "Internal server error occurred")
                }
            }

            // DELETE
            delete("{recipeId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")

                if (role != UserRole.Admin) {
                    call.respond(HttpStatusCode.Forbidden, "Only admins can delete recipe.")
                    return@delete
                }

                val recipeId = call.parameters["recipeId"]?.toIntOrNull()
                if (recipeId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
                    return@delete
                }

                val isDeleted = recipeRepository.deleteRecipeById(recipeId)
                if (!isDeleted) {
                    call.respond(HttpStatusCode.NotFound, "Recipe with ID $recipeId not found.")
                    return@delete
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}