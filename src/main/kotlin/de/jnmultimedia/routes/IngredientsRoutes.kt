package de.jnmultimedia.routes

import de.jnmultimedia.data.model.Ingredient
import de.jnmultimedia.data.model.Recipe
import de.jnmultimedia.data.model.UserRole
import de.jnmultimedia.data.repositories.IngredientRepository
import de.jnmultimedia.data.repositories.Repositories
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.ingredientsRoutes(repositories: Repositories) {
    val ingredientRepository = repositories.ingredientRepository
    val recipeIngredientRepository = repositories.recipeIngredientRepository
    val recipeRepository = repositories.recipeRepository

    route("/ingredients") {
        get {
            val ingredientInfo = ingredientRepository.getAllIngredients()
            call.respond(ingredientInfo)
        }
    }

    route("/ingredient") {
        get("{ingredientId}") {
            val ingredientId = call.parameters["ingredientId"]?.toIntOrNull()
            if (ingredientId != null) {
                val ingredient = ingredientRepository.getIngredientById(ingredientId)
                if (ingredient != null) {
                    val recipesIds = recipeIngredientRepository.getRecipeIdsForIngredient(ingredientId)
                    val recipes = mutableListOf<Recipe>()

                    recipesIds.forEach { recipeId ->
                        val recipe = recipeRepository.getRecipeById(recipeId)

                        if (recipe != null) {
                            recipes.add(recipe)
                        }
                    }

                    call.respond(recipes)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Ingredient not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ingredient ID")
            }
        }

        authenticate {
            // CREATE
            post {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Author || role == UserRole.Admin) {
                    val ingredient = call.receive<Ingredient>()
                    ingredientRepository.createIngredient(ingredient)
                    call.respond(HttpStatusCode.OK, ingredient)
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to create ingredient")
                }
            }

            // UPDATE
            put("{ingredientId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Author || role == UserRole.Admin) {
                    val ingredientId = call.parameters["ingredientId"]?.toIntOrNull()
                    if (ingredientId != null) {
                        val updatedIngredient = call.receive<Ingredient>()
                        try {
                            val isUpdated = ingredientRepository.updateIngredientById(ingredientId, updatedIngredient)
                            if (isUpdated) {
                                call.respond(HttpStatusCode.OK, updatedIngredient)
                            } else {
                                call.respond(HttpStatusCode.NotFound, "Ingredient with ID $ingredientId not found.")
                            }
                        } catch (e: Throwable) {
                            application.log.error("Failed to update ingredient", e)
                            call.respond(HttpStatusCode.InternalServerError, "Internal server error occurred")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ingredient ID")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to update ingredient.")
                }
            }

            // DELETE
            delete("{ingredientId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Admin) {
                    val ingredientId = call.parameters["ingredientId"]?.toIntOrNull()
                    if (ingredientId != null) {
                        val isDeleted = ingredientRepository.deleteIngredientById(ingredientId)
                        if (isDeleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Ingredient with ID $ingredientId not found.")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ingredient ID")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "Only admins can delete ingredient.")
                }
            }
        }
    }
}