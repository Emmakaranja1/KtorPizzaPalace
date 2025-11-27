package com.emmascode.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Pizzas : Table("pizzas") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val imageUrl = varchar("image_url", 255).nullable()
    val basePrice = decimal("base_price", 10, 2)
    val category = varchar("category", 50).default("classic") // classic, specialty, vegan, etc.
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}