package de.jnmultimedia.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

var APP_DATA_SOURCE: HikariDataSource? = null

fun Application.configureDatabases() {
    val hikariConfig = HikariConfig(). apply {
        //jdbcUrl = "jdbc:mysql://ktor-sample-admin-panel-login-db:3306/eb_tageschroniken"
        jdbcUrl = "jdbc:mysql://localhost:3310/jno_testapi"
        driverClassName = "org.mariadb.jdbc.Driver"
        username = "jno_api"
        password = "jno_api"
        maximumPoolSize = 5
    }
    val dataSource = HikariDataSource(hikariConfig)
    val database = Database.connect(dataSource)

    APP_DATA_SOURCE = dataSource

    transaction(database) {
        val result = exec("SELECT 1") {
            it.next()
            it.getInt(1)
        }
        println("Datenbank funktioniert: ${(result == 1)}")
    }
}
