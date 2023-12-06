package de.jnmultimedia.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.jnmultimedia.data.model.User
import de.jnmultimedia.plugins.JWTAUDIENCE
import de.jnmultimedia.plugins.JWTDOMAIN
import de.jnmultimedia.plugins.JWTSECRET
import java.util.*

object JWTUtil {
    private const val SECRET = JWTSECRET
    private const val ISSUER = JWTDOMAIN
    private const val AUDIENCE = JWTAUDIENCE

    fun createJWT(user: User): String {
        val algorithm = Algorithm.HMAC256(SECRET)
        val jwtId = UUID.randomUUID().toString()
        return JWT.create()
            .withAudience(AUDIENCE)
            .withSubject(user.userId.toString())
            .withIssuer(ISSUER)
            .withClaim("role", user.role.name)
            .withClaim("userId", user.userId)
            .withClaim("username", user.username)
            .withClaim("jti", jwtId)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
            .sign(algorithm)
    }

}