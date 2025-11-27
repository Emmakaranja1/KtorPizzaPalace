package com.emmascode.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Restaurants : Table("restaurants") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val address = text("address")
    val phone = varchar("phone", 20).nullable()
    val email = varchar("email", 100).nullable()
    val ownerId = integer("owner_id").references(Users.id)
    val rating = decimal("rating", 3, 2).default(0.0.toBigDecimal())
    val imageUrl = varchar("image_url", 255).nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}