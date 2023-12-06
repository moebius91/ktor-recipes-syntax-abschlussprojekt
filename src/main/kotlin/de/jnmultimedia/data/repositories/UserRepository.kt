package de.jnmultimedia.data.repositories

import de.jnmultimedia.data.model.User
import de.jnmultimedia.data.model.UserCredentials
import de.jnmultimedia.data.model.UserRole
import de.jnmultimedia.data.tables.UsersTable
import de.jnmultimedia.data.extensions.toUser
import org.jetbrains.exposed.sql.transactions.transaction
import de.jnmultimedia.utils.PasswordUtil.checkPassword
import de.jnmultimedia.utils.PasswordUtil.hashPassword
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserRepository {

    fun createUser(userCredentials: UserCredentials, role: UserRole): User? {
        val username = userCredentials.username
        val password = userCredentials.password

        val hashedPassword = hashPassword(password)

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val now = LocalDateTime.now().format(formatter).toString()

        return transaction {
            val userId = UsersTable.insert {
                it[UsersTable.username] = username
                it[UsersTable.password] = hashedPassword
                it[UsersTable.role] = role
            }.resultedValues
                ?.firstOrNull()
                ?.get(UsersTable.userId)

            userId?.let {

                User(
                    it,
                    username,
                    hashedPassword,
                    role,
                    createdAt = now,
                    updatedAt = now
                )
            }
        }
    }

    fun verifyUser(userCredentials: UserCredentials): User? {
        val username = userCredentials.username
        val password = userCredentials.password

        return transaction {
            val row = UsersTable.select { UsersTable.username eq username }.singleOrNull()
            row?.let {
                val hashedPassword = it[UsersTable.password]
                if (checkPassword(password, hashedPassword)) {
                    it.toUser()
                } else {
                    null
                }
            }
        }
    }

    fun getAllUsers(): List<User> {
        return transaction {
            UsersTable.selectAll().map { it.toUser() }
        }
    }

    fun getUser(userId: Int): User? {
        return transaction {
            UsersTable.select { UsersTable.userId eq userId }.singleOrNull()?.toUser()
        }
    }

    fun updateUser(
        userId: Int,
        username: String? = null,
        password: String? = null,
        role: UserRole? = null
    ): User? {
        val hashedPassword = password?.let { hashPassword(it) }
        return transaction {
            val updatedRowCount = UsersTable.update({ UsersTable.userId eq userId }) {
                if (username != null) it[UsersTable.username] = username
                if (hashedPassword != null) it[UsersTable.password] = hashedPassword
                if (role != null) it[UsersTable.role] = role
            }

            if (updatedRowCount > 0) {
                UsersTable.select { UsersTable.userId eq userId }.singleOrNull()?.toUser()
            } else {
                null
            }
        }
    }

    fun deleteUser(userId: Int): Boolean {
        return transaction {
            val deletedRowCount = UsersTable.deleteWhere { UsersTable.userId eq userId }
            deletedRowCount > 0
        }
    }
}