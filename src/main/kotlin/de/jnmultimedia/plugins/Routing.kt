package de.jnmultimedia.plugins

import de.jnmultimedia.data.repositories.Repositories
import de.jnmultimedia.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repositories: Repositories) {
    routing {
        authenticationRoutes(repositories)
        userRoutes(repositories)
        recipeRoutes(repositories)
        ingredientsRoutes(repositories)
        categoriesRoutes(repositories)
        tagsRoutes(repositories)
    }
}
