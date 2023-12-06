package de.jnmultimedia.routes

import de.jnmultimedia.data.dto.UserUpdateDTO
import de.jnmultimedia.data.model.Token
import de.jnmultimedia.data.model.User
import de.jnmultimedia.data.model.UserCredentials
import de.jnmultimedia.data.model.UserRole
import de.jnmultimedia.data.repositories.UserRepository
import de.jnmultimedia.utils.JWTUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userRepository: UserRepository) {
    route("/users") {
        authenticate {
            // READ
            get {
                call.respond(userRepository.getAllUsers())
            }
        }
    }

    route("/user") {
        authenticate {
            // CREATE
            post {
                val user = call.receive<User>()
                val principal = call.principal<JWTPrincipal>()
                val roleName = principal?.payload?.getClaim("role")?.asString()
                val role = UserRole.valueOf(roleName ?: UserRole.None.name)
                if (role == UserRole.Admin) {
                    val newUser = userRepository.createUser(UserCredentials(user.username,user.password), user.role)
                    if (newUser != null) {
                        val token = JWTUtil.createJWT(newUser)
                        call.respond(HttpStatusCode.Created, Token(token))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
                    }
                } else {
                    call.respond(HttpStatusCode.Forbidden, "Only admins can create users")
                }
            }

            get("{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull()

                if (userId != null) {
                    val user = userRepository.getUser(userId)

                    if (user != null) {
                        call.respond(user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
                }
            }

            // UPDATE
            put("{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                val principal = call.principal<JWTPrincipal>()
                val principalUserId = principal?.payload?.getClaim("userId")?.asInt()
                val principalRoleName = principal?.payload?.getClaim("role")?.toString()
                val principalRole = UserRole.valueOf(principalRoleName ?: UserRole.None.name)

                if (userId != null) {
                    if (principalUserId != null || principalRole == UserRole.Admin) {
                        val updateUserDTO = call.receive<UserUpdateDTO>()
                        val user = userRepository.updateUser(
                            userId,
                            updateUserDTO.username,
                            updateUserDTO.password,
                            updateUserDTO.role
                        )
                        if (user != null) {
                            call.respond(user)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.Forbidden, "You are not authorized to edit this user")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
                }
            }

            delete("{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId != null) {
                    val principal = call.principal<JWTPrincipal>()
                    val roleName = principal?.payload?.getClaim("role")?.asString()
                    val role = UserRole.valueOf(roleName ?: UserRole.None.name)

                    if (role == UserRole.Admin) {
                        val isDeleted = userRepository.deleteUser(userId)
                        if (isDeleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.Forbidden, "Only admins can delete users")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid User ID")
                }
            }
        }
    }
}