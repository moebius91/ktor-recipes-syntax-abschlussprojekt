package de.jnmultimedia

import de.jnmultimedia.data.repositories.Repositories
import de.jnmultimedia.plugins.*
import io.ktor.server.application.*
import de.jnmultimedia.utils.PasswordUtil.hashPassword
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //println( hashPassword("passwort1"))

    configureDatabases()
    val dataSource = APP_DATA_SOURCE ?: throw IllegalStateException("Database not configured")
    val repositories = Repositories(dataSource)

    configureSecurity(repositories.tokenRepository)
    configureSerialization()
    configureRouting(repositories)
}
