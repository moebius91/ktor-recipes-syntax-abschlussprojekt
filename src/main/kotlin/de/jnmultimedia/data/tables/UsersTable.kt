package de.jnmultimedia.data.tables

import de.jnmultimedia.data.model.UserRole
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UsersTable: Table("users") {
    val userId = integer("user_id").autoIncrement()
    override val primaryKey = PrimaryKey(userId)
    val username = varchar("username", 255)
    val password = varchar("password", 255)
    val role = enumerationByName("role", 10, UserRole::class)
    val createdAt = datetime("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = datetime("updated_at").defaultExpression(CurrentTimestamp())
}