package com.emmascode.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Orders : Table("orders") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val restaurantId = integer("restaurant_id").references(Restaurants.id)
    val status = varchar("status", 20).default("pending") // pending, confirmed, preparing, ready, delivered, cancelled
    val totalAmount = decimal("total_amount", 10, 2)
    val deliveryAddress = text("delivery_address")
    val paymentMethod = varchar("payment_method", 50).default("cash") // cash, card, mobile_money
    val paymentStatus = varchar("payment_status", 20).default("unpaid") // unpaid, paid
    val notes = text("notes").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}