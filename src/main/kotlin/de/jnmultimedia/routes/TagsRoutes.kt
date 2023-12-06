package de.jnmultimedia.routes

import de.jnmultimedia.data.model.Recipe
import de.jnmultimedia.data.model.User
import de.jnmultimedia.data.model.UserRole
import de.jnmultimedia.data.repositories.TagRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import de.jnmultimedia.data.model.Tag
import de.jnmultimedia.data.repositories.Repositories
import io.ktor.http.*
import io.ktor.server.response.*

fun Route.tagsRoutes(repositories: Repositories) {
    val tagRepository = repositories.tagRepository
    val recipeTagRepository = repositories.recipeTagRepository
    val recipeRepository = repositories.recipeRepository

    route("/tags") {

        // READ
        get {
            call.respond(tagRepository.getAllTags())
        }
    }

    route("/tag") {
        get("{tagId}") {
            val tagId = call.parameters["tagId"]?.toIntOrNull()

            if (tagId != null) {
                val tag = tagRepository.getTagById(tagId)

                if (tag != null) {
                    val recipesIds = recipeTagRepository.getRecipeIdsForTag(tagId)
                    val recipes = mutableListOf<Recipe>()

                    recipesIds.forEach { recipeId ->
                        val recipe = recipeRepository.getRecipeById(recipeId)

                        if (recipe != null) {
                            recipes.add(recipe)
                        }
                    }

                    call.respond(recipes)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Tag not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid Tag ID")
            }
        }

        authenticate {
            // CREATE
            post {
                val tag = call.receive<Tag>()
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Admin || role == UserRole.Author) {
                    val newTag = tagRepository.createTag(tag)
                    if (newTag != null) {
                        call.respond(HttpStatusCode.Created, newTag)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create tag")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to create tags")
                }
            }

            // UPDATE
            put("{tagId}") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role == UserRole.Author ||role == UserRole.Admin) {
                    val tagId = call.parameters["tagId"]?.toIntOrNull()
                    if (tagId != null) {
                        val updatedTag = call.receive<Tag>()
                        try {
                            val isUpdated = tagRepository.updateTagById(tagId, updatedTag)
                            if (isUpdated) {
                                call.respond(HttpStatusCode.OK, updatedTag)
                            } else {
                                call.respond(HttpStatusCode.NotFound, "Tag with ID $tagId not found.")
                            }
                        } catch (e: Throwable) {
                            application.log.error("Failed to update tag", e)
                            call.respond(HttpStatusCode.InternalServerError, "Internal server error occurred")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid tag ID")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You are not authorized to update tag.")
                }
            }

            // DELETE
            delete("tagID") {
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: "None")
                if (role ==UserRole.Admin) {
                    val tagId = call.parameters["tagId"]?.toIntOrNull()
                    if (tagId != null) {
                        val isDeleted = tagRepository.deleteTagById(tagId)
                        if (isDeleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Tag with ID $tagId not found.")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid Tag ID")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "Only admins can delete tag.")
                }
            }
        }
    }
}