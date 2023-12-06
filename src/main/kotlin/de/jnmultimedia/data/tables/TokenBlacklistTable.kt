package de.jnmultimedia.data.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object TokenBlacklistTable: Table("token_blacklist") {
    val token = varchar("token", 1024)
    val expiresAt = datetime("expiry_date")

    override val primaryKey = PrimaryKey(token)
}