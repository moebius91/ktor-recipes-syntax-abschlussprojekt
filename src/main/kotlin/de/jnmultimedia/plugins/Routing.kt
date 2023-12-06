package de.jnmultimedia.plugins

import de.jnmultimedia.data.repositories.Repositories
import de.jnmultimedia.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repositories: Repositories) {
    val userRepository = repositories.userRepository
    val tokenRepository = repositories.tokenRepository
    val tagRepository = repositories.tagRepository
    val categoryRepository = repositories.categoryRepository

    routing {
        authenticationRoutes(userRepository, tokenRepository)
        userRoutes(userRepository)
        recipeRoutes(repositories)
        ingredientsRoutes(repositories)
        categoriesRoutes(repositories)
        tagsRoutes(repositories)
    }
}
