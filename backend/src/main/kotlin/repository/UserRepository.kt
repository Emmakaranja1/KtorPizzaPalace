package com.emmascode.repository

import com.emmascode.models.Users
import com.emmascode.dto.UserDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class UserRepository {
    fun findAll(): List<UserDTO> = transaction {
        Users.selectAll().map { toDTO(it) }
    }

    fun findById(id: Int): UserDTO? = transaction {
        Users.select { Users.id eq id }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByEmail(email: String): UserDTO? = transaction {
        Users.select { Users.email eq email }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByUsername(username: String): UserDTO? = transaction {
        Users.select { Users.username eq username }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun create(username: String, email: String, passwordHash: String, role: String): UserDTO = transaction {
        val now = LocalDateTime.now()
        val id = Users.insert {
            it[Users.username] = username
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
            it[Users.role] = role
            it[createdAt] = now
            it[updatedAt] = now
        } get Users.id

        findById(id)!!
    }

    fun update(id: Int, username: String?, email: String?, passwordHash: String?, role: String?): UserDTO? = transaction {
        val exists = Users.select { Users.id eq id }.count() > 0
        if (!exists) return@transaction null

        Users.update({ Users.id eq id }) {
            username?.let { value -> it[Users.username] = value }
            email?.let { value -> it[Users.email] = value }
            passwordHash?.let { value -> it[Users.passwordHash] = value }
            role?.let { value -> it[Users.role] = value }
            it[updatedAt] = LocalDateTime.now()
        }

        findById(id)
    }

    fun delete(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.id eq id } > 0
    }

    fun getPasswordHash(email: String): String? = transaction {
        Users.select { Users.email eq email }
            .mapNotNull { it[Users.passwordHash] }
            .singleOrNull()
    }

    private fun toDTO(row: ResultRow): UserDTO = UserDTO(
        id = row[Users.id],
        username = row[Users.username],
        email = row[Users.email],
        role = row[Users.role],
        createdAt = row[Users.createdAt]
    )
}


