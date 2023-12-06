package de.jnmultimedia.routes

import de.jnmultimedia.data.model.Token
import de.jnmultimedia.data.model.UserCredentials
import de.jnmultimedia.data.repositories.Repositories
import de.jnmultimedia.data.repositories.TokenRepository
import de.jnmultimedia.data.repositories.UserRepository
import de.jnmultimedia.utils.JWTUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.ZoneId

fun Route.authenticationRoutes(repositories: Repositories) {
    val userRepository = repositories.userRepository
    val tokenRepository = repositories.tokenRepository

    route("/login") {
        post {
            val credentials = call.receive<UserCredentials>()
            val user = userRepository.verifyUser(credentials)
            if (user != null) {
                val token = JWTUtil.createJWT(user)
                call.respond(Token(token))
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    route("/logout") {
        authenticate {
            post {
                val principal = call.principal<JWTPrincipal>()
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val jti = principal?.payload?.getClaim("jti")?.asString()

                if (token != null && principal != null) {
                    val expirationDate = principal.expiresAt
                    val expiration = expirationDate?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDateTime()

                    if (expiration != null) {
                        tokenRepository.addToBlacklist(jti ?: token, expiration)
                        call.respond(HttpStatusCode.OK, "Logged out successfully.")
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "You arte not authorized to log out.")
                }
            }
        }
    }
}