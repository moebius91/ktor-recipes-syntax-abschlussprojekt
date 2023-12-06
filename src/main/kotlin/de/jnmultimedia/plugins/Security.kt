package de.jnmultimedia.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.jnmultimedia.data.repositories.TokenRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

const val JWTSECRET = "Der Osterhase hat viele Geheimnisse!"
const val JWTAUDIENCE = "jwt-audience"
const val JWTDOMAIN = "https://jwt.jnmultimedia.de"
const val JWTREALM = "ktor tageschroniken beispiel api server"

fun Application.configureSecurity(tokenRepository: TokenRepository) {
    authentication {
        jwt {
            realm = JWTREALM
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JWTSECRET))
                    .withAudience(JWTAUDIENCE)
                    .withIssuer(JWTDOMAIN)
                    .build()
            )
            validate { credential ->
                //if (credential.payload.audience.contains(JWTAUDIENCE)) JWTPrincipal(credential.payload) else null
                val jwtId = credential.payload.getClaim("jti").asString()
                if (credential.payload.audience.contains(JWTAUDIENCE)
                    && jwtId != null
                    && !tokenRepository.isTokenBlacklisted(jwtId)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}
