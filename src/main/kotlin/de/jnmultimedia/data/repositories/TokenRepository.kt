package de.jnmultimedia.data.repositories

import com.zaxxer.hikari.HikariDataSource
import de.jnmultimedia.data.tables.TokenBlacklistTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TokenRepository(private val dataSource: HikariDataSource) {

    init {
        Database.connect(dataSource)
    }

    fun addToBlacklist(jti: String, expiresAt: LocalDateTime) {
        transaction {
            TokenBlacklistTable.insert {
                it[TokenBlacklistTable.token] = jti
                it[TokenBlacklistTable.expiresAt] = expiresAt
            }
        }
    }

    fun isTokenBlacklisted(jti: String): Boolean {
        return transaction {
            TokenBlacklistTable.select { TokenBlacklistTable.token eq jti }
                .any()
        }
    }

    fun removeExpiredTokens() {
        transaction {
            TokenBlacklistTable.deleteWhere { TokenBlacklistTable.expiresAt lessEq LocalDateTime.now() }
        }
    }

}