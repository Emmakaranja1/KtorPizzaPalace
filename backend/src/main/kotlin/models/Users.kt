package com.emmascode.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("customer") // customer, restaurant_owner, admin
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}